package gui;

import gui.MainFrame;
import gui.MapSelectBase;


import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Tutorial extends MapSelectBase {

    public static final List<String> MAPS = Arrays.asList(
       //     "Moves-torpedo,linebreaker,carrier,rotate,water Kill all kings"
    );

    public Tutorial(MainFrame frame) {
        super(frame, MAPS, false, "Bot 1", false, "White", "PLAYMENU");
    }

    @Override
    protected LayoutManager createMapsPanelLayout() {
        return new GridLayout(0, 3, 12, 12); // 3 sloupce, mezery 12px
    }

    @Override
    protected void styleMapButton(JToggleButton button, boolean selected) {
        // jiný vizuální styl pro tutoriál, např. zaoblené rohy / jiná barva
        if (selected) {
            button.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 4));
            button.setBackground(new Color(255, 230, 190));
            button.setOpaque(true);
        } else {
            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            button.setOpaque(false);
        }
    }
}