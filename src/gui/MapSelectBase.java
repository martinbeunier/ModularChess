package gui;

import profile.PlayerManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Sdílený základ pro všechny obrazovky výběru mapy (volný výběr, tutoriál, kampaň).
 * Konkrétní chování (jaké mapy se nabízí, jestli lze vybrat bota/barvu) si určuje
 * každá podtřída přes parametry konstruktoru. Vzhled a rozmístění lze přizpůsobit
 * přepsáním protected "hook" metod níže.
 */
public abstract class MapSelectBase extends JPanel {

    protected final MainFrame frame;

    private JTextField searchField;
    private JPanel mapsPanel;
    private ButtonGroup mapGroup;
    private JLabel points;

    private ButtonGroup botGroup;
    private ButtonGroup colourGroup;

    private final boolean botSelectionEnabled;
    private final String fixedOpponentCommand;

    private final boolean colourSelectionEnabled;
    private final String fixedColourCommand;

    private final String backScene;

    // --------------------------------------------------
    // SLOŽKA S IKONAMI MAP
    // --------------------------------------------------
    private static final String MAPS_ICONS_PATH = "src/files/images/mapsicons";
    private static final String DEFAULT_ICON_NAME = "default.png";

    private static final double THUMB_WIDTH_PERCENT = 6;
    private static final double THUMB_HEIGHT_PERCENT = 10;

    private static final int MIN_THUMB_WIDTH = 70;
    private static final int MIN_THUMB_HEIGHT = 70;

    private static final int THUMB_WIDTH = 96;
    private static final int THUMB_HEIGHT = 96;

    private static final String[] ICON_EXTENSIONS = {
            ".png", ".jpg", ".jpeg", ".gif"
    };

    private ImageIcon defaultIcon;

    private static class MapEntry {
        String name;
        ImageIcon icon;

        MapEntry(String name, ImageIcon icon) {
            this.name = name;
            this.icon = icon;
        }
    }

    private ArrayList<MapEntry> maps;

    /**
     * @param mapNames               mapy k zobrazení, v pořadí, v jakém se mají vykreslit
     * @param botSelectionEnabled    true = zobrazit výběr bota; false = použít fixedOpponentCommand
     * @param fixedOpponentCommand   opponent, pokud je výběr bota zakázán (jinak ignorováno)
     * @param colourSelectionEnabled true = zobrazit výběr barvy; false = použít fixedColourCommand
     * @param fixedColourCommand     barva, pokud je výběr barvy zakázán (jinak ignorováno)
     * @param backScene              scéna, na kterou se skočí po ESC
     */
    protected MapSelectBase(
            MainFrame frame,
            List<String> mapNames,
            boolean botSelectionEnabled,
            String fixedOpponentCommand,
            boolean colourSelectionEnabled,
            String fixedColourCommand,
            String backScene
    ) {
        this.frame = frame;
        this.botSelectionEnabled = botSelectionEnabled;
        this.fixedOpponentCommand = fixedOpponentCommand;
        this.colourSelectionEnabled = colourSelectionEnabled;
        this.fixedColourCommand = fixedColourCommand;
        this.backScene = backScene;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        loadMaps(mapNames);

        mapGroup = new ButtonGroup();

        // --------------------------------------------------
        // START BUTTON
        // --------------------------------------------------

        JButton startB = new JButton("Start");
        startB.setBounds(getStartButtonBounds(w, h));

        // --------------------------------------------------
        // SEARCH
        // --------------------------------------------------

        searchField = new JTextField();
        searchField.setBounds(getSearchFieldBounds(w, h));

        // --------------------------------------------------
        // MAP PANEL
        // --------------------------------------------------

        mapsPanel = new JPanel();
        mapsPanel.setLayout(createMapsPanelLayout());

        JScrollPane scrollPane = new JScrollPane(mapsPanel);

        configureScrollPane(scrollPane);

        scrollPane.setBounds(getScrollPaneBounds(w, h));

        points = new JLabel();
        points.setBounds(getPointsLabelBounds(w, h));
        add(points);

        add(startB);
        add(scrollPane);
        add(searchField);

        // --------------------------------------------------
        // VÝBĚR BOTA (jen pokud je povolený)
        // --------------------------------------------------

        if (botSelectionEnabled) {

            botGroup = new ButtonGroup();

            JPanel radioPanel = new JPanel();
            radioPanel.setLayout(null);
            radioPanel.setBounds(getBotPanelBounds(w, h));

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

            botGroup.add(selfButton);
            botGroup.add(bot1Button);
            botGroup.add(bot2Button);
            botGroup.add(bot3Button);

            radioPanel.add(selfButton);
            radioPanel.add(bot1Button);
            radioPanel.add(bot2Button);
            radioPanel.add(bot3Button);

            add(radioPanel);
        }

        // --------------------------------------------------
        // VÝBĚR BARVY (jen pokud je povolený)
        // --------------------------------------------------

        if (colourSelectionEnabled) {

            colourGroup = new ButtonGroup();

            JPanel radioPanelColour = new JPanel();
            radioPanelColour.setLayout(null);
            radioPanelColour.setBounds(getColourPanelBounds(w, h));

            JRadioButton whiteButton = new JRadioButton("White");
            whiteButton.setBounds(0, 0, UI.toPercent(5, w), UI.toPercent(3, h));
            whiteButton.setActionCommand("White");
            colourGroup.add(whiteButton);
            radioPanelColour.add(whiteButton);

            JRadioButton randomButton = new JRadioButton("Random");
            randomButton.setBounds(UI.toPercent(7, w), 0, UI.toPercent(5, w), UI.toPercent(3, h));
            randomButton.setActionCommand("Random");
            colourGroup.add(randomButton);
            radioPanelColour.add(randomButton);

            JRadioButton blackButton = new JRadioButton("Black");
            blackButton.setBounds(UI.toPercent(14, w), 0, UI.toPercent(5, w), UI.toPercent(3, h));
            blackButton.setActionCommand("Black");
            colourGroup.add(blackButton);
            radioPanelColour.add(blackButton);

            randomButton.setSelected(true);

            add(radioPanelColour);
        }

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

            ButtonModel selectedMapModel = mapGroup.getSelection();

            if (selectedMapModel == null) {
                JOptionPane.showMessageDialog(this, "Vyber prosím mapu!");
                return;
            }

            String opponent;

            if (this.botSelectionEnabled) {
                ButtonModel selectedBotModel = botGroup.getSelection();

                if (selectedBotModel == null) {
                    JOptionPane.showMessageDialog(this, "Vyber prosím protivníka!");
                    return;
                }

                opponent = selectedBotModel.getActionCommand();
            } else {
                opponent = this.fixedOpponentCommand;
            }

            String colour;

            if (this.colourSelectionEnabled) {
                ButtonModel selectedColourModel = colourGroup.getSelection();

                if (selectedColourModel == null) {
                    JOptionPane.showMessageDialog(this, "Vyber prosím barvu!");
                    return;
                }

                colour = selectedColourModel.getActionCommand();

                if (colour.equals("Random")) {
                    Random random = new Random();
                    colour = (random.nextInt(2) == 0) ? "Black" : "White";
                }
            } else {
                colour = this.fixedColourCommand;
            }

            String map = selectedMapModel.getActionCommand();

            System.out.println("Selected map: " + map);
            System.out.println("Selected opponent: " + opponent);
            System.out.println("Selected colour: " + colour);

            Loop loop = frame.getLoopPanel();

            loop.setSelectedMap(map);
            loop.setSelectedOpponent(opponent);
            loop.setSelectedColour(colour);

            loop.startGame();

            frame.showScene("LOOP");
        });

        // --------------------------------------------------
        // ESC
        // --------------------------------------------------

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backTo");

        getActionMap().put(
                "backTo",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        frame.showScene(backScene);
                    }
                }
        );
    }

    // ======================================================
    // HOOKY PRO ROZMÍSTĚNÍ KOMPONENT (podtřídy mohou přepsat)
    // ======================================================

    protected Rectangle getStartButtonBounds(int w, int h) {
        return new Rectangle(
                UI.toCenter(20, w),
                UI.toPercent(60, h),
                UI.toPercent(20, w),
                UI.toPercent(10, h)
        );
    }

    protected Rectangle getSearchFieldBounds(int w, int h) {
        return new Rectangle(
                UI.toPercent(20, w),
                UI.toPercent(10, h),
                UI.toPercent(40, w),
                UI.toPercent(5, h)
        );
    }

    protected Rectangle getScrollPaneBounds(int w, int h) {
        return new Rectangle(
                UI.toPercent(20, w),
                UI.toPercent(20, h),
                UI.toPercent(60, w),
                UI.toPercent(20, h)
        );
    }

    protected Rectangle getPointsLabelBounds(int w, int h) {
        return new Rectangle(
                UI.toPercent(10, w),
                UI.toPercent(20, h),
                UI.toPercent(10, w),
                UI.toPercent(10, h)
        );
    }

    protected Rectangle getBotPanelBounds(int w, int h) {
        return new Rectangle(
                UI.toPercent(15, w),
                UI.toPercent(60, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );
    }

    protected Rectangle getColourPanelBounds(int w, int h) {
        return new Rectangle(
                UI.toPercent(40, w),
                UI.toPercent(50, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );
    }

    /** Nastavení scroll politiky. Default: horizontální scroll vždy, vertikální nikdy. */
    protected void configureScrollPane(JScrollPane scrollPane) {
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    }

    // ======================================================
    // HOOKY PRO VZHLED MAP (podtřídy mohou přepsat)
    // ======================================================

    /** Layout manager pro panel s mapami. Default: FlowLayout zleva. */
    protected LayoutManager createMapsPanelLayout() {
        return new FlowLayout(FlowLayout.LEFT);
    }

    /**
     * Vytvoří a nastylizuje jedno tlačítko mapy. Podtřída může přepsat
     * celé vytváření (např. jiná velikost, jiný typ komponenty) nebo
     * zavolat super a jen dolepit vlastní úpravy.
     */
    protected JToggleButton createMapButton(
            String mapName,
            ImageIcon icon,
            String htmlLabel,
            boolean unlocked,
            int buttonW
    ) {
        JToggleButton mapButton = new JToggleButton(htmlLabel, icon);

        mapButton.setHorizontalTextPosition(SwingConstants.CENTER);
        mapButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mapButton.setIconTextGap(6);
        mapButton.setFocusPainted(false);
        mapButton.setEnabled(unlocked);

        Dimension natural = mapButton.getPreferredSize();
        mapButton.setPreferredSize(
                new Dimension(Math.max(natural.width, buttonW), natural.height)
        );

        mapButton.setActionCommand(mapName);

        return mapButton;
    }

    /** Sestaví HTML popisek tlačítka. Podtřída může změnit vzhled textu/zámku. */
    protected String buildMapLabel(String mapName, boolean unlocked, int requiredPoints, int buttonW) {
        if (unlocked) {
            return "<html><div style='text-align:center; width:" + buttonW + "px;'>"
                    + mapName
                    + "</div></html>";
        }
        return "<html><div style='text-align:center; width:" + buttonW + "px; color:gray;'>"
                + "&#128274; " + mapName
                + "<br><span style='font-size:10px;'>(" + requiredPoints + " b. potřeba)</span>"
                + "</div></html>";
    }

    /** Zvýraznění vybraného/nevybraného tlačítka. Podtřída může přepsat barvy/styl. */
    protected void styleMapButton(JToggleButton button, boolean selected) {
        if (selected) {
            button.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 3));
            button.setBackground(new Color(200, 225, 255));
            button.setOpaque(true);
        } else {
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            button.setOpaque(false);
        }
    }

    // ======================================================
    // NAČTENÍ MAP (podle seznamu názvů dodaného podtřídou)
    // ======================================================

    private void loadMaps(List<String> mapNames) {
        maps = new ArrayList<>();

        for (String mapName : mapNames) {
            ImageIcon icon = loadIconForMap(mapName);
            maps.add(new MapEntry(mapName, icon));
        }
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

    protected void updateMaps() {

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

            int requiredPoints = PlayerManager.getRequiredPointsForMap(entry.name);
            boolean unlocked = PlayerManager.isMapUnlocked(entry.name);

            ImageIcon scaledIcon = scaleIcon(entry.icon, thumbW, thumbH);

            if (!unlocked) {
                scaledIcon = makeGrayscale(scaledIcon);
            }

            String htmlName = buildMapLabel(entry.name, unlocked, requiredPoints, buttonW);

            JToggleButton mapButton = createMapButton(entry.name, scaledIcon, htmlName, unlocked, buttonW);

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
}