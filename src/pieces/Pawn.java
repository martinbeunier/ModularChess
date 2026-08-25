package pieces;
import logic.*;

import static pieces.RotablePiece.rotateMove;

public class Pawn extends OrientedPiece{


    public Pawn(String name, int x, int y, Colour colour, int rotation) {

        super(name, x,  y, colour,100 , rotation);

        addPromotionPiece((px, py, pc) ->
                new Queen("Queen", px, py, pc)
        );

        addPromotionPiece((px, py, pc) ->
                new Rook("Rook", px, py, pc)
        );

        addPromotionPiece((px, py, pc) ->
                new Knight("Knight", px, py, pc)
        );

        addPromotionPiece((px, py, pc) ->
                new Bishop("Bishop", px, py, pc)
        );
/*
        addPromotionPiece((px, py, c) ->
                new Pawn("Pawn", px, py, c, (rotation + 2) % 4)

        );*/







               addMove(rotateMove(new MoveType(0,-1,MoveBehaviour.MOVE,MoveClass.LEAP),rotation));
               addMove(rotateMove(new MoveType(-1,-1,MoveBehaviour.TAKE, MoveClass.LEAP),rotation));
               addMove(rotateMove(new MoveType(1,-1,MoveBehaviour.TAKE, MoveClass.LEAP),rotation));
               addMove(rotateMove(new MoveType(0,-2,true,MoveBehaviour.MOVE ,MoveClass.BIG),rotation));


    }
    @Override
    public String toString() {
        return "P  P";
    }


}
/*
switch(rotation) { case 0: addMove(new MoveType(-1,0,false,false,true)); addMove(new MoveType(-1,-1,false,true,false)); addMove(new MoveType(-1,1,false,true,false)); addMove(new MoveType(-2,0,true,false,false,true)); break; case 1: addMove(new MoveType(0,-1,false,false,true)); addMove(new MoveType(-1,-1,false,true,false)); addMove(new MoveType(1,-1,false,true,false)); addMove(new MoveType(0,-2,true,false,false,true)); break; case 2: addMove(new MoveType(1,0,false,false,true)); addMove(new MoveType(1,1,false,true,false)); addMove(new MoveType(1,-1,false,true,false)); addMove(new MoveType(2,0,true,false,false,true)); break; case 3: addMove(new MoveType(0,1,false,false,true)); addMove(new MoveType(-1,1,false,true,false)); addMove(new MoveType(1,1,false,true,false)); addMove(new MoveType(0, 2,true,false,false,true)); break; }
*/