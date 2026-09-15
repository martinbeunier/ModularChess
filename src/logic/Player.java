package logic;
import java.util.HashSet;

public class Player {
    private String id;          // trvalý identifikátor (např. "player_local", "bot_easy")
    private String name;
    private Colour colour;
    private int elo;
    private String avatarPath;  // cesta k obrázku profilu, může být null
    private HashSet<PowerUpName> powerUps;

    private int wins;
    private int losses;
    private int draws;

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

        // Identitu i statistiky kopírujeme jako DATA (ne referenci) —
        // simulace na klonované desce nesmí ovlivnit reálné statistiky hráče.
        copy.id = this.id;
        copy.avatarPath = this.avatarPath;
        copy.wins = this.wins;
        copy.losses = this.losses;
        copy.draws = this.draws;

        return copy;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public Colour getColor() {
        return colour;
    }

    public void setColor(Colour colour) {
        this.colour = colour;
    }

    public void addPowerUp(PowerUpName powerUp){
        powerUps.add(powerUp);
    }

    public HashSet<PowerUpName> getPowerUps() {
        return powerUps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getDraws() { return draws; }

    public void recordWin() { wins++; }
    public void recordLoss() { losses++; }
    public void recordDraw() { draws++; }

    /**
     * Zkopíruje identitu a statistiky (id, jméno, elo, avatar, wins/losses/draws)
     * z trvalého "template" hráče (viz PlayerManager) do TOHOTO živého Player
     * objektu, který právě hraje danou barvu v aktuální hře. Barva se nemění.
     */
    public void copyIdentityFrom(Player template) {
        this.id = template.id;
        this.name = template.name;
        this.elo = template.elo;
        this.avatarPath = template.avatarPath;
        this.wins = template.wins;
        this.losses = template.losses;
        this.draws = template.draws;
    }
    public void setName(String name) {
        this.name = name;
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
        String idPart = (id != null) ? "id:" + id : "id:none";
        return "Player : " + " ;"
                + name + "; "
                + colour + " "
                + elo + " " + idPart
                + " wins:" + wins + " losses:" + losses + " draws:" + draws;
    }
}