package pieces;
import logic.*;
import pieces.Piece;


public class Rook extends Piece {
    public Rook(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,500);

        addMove(new MoveType(1,0,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(-1,0,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,1,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,-1,MoveBehaviour.BOTH,MoveClass.REPEAT));


    }

    @Override
    public String toString() {
        return "R  R";
    }
}