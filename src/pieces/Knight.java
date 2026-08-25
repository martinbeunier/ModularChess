package pieces;
import logic.*;

public class Knight extends Piece {

    public Knight(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,300);


        addMove(new MoveType(2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));
    }

    @Override
    public String toString() {
        return "N  N";
    }
}
