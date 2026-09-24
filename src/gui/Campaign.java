package gui;

import java.util.Arrays;
import java.util.List;

public class Campaign extends MapSelectBase {

    // --------------------------------------------------
    // Mapy zobrazené v kampani — pořadí = pořadí v tomto listu
    // --------------------------------------------------
    public static final List<String> MAPS = Arrays.asList(
            "standard",
            "fighter defense",
            "XXL chess (rip of)",
            "The discovery of America"
    );

    public Campaign(MainFrame frame) {
        super(
                frame,
                MAPS,
                true,       // výběr bota povolen
                null,
                false,      // výběr barvy zakázán
                "White",    // vždy bílý
                "PLAYMENU"
        );
    }
}