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
 * Vylepšená verze TacticalBota:
 *  1) Riziko počítá SPRÁVNĚ — hodnotu VLASTNÍ figurky, kterou by mohl ztratit,
 *     ne hodnotu útočníka.
 *  2) Pokud je vlastní Head PRÁVĚ TEĎ ohrožený, dá obrovskou prioritu tahům,
 *     které hrozbu řeší (sebrání útočící figurky, nebo útěk samotné Head figurky).
 *  3) Lehce penalizuje okamžité vrácení svého vlastního posledního tahu,
 *     ať se zbytečně netočí tam a zpátky.
 */
public class TacticalBot implements Bot {
    private final Random random = new Random();

    private static final int TYPE_MOVE = 0;
    private static final int TYPE_ROTATE = 1;

    private static final int HEAD_DEFENSE_BONUS = 100000; // dominuje nad vším ostatním
    private static final int UNDO_PENALTY = 5;             // lehké odrazení od tahu tam a zpátky

    // Pamatuje si poslední VLASTNÍ tah, aby se ho vyhnul okamžitě vrátit
    private int lastFromX = -1, lastFromY = -1, lastToX = -1, lastToY = -1;

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

        // Zjistíme, jestli je NAŠE Head PRÁVĚ TEĎ ohrožená, a pokud ano, odkud (pozice útočníků)
        List<int[]> threatSquares = new ArrayList<>();
        for (int[] head : board.getHeadPositions(myColour)) {
            ArrayList<int[]> attackers = board.getAttackersOfSquare(head[0], head[1], myColour);
            threatSquares.addAll(attackers);
        }
        boolean headIsThreatened = !threatSquares.isEmpty();

        List<Candidate> candidates = new ArrayList<>();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece == null || piece.getColour() != myColour) continue;

                for (int[] target : board.getPossibleTargets(x, y, currentPlayer)) {
                    int score = evaluateDestination(board, x, y, target[0], target[1], myColour);

                    // Obrana krále má přednost přede vším — sebrání figurky, co na Head útočí
                    if (headIsThreatened && isAttackerSquare(threatSquares, target[0], target[1])) {
                        score += HEAD_DEFENSE_BONUS;
                    }

                    // Pokud je Head ohrožená a tahle figurka JE ta Head, útěk taky velmi ceníme
                    if (headIsThreatened && piece instanceof pieces.Head) {
                        score += HEAD_DEFENSE_BONUS / 2;
                    }

                    // Lehká penalizace za okamžité vrácení posledního tahu
                    if (isUndoOfLastMove(x, y, target[0], target[1])) {
                        score -= UNDO_PENALTY;
                    }

                    candidates.add(new Candidate(TYPE_MOVE, x, y, target[0], target[1], score));
                }

                ArrayList<MoveType> rawRotates = board.getRotateMoves(x, y);
                ArrayList<MoveType> validRotates = board.validRotateMoves(x, y, rawRotates);
                for (MoveType m : validRotates) {
                    int delta = ((m.getRotate() % 4) + 4) % 4;
                    candidates.add(new Candidate(TYPE_ROTATE, x, y, delta, 0, 0));
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
                if (moved) {
                    lastFromX = c.startX; lastFromY = c.startY;
                    lastToX = c.a; lastToY = c.b;
                    return new int[]{c.startX, c.startY, c.a, c.b};
                }
            } else {
                moved = gameLoop.tryMove(c.startX, c.startY, c.a);
                if (moved) {
                    lastFromX = lastToX = c.startX;
                    lastFromY = lastToY = c.startY;
                    return new int[]{c.startX, c.startY, c.startX, c.startY};
                }
            }
        }

        return null;
    }

    /**
     * ZISK (hodnota figurky, kterou bychom sebrali) MÍNUS RIZIKO
     * (hodnota NAŠÍ VLASTNÍ figurky, pokud by na cílovém poli hrozilo braní zpět).
     */
    private int evaluateDestination(ChessBoard board, int startX, int startY, int endX, int endY, Colour myColour) {
        Piece target = board.getPiece(endX, endY);
        int gain = (target != null) ? target.getValue() : 0;

        Piece movingPiece = board.getPiece(startX, startY);
        int movingValue = (movingPiece != null) ? movingPiece.getValue() : 0;

        int risk = 0;
        ArrayList<int[]> attackers = board.getAttackersOfSquare(endX, endY, myColour);
        if (!attackers.isEmpty()) {
            risk = movingValue; // riskujeme ztrátu VLASTNÍ figurky, ne hodnotu útočníka
        }

        return gain - risk;
    }

    private boolean isAttackerSquare(List<int[]> attackerSquares, int x, int y) {
        for (int[] a : attackerSquares) {
            if (a[0] == x && a[1] == y) return true;
        }
        return false;
    }

    private boolean isUndoOfLastMove(int startX, int startY, int endX, int endY) {
        return startX == lastToX && startY == lastToY && endX == lastFromX && endY == lastFromY;
    }

    @Override
    public int choosePromotion(String[] pieceNames) {
        return 0; // bere první možnost (u Pawn je to Queen)
    }
}