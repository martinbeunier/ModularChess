package gui;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MapSelect extends JPanel {
    private MainFrame frame;

    public MapSelect(MainFrame frame) {
        this.frame = frame;
        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));



        JButton startB = new JButton("Start");
        startB.setBounds(UI.toCenter(20,w), UI.toPercent(60,h), UI.toPercent(20, w), UI.toPercent(10, h));



        JTextField searchField = new JTextField();
        searchField.setBounds(
                UI.toPercent(20, w),
                UI.toPercent(10, h),
                UI.toPercent(40, w),
                UI.toPercent(5, h));




        JPanel mapsPanel = new JPanel();
        mapsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));




        ArrayList<String> maps = new ArrayList<>();

        ButtonGroup mapGroup = new ButtonGroup();

        maps.add("Mapa 1");
        maps.add("Mapa 2");
        maps.add("Velká mapa");
        maps.add("Poušť");
        maps.add("Castle");
        maps.add("Fighter defense");



        for (String map : maps) {
            JToggleButton mapButton = new JToggleButton(map);
            mapGroup.add(mapButton);
            mapsPanel.add(mapButton);
        }



        JScrollPane scrollPane = new JScrollPane(mapsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(UI.toPercent(20,w), UI.toPercent(20,h),UI.toPercent(60,w) ,UI.toPercent(20,h));

        // Výběr bota

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(null);
        radioPanel.setBounds(UI.toPercent(15, w), UI.toPercent(60, h), UI.toPercent(30, w), UI.toPercent(15, h));

        ButtonGroup group = new ButtonGroup();

        JRadioButton selfButton = new JRadioButton("Against yourself");
        selfButton.setBounds(0, 0, UI.toPercent(8, w), UI.toPercent(3, h));
        selfButton.setActionCommand("Against yourself");

     //   ImageIcon icon = new ImageIcon("C:/Users/marti/Documents/Šachy/skiny/icon.png");
        JRadioButton bot1Button = new JRadioButton("Bot 1");
        bot1Button.setBounds(0, UI.toPercent(3, h), UI.toPercent(8, w), UI.toPercent(3, h));
        bot1Button.setActionCommand("Bot 1");

        group.add(selfButton);
        group.add(bot1Button);

        radioPanel.add(selfButton);
        radioPanel.add(bot1Button);



        //

        add(startB);
        add(scrollPane);
        add(searchField);

        add(radioPanel);




        searchField.getDocument().addDocumentListener(new DocumentListener() {
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

            private void updateMaps() {
                String filter = searchField.getText().toLowerCase();

                mapsPanel.removeAll();

                for (String map : maps) {
                    if (map.toLowerCase().contains(filter)) {
                        mapsPanel.add(new JButton(map));
                    }
                }

                mapsPanel.revalidate();
                mapsPanel.repaint();
            }
        });

        startB.addActionListener(e -> {
            ButtonModel selectedMapModel = mapGroup.getSelection();
            ButtonModel selectedBotModel = group.getSelection();

            // Kontrola, zda uživatel vůbec něco vybral
            if (selectedMapModel == null) {
                JOptionPane.showMessageDialog(this, "Vyber prosím mapu!");
                return;
            }

            if (selectedBotModel == null) {
                JOptionPane.showMessageDialog(this, "Vyber prosím protivníka!");
                return;
            }

            // Vytažení textových hodnot
            String map = selectedMapModel.getActionCommand();
            //map = "map1 fighter defense";
            //map = "standard";
            map = "WaterFight";

            String bot = selectedBotModel.getActionCommand();

            // Předání do Loop a přepnutí
            Loop loop = frame.getLoopPanel();
            loop.setSelectedMap(map );
            loop.setSelectedOpponent(bot);
            loop.startGame();

            frame.showScene("LOOP");
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backTo"            );
        getActionMap().put("backTo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showScene("PLAYMENU");
            }
        });

    }

}
