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

public class MinimaxBot implements Bot {
    private static final int SEARCH_DEPTH = 6;
    private final Random random = new Random();

    private static class MoveCandidate {
        final boolean isMove;
        final int startX, startY, targetX, targetY, rotateDelta;

        MoveCandidate(int startX, int startY, int targetX, int targetY) {
            this.isMove = true;
            this.startX = startX;
            this.startY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.rotateDelta = 0;
        }

        MoveCandidate(int startX, int startY, int rotateDelta) {
            this.isMove = false;
            this.startX = startX;
            this.startY = startY;
            this.targetX = startX;
            this.targetY = startY;
            this.rotateDelta = rotateDelta;
        }
    }

    @Override
    public int[] chooseAndPlayMove(GameLoop gameLoop) {
        ChessBoard board = gameLoop.getChessBoard();
        Player currentPlayer = gameLoop.getCurrentPlayer();
        Colour myColour = currentPlayer.getColor();

        List<MoveCandidate> moves = generateAllMoves(board, currentPlayer, myColour);
        if (moves.isEmpty()) return null;

        Collections.shuffle(moves, random);

        MoveCandidate bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (MoveCandidate move : moves) {
            // Simulace: vyhodnocujeme bez reálného volání gameLoop.tryMove v cyklu
            int value = minimaxSimulated(board, SEARCH_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, myColour, currentPlayer);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        // Až po výběru nejlepšího tahu provádíme JEDINÝ reálný tah
        if (bestMove != null) {
            boolean moved;
            if (bestMove.isMove) {
                moved = gameLoop.tryMove(bestMove.startX, bestMove.startY, bestMove.targetX, bestMove.targetY);
                if (moved) return new int[]{bestMove.startX, bestMove.startY, bestMove.targetX, bestMove.targetY};
            } else {
                moved = gameLoop.tryMove(bestMove.startX, bestMove.startY, bestMove.rotateDelta);
                if (moved) return new int[]{bestMove.startX, bestMove.startY, bestMove.startX, bestMove.startY};
            }
        }

        return null;
    }

    private int minimaxSimulated(ChessBoard board, int depth, int alpha, int beta, boolean isMaximizing, Colour myColour, Player player) {
        if (depth == 0) {
            return evaluateBoard(board, myColour);
        }

        List<MoveCandidate> moves = generateAllMoves(board, player, player.getColor());
        if (moves.isEmpty()) {
            return evaluateBoard(board, myColour);
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (MoveCandidate move : moves) {
                int eval = evaluateBoard(board, myColour); // Odhad pozice
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (MoveCandidate move : moves) {
                int eval = evaluateBoard(board, myColour);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluateBoard(ChessBoard board, Colour myColour) {
        int score = 0;
        int cols = board.getWidth();
        int rows = board.getHeight();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null) {
                    int val = piece.getValue() * 10;
                    if (piece.getColour() == myColour) {
                        score += val;
                    } else {
                        score -= val;
                    }
                }
            }
        }
        return score;
    }

    private List<MoveCandidate> generateAllMoves(ChessBoard board, Player player, Colour colour) {
        List<MoveCandidate> moves = new ArrayList<>();
        int cols = board.getWidth();
        int rows = board.getHeight();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece == null || piece.getColour() != colour) continue;

                for (int[] target : board.getPossibleTargets(x, y, player)) {
                    moves.add(new MoveCandidate(x, y, target[0], target[1]));
                }

                ArrayList<MoveType> rawRotates = board.getRotateMoves(x, y);
                ArrayList<MoveType> validRotates = board.validRotateMoves(x, y, rawRotates);
                for (MoveType m : validRotates) {
                    int delta = ((m.getRotate() % 4) + 4) % 4;
                    moves.add(new MoveCandidate(x, y, delta));
                }
            }
        }
        return moves;
    }

    @Override
    public int choosePromotion(String[] pieceNames) {
        return 0;
    }
}