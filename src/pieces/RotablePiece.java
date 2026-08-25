package pieces;
import logic.*;

import java.util.ArrayList;



//class of other figures
public class RotablePiece extends OrientedPiece{

    public ArrayList<RotateFactory> rotatePieces;


    public RotablePiece(String name, int x, int y, Colour colour, int value, int rotation) {

        super(name, x,  y, colour,value,rotation);
        this.rotatePieces = new ArrayList<>();


    }

    public static MoveType rotateMove(MoveType m, int rotation)
    {
        int x = m.getX();
        int y = m.getY();

        int rx = x;
        int ry = y;

        switch(rotation)
        {
            case 0: return m;
            case 1: rx = -y; ry = x; break;

            case 2: rx = -x; ry = -y; break;
            case 3: rx = y; ry = -x; break;
        }

        return new MoveType(rx, ry,m.getRequiresFirstMove() ,m.getBehaviour(), m.getMoveClass());
    }




    public void addRotatePiece(RotateFactory factory)
    {
        rotatePieces.add(factory);
    }

    public ArrayList<RotateFactory> getRotatePieces() {
        return rotatePieces;
    }


}
