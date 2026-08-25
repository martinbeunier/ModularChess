package pieces.PoweUps;

import logic.PowerUpName;

public class OverClocker extends PowerUp {

    public OverClocker(String name, int x, int y) {

        super(name, x, y,PowerUpName.OVERCLOCKER);

    }

    @Override
    public String toString() {
        return "----";
    }
}
