package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class MapSelect extends JPanel {

    private MainFrame frame;

    private JTextField searchField;
    private JPanel mapsPanel;
    private ButtonGroup mapGroup;

    private ArrayList<String> maps;

    public MapSelect(MainFrame frame) {

        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // --------------------------------------------------
        // MAPY
        // --------------------------------------------------

        maps = new ArrayList<>();

        maps.add("standard");
        maps.add("map1 fighter defense");
        maps.add("WaterFight");
        maps.add("test");

        mapGroup = new ButtonGroup();

        // --------------------------------------------------
        // START BUTTON
        // --------------------------------------------------

        JButton startB = new JButton("Start");

        startB.setBounds(
                UI.toCenter(20, w),
                UI.toPercent(60, h),
                UI.toPercent(20, w),
                UI.toPercent(10, h)
        );

        // --------------------------------------------------
        // SEARCH
        // --------------------------------------------------

        searchField = new JTextField();

        searchField.setBounds(
                UI.toPercent(20, w),
                UI.toPercent(10, h),
                UI.toPercent(40, w),
                UI.toPercent(5, h)
        );

        // --------------------------------------------------
        // MAP PANEL
        // --------------------------------------------------

        mapsPanel = new JPanel();

        mapsPanel.setLayout(
                new FlowLayout(FlowLayout.LEFT)
        );

        JScrollPane scrollPane = new JScrollPane(mapsPanel);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );

        scrollPane.setBounds(
                UI.toPercent(20, w),
                UI.toPercent(20, h),
                UI.toPercent(60, w),
                UI.toPercent(20, h)
        );

        // --------------------------------------------------
        // VÝBĚR BOTA
        // --------------------------------------------------

        JPanel radioPanel = new JPanel();

        radioPanel.setLayout(null);

        radioPanel.setBounds(
                UI.toPercent(15, w),
                UI.toPercent(60, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );

        ButtonGroup group = new ButtonGroup();

        JRadioButton selfButton =
                new JRadioButton("Against yourself");

        selfButton.setBounds(
                0,
                0,
                UI.toPercent(15, w),
                UI.toPercent(3, h)
        );

        selfButton.setActionCommand("Against yourself");

        JRadioButton bot1Button =
                new JRadioButton("Bot 1");

        bot1Button.setBounds(
                0,
                UI.toPercent(3, h),
                UI.toPercent(15, w),
                UI.toPercent(3, h)
        );

        bot1Button.setActionCommand("Bot 1");

        group.add(selfButton);
        group.add(bot1Button);

        radioPanel.add(selfButton);
        radioPanel.add(bot1Button);

        // --------------------------------------------------
        // PŘIDÁNÍ KOMPONENT
        // --------------------------------------------------

        add(startB);
        add(scrollPane);
        add(searchField);
        add(radioPanel);

        // --------------------------------------------------
        // PRVNÍ VYKRESLENÍ MAP
        // --------------------------------------------------

        updateMaps();

        // --------------------------------------------------
        // SEARCH LISTENER
        // --------------------------------------------------

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateMaps();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateMaps();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateMaps();
                    }
                }
        );

        // --------------------------------------------------
        // START
        // --------------------------------------------------

        startB.addActionListener(e -> {

            ButtonModel selectedMapModel =
                    mapGroup.getSelection();

            ButtonModel selectedBotModel =
                    group.getSelection();

            // ----------------------------------------------
            // KONTROLA MAPY
            // ----------------------------------------------

            if (selectedMapModel == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vyber prosím mapu!"
                );

                return;
            }

            // ----------------------------------------------
            // KONTROLA PROTIVNÍKA
            // ----------------------------------------------

            if (selectedBotModel == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vyber prosím protivníka!"
                );

                return;
            }

            // ----------------------------------------------
            // ZÍSKÁNÍ VÝBĚRU
            // ----------------------------------------------

            String map =
                    selectedMapModel.getActionCommand();

            String bot =
                    selectedBotModel.getActionCommand();

            System.out.println("Selected map: " + map);
            System.out.println("Selected opponent: " + bot);

            // ----------------------------------------------
            // PŘEDÁNÍ DO LOOP
            // ----------------------------------------------

            Loop loop = frame.getLoopPanel();

            loop.setSelectedMap(map);
            loop.setSelectedOpponent(bot);

            loop.startGame();

            frame.showScene("LOOP");
        });

        // --------------------------------------------------
        // ESC
        // --------------------------------------------------

        getInputMap(
                WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke("ESCAPE"),
                "backTo"
        );

        getActionMap().put(
                "backTo",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        frame.showScene("PLAYMENU");
                    }
                }
        );
    }

    // ======================================================
    // VYKRESLENÍ / FILTROVÁNÍ MAP
    // ======================================================

    private void updateMaps() {

        /*
         * Zapamatujeme si právě vybranou mapu.
         *
         * To je důležité, protože při removeAll()
         * původní JToggleButton zmizí.
         */

        String selectedMap = null;

        ButtonModel selected =
                mapGroup.getSelection();

        if (selected != null) {
            selectedMap =
                    selected.getActionCommand();
        }

        // --------------------------------------------------
        // ODSTRANĚNÍ STARÝCH TLAČÍTEK Z BUTTON GROUP
        // --------------------------------------------------

        /*
         * ButtonGroup nemá clear().
         *
         * Proto projdeme všechny jeho tlačítka
         * a odstraníme je.
         */

        ArrayList<AbstractButton> oldButtons =
                new ArrayList<>();

        for (java.util.Enumeration<AbstractButton> e =
             mapGroup.getElements();
             e.hasMoreElements();) {

            oldButtons.add(e.nextElement());
        }

        for (AbstractButton button : oldButtons) {
            mapGroup.remove(button);
        }

        // --------------------------------------------------
        // ODSTRANĚNÍ STARÝCH KOMPONENT
        // --------------------------------------------------

        mapsPanel.removeAll();

        // --------------------------------------------------
        // FILTR
        // --------------------------------------------------

        String filter =
                searchField.getText()
                        .trim()
                        .toLowerCase();

        // --------------------------------------------------
        // VYTVOŘENÍ MAP
        // --------------------------------------------------

        for (String map : maps) {

            if (!map.toLowerCase().contains(filter)) {
                continue;
            }

            JToggleButton mapButton =
                    new JToggleButton(map);

            /*
             * ActionCommand musí obsahovat skutečný
             * název mapy.
             */

            mapButton.setActionCommand(map);

            /*
             * Přidáme tlačítko do ButtonGroup.
             */

            mapGroup.add(mapButton);

            /*
             * A zároveň do GUI.
             */

            mapsPanel.add(mapButton);

            // --------------------------------------------------
            // OBNOVENÍ PŮVODNÍHO VÝBĚRU
            // --------------------------------------------------

            if (map.equals(selectedMap)) {
                mapButton.setSelected(true);
            }
        }

        // --------------------------------------------------
        // REFRESH GUI
        // --------------------------------------------------

        mapsPanel.revalidate();
        mapsPanel.repaint();
    }
}