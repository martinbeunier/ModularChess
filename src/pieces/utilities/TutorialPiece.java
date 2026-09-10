package pieces.utilities;

import logic.Colour;
import pieces.Piece;

public class TutorialPiece extends Piece {
    public TutorialPiece(String name, int x, int y) {
        super(name, x,  y, Colour.Blockade);

    }
    @Override
    public String toString() {
        return "----";
    }

}