package pieces;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

import static pieces.RotablePiece.rotateMove;

public class Wasp extends Piece{
    public Wasp(String name, int x, int y, Colour colour) {

        super(name, x, y, colour, 800);




        addMove(new MoveType(0,-1, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(0,-2, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(0,1, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(0,2, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(1,0, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(2,0, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(-1,0, MoveBehaviour.BOTH, MoveClass.TORPEDO));
        addMove(new MoveType(-2,0, MoveBehaviour.BOTH, MoveClass.TORPEDO));

        addMove(new MoveType(1,1,MoveBehaviour.BOTH, MoveClass.REPEAT));
        addMove(new MoveType(-1,-1,MoveBehaviour.BOTH, MoveClass.REPEAT));
        addMove(new MoveType(1,-1,MoveBehaviour.BOTH, MoveClass.REPEAT));
        addMove(new MoveType(-1,1,MoveBehaviour.BOTH, MoveClass.REPEAT));

    }

    @Override
    public String toString() {
        return "W  W";
    }
}