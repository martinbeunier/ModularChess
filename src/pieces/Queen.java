
package pieces;
import logic.*;
import pieces.Piece;


public class Queen extends Piece {
    public Queen(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,900);

        //Ortogonal
        addMove(new MoveType(1,0,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(-1,0,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,1,MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,-1,MoveBehaviour.BOTH,MoveClass.REPEAT));

        //Diagonal
        addMove(new MoveType(1,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(1,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));




    }

    @Override
    public String toString() {
        return "Q  Q";
    }
}