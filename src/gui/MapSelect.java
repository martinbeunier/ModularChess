package gui;

import profile.PlayerManager;

import java.util.*;
import java.util.List;

public class MapSelect extends MapSelectBase {

    // --------------------------------------------------
    // Pořadí jmenovaných map — mají přednost před abecedním řazením
    // a zobrazí se v MapSelect i tehdy, když jsou zároveň
    // v Tutorial.MAPS nebo Campaign.MAPS.
    // --------------------------------------------------
    private static final List<String> PRIORITY_ORDER = Arrays.asList(
          //  "tutorial - Kill all kings as white",
            "standard",
            "fighter defense",
            "XXL chess (rip of)"
    );

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
     * 1. Jmenované mapy (PRIORITY_ORDER) se zobrazí vždy, bez ohledu na to,
     *    jestli jsou i v Tutorial/Campaign.
     * 2. Zbylé mapy z Tutorial/Campaign (ty NEJMENOVANÉ v PRIORITY_ORDER) se vynechají.
     * 3. Zbytek (nejmenované a nikde jinde nepoužité) se zobrazí, seřazený abecedně.
     */
    private static List<String> buildMapNames() {

        List<String> allMaps = PlayerManager.getAllMapNames();

        Set<String> excludedByOtherModes = new HashSet<>();
        excludedByOtherModes.addAll(Tutorial.MAPS);
        excludedByOtherModes.addAll(Campaign.MAPS);
        excludedByOtherModes.removeAll(PRIORITY_ORDER); // jmenované mapy se nevylučují

        List<String> names = new ArrayList<>();
        for (String mapName : allMaps) {
            if (!excludedByOtherModes.contains(mapName)) {
                names.add(mapName);
            }
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
}