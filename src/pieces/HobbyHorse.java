package pieces;
import logic.*;

public class HobbyHorse extends Piece {

    public  HobbyHorse(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,300);


        addMove(new MoveType(3,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(3,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-3,1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-3,-1,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,3,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(1,-3,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,3,MoveBehaviour.BOTH , MoveClass.LEAP));
        addMove(new MoveType(-1,-3,MoveBehaviour.BOTH , MoveClass.LEAP));
    }

    @Override
    public String toString() {
        return "H  H";
    }
}
