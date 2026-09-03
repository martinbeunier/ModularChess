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
 * Bot, který skutečně "vidí dopředu" — pro každý svůj kandidátní tah zkusí
 * simulaci na KOPII desky (ChessBoard.clone()), předpoví soupeřovu nejlepší
 * odpověď, a teprve pak se rozhodne. Simulace nikdy nezasáhne skutečnou hru.
 *
 * SEARCH_DEPTH = 2 znamená: náš tah + soupeřova nejlepší odpověď na něj.
 * Vyšší hloubka = chytřejší, ale výrazně pomalejší (roste exponenciálně).
 */
public class MinimaxBot implements Bot {    // 3/10
    private static final int SEARCH_DEPTH = 2;
    private static final int HEAD_THREAT_PENALTY = 5000;

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

        for (Move m : rootMoves) {
            ChessBoard childBoard = realBoard.clone();
            Player childMover = findPlayerByColour(childBoard, myColour);
            applyMove(childBoard, childMover, m);

            int score = minimax(childBoard, otherColour(myColour), myColour, SEARCH_DEPTH - 1);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(m);
            } else if (score == bestScore) {
                bestMoves.add(m);
            }
        }

        Collections.shuffle(bestMoves, random);

        // Teprve TEĎ hrajeme SKUTEČNÝ tah, na SKUTEČNÉ hře (přes gameLoop, ne clone)
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
     * Rekurzivní minimax: na úrovni "myColour" hledáme MAXIMUM (náš nejlepší tah),
     * na úrovni soupeře hledáme MINIMUM (soupeř hraje nejlíp pro sebe, tedy
     * nejhůř pro nás).
     */
    private int minimax(ChessBoard board, Colour colourToMove, Colour myColour, int depth) {
        if (depth == 0 || board.countHeads() <= 1) {
            return evaluatePosition(board, myColour);
        }

        Player mover = findPlayerByColour(board, colourToMove);
        if (mover == null) return evaluatePosition(board, myColour);

        List<Move> moves = collectAllMoves(board, mover, colourToMove);
        if (moves.isEmpty()) {
            return evaluatePosition(board, myColour); // žádný tah -> bereme aktuální stav
        }

        boolean maximizing = (colourToMove == myColour);
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move m : moves) {
            ChessBoard childBoard = board.clone();
            Player childMover = findPlayerByColour(childBoard, colourToMove);
            applyMove(childBoard, childMover, m);

            int score = minimax(childBoard, otherColour(colourToMove), myColour, depth - 1);

            if (maximizing) {
                if (score > best) best = score;
            } else {
                if (score < best) best = score;
            }
        }

        return best;
    }

    /**
     * Statické ohodnocení pozice z pohledu "myColour":
     * součet hodnot vlastních figurek mínus soupeřových, plus penalizace/bonus
     * podle toho, čí Head je zrovna ohrožená.
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