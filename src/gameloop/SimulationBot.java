package gameloop;

import logic.ChessBoard;
import logic.Colour;
import logic.MoveType;
import logic.Player;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimulationBot implements Bot {
    private final Random random = new Random();

    private static final int TYPE_MOVE = 0;
    private static final int TYPE_ROTATE = 1;

    private static class Candidate {
        final int type, startX, startY, targetX, targetY, delta;
        final int score;

        Candidate(int type, int startX, int startY, int targetX, int targetY, int delta, int score) {
            this.type = type;
            this.startX = startX;
            this.startY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.delta = delta;
            this.score = score;
        }
    }

    @Override
    public int[] chooseAndPlayMove(GameLoop gameLoop) {
        ChessBoard board = gameLoop.getChessBoard();
        Player currentPlayer = gameLoop.getCurrentPlayer();
        Colour myColour = currentPlayer.getColor();

        List<Candidate> candidates = new ArrayList<>();

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Piece movingPiece = board.getPiece(x, y);
                if (movingPiece == null || movingPiece.getColour() != myColour) continue;

                // 1. Testování posunů s plnou simulací
                for (int[] target : board.getPossibleTargets(x, y, currentPlayer)) {
                    int tx = target[0];
                    int ty = target[1];

                    // Vytvoření kopie desky
                    ChessBoard simBoard = new ChessBoard(board);
                    Piece targetPiece = simBoard.getPiece(tx, ty);

                    // Základní zisk za sebranou figurku
                    int score = (targetPiece != null) ? targetPiece.getValue() * 10 : 0;

                    // Provedení simulovaného tahu
                    simBoard.simulateMove(x, y, tx, ty);

                    // SEBEZÁCHOVA: Zjistíme hrozby od soupeře
                    int maxPossibleLoss = 0;
                    for (int ex = 0; ex < simBoard.getWidth(); ex++) {
                        for (int ey = 0; ey < simBoard.getHeight(); ey++) {
                            Piece enemy = simBoard.getPiece(ex, ey);
                            if (enemy == null || enemy.getColour() == myColour) continue;

                            // OPRAVA: Vytvoříme dočasný objekt hráče pro soupeře, aby getColor() nevracelo null
                            Player enemyPlayer = new Player("EnemySim", enemy.getColour(),500);

                            for (int[] enemyTarget : simBoard.getPossibleTargets(ex, ey, enemyPlayer)) {
                                Piece threatenedPiece = simBoard.getPiece(enemyTarget[0], enemyTarget[1]);
                                if (threatenedPiece != null && threatenedPiece.getColour() == myColour) {
                                    int loss = threatenedPiece.getValue() * 10;
                                    if (loss > maxPossibleLoss) {
                                        maxPossibleLoss = loss;
                                    }
                                }
                            }
                        }
                    }

                    score -= maxPossibleLoss;
                    candidates.add(new Candidate(TYPE_MOVE, x, y, tx, ty, 0, score));
                }

                // 2. Testování rotací
                ArrayList<MoveType> rawRotates = board.getRotateMoves(x, y);
                ArrayList<MoveType> validRotates = board.validRotateMoves(x, y, rawRotates);
                for (MoveType m : validRotates) {
                    int delta = ((m.getRotate() % 4) + 4) % 4;
                    candidates.add(new Candidate(TYPE_ROTATE, x, y, x, y, delta, 0));
                }
            }
        }

        if (candidates.isEmpty()) return null;

        int bestScore = Integer.MIN_VALUE;
        for (Candidate c : candidates) {
            if (c.score > bestScore) bestScore = c.score;
        }

        List<Candidate> bestCandidates = new ArrayList<>();
        for (Candidate c : candidates) {
            if (c.score == bestScore) bestCandidates.add(c);
        }

        Collections.shuffle(bestCandidates, random);

        for (Candidate c : bestCandidates) {
            boolean moved;
            if (c.type == TYPE_MOVE) {
                moved = gameLoop.tryMove(c.startX, c.startY, c.targetX, c.targetY);
                if (moved) return new int[]{c.startX, c.startY, c.targetX, c.targetY};
            } else {
                moved = gameLoop.tryMove(c.startX, c.startY, c.delta);
                if (moved) return new int[]{c.startX, c.startY, c.startX, c.startY};
            }
        }

        return null;
    }

    @Override
    public int choosePromotion(String[] pieceNames) {
        return 0;
    }
}