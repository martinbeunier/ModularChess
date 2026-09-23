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
    // Registr map (název + požadavky na odemčení) — sdílené napříč panely
    // ------------------------------------------------------------------

    private static final String MAPS_PATH = "src/files/positions";
    private static final String MAP_EXTENSION = ".chess";

    private static final Map<String, Integer> UNLOCK_REQUIREMENTS = new HashMap<>();
    static {
        UNLOCK_REQUIREMENTS.put("tutorial - Kill all kings as white", 0);
        UNLOCK_REQUIREMENTS.put("standard", 0);
        UNLOCK_REQUIREMENTS.put("fighter defense", 3);
        UNLOCK_REQUIREMENTS.put("XXL chess (rip of)", 6);
    }

    private static List<String> allMapNamesCache = null;

    /** Vrátí názvy všech map nalezených ve složce s pozicemi (bez přípony .chess). */
    public static List<String> getAllMapNames() {
        if (allMapNamesCache != null) {
            return allMapNamesCache;
        }

        List<String> names = new ArrayList<>();

        File dir = new File(MAPS_PATH);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(MAP_EXTENSION));

        if (files != null) {
            Arrays.sort(files);
            for (File file : files) {
                String fileName = file.getName();
                int dotIndex = fileName.lastIndexOf('.');
                String mapName = (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
                names.add(mapName);
            }
        }

        allMapNamesCache = names;
        return names;
    }

    /** Kolik bodů je potřeba pro odemčení dané mapy (0, pokud není nastaveno jinak). */
    public static int getRequiredPointsForMap(String mapName) {
        return UNLOCK_REQUIREMENTS.getOrDefault(mapName, 0);
    }

    /** Je daná mapa odemčená? Počítá se ze všech aktuálně existujících map. */
    public static boolean isMapUnlocked(String mapName) {
        if (cheatcodeActivated) return true;
        int required = getRequiredPointsForMap(mapName);
        return getTotalPoints() >= required;
    }

    /** Celkový součet bodů za všechny mapy, které existují a byly dohrány s výhrou. */
    public static int getTotalPoints() {
        return getTotalPoints(getAllMapNames());
    }

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

    public static void recordMapPlayed(String mapName, String opponent, String result, String historyFileName) {
        try {
            File file = new File(MAP_HISTORY_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file, true)) {
                String fileField = (historyFileName == null) ? "" : historyFileName;
                String line = LocalDateTime.now() + ";" + mapName + ";" + opponent + ";" + result + ";" + fileField;
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
    public static class HistoryEntry {
        public final String timestamp;
        public final String mapName;
        public final String opponent;
        public final String result;
        public final String historyFileName; // null u starších záznamů bez 5. sloupce

        public HistoryEntry(String timestamp, String mapName, String opponent, String result, String historyFileName) {
            this.timestamp = timestamp;
            this.mapName = mapName;
            this.opponent = opponent;
            this.result = result;
            this.historyFileName = historyFileName;
        }
    }

    /** Vrátí celou historii her jako rozparsované záznamy. */
    public static List<HistoryEntry> getMapHistoryEntries() {
        List<HistoryEntry> entries = new ArrayList<>();
        for (String line : getMapHistory()) {
            String[] parts = line.split(";", -1);
            if (parts.length < 4) continue;

            String fileName = (parts.length >= 5 && !parts[4].isBlank()) ? parts[4].trim() : null;
            entries.add(new HistoryEntry(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), fileName
            ));
        }
        return entries;
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

    // ------------------------------------------------------------------
    // Progression / body za dohrané mapy
    // ------------------------------------------------------------------

    /** Body za poražení daného oponenta (podle jména bota v záznamu). */
    private static int pointsForOpponent(String opponentName) {
        if (opponentName == null) return 0;
        String o = opponentName.toLowerCase();

        if (o.contains("trapper")) return 3;
        if (o.contains("greedy"))  return 2;
        if (o.contains("bold"))    return 1;

        return 0; // "Against yourself" a neznámí oponenti body nedávají
    }

    /**
     * Nejvyšší počet bodů, které hráč na dané mapě získal
     * (podle nejtěžšího poraženého bota). Max 3 body na mapu.
     */
    public static int getMapPoints(String mapName) {
        ensureCompletedMapsLoaded();

        int best = 0;
        for (String entry : completedMapEntries) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2 || !parts[0].equals(mapName)) continue;

            best = Math.max(best, pointsForOpponent(parts[1]));
        }

        return Math.min(best, 3);
    }

    /** Sečte body za všechny zadané mapy (viz getMapPoints). */
    public static int getTotalPoints(java.util.Collection<String> mapNames) {
        int total = 0;
        for (String mapName : mapNames) total += getMapPoints(mapName);
        return total;
    }
}