package profile;

import logic.Player;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Jediný trvalý profil hráče v celé hře. Žádné boty, žádné multi-účty —
 * jen jeden profil, kterému se dá měnit jméno a který si pamatuje elo,
 * statistiky, historii odehraných map a mapy dokončené (vyhrané) proti
 * konkrétním oponentům.
 */
public class PlayerManager {

    private static final String PROFILE_PATH = "src\\files\\players\\profile.txt";
    private static final String MAP_HISTORY_PATH = "src\\files\\players\\map_history.txt";
    private static final String COMPLETED_MAPS_PATH = "src\\files\\players\\completed_maps.txt";
    private static String DEFAULT_AVATAR = "src/files/images/avatars/defaultAvatar.png";

    public static boolean cheatcodeActivated = false;

    private static Player profile = null;

    // ------------------------------------------------------------------
    // Načtení / vytvoření profilu
    // ------------------------------------------------------------------

    private static void ensureLoaded() {
        if (profile != null) return;

        File file = new File(PROFILE_PATH);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                if (line != null && !line.isBlank()) {
                    String[] parts = line.split(";");
                    if (parts.length >= 6) {
                        String name = parts[0].trim();
                        int elo = Integer.parseInt(parts[1].trim());
                        String avatarRaw = parts[2].trim();
                        String avatar = avatarRaw.equals("none") ? DEFAULT_AVATAR : avatarRaw;
                        int wins = Integer.parseInt(parts[3].trim());
                        int losses = Integer.parseInt(parts[4].trim());
                        int draws = Integer.parseInt(parts[5].trim());

                        profile = new Player(name, null, elo);
                        profile.setId("player");
                        profile.setAvatarPath(avatar);
                        for (int i = 0; i < wins; i++) profile.recordWin();
                        for (int i = 0; i < losses; i++) profile.recordLoss();
                        for (int i = 0; i < draws; i++) profile.recordDraw();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (profile == null) {
            profile = new Player("Hráč", null, 1000);
            profile.setId("player");
            profile.setAvatarPath(DEFAULT_AVATAR);
        }
    }

    public static Player getCurrentHumanPlayer() {
        ensureLoaded();
        return profile;
    }

    public static void setName(String newName) {
        ensureLoaded();
        profile.setName(newName);
        save();
    }
    public static void setAvatarPath(String avatarPath) {
        ensureLoaded();
        profile.setAvatarPath(avatarPath);
        save();
    }
    // ------------------------------------------------------------------
    // Uložení profilu
    // ------------------------------------------------------------------

    public static void save() {
        ensureLoaded();
        try {
            File file = new File(PROFILE_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file, false)) {
                String avatar = (profile.getAvatarPath() == null) ? "none" : profile.getAvatarPath();
                String line = profile.getName() + ";" + profile.getElo() + ";" + avatar + ";"
                        + profile.getWins() + ";" + profile.getLosses() + ";" + profile.getDraws();
                fw.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        save();
    }

    public static Player getById(String id) {
        ensureLoaded();
        if ("player".equals(id)) return profile;
        return null;
    }

    // ------------------------------------------------------------------
    // Historie odehraných map (log každé hry)
    // Formát řádku: timestamp;mapName;opponent;result
    // ------------------------------------------------------------------

    public static void recordMapPlayed(String mapName, String opponent, String result) {
        try {
            File file = new File(MAP_HISTORY_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file, true)) {
                String line = LocalDateTime.now() + ";" + mapName + ";" + opponent + ";" + result;
                fw.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMapHistory() {
        List<String> history = new ArrayList<>();
        File file = new File(MAP_HISTORY_PATH);
        if (!file.exists()) return history;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) history.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    // ------------------------------------------------------------------
    // Splněné (vyhrané) mapy — pro odemykání progresu
    // Formát řádku: mapName;opponent
    // ------------------------------------------------------------------

    private static Set<String> completedMapEntries = null; // "mapName;opponent"

    private static void ensureCompletedMapsLoaded() {
        if (completedMapEntries != null) return;
        completedMapEntries = new LinkedHashSet<>();

        File file = new File(COMPLETED_MAPS_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) completedMapEntries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zapíše mapu+oponenta jako splněné (pokud tam ještě není) a hned uloží.
     * Pokud je aktivní cheatcode, nic se nezapíše (žádné odemykání) a vrátí se false.
     * Vrací true, pokud jde o nové (skutečné) odemčení.
     */
    public static boolean markMapCompleted(String mapName, String opponent) {
        if (cheatcodeActivated) {
            return false;
        }

        ensureCompletedMapsLoaded();

        String entry = mapName + ";" + opponent;
        if (completedMapEntries.contains(entry)) {
            return false; // tuhle kombinaci mapa+oponent už má odemčenou
        }

        completedMapEntries.add(entry);
        saveCompletedMaps();
        return true;
    }

    private static void saveCompletedMaps() {
        try {
            File file = new File(COMPLETED_MAPS_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file, false)) {
                for (String e : completedMapEntries) {
                    fw.write(e + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Vrátí, zda hráč danou mapu už dohrál s výhrou, s libovolným oponentem. */
    public static boolean isMapCompleted(String mapName) {
        ensureCompletedMapsLoaded();
        for (String entry : completedMapEntries) {
            if (entry.startsWith(mapName + ";")) return true;
        }
        return false;
    }

    /** Vrátí, zda hráč porazil konkrétního oponenta na konkrétní mapě. */
    public static boolean isMapCompletedAgainst(String mapName, String opponent) {
        ensureCompletedMapsLoaded();
        return completedMapEntries.contains(mapName + ";" + opponent);
    }

    /** Vrátí všechny oponenty, které hráč na dané mapě porazil. */
    public static Set<String> getDefeatedOpponentsOnMap(String mapName) {
        ensureCompletedMapsLoaded();
        Set<String> opponents = new LinkedHashSet<>();
        for (String entry : completedMapEntries) {
            String[] parts = entry.split(";", 2);
            if (parts.length == 2 && parts[0].equals(mapName)) {
                opponents.add(parts[1]);
            }
        }
        return opponents;
    }

    /** Vrátí všechny unikátní názvy map, na kterých hráč aspoň jednou vyhrál. */
    public static Set<String> getCompletedMaps() {
        ensureCompletedMapsLoaded();
        Set<String> maps = new LinkedHashSet<>();
        for (String entry : completedMapEntries) {
            String[] parts = entry.split(";", 2);
            if (parts.length == 2) maps.add(parts[0]);
        }
        return maps;
    }
}