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

/**
 * Vždy preferuje nejcennější dostupné braní. Mezi rovnocennými možnostmi
 * (žádné braní k dispozici) si vybírá náhodně.
 */
public class GreedyBot implements Bot {       //   1/10
    private final Random random = new Random();

    private static final int TYPE_MOVE = 0;
    private static final int TYPE_ROTATE = 1;
    private static final int NEUTRAL_SCORE = 1;

    private static class Candidate {
        final int type, startX, startY, a, b, score;

        Candidate(int type, int startX, int startY, int a, int b, int score) {
            this.type = type;
            this.startX = startX;
            this.startY = startY;
            this.a = a;
            this.b = b;
            this.score = score;
        }
    }

    @Override
    public int[] chooseAndPlayMove(GameLoop gameLoop) {
        ChessBoard board = gameLoop.getChessBoard();
        Player currentPlayer = gameLoop.getCurrentPlayer();
        Colour myColour = currentPlayer.getColor();

        int cols = board.getWidth();
        int rows = board.getHeight();

        List<Candidate> candidates = new ArrayList<>();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece == null || piece.getColour() != myColour) continue;

                for (int[] target : board.getPossibleTargets(x, y, currentPlayer)) {
                    Piece occupied = board.getPiece(target[0], target[1]);
                    int score = (occupied != null) ? occupied.getValue() : NEUTRAL_SCORE;
                    candidates.add(new Candidate(TYPE_MOVE, x, y, target[0], target[1], score));
                }

                ArrayList<MoveType> rawRotates = board.getRotateMoves(x, y);
                ArrayList<MoveType> validRotates = board.validRotateMoves(x, y, rawRotates);
                for (MoveType m : validRotates) {
                    int delta = ((m.getRotate() % 4) + 4) % 4;
                    candidates.add(new Candidate(TYPE_ROTATE, x, y, delta, 0, NEUTRAL_SCORE));
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
                moved = gameLoop.tryMove(c.startX, c.startY, c.a, c.b);
                if (moved) return new int[]{c.startX, c.startY, c.a, c.b};
            } else {
                moved = gameLoop.tryMove(c.startX, c.startY, c.a);
                if (moved) return new int[]{c.startX, c.startY, c.startX, c.startY};
            }
        }

        return null;
    }

    @Override
    public int choosePromotion(String[] pieceNames) {
        return 0; // zjednodušeně bere první možnost (u Pawn je to Queen)
    }
}