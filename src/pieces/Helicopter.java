package pieces;
import logic.*;

public class Helicopter extends Piece {
    public Helicopter(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,400);


        addMove(new MoveType(2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));

        addMove(new MoveType(1,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
    }

    @Override
    public String toString() {
        return "H  H";
    }
}
