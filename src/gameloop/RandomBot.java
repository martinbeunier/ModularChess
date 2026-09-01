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
 * Náhodně vybere jednu z figurek a náhodně jednu z jejích platných možností
 * (pohyb i rotaci) — žádná strategie, čistá náhoda.
 */
public class RandomBot implements Bot {
    private final Random random = new Random();

    @Override
    public int[] chooseAndPlayMove(GameLoop gameLoop) {
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

            List<int[]> options = new ArrayList<>(); // {typ, a, b} typ: 0 = pohyb, 1 = rotace

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
                    if (moved) return new int[]{px, py, px, py};
                }
            }
        }

        return null;
    }

    @Override
    public int choosePromotion(String[] pieceNames) {
        return random.nextInt(pieceNames.length);
    }
}