
package pieces;
import logic.*;
        import pieces.Piece;


public class Bishop extends Piece {
    public Bishop(String name,int x, int y, Colour colour) {
        super(name, x,  y, colour,320);

        addMove(new MoveType(1,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(-1,1,MoveBehaviour.BOTH , MoveClass.REPEAT));
        addMove(new MoveType(1,-1,MoveBehaviour.BOTH , MoveClass.REPEAT));    }

    @Override
    public String toString() {
        return "B  B";
    }
}