package gui;

import profile.PlayerManager;

import java.util.*;
import java.util.List;

public class MapSelect extends MapSelectBase {

    private static final List<String> PRIORITY_ORDER = Arrays.asList(
            //  "tutorial - Kill all kings as white",
            "standard",
            "fighter defense",
            "XXL chess (rip of)"
    );

    // --------------------------------------------------
    // Zakázané mapy — nezobrazí se, dokud není aktivní cheat code.
    // Sem doplň přesné názvy map.
    // --------------------------------------------------
    private static final Set<String> BANNED_MAPS = new HashSet<>(Arrays.asList(
            "test 2 v 2",
            "test 2",
            "test",
            "trash",
            "Water Fight(unready)",
            "level 3"
    ));


    public MapSelect(MainFrame frame) {
        super(
                frame,
                buildMapNames(),
                true,   // výběr bota povolen
                null,
                true,   // výběr barvy povolen
                null,
                "PLAYMENU"
        );
    }

    /**
     * Sestaví seznam map pro volný výběr:
     * 1. Jmenované mapy (PRIORITY_ORDER) se zobrazí vždy (kromě banned bez cheatu).
     * 2. Mapy z Tutorial/Campaign, které nejsou v PRIORITY_ORDER, se vynechají.
     * 3. Banned mapy se zobrazí jen při aktivním cheat code.
     * 4. Zbytek se zobrazí, seřazený abecedně.
     */
    private static List<String> buildMapNames() {

        List<String> allMaps = PlayerManager.getAllMapNames();

        Set<String> excludedByOtherModes = new HashSet<>();
        excludedByOtherModes.addAll(Tutorial.MAPS);
        excludedByOtherModes.addAll(Campaign.MAPS);
        excludedByOtherModes.removeAll(PRIORITY_ORDER);

        List<String> names = new ArrayList<>();
        for (String mapName : allMaps) {
            if (excludedByOtherModes.contains(mapName)) continue;
            if (!PlayerManager.cheatcodeActivated && BANNED_MAPS.contains(mapName)) continue;
            names.add(mapName);
        }

        names.sort((a, b) -> {
            int ia = PRIORITY_ORDER.indexOf(a);
            int ib = PRIORITY_ORDER.indexOf(b);

            boolean aInList = ia != -1;
            boolean bInList = ib != -1;

            if (aInList && bInList) return Integer.compare(ia, ib);
            if (aInList) return -1;
            if (bInList) return 1;
            return a.compareToIgnoreCase(b);
        });

        return names;
    }
    @Override
    protected List<String> provideMapNames() {
        return buildMapNames();
    }
}