
package profile;

import logic.Player;

import java.io.*;
        import java.util.*;

/**
 * Jediné místo, kde žijí TRVALÉ identity hráčů (lidský hráč i všichni boti),
 * uložené v jednom souboru. Boti mají pevné, neměnné id (např. "bot_easy"),
 * takže se jejich elo/statistiky kumulují napříč hrami stejně jako u člověka.
 */
public class PlayerManager {
    private static final String PLAYERS_PATH = "src\\files\\players\\players.txt";
    private static Map<String, Player> players = new HashMap<>();
    private static Player currentHumanPlayer = null;
    private static boolean loaded = false;
    private static String currentHumanPlayerId = null;






    // Jednoduchý formát nezávislý na Colour (trvalá identita barvu nemá — ta se
    // přiděluje až za běhu hry). id;name;elo;avatar;wins;losses;draws
    private static String formatStoredPlayer(Player p) {
        return p.getId() + ";" + p.getName() + ";" + p.getElo() + ";"
                + (p.getAvatarPath() == null ? "none" : p.getAvatarPath()) + ";"
                + p.getWins() + ";" + p.getLosses() + ";" + p.getDraws();
    }

    private static Player parseStoredPlayer(String line) {
        String[] parts = line.split(";");
        if (parts.length < 7) return null;

        String id = parts[0].trim();
        String name = parts[1].trim();
        int elo = Integer.parseInt(parts[2].trim());
        String avatarRaw = parts[3].trim();



        String avatar = "";
                if(avatarRaw.equals("none")){
                    avatar = "src/files/images/avatars/defaultAvatar.png";
                    System.out.println("hracuv defaultni avatar "+avatarRaw);
                }else{
                    avatar = avatarRaw;
                    System.out.println("hracuv avatar "+avatarRaw);
                }




        int wins = Integer.parseInt(parts[4].trim());
        int losses = Integer.parseInt(parts[5].trim());
        int draws = Integer.parseInt(parts[6].trim());


        Player p = new Player(name, null, elo); // barva se přiřadí až ve hře
        p.setId(id);
        p.setAvatarPath(avatar);
        for (int i = 0; i < wins; i++) p.recordWin();
        for (int i = 0; i < losses; i++) p.recordLoss();
        for (int i = 0; i < draws; i++) p.recordDraw();
        return p;
    }

    /** Vrátí existujícího trvalého hráče podle id, nebo ho vytvoří s danými výchozími hodnotami. */
    public static Player getOrCreate(String id, String name, int elo, String avatarPath) {
        ensureLoaded();

        Player existing = players.get(id);
        if (existing == null) {
            Player p = new Player(name, null, elo);
            p.setId(id);
            p.setAvatarPath(avatarPath);
            players.put(id, p);

            return p;


        }

        // Hráč (typicky bot) už existuje ze souboru — jméno/avatar bere kód jako
        // zdroj pravdy, takže je dosynchronizujeme, i když už záznam existoval.
        // Elo a statistiky (wins/losses/draws) NEPŘEPISUJEME — ty patří disku.
        if (existing.getAvatarPath() == null && avatarPath != null) {
            existing.setAvatarPath(avatarPath);
        }
        if (existing.getName() == null || existing.getName().isBlank()) {
            // jen pojistka pro poškozený/starý záznam
        }

        return existing;
    }

    public static Player getById(String id) {
        ensureLoaded();
        return players.get(id);
    }

    public static void setCurrentHumanPlayer(Player p) {
        ensureLoaded();
        currentHumanPlayer = p;
        currentHumanPlayerId = p.getId();
        players.put(p.getId(), p);
    }

    public static Player getCurrentHumanPlayer() {
        ensureLoaded();
        if (currentHumanPlayer == null) {
            currentHumanPlayer = getOrCreate("player_local", "Hráč", 1000, null);
        }
        return currentHumanPlayer;
    }


    /** Explicitně nahraje hráče ze souboru. Bezpečné zavolat víckrát — druhé a další volání nic neudělají. */
    public static void load() {
        if (loaded) return;
        loaded = true;
        loadFromDisk();
       // ensureDefaultBots(); // pokud máš i tohle, jinak vynech
    }
    private static void loadFromDisk() {
        File file = new File(PLAYERS_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("current:")) {
                    currentHumanPlayerId = line.substring("current:".length()).trim();
                    continue;
                }

                Player p = parseStoredPlayer(line);
                if (p != null) players.put(p.getId(), p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Pokud soubor obsahoval "current:", a odpovídající hráč se skutečně
        // načetl, nastavíme ho hned jako aktuálního — Main pak jen zavolá
        // getCurrentHumanPlayer() a dostane přesně tohohle hráče.
        if (currentHumanPlayerId != null) {
            Player fromFile = players.get(currentHumanPlayerId);
            if (fromFile != null) {
                currentHumanPlayer = fromFile;
            }
        }
    }
    private static void ensureLoaded() {
        load(); // interní metody si samy zajistí, že je manager nahraný, i kdyby na to Main zapomněl
    }

    public static void saveAll() {
        try {
            File file = new File(PLAYERS_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file, false)) {
                if (currentHumanPlayer != null) {
                    fw.write("current:" + currentHumanPlayer.getId() + "\n");
                }
                for (Player p : players.values()) {
                    fw.write(formatStoredPlayer(p) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}