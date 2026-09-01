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
 * Jednoduchý bot — z figurek, které aktuálně mohou táhnout, náhodně vybere
 * jednu z nich a náhodně jeden z jejích platných cílů (ChessBoard.getPossibleTargets).
 * Pracuje výhradně přes GameLoop.tryMove(...), takže respektuje úplně stejná
 * pravidla (validace, promotion, water, atd.) jako lidský hráč — žádná duplicitní logika.
 *
 * Žádná závislost na Swingu/GUI — bezpečné volat z libovolného vlákna.
 */
public class Bot {
    private final Random random = new Random();

    /**
     * Zkusí najít a provést jeden platný tah pro AKTUÁLNÍHO hráče v gameLoop.
     *
     * @return {fromX, fromY, toX, toY} provedeného tahu, nebo null, pokud bot
     *         nemá žádný platný tah (mat/pat/uvíznutí).
     */
    public int[] playRandomMove(GameLoop gameLoop) {
        ChessBoard board = gameLoop.getChessBoard();
        Player currentPlayer = gameLoop.getCurrentPlayer();
        Colour myColour = currentPlayer.getColor();

        int cols = board.getWidth();
        int rows = board.getHeight();

        List<int[]> myPiecePositions = new ArrayList<>();
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece p = board.getPiece(x, y);
                if (p != null && p.getColour() == myColour) {
                    myPiecePositions.add(new int[]{x, y});
                }
            }
        }

        Collections.shuffle(myPiecePositions, random);

        for (int[] pos : myPiecePositions) {
            int px = pos[0];
            int py = pos[1];

            // Posbíráme VŠECHNY možnosti téhle figurky — klasické tahy i rotace — dohromady
            List<int[]> options = new ArrayList<>(); // {typ, a, b}  typ: 0 = pohyb (a,b = cílové x,y), 1 = rotace (a = delta)

            for (int[] target : board.getPossibleTargets(px, py, currentPlayer)) {
                options.add(new int[]{0, target[0], target[1]});
            }

            ArrayList<MoveType> rawRotates = board.getRotateMoves(px, py);
            ArrayList<MoveType> validRotates = board.validRotateMoves(px, py, rawRotates);
            for (MoveType m : validRotates) {
                int delta = ((m.getRotate() % 4) + 4) % 4;
                options.add(new int[]{1, delta, 0});
            }

            if (options.isEmpty()) continue;

            Collections.shuffle(options, random);

            for (int[] option : options) {
                boolean moved;
                if (option[0] == 0) {
                    moved = gameLoop.tryMove(px, py, option[1], option[2]);
                    if (moved) return new int[]{px, py, option[1], option[2]};
                } else {
                    moved = gameLoop.tryMove(px, py, option[1]);
                    if (moved) return new int[]{px, py, px, py}; // rotace = "from" i "to" stejné pole
                }
            }
        }

        return null;
    }
    /**
     * Náhodně vybere jednu z dostupných promoční figurek.
     * @param pieceNames jména dostupných možností (viz PromotionChooser)
     * @return náhodný index v poli pieceNames
     */
    public int chooseRandomPromotion(String[] pieceNames) {
        return random.nextInt(pieceNames.length);
    }
}