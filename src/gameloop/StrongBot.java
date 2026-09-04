package gameloop;

import logic.ChessBoard;
import logic.Colour;
import logic.MoveType;
import logic.Player;
import pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Nejsilnější bot v sadě. Kombinuje:
 *  - Iterative deepening s časovým limitem (adaptivní hloubka prohledávání)
 *  - Řazení tahů (lepší alpha-beta prořezávání)
 *  - Quiescence search (řeší horizon efekt u rozjetých výměn)
 *  - Transpoziční tabulku (nepočítá stejnou pozici dvakrát)
 *
 * Evaluace: materiál + ohrožení Head (obou stran) + mobilita.
 */
public class StrongBot implements Bot { //6 /10 trapper
    private static final int MAX_DEPTH = 8;                 // bezpečnostní strop, obvykle se nedosáhne
    private static final long TIME_BUDGET_MILLIS = 1500;    // kolik smí bot maximálně "přemýšlet"
    private static final int QUIESCENCE_MAX_DEPTH = 4;
    private static final int HEAD_THREAT_PENALTY = 5000;
    private static final int MOBILITY_WEIGHT = 2;

    private final Random random = new Random();
    private final Map<String, Integer> transpositionTable = new HashMap<>();
    private long searchDeadline;

    private static class Move {
        static final int TYPE_MOVE = 0;
        static final int TYPE_ROTATE = 1;

        final int type, startX, startY, a, b;
        int orderScore; // jen pro řazení, NENÍ to finální ohodnocení pozice

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

        transpositionTable.clear(); // nový tah = nová tabulka, ať paměť neroste přes celou partii
        searchDeadline = System.currentTimeMillis() + TIME_BUDGET_MILLIS;

        List<Move> rootMoves = collectAllMoves(realBoard, currentPlayer, myColour);
        if (rootMoves.isEmpty()) return null;

        Move bestOverall = null;

        // Iterative deepening — postupně zkoušíme hlubší a hlubší prohledávání.
        // Výsledek z předchozí (mělčí) iterace se použije k seřazení tahů
        // v té další, což zlepší prořezávání.
        for (int depth = 1; depth <= MAX_DEPTH; depth++) {
            if (System.currentTimeMillis() >= searchDeadline) break;

            orderMoves(rootMoves, realBoard);

            Move iterationBest = null;
            int iterationBestScore = Integer.MIN_VALUE;
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            boolean timedOut = false;

            for (Move m : rootMoves) {
                if (System.currentTimeMillis() >= searchDeadline) {
                    timedOut = true;
                    break;
                }

                ChessBoard childBoard = realBoard.clone();
                Player childMover = findPlayerByColour(childBoard, myColour);
                applyMove(childBoard, childMover, m);

                int score = alphaBeta(childBoard, otherColour(myColour), myColour, depth - 1, alpha, beta);

                if (score > iterationBestScore) {
                    iterationBestScore = score;
                    iterationBest = m;
                }
                if (score > alpha) alpha = score;
            }

            // Nedokončenou iteraci zahazujeme — mohla by být zkreslená
            // (některé tahy se nestihly ani vyhodnotit).
            if (!timedOut && iterationBest != null) {
                bestOverall = iterationBest;
            }
        }

        if (bestOverall == null) {
            bestOverall = rootMoves.get(random.nextInt(rootMoves.size())); // krajní pojistka
        }

        boolean moved;
        if (bestOverall.type == Move.TYPE_MOVE) {
            moved = gameLoop.tryMove(bestOverall.startX, bestOverall.startY, bestOverall.a, bestOverall.b);
            if (moved) return new int[]{bestOverall.startX, bestOverall.startY, bestOverall.a, bestOverall.b};
        } else {
            moved = gameLoop.tryMove(bestOverall.startX, bestOverall.startY, bestOverall.a);
            if (moved) return new int[]{bestOverall.startX, bestOverall.startY, bestOverall.startX, bestOverall.startY};
        }

        return null;
    }

    private int alphaBeta(ChessBoard board, Colour colourToMove, Colour myColour, int depth, int alpha, int beta) {
        if (System.currentTimeMillis() >= searchDeadline) {
            return evaluatePosition(board, myColour); // čas vypršel uprostřed větve -> vrátíme aktuální odhad
        }

        if (board.countHeads() <= 1) {
            return evaluatePosition(board, myColour);
        }

        if (depth == 0) {
            return quiescence(board, colourToMove, myColour, alpha, beta, QUIESCENCE_MAX_DEPTH);
        }

        String signature = board.getPositionSignature(colourToMove) + "|d" + depth;
        Integer cached = transpositionTable.get(signature);
        if (cached != null) return cached;

        Player mover = findPlayerByColour(board, colourToMove);
        if (mover == null) return evaluatePosition(board, myColour);

        List<Move> moves = collectAllMoves(board, mover, colourToMove);
        if (moves.isEmpty()) return evaluatePosition(board, myColour);

        orderMoves(moves, board);

        boolean maximizing = (colourToMove == myColour);
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move m : moves) {
            ChessBoard childBoard = board.clone();
            Player childMover = findPlayerByColour(childBoard, colourToMove);
            applyMove(childBoard, childMover, m);

            int score = alphaBeta(childBoard, otherColour(colourToMove), myColour, depth - 1, alpha, beta);

            if (maximizing) {
                if (score > best) best = score;
                if (best > alpha) alpha = best;
            } else {
                if (score < best) best = score;
                if (best < beta) beta = best;
            }

            if (alpha >= beta) break; // prořezání
        }

        transpositionTable.put(signature, best);
        return best;
    }

    /**
     * Po vyčerpání hlavní hloubky pokračuje POUZE v braních, dokud se pozice
     * "neusadí" — řeší horizon efekt (bot by jinak mohl vidět rozjetou výměnu
     * figurek uprostřed a vyvodit z toho špatný závěr).
     */
    private int quiescence(ChessBoard board, Colour colourToMove, Colour myColour, int alpha, int beta, int depth) {
        int standPat = evaluatePosition(board, myColour);

        if (depth == 0 || System.currentTimeMillis() >= searchDeadline) {
            return standPat;
        }

        boolean maximizing = (colourToMove == myColour);

        if (maximizing) {
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat < beta) beta = standPat;
        }
        if (alpha >= beta) return standPat;

        Player mover = findPlayerByColour(board, colourToMove);
        if (mover == null) return standPat;

        List<Move> captureMoves = collectCaptureMoves(board, mover, colourToMove);
        if (captureMoves.isEmpty()) return standPat;

        orderMoves(captureMoves, board);

        int best = standPat;

        for (Move m : captureMoves) {
            ChessBoard childBoard = board.clone();
            Player childMover = findPlayerByColour(childBoard, colourToMove);
            applyMove(childBoard, childMover, m);

            int score = quiescence(childBoard, otherColour(colourToMove), myColour, alpha, beta, depth - 1);

            if (maximizing) {
                if (score > best) best = score;
                if (best > alpha) alpha = best;
            } else {
                if (score < best) best = score;
                if (best < beta) beta = best;
            }

            if (alpha >= beta) break;
        }

        return best;
    }

    /** Jen tahy, kde cílové pole je obsazené (skutečné braní) — pro quiescence search. */
    private List<Move> collectCaptureMoves(ChessBoard board, Player player, Colour colour) {
        List<Move> moves = new ArrayList<>();
        int cols = board.getWidth();
        int rows = board.getHeight();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece p = board.getPiece(x, y);
                if (p == null || p.getColour() != colour) continue;

                for (int[] target : board.getPossibleTargets(x, y, player)) {
                    if (board.getPiece(target[0], target[1]) != null) {
                        moves.add(new Move(Move.TYPE_MOVE, x, y, target[0], target[1]));
                    }
                }
            }
        }
        return moves;
    }

    /** Seřadí tahy od nejslibnějších (braní nejcennějších figurek první) — zlepší prořezávání. */
    private void orderMoves(List<Move> moves, ChessBoard board) {
        for (Move m : moves) {
            if (m.type == Move.TYPE_MOVE) {
                Piece target = board.getPiece(m.a, m.b);
                m.orderScore = (target != null) ? target.getValue() : 0;
            } else {
                m.orderScore = 0;
            }
        }
        moves.sort((a, b) -> b.orderScore - a.orderScore);
    }

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