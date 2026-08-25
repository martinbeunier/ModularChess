package logic;

public class OcupationSquare {
    private int x;
    private int y;

    public OcupationSquare(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "OcupationSquare{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
