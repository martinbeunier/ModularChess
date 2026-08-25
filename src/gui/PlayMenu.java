package gui;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class PlayMenu extends JPanel {
    private MainFrame frame;

    public PlayMenu(MainFrame frame) {
        this.frame = frame;

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));


        int w = frame.getWidth();
        int h = frame.getHeight();

        JLabel title = new JLabel("Select gamemode ");
        title.setFont(new Font("SansSerif", Font.BOLD,UI.toPercent(5, h)));
        title.setBounds(UI.toCenter(25, w), UI.toPercent(10, h), UI.toPercent(25, w), UI.toPercent(10, h));


        JButton selectMapB = new JButton("Select Map");
        selectMapB.setBounds(UI.toPercent(20,w), UI.toPercent(35, h), UI.toPercent(20, w), UI.toPercent(10, h));
        selectMapB.addActionListener(e -> {
            frame.showScene("MAPSELECT");
        });

        JButton campaingB = new JButton("Campaing");
        campaingB.setBounds(UI.toPercent(60,w), UI.toPercent(35, h), UI.toPercent(20, w), UI.toPercent(10, h));

        JButton multiplayerB = new JButton("Multiplayer");
        multiplayerB.setBounds(UI.toPercent(20,w), UI.toPercent(55, h), UI.toPercent(20, w), UI.toPercent(10, h));

        JButton rogueB = new JButton("Rogue like mode");
        rogueB .setBounds(UI.toPercent(60,w), UI.toPercent(55, h), UI.toPercent(20, w), UI.toPercent(10, h));



        add(title);
        add(selectMapB);
        add(campaingB);
        add(multiplayerB);
        add(rogueB);




        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backTo"            );

        getActionMap().put("backTo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showScene("MENU");
            }
        });

    }

}
