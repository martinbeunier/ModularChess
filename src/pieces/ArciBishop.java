package pieces;
import logic.*;


public class ArciBishop extends Piece{
    public ArciBishop(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,700);

        //Bishop
        addMove(new MoveType(1,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(1,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));

        //Knight
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
        return "A  B";
    }
}

