package pieces;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

public class Airplane extends RotablePiece {

    public Airplane(String name, int x, int y, Colour colour, int rotation) {
        super(name, x,  y, colour,200,rotation);

addMove(new MoveType(1));
addMove(new MoveType(3));

        addRotatePiece((n,px, py, r,c) ->
                new Airplane( n,px, py,  c,r)

        );


        addPromotionPiece((px, py, pc) ->
                new Helicopter("Helicopter", px, py, pc)
        );


        addPromotionPiece((px, py, c) ->
                new Fighter("Fighter", px, py, c, (rotation + 2) % 4)

        );

                addMove(rotateMove(new MoveType(1,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(2,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-1,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-2,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));







    }
    @Override
    public String toString() {
        return "A  A";
    }





}


