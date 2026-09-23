package gui;

import profile.PlayerManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


public class MapSelect extends JPanel {

    private MainFrame frame;

    private JTextField searchField;
    private JPanel mapsPanel;
    private ButtonGroup mapGroup;
    private JLabel points;

    // --------------------------------------------------
    // SLOŽKA S IKONAMI MAP
    // --------------------------------------------------
    private static final String MAPS_ICONS_PATH = "src/files/images/mapsicons";
    private static final String DEFAULT_ICON_NAME = "default.png";
    // --------------------------------------------------
    // ROZMĚRY TLAČÍTEK MAP (RELATIVNÍ)
    // --------------------------------------------------
    private static final double THUMB_WIDTH_PERCENT = 6;
    private static final double THUMB_HEIGHT_PERCENT = 10;

    private static final int MIN_THUMB_WIDTH = 70;
    private static final int MIN_THUMB_HEIGHT = 70;

    private static final int THUMB_WIDTH = 96;
    private static final int THUMB_HEIGHT = 96;

    private static final List<String> PRIORITY_ORDER = Arrays.asList(
            "tutorial - Kill all kings as white",
            "standard",
            "fighter defense",
            "XXL chess (rip of)"
    );

    private static final String[] ICON_EXTENSIONS = {
            ".png", ".jpg", ".jpeg", ".gif"
    };

    private ImageIcon defaultIcon;

    // --------------------------------------------------
    // JEDNA MAPA = NÁZEV + IKONA
    // --------------------------------------------------
    private static class MapEntry {
        String name;
        ImageIcon icon;

        MapEntry(String name, ImageIcon icon) {
            this.name = name;
            this.icon = icon;
        }
    }

    private ArrayList<MapEntry> maps;

    public MapSelect(MainFrame frame) {

        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        loadMapsFromFolder();

        mapGroup = new ButtonGroup();

        JButton startB = new JButton("Start");

        startB.setBounds(
                UI.toCenter(20, w),
                UI.toPercent(60, h),
                UI.toPercent(20, w),
                UI.toPercent(10, h)
        );

        searchField = new JTextField();

        searchField.setBounds(
                UI.toPercent(20, w),
                UI.toPercent(10, h),
                UI.toPercent(40, w),
                UI.toPercent(5, h)
        );

        mapsPanel = new JPanel();
        mapsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JScrollPane scrollPane = new JScrollPane(mapsPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        scrollPane.setBounds(
                UI.toPercent(20, w),
                UI.toPercent(20, h),
                UI.toPercent(60, w),
                UI.toPercent(20, h)
        );

        points = new JLabel();
        points.setBounds(UI.toPercent(10, w), UI.toPercent(20, h), UI.toPercent(10, w), UI.toPercent(10, h));
        add(points);

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

        JRadioButton selfButton = new JRadioButton("Against yourself");
        selfButton.setBounds(0, 0, UI.toPercent(15, w), UI.toPercent(3, h));
        selfButton.setActionCommand("Against yourself");

        JRadioButton bot1Button = new JRadioButton("Bold man bot : Dificulty 1/10");
        bot1Button.setBounds(0, UI.toPercent(3, h), UI.toPercent(15, w), UI.toPercent(3, h));
        bot1Button.setActionCommand("Bot 1");

        JRadioButton bot2Button = new JRadioButton("Greedy bot : Dificulty 2/10");
        bot2Button.setBounds(0, UI.toPercent(6, h), UI.toPercent(15, w), UI.toPercent(3, h));
        bot2Button.setActionCommand("Bot 2");

        JRadioButton bot3Button = new JRadioButton("Trapper bot : Dificulty 6/10");
        bot3Button.setBounds(0, UI.toPercent(9, h), UI.toPercent(15, w), UI.toPercent(3, h));
        bot3Button.setActionCommand("Bot 3");

        group.add(selfButton);
        group.add(bot1Button);
        group.add(bot2Button);
        group.add(bot3Button);

        radioPanel.add(selfButton);
        radioPanel.add(bot1Button);
        radioPanel.add(bot2Button);
        radioPanel.add(bot3Button);

        // --------------------------------------------------
        // Výběr barvy
        // --------------------------------------------------
        JPanel radioPanelColour = new JPanel();
        radioPanelColour.setLayout(null);

        ButtonGroup groupColour = new ButtonGroup();

        radioPanelColour.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(50, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );

        JRadioButton whiteButton = new JRadioButton("White");
        whiteButton.setBounds(0, 0, UI.toPercent(5, w), UI.toPercent(3, h));
        whiteButton.setActionCommand("White");
        groupColour.add(whiteButton);
        radioPanelColour.add(whiteButton);

        JRadioButton randomButton = new JRadioButton("Random");
        randomButton.setBounds(UI.toPercent(7, w), 0, UI.toPercent(5, w), UI.toPercent(3, h));
        randomButton.setActionCommand("Random");
        groupColour.add(randomButton);
        radioPanelColour.add(randomButton);

        JRadioButton blackButton = new JRadioButton("Black");
        blackButton.setBounds(UI.toPercent(14, w), 0, UI.toPercent(5, w), UI.toPercent(3, h));
        blackButton.setActionCommand("Black");
        groupColour.add(blackButton);
        radioPanelColour.add(blackButton);

        randomButton.setSelected(true);

        add(startB);
        add(scrollPane);
        add(searchField);
        add(radioPanel);
        add(radioPanelColour);

        updateMaps();

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

        startB.addActionListener(e -> {

            ButtonModel selectedMapModel = mapGroup.getSelection();
            ButtonModel selectedBotModel = group.getSelection();
            ButtonModel selectedColourModel = groupColour.getSelection();

            if (selectedMapModel == null) {
                JOptionPane.showMessageDialog(this, "Vyber prosím mapu!");
                return;
            }

            if (selectedBotModel == null) {
                JOptionPane.showMessageDialog(this, "Vyber prosím protivníka!");
                return;
            }

            String map = selectedMapModel.getActionCommand();
            String bot = selectedBotModel.getActionCommand();
            String colour = selectedColourModel.getActionCommand();

            System.out.println("Selected map: " + map);
            System.out.println("Selected opponent: " + bot);
            System.out.println("Selected colour: " + colour);

            Loop loop = frame.getLoopPanel();

            loop.setSelectedMap(map);
            loop.setSelectedOpponent(bot);

            if (colour.equals("Random")) {
                Random random = new Random();
                if (random.nextInt(2) == 0) { colour = "Black"; } else { colour = "White"; }
            }

            loop.setSelectedColour(colour);

            loop.startGame();

            frame.showScene("LOOP");
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backTo");

        getActionMap().put(
                "backTo",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        frame.showScene("PLAYMENU");
                    }
                }
        );

    }

    // ======================================================
    // NAČTENÍ MAP ZE SLOŽKY S POZICEMI (.chess)
    // ======================================================

    private void loadMapsFromFolder() {

        maps = new ArrayList<>();

        for (String mapName : PlayerManager.getAllMapNames()) {
            ImageIcon icon = loadIconForMap(mapName);
            maps.add(new MapEntry(mapName, icon));
        }

        maps.sort((a, b) -> {
            int ia = PRIORITY_ORDER.indexOf(a.name);
            int ib = PRIORITY_ORDER.indexOf(b.name);

            boolean aInList = ia != -1;
            boolean bInList = ib != -1;

            if (aInList && bInList) {
                return Integer.compare(ia, ib);
            }
            if (aInList) return -1;
            if (bInList) return 1;
            return a.name.compareToIgnoreCase(b.name);
        });
    }

    // ======================================================
    // NAČTENÍ IKONY PRO DANOU MAPU (S FALLBACKEM NA DEFAULT)
    // ======================================================

    private ImageIcon loadIconForMap(String mapName) {

        for (String ext : ICON_EXTENSIONS) {

            File iconFile = new File(MAPS_ICONS_PATH + File.separator + mapName + ext);

            if (iconFile.exists() && iconFile.isFile()) {

                ImageIcon rawIcon = new ImageIcon(iconFile.getPath());

                if (rawIcon.getImageLoadStatus() != MediaTracker.ERRORED
                        && rawIcon.getIconWidth() > 0) {

                    return scaleIcon(rawIcon, THUMB_WIDTH, THUMB_HEIGHT);
                }
            }
        }

        System.out.println("Ikona pro mapu '" + mapName + "' nenalezena, používám výchozí ikonu.");

        return getDefaultIcon();
    }

    private ImageIcon getDefaultIcon() {

        if (defaultIcon != null) {
            return defaultIcon;
        }

        File defaultFile = new File(MAPS_ICONS_PATH + File.separator + DEFAULT_ICON_NAME);

        if (defaultFile.exists() && defaultFile.isFile()) {

            ImageIcon rawIcon = new ImageIcon(defaultFile.getPath());

            if (rawIcon.getImageLoadStatus() != MediaTracker.ERRORED
                    && rawIcon.getIconWidth() > 0) {

                defaultIcon = scaleIcon(rawIcon, THUMB_WIDTH, THUMB_HEIGHT);
                return defaultIcon;
            }
        }

        System.out.println("Výchozí ikona '" + DEFAULT_ICON_NAME + "' nebyla nalezena ve složce: " + MAPS_ICONS_PATH);

        defaultIcon = new ImageIcon(
                new java.awt.image.BufferedImage(
                        THUMB_WIDTH,
                        THUMB_HEIGHT,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB
                )
        );

        return defaultIcon;
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ======================================================
    // VYKRESLENÍ / FILTROVÁNÍ MAP
    // ======================================================

    private void updateMaps() {

        String selectedMap = null;

        ButtonModel selected = mapGroup.getSelection();

        if (selected != null) {
            selectedMap = selected.getActionCommand();
        }

        ArrayList<AbstractButton> oldButtons = new ArrayList<>();

        for (java.util.Enumeration<AbstractButton> e = mapGroup.getElements(); e.hasMoreElements(); ) {
            oldButtons.add(e.nextElement());
        }

        for (AbstractButton button : oldButtons) {
            mapGroup.remove(button);
        }

        mapsPanel.removeAll();

        String filter = searchField.getText().trim().toLowerCase();

        int frameW = frame.getWidth();
        int frameH = frame.getHeight();

        int thumbW = Math.max(MIN_THUMB_WIDTH, UI.toPercent((int) THUMB_WIDTH_PERCENT, frameW));
        int thumbH = Math.max(MIN_THUMB_HEIGHT, UI.toPercent((int) THUMB_HEIGHT_PERCENT, frameH));

        int buttonW = (int) (thumbW * 1.4);

        for (MapEntry entry : maps) {

            if (!entry.name.toLowerCase().contains(filter)) {
                continue;
            }

            // ------------------------------------------
            // KONTROLA ODEMČENÍ MAPY (progression systém)
            // ------------------------------------------

            int requiredPoints = PlayerManager.getRequiredPointsForMap(entry.name);
            boolean unlocked = PlayerManager.isMapUnlocked(entry.name);

            ImageIcon scaledIcon = scaleIcon(entry.icon, thumbW, thumbH);

            if (!unlocked) {
                scaledIcon = makeGrayscale(scaledIcon);
            }

            String htmlName;

            if (unlocked) {
                htmlName =
                        "<html><div style='text-align:center; width:" + buttonW + "px;'>"
                                + entry.name
                                + "</div></html>";
            } else {
                htmlName =
                        "<html><div style='text-align:center; width:" + buttonW + "px; color:gray;'>"
                                + "&#128274; " + entry.name
                                + "<br><span style='font-size:10px;'>(" + requiredPoints + " b. potřeba)</span>"
                                + "</div></html>";
            }

            JToggleButton mapButton = new JToggleButton(htmlName, scaledIcon);

            mapButton.setHorizontalTextPosition(SwingConstants.CENTER);
            mapButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            mapButton.setIconTextGap(6);
            mapButton.setFocusPainted(false);
            mapButton.setEnabled(unlocked);

            Dimension natural = mapButton.getPreferredSize();

            mapButton.setPreferredSize(
                    new Dimension(
                            Math.max(natural.width, buttonW),
                            natural.height
                    )
            );

            mapButton.setActionCommand(entry.name);

            styleMapButton(mapButton, mapButton.isSelected());

            mapButton.addChangeListener(e ->
                    styleMapButton(mapButton, mapButton.isSelected())
            );

            mapGroup.add(mapButton);
            mapsPanel.add(mapButton);

            if (unlocked && entry.name.equals(selectedMap)) {
                mapButton.setSelected(true);
            }
        }

        mapsPanel.revalidate();
        mapsPanel.repaint();
    }

    // ======================================================
    // ODBARVENÍ IKONY (vizuální indikace zamčené mapy)
    // ======================================================

    private ImageIcon makeGrayscale(ImageIcon icon) {

        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        if (w <= 0 || h <= 0) {
            return icon;
        }

        java.awt.image.BufferedImage gray = new java.awt.image.BufferedImage(
                w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = gray.createGraphics();
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.dispose();

        java.awt.image.ColorConvertOp op = new java.awt.image.ColorConvertOp(
                java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_GRAY),
                null
        );

        op.filter(gray, gray);

        return new ImageIcon(gray);
    }

    public void refresh() {
        updateMaps();
        points.setText("Body: " + PlayerManager.getTotalPoints());
    }

    // ======================================================
    // VIZUÁLNÍ ZVÝRAZNĚNÍ VYBRANÉ MAPY
    // ======================================================

    private void styleMapButton(JToggleButton button, boolean selected) {

        if (selected) {
            button.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 3));
            button.setBackground(new Color(200, 225, 255));
            button.setOpaque(true);
        } else {
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            button.setOpaque(false);
        }
    }
}