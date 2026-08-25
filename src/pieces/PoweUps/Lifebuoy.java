package pieces.PoweUps;

import logic.PowerUpName;
import pieces.Piece;

public class Lifebuoy extends PowerUp {
    public Lifebuoy(String name, int x, int y) {

        super(name, x, y, PowerUpName.LIFEBUOY);

    }

    @Override
    public String toString() {
        return "----";
    }

}
