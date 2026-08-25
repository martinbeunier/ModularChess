package pieces;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

import static pieces.RotablePiece.rotateMove;

public class Torpedo extends OrientedPiece {
    public Torpedo(String name, int x, int y, Colour colour, int rotation) {



        super(name, x, y, colour, 200,rotation);


        addPromotionPiece((px, py, pc) ->
                new LinebreakerRook("Linebraker Rook", px, py, pc)
        );
        addPromotionPiece((px, py, pc) ->
                new Queen("Queen", px, py, pc)
        );

        addMove(rotateMove(new MoveType(0,-1, MoveBehaviour.BOTH, MoveClass.TORPEDO),rotation));
        addMove(rotateMove(new MoveType(0,-2, MoveBehaviour.BOTH, MoveClass.TORPEDO),rotation));


    }
    @Override
    public String toString() {
        return "T  T";
    }
}
