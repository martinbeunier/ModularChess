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
 * Vylepšená verze MinimaxBota:
 *  - Alpha-beta prořezávání — stejný výsledek jako čistý minimax, ale
 *    prořeže větve, které nemůžou ovlivnit finální rozhodnutí, takže jde
 *    prohledat VĚTŠÍ hloubku za podobný čas.
 *  - Evaluace navíc počítá MOBILITU (počet reálně dostupných tahů), aby
 *    tiché pozice bez braní/ohrožení krále přestaly být dokonale vyrovnané
 *    (a bot přestal bloudit náhodně, jako to dělal Greedy/TacticalBot).
 */
public class AlphaBetaBot implements Bot {              // 2/10
    private static final int SEARCH_DEPTH = 2; // díky prořezávání zvládneme jít hlouběji než MinimaxBot
    private static final int HEAD_THREAT_PENALTY = 5000;
    private static final int MOBILITY_WEIGHT = 2; // váha jednoho dostupného tahu v ohodnocení

    private final Random random = new Random();

    private static class Move {
        static final int TYPE_MOVE = 0;
        static final int TYPE_ROTATE = 1;

        final int type, startX, startY, a, b;

        Move(int type, int startX, int startY, int a, int b) {
            this.type = type;
            this.startX = startX;
            this.startY = startY;
            this.a = a;
            this.b = b;
        }
    }

    @Override
    public int[] chooseAndPlayMove(GameLoop gameLoop) {
        ChessBoard realBoard = gameLoop.getChessBoard();
        Player currentPlayer = gameLoop.getCurrentPlayer();
        Colour myColour = currentPlayer.getColor();

        List<Move> rootMoves = collectAllMoves(realBoard, currentPlayer, myColour);
        if (rootMoves.isEmpty()) return null;

        int bestScore = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move m : rootMoves) {
            ChessBoard childBoard = realBoard.clone();
            Player childMover = findPlayerByColour(childBoard, myColour);
            applyMove(childBoard, childMover, m);

            int score = alphaBeta(childBoard, otherColour(myColour), myColour, SEARCH_DEPTH - 1, alpha, beta);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(m);
            } else if (score == bestScore) {
                bestMoves.add(m);
            }

            if (score > alpha) alpha = score;
        }

        Collections.shuffle(bestMoves, random);

        // Teprve TEĎ hrajeme SKUTEČNÝ tah na SKUTEČNÉ hře (přes gameLoop, ne clone)
        for (Move m : bestMoves) {
            boolean moved;
            if (m.type == Move.TYPE_MOVE) {
                moved = gameLoop.tryMove(m.startX, m.startY, m.a, m.b);
                if (moved) return new int[]{m.startX, m.startY, m.a, m.b};
            } else {
                moved = gameLoop.tryMove(m.startX, m.startY, m.a);
                if (moved) return new int[]{m.startX, m.startY, m.startX, m.startY};
            }
        }

        return null;
    }

    /**
     * Minimax s alpha-beta prořezáváním. alpha = nejlepší garantované skóre
     * pro maximalizujícího hráče (nás), beta = nejlepší garantované skóre
     * pro minimalizujícího (soupeře). Jakmile alpha >= beta, zbytek téhle
     * větve už nemůže ovlivnit finální rozhodnutí, takže ji přeskočíme.
     */
    private int alphaBeta(ChessBoard board, Colour colourToMove, Colour myColour, int depth, int alpha, int beta) {
        if (depth == 0 || board.countHeads() <= 1) {
            return evaluatePosition(board, myColour);
        }

        Player mover = findPlayerByColour(board, colourToMove);
        if (mover == null) return evaluatePosition(board, myColour);

        List<Move> moves = collectAllMoves(board, mover, colourToMove);
        if (moves.isEmpty()) {
            return evaluatePosition(board, myColour);
        }

        boolean maximizing = (colourToMove == myColour);

        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (Move m : moves) {
                ChessBoard childBoard = board.clone();
                Player childMover = findPlayerByColour(childBoard, colourToMove);
                applyMove(childBoard, childMover, m);

                int score = alphaBeta(childBoard, otherColour(colourToMove), myColour, depth - 1, alpha, beta);
                if (score > best) best = score;
                if (best > alpha) alpha = best;
                if (alpha >= beta) break; // prořezání — soupeř by tuhle větev nikdy nedovolil
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Move m : moves) {
                ChessBoard childBoard = board.clone();
                Player childMover = findPlayerByColour(childBoard, colourToMove);
                applyMove(childBoard, childMover, m);

                int score = alphaBeta(childBoard, otherColour(colourToMove), myColour, depth - 1, alpha, beta);
                if (score < best) best = score;
                if (best < beta) beta = best;
                if (alpha >= beta) break; // prořezání
            }
            return best;
        }
    }

    /**
     * Materiál + ohrožení Head (stejně jako MinimaxBot) + MOBILITA — počet
     * reálně dostupných tahů pro obě strany. Tiché pozice bez braní/ohrožení
     * tak přestávají být dokonale vyrovnané a bot přestává bloudit náhodně.
     */
    private int evaluatePosition(ChessBoard board, Colour myColour) {
        int score = 0;

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Piece p = board.getPiece(x, y);
                if (p == null) continue;
                score += (p.getColour() == myColour) ? p.getValue() : -p.getValue();
            }
        }

        for (int[] head : board.getHeadPositions(myColour)) {
            if (!board.getAttackersOfSquare(head[0], head[1], myColour).isEmpty()) {
                score -= HEAD_THREAT_PENALTY;
            }
        }

        Colour enemyColour = otherColour(myColour);
        for (int[] head : board.getHeadPositions(enemyColour)) {
            if (!board.getAttackersOfSquare(head[0], head[1], enemyColour).isEmpty()) {
                score += HEAD_THREAT_PENALTY;
            }
        }

        Player myPlayer = findPlayerByColour(board, myColour);
        Player enemyPlayer = findPlayerByColour(board, enemyColour);

        if (myPlayer != null) {
            score += collectAllMoves(board, myPlayer, myColour).size() * MOBILITY_WEIGHT;
        }
        if (enemyPlayer != null) {
            score -= collectAllMoves(board, enemyPlayer, enemyColour).size() * MOBILITY_WEIGHT;
        }

        return score;
    }

    private List<Move> collectAllMoves(ChessBoard board, Player player, Colour colour) {
        List<Move> moves = new ArrayList<>();
        int cols = board.getWidth();
        int rows = board.getHeight();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece p = board.getPiece(x, y);
                if (p == null || p.getColour() != colour) continue;

                for (int[] target : board.getPossibleTargets(x, y, player)) {
                    moves.add(new Move(Move.TYPE_MOVE, x, y, target[0], target[1]));
                }

                ArrayList<MoveType> rawRotates = board.getRotateMoves(x, y);
                ArrayList<MoveType> validRotates = board.validRotateMoves(x, y, rawRotates);
                for (MoveType m : validRotates) {
                    int delta = ((m.getRotate() % 4) + 4) % 4;
                    moves.add(new Move(Move.TYPE_ROTATE, x, y, delta, 0));
                }
            }
        }

        return moves;
    }

    private void applyMove(ChessBoard board, Player mover, Move m) {
        if (m.type == Move.TYPE_MOVE) {
            board.movePiece(m.startX, m.startY, m.a, m.b, mover);
        } else {
            board.movePiece(m.startX, m.startY, m.a, mover);
        }
        board.waterInterAction();
    }

    private Player findPlayerByColour(ChessBoard board, Colour colour) {
        for (Player p : board.getPlayers()) {
            if (p.getColor() == colour) return p;
        }
        return null;
    }

    private Colour otherColour(Colour c) {
        return (c == Colour.White) ? Colour.Black : Colour.White;
    }

    @Override
    public int choosePromotion(String[] pieceNames) {
        return 0; // bere první možnost (u Pawn je to Queen)
    }
}