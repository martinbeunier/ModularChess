package gui;

import java.awt.*;
import java.io.*;
import java.util.Properties;

public class UIconfiguration {
    private static UIconfiguration instance;

    public static Color boardColorDark;
    public static Color boardColorLight;
    public static Color waterColorDark;
    public static Color waterColorLight;

    public static final String THREAT_SOUND_PATH = "src/files/sounds/threat_warning2.wav";
    public static final String MOVE_SOUND_PATH = "src/files/sounds/move.wav";

    private static final String SETTINGS_FILE = "src/files/settings.properties";

    // už NEjsou final - potřebujeme je moct za běhu měnit a ukládat
    public static int windowMode ;
    public static float soundEfectsVolume ; // 0.0 - 1.0
    public static int boardColours ;
    public static int waterColours ;

    private UIconfiguration() {
        loadSettings(); // při prvním vytvoření rovnou zkusíme načíst soubor
        applyBoardColours();
        applyWaterColours();
    }

    public static UIconfiguration getInstance() {
        if (instance == null) {
            instance = new UIconfiguration();
        }
        return instance;
    }

    // --- převod indexu na skutečné barvy ---

    private static void applyBoardColours() {
        switch (boardColours) {
            case 0: //Dark

                boardColorDark = new Color(78, 78, 78);
                boardColorLight = new Color(203, 203, 203);
                break;
            case 1: // chess.com /Green
            default:
                boardColorDark = new Color(118, 150, 86);
                boardColorLight = new Color(238, 238, 210);

                break;
        }
    }

    private static void applyWaterColours() {
        switch (waterColours) {
            case 0: // classic
            default:
                waterColorDark = new Color(95, 150, 221);
                waterColorLight = new Color(204, 217, 255);
                break;

        }
    }

    // --- ukládání / načítání ---

    public static void saveSettings() {
        Properties props = new Properties();
        props.setProperty("windowMode", String.valueOf(windowMode));
        props.setProperty("soundEfectsVolume", String.valueOf(soundEfectsVolume));
        props.setProperty("boardColours", String.valueOf(boardColours));
        props.setProperty("waterColours", String.valueOf(waterColours));

        try (OutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Game settings");
        } catch (IOException e) {
            System.err.println("Nepodařilo se uložit nastavení: " + e.getMessage());
        }
    }

    public static void loadSettings() {
        File file = new File(SETTINGS_FILE);
        System.out.println("DEBUG cesta k souboru: " + file.getAbsolutePath());
        System.out.println("DEBUG soubor existuje: " + file.exists());

        if (!file.exists()) {
            // soubor ještě neexistuje -> necháme výchozí hodnoty a rovnou je uložíme
            saveSettings();
            return;
        }

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);

            System.out.println("načteny hodnoty ze souboru");
            windowMode = parseIntSafe(props.getProperty("windowMode"), windowMode, 0, 6);
            soundEfectsVolume = parseFloatSafe(props.getProperty("soundEfectsVolume"), soundEfectsVolume, 0f, 1f);
            boardColours = parseIntSafe(props.getProperty("boardColours"), boardColours, 0, 2);
            waterColours = parseIntSafe(props.getProperty("waterColours"), waterColours, 0, 1);

        } catch (IOException e) {
            System.err.println("Nepodařilo se načíst nastavení, používám výchozí hodnoty: " + e.getMessage());
        }

        applyBoardColours();
        applyWaterColours();
    }

    // --- validace hodnot, aby rozbitý/upravený soubor nehodil hru do nesmyslného stavu ---

    private static int parseIntSafe(String value, int fallback, int min, int max) {
        if (value == null) return fallback;
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < min || parsed > max) return fallback;
            return parsed;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static float parseFloatSafe(String value, float fallback, float min, float max) {
        if (value == null) return fallback;
        try {
            float parsed = Float.parseFloat(value.trim());
            if (parsed < min || parsed > max) return fallback;
            return parsed;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}