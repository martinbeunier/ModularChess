package logic;
import java.util.ArrayList;
import java.util.HashSet;

public class Player {
    private String name;
    private Colour colour;
    private int elo;
    private HashSet<PowerUpName> powerUps;

    public Player(String name, Colour colour, int elo) {
        this.name = name;
        this.colour = colour;
        this.elo = elo;
        this.powerUps = new HashSet<>();
    }

    @Override
    public Player clone() {
        Player copy = new Player(this.getName(), this.getColor(), this.getElo());

        for (PowerUpName p : this.getPowerUps()) {
            copy.addPowerUp(p);
        }

        return copy;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
    }

    public Colour getColor() {
        return colour;
    }
    public void addPowerUp(PowerUpName powerUp){
        powerUps.add(powerUp);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", colour=" + colour +
                ", elo=" + elo +
                ", powerUps=" + powerUps +
                '}';
    }


    public String myToString() {
        return "Player : " + " ;"
               + name + "; "
                 + colour + " "
             + elo ;


    }


    public HashSet<PowerUpName> getPowerUps() {
        return powerUps;
    }
}
