package pieces;

import logic.Colour;

public class Blocade extends Piece {
    public Blocade(String name, int x, int y) {
        super(name, x,  y, Colour.Blockade);


}
    @Override
    public String toString() {
        return "----";
    }


}