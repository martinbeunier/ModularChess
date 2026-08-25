package pieces;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

public class Fighter extends RotablePiece {

    public Fighter(String name, int x, int y, Colour colour, int rotation) {
        super(name, x,  y, colour,800,rotation);

        addMove(new MoveType(1));
        addMove(new MoveType(2));
        addMove(new MoveType(3));

        addRotatePiece((n,px, py, r,c) ->
                new Fighter( n,px, py,  c,r)

        );






                //line 5
                addMove(rotateMove(new MoveType(0,-3, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));



                //line 4
                addMove(rotateMove(new MoveType(1,-2, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(0,-2, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-1,-2, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));

                //line 3
                addMove(rotateMove(new MoveType(1,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(0,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-1,-1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));

                //line 2
                addMove(rotateMove(new MoveType(2,0, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(1,0, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-1,0, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-2,0, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));

                //line 1
                addMove(rotateMove(new MoveType(1,1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));
                addMove(rotateMove(new MoveType(-1,1, MoveBehaviour.BOTH, MoveClass.LEAP),rotation));








    }

    @Override
    public String toString() {
        return "F  F";
    }





}
