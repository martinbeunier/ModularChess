package pieces;
import logic.*;

import java.util.ArrayList;

//class of other figures


public class Carrier extends Piece {
    public ArrayList<OcupationSquare> ocupationSquares;
    public Carrier(String name, int x, int y, Colour colour, int value) {

        super(name, x,  y, colour,value);
        this.ocupationSquares = new ArrayList<OcupationSquare>() ;


    }

    public ArrayList<OcupationSquare> getOcupationSquares() {
        return ocupationSquares;
    }
}
