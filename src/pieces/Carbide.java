package pieces;

import logic.Colour;

public class Carbide extends Piece{
    public Carbide(String name , int x , int y , Colour colour) {
        super(name, x, y, colour, 1300);
    }
    @Override
    public String toString() {
        return "B  D";
    }
}
