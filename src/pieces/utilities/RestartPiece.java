package pieces.utilities;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;
import pieces.Piece;

public class RestartPiece extends Piece {

    public RestartPiece(String name, int x, int y, Colour colour) {
        super(name, x,  y, colour);


        addMove(new MoveType(1,0, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,0, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(0,1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(0,-1, MoveBehaviour.BOTH,MoveClass.LEAP));
    }
    @Override
    public String toString() {
        return "!RR!";
    }
}
