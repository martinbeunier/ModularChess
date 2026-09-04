package pieces;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

public class Emperor extends Head {
    private int rotation;

    public Emperor(String name, int x, int y, Colour colour) {
        super(name, x, y, colour ,100000);

        //king moves
        addMove(new MoveType(1,1, MoveBehaviour.BOTH, MoveClass.LEAP));
        addMove(new MoveType(0,1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(1,0, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,0, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(1,-1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(0,-1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,-1, MoveBehaviour.BOTH,MoveClass.LEAP));

        //knight moves
        addMove(new MoveType(2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));

    }
}
