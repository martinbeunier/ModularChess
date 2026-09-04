package pieces;
import logic.*;

public class King extends Head {
    private int rotation;

    public King(String name, int x, int y, Colour colour,int rotation) {
        super(name, x, y, colour ,100000);
        this.rotation = rotation;

        addMove(new MoveType(1,1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(0,1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(1,0, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,0, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(1,-1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(0,-1, MoveBehaviour.BOTH,MoveClass.LEAP));
        addMove(new MoveType(-1,-1, MoveBehaviour.BOTH,MoveClass.LEAP));

        switch (rotation) {
            case 0:
            case 2:
                addMove(new MoveType(2,0, MoveBehaviour.MOVE,MoveClass.CASTLE));
                addMove(new MoveType(-2,0, MoveBehaviour.MOVE,MoveClass.CASTLE));
                break;
            case 1:
            case 3:
                addMove(new MoveType(0,2, MoveBehaviour.MOVE,MoveClass.CASTLE));
                addMove(new MoveType(0,-2, MoveBehaviour.MOVE,MoveClass.CASTLE));
                break;
        }

    }
    @Override
    public String toString() {
        return "K  K";
    }
}