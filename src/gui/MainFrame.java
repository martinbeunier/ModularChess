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
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 1. NEJPRVE nastavení režimu okna
        switch (UIconfiguration.winddowMode) {
            case 0:
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                Dimension screen0 = Toolkit.getDefaultToolkit().getScreenSize();
                this.width = screen0.width;
                this.height = screen0.height;
                break;

            case 1:
                setUndecorated(true);
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice()
                        .setFullScreenWindow(this);
                Dimension screen1 = Toolkit.getDefaultToolkit().getScreenSize();
                this.width = screen1.width;
                this.height = screen1.height;
                break;

            case 2:
                setSize(1600, 900);
                setLocationRelativeTo(null);
                setResizable(false);
                break;
            case 3:
                setSize(1280, 720);
                setLocationRelativeTo(null);
                setResizable(false);
                break;
        }

        // 2. ZOBRAZÍME okno — teprve teď má okno reálnou velikost!
        setVisible(true);

        // 3. Pro režim okna načteme přesné rozměry plátna bez lišt Windows
        if (UIconfiguration.winddowMode >= 2) {
            this.width = getContentPane().getWidth();
            this.height = getContentPane().getHeight();
        }

        // 4. AŽ TEĎ vytvoříme karty a panely (Loop dostane správnou šířku a výšku)
        layout = new CardLayout();
        cards = new JPanel(layout);

        this.loopPanel = new Loop(this);

        cards.add(new MenuPanel(this), "MENU");
        cards.add(new PlayMenu(this), "PLAYMENU");
        cards.add(new MapSelect(this), "MAPSELECT");
        cards.add(loopPanel, "LOOP");

        add(cards);

        // 5. Překreslíme komponenty přidané po setVisible(true)
        revalidate();
        repaint();

        Image img = new ImageIcon("src\\files\\images\\icon.png").getImage();
        Image scaled = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaled);

        showScene("MENU");
    }

    public void showScene(String name) {
        layout.show(cards, name);
    }

    public Loop getLoopPanel() {
        return loopPanel;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}