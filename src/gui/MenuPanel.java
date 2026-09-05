package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import main.Main;
import static java.awt.SystemColor.menu;

public class MenuPanel extends JPanel {

    private MainFrame frame;

    public MenuPanel(MainFrame frame) {
        this.frame = frame;



        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));



        int w = frame.getWidth();
        int h = frame.getHeight();

        JLabel title = new JLabel("Modular chess α ");
        title.setFont(new Font("SansSerif", Font.BOLD,UI.toPercent(5, h)));
        title.setBounds(UI.toCenter(25, w), UI.toPercent(10, h), UI.toPercent(25, w), UI.toPercent(10, h));

        JButton playButton = new JButton("Play");
        playButton.setBounds(UI.toPercent(40, w), UI.toPercent(30, h), UI.toPercent(20, w), UI.toPercent(10, h));
        playButton.addActionListener(e -> {
            frame.showScene("PLAYMENU");
        });


        JButton pieceologyButton = new JButton("Pieceology");
        pieceologyButton.setBounds(UI.toPercent(50, w), UI.toPercent(40, h), UI.toPercent(10, w), UI.toPercent(10, h));
        pieceologyButton.addActionListener(e -> {
            File file = new File("files/Pieceology.pdf");

            if (!file.exists()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Soubor manual.pdf nebyl nalezen.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Kontrola, zda systém podporuje třídu Desktop
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        // Otevře PDF ve výchozím prohlížeči v OS (Adobe Reader, Chrome, Edge atd.)
                        desktop.open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                this,
                                "Nepodařilo se otevřít soubor v externí aplikaci.",
                                "Chyba",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Otevírání souborů není na tomto systému podporováno.",
                            "Chyba",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Funkce Desktop není na tomto systému dostupná.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton gameHistoryButton = new JButton("Game History");
        gameHistoryButton.setBounds(UI.toPercent(40, w), UI.toPercent(40, h), UI.toPercent(10, w), UI.toPercent(10, h));

        JButton mapEditorButton = new JButton("Map editor");
        mapEditorButton.setBounds(UI.toPercent(50, w), UI.toPercent(50, h), UI.toPercent(10, w), UI.toPercent(10, h));

        JButton pieceEditorButton = new JButton("Piece editor");
        pieceEditorButton.setBounds(UI.toPercent(40, w), UI.toPercent(50, h), UI.toPercent(10, w), UI.toPercent(10, h));

        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(UI.toCenter(20, w), UI.toPercent(60, h), UI.toPercent(20, w), UI.toPercent(10, h));

        JButton turnOffButton = new JButton("Exit game");
        turnOffButton.setBounds(UI.toCenter(20, w), UI.toPercent(70, h), UI.toPercent(20, w), UI.toPercent(10, h));





        turnOffButton.addActionListener(e -> {
            System.exit(0);
        });

        add(mapEditorButton);
        add(pieceEditorButton);
        add(settingsButton);
        add(turnOffButton);
        add(playButton);
        add(pieceologyButton);
        add(gameHistoryButton);

        add(title);

/*
        panel.add(Box.createVerticalGlue()); // margin nahoře
        panel.add(title);
        panel.add(Box.createVerticalStrut(30)); // mezera mezi title a tlačítkem
        panel.add(button);
        panel.add(Box.createVerticalGlue()); // margin dole
        */



      //  frame.add(panel);
       // frame.setVisible(true);

    }

}