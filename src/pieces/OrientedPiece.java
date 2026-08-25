package pieces;

import logic.Colour;

public class OrientedPiece extends Piece {
    private int rotation;

    public OrientedPiece(String name, int x, int y, Colour colour, int value, int rotation) {
        super(name, x, y, colour, value);
        this.rotation = rotation;



    }

    public int getRotation() {
        return rotation;
    }
}
