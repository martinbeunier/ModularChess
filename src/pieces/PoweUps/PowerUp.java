package pieces.PoweUps;

import logic.*;


import pieces.Piece;

public class PowerUp extends Piece {
    public PowerUpName powerUpName;

    public PowerUp(String name, int x, int y,PowerUpName powerUpName) {
        super(name, x, y, Colour.Neutral);
        this.powerUpName = powerUpName;


    }
    public PowerUpName getPowerUpName() {
        return powerUpName;
    }

    @Override
    public String toString() {
        return "----";
    }


}
