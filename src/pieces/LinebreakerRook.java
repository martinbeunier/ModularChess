package pieces;
import logic.*;
import pieces.Piece;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

public class LinebreakerRook extends Piece {

    public LinebreakerRook(String name, int x, int y, Colour colour) {
        super(name, x,  y, colour,600);

        addMove(new MoveType(1,0,MoveBehaviour.BOTH, MoveClass.REPEAT));
        addMove(new MoveType(-1,0,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,1,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,-1,MoveBehaviour.BOTH,MoveClass.REPEAT));

        addMove(new MoveType(1,0,MoveBehaviour.BOTH, MoveClass.LINEBREAKER));
        addMove(new MoveType(-1,0,MoveBehaviour.BOTH,MoveClass.LINEBREAKER));
        addMove(new MoveType(0,1,MoveBehaviour.BOTH,MoveClass.LINEBREAKER));
        addMove(new MoveType(0,-1,MoveBehaviour.BOTH,MoveClass.LINEBREAKER));

    }


    @Override
    public String toString() {
        return "L  R";

    }

}
