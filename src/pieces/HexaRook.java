package pieces;
import logic.*;

public class HexaRook extends Piece {

    public HexaRook(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,800);


        addMove(new MoveType(2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-2,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,2,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,-2,MoveBehaviour.BOTH , MoveClass.LEAP));

        addMove(new MoveType(1,0,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,0,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(0,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(0,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));

    }

    @Override
    public String toString() {
        return "H  R";
    }
}
