package pieces;

import logic.Colour;
import logic.MoveBehaviour;
import logic.MoveClass;
import logic.MoveType;

public class Empress extends  Piece{
    public Empress(String name , int x , int y , Colour colour) {
        super(name, x, y, colour,1300);


        addMove(new MoveType(1,1, MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(1,0, MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(1,-1, MoveBehaviour.BOTH,MoveClass.REPEAT));

        addMove(new MoveType(0,1, MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(0,-1, MoveBehaviour.BOTH,MoveClass.REPEAT));

        addMove(new MoveType(-1,1, MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(-1,0, MoveBehaviour.BOTH,MoveClass.REPEAT));
        addMove(new MoveType(-1,-1, MoveBehaviour.BOTH,MoveClass.REPEAT));



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
        return "E  E";
    }

}
