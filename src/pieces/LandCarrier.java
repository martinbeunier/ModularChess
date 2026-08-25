package pieces;

import logic.*;

public class LandCarrier extends Carrier {
    public LandCarrier(String name, int x, int y, Colour colour) {
        super(name, x,  y, colour,450);

        addMove(new MoveType(1,0, MoveBehaviour.MOVE, MoveClass.CARRIER));
        addMove(new MoveType(0,1, MoveBehaviour.MOVE, MoveClass.CARRIER));
        addMove(new MoveType(-1,0, MoveBehaviour.MOVE, MoveClass.CARRIER));
        addMove(new MoveType(0,-1, MoveBehaviour.MOVE, MoveClass.CARRIER));

        ocupationSquares.add(new OcupationSquare(1,1));
        ocupationSquares.add(new OcupationSquare(1,0));
        ocupationSquares.add(new OcupationSquare(1,-1));

        ocupationSquares.add(new OcupationSquare(0,1));
        ocupationSquares.add(new OcupationSquare(0,-1));

        ocupationSquares.add(new OcupationSquare(-1,1));
        ocupationSquares.add(new OcupationSquare(-1,0));
        ocupationSquares.add(new OcupationSquare(-1,-1));

    }

    @Override
    public String toString() {
        return "L  C";
    }
}
