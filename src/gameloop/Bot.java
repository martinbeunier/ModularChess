package gameloop;

import logic.ChessBoard;
import logic.Colour;
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

        // Zamícháme pořadí, ať bot netáhne pořád tou samou (první nalezenou) figurkou
        Collections.shuffle(myPiecePositions, random);

        for (int[] pos : myPiecePositions) {
            int px = pos[0];
            int py = pos[1];

            ArrayList<int[]> targets = board.getPossibleTargets(px, py, currentPlayer);
            if (targets.isEmpty()) continue;

            Collections.shuffle(targets, random);

            for (int[] target : targets) {
                boolean moved = gameLoop.tryMove(px, py, target[0], target[1]);
                if (moved) {
                    return new int[]{px, py, target[0], target[1]};
                }
                // Ve velmi vzácném edge-case by tah přesto neprošel — zkusíme další cíl
            }
        }

        return null; // bot nemá žádný platný tah
    }
}