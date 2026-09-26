package pieces;
import logic.*;

import static pieces.RotablePiece.rotateMove;

public class RaftCarrier extends OrientedCarrier {

    public RaftCarrier(String name, int x, int y, Colour colour, int rotation) {
        super(name, x, y, colour, 20, rotation);

        addMove(rotateMove(new MoveType(0, -1, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));
        addMove(rotateMove(new MoveType(-1, 0, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));
        addMove(rotateMove(new MoveType(1, 0, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));
        addMove(rotateMove(new MoveType(0, 1, MoveBehaviour.MOVE, MoveClass.CARRIER), rotation));

        ocupationSquares.add(rotatePoint(new OcupationSquare(0, -1), rotation));



    }

    @Override
    public String toString() {
        return "R  C";
    }
}