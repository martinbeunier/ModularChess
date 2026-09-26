package pieces;
import logic.*;

import static pieces.RotablePiece.rotateMove;

public class ShipCarrier extends OrientedCarrier {

    public ShipCarrier(String name, int x, int y, Colour colour, int rotation) {
        super(name, x, y, colour, 290, rotation);

        addMove(rotateMove(new MoveType(0, -1, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));
        addMove(rotateMove(new MoveType(-1, 0, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));
        addMove(rotateMove(new MoveType(1, 0, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));
        addMove(rotateMove(new MoveType(0, 1, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));

        ocupationSquares.add(rotatePoint(new OcupationSquare(0, -2), rotation));

        ocupationSquares.add(rotatePoint(new OcupationSquare(1, -1), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(0, -1), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(-1, -1), rotation));

        ocupationSquares.add(rotatePoint(new OcupationSquare(1, 0), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(-1, 0), rotation));

        ocupationSquares.add(rotatePoint(new OcupationSquare(1, 1), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(0, 1), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(-1, 1), rotation));

        ocupationSquares.add(rotatePoint(new OcupationSquare(1, 2), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(0, 2), rotation));
        ocupationSquares.add(rotatePoint(new OcupationSquare(-1, 2), rotation));

    }

    @Override
    public String toString() {
        return "S  C";
    }
}