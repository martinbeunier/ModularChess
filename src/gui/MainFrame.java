package gui;

import javax.swing.*;
import java.awt.*;




public class MainFrame extends JFrame {

    private CardLayout layout;
    private JPanel cards;
    private Loop loopPanel;


    private int width;
    private int height;

    public MainFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.width = screen.width;
        this.height = screen.height;


        layout = new CardLayout();
        cards = new JPanel(layout);

        this.loopPanel = new Loop(this); // <--- Vytvoříme

        cards.add(new MenuPanel(this), "MENU");
        cards.add(new PlayMenu(this), "PLAYMENU");
        cards.add(new MapSelect(this), "MAPSELECT");
        cards.add(loopPanel,"LOOP");

        add(cards);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setVisible(true);

        Image img = new ImageIcon("files\\images\\icon.png").getImage();
        Image scaled = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaled);

        showScene("MENU");

        //showScene("MAPSELECT");
    }

    public void showScene(String name) {
        layout.show(cards, name);
    }

    public Loop getLoopPanel() {
        return loopPanel;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }


}