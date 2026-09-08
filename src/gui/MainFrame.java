package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

    private CardLayout layout;
    private JPanel cards;
    private Loop loopPanel;

    private int width;
    private int height;

    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        System.out.println("DEBUG windowMode = " + UIconfiguration.windowMode);

        // 1. NEJPRVE nastavení režimu okna
        switch (UIconfiguration.windowMode) {
            case 0:  //full windowed
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                Dimension screen0 = Toolkit.getDefaultToolkit().getScreenSize();
                this.width = screen0.width;
                this.height = screen0.height;
                break;

            case 1: //true full windowed
                setUndecorated(true);
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                Dimension screen1 = Toolkit.getDefaultToolkit().getScreenSize();
                this.width = screen1.width;
                this.height = screen1.height;
                break;

            case 2:
                Dimension size2 = getSafeWindowSize(1600, 900);
                setSize(size2);
                setLocationRelativeTo(null);
                setResizable(false);
                this.width = size2.width;
                this.height = size2.height;
                break;

            case 3:
                Dimension size3 = getSafeWindowSize(1280, 720);
                setSize(size3);
                setLocationRelativeTo(null);
                setResizable(false);
                this.width = size3.width;
                this.height = size3.height;
                break;

            case 4:
                Dimension size4 = getSafeWindowSize(1920, 1080);
                setSize(size4);
                setLocationRelativeTo(null);
                setResizable(false);
                this.width = size4.width;
                this.height = size4.height;
                break;

            case 5:
                Dimension size5 = getSafeWindowSize(2560, 1440);
                setSize(size5);
                setLocationRelativeTo(null);
                setResizable(false);
                this.width = size5.width;
                this.height = size5.height;
                break;

            case 6:
                Dimension size6 = getSafeWindowSize(3840, 2160);
                setSize(size6);
                setLocationRelativeTo(null);
                setResizable(false);
                this.width = size6.width;
                this.height = size6.height;
                break;
        }



        // 2. ZOBRAZÍME okno — teprve teď má okno reálnou velikost!
        setVisible(true);

        // 3. Pro režim okna načteme přesné rozměry plátna bez lišt Windows
        if (UIconfiguration.windowMode >= 2) {
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
        cards.add(new Settings(this), "SETTINGS");
        cards.add(loopPanel, "LOOP");

        add(cards);

        // 5. Překreslíme komponenty přidané po setVisible(true)
        revalidate();
        repaint();

        Image img = new ImageIcon("src\\files\\images\\icon.png").getImage();
        Image scaled = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(scaled);

        // na konec MainFrame konstruktoru, po kroku 3 (kde se width/height finálně nastaví)
        System.out.println("DEBUG: windowMode=" + UIconfiguration.windowMode
                + " -> width=" + this.width + ", height=" + this.height);

        setupEmergencyResetShortcut();

        showScene("MENU");
    }

    private Dimension getSafeWindowSize(int requestedWidth, int requestedHeight) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int safeWidth = Math.min(requestedWidth, screen.width);
        int safeHeight = Math.min(requestedHeight, screen.height);
        return new Dimension(safeWidth, safeHeight);
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

    private void setupEmergencyResetShortcut() {
        JRootPane root = getRootPane();

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl shift R"), "emergencyReset");

        root.getActionMap().put("emergencyReset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Nouzový reset rozlišení aktivován.");

                UIconfiguration.windowMode = 2; // bezpečná pevná velikost 1600x900
                UIconfiguration.saveSettings();

                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Rozlišení bylo obnoveno na výchozí. Restartuj hru, aby se změna projevila.",
                        "Reset rozlišení",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

}