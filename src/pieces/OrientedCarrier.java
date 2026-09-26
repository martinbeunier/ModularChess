package pieces;

import logic.Colour;
import logic.OcupationSquare;

public class OrientedCarrier extends Carrier{
    private int rotation;


    OrientedCarrier(String name, int x, int y, Colour colour, int value, int rotation){
        super(name,x,  y, colour,value );
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }
    public static OcupationSquare rotatePoint(OcupationSquare s, int rotation)
    {
        int x = s.getX();
        int y = s.getY();

        int rx = x;
        int ry = y;

        switch(rotation)
        {
            case 0: return s;
            case 1: rx = -y; ry = x; break;

            case 2: rx = -x; ry = -y; break;
            case 3: rx = y; ry = -x; break;
        }

        return new OcupationSquare(rx, ry);
    }
}
