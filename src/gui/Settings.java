package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


public class Settings extends JPanel {

    public static boolean openedInLoop;
    private MainFrame frame;
    private ButtonGroup boardColourGroup;
    private ButtonGroup waterColourGroup;
    private ButtonGroup windowModeGroup;
    private JSlider volumeSlider;
    private JLabel restartNoticeLabel;

    // ------------------------------------------------------------
    // Barevná paleta — sjednocená s hlavní hrací plochou (Loop)
    // ------------------------------------------------------------
    private static final Color BG_MAIN     = new Color(0x1A, 0x1C, 0x25); // pozadí celé obrazovky
    private static final Color BG_PANEL    = new Color(0x24, 0x27, 0x33); // trochu světlejší panely sekcí
    private static final Color BORDER_COL  = new Color(0x3A, 0x3E, 0x4D);
    private static final Color TEXT_MAIN   = new Color(0xE8, 0xE8, 0xEC);
    private static final Color TEXT_MUTED  = new Color(0xA0, 0xA4, 0xB0);
    private static final Color ACCENT      = new Color(0xF0, 0x9A, 0x2E); // oranžová, jako score labely v Loop
    private static final Color ACCENT_DARK = new Color(0xC7, 0x7E, 0x1E);

    public Settings(MainFrame frame) {
        this.frame = frame;

        setLayout(null);
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        int w = frame.getWidth();
        int h = frame.getHeight();

        // ------------------------------------------------------------
        // Nadpis
        // ------------------------------------------------------------
        JLabel heading = new JLabel("Settings", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, UI.toPercent(5, h)));
        heading.setForeground(ACCENT);
        heading.setBounds(0, UI.toPercent(4, h), w, UI.toPercent(8, h));
        add(heading);

        // Výběr barvy šachovnice
        // --------------------------------------------------

        JPanel boardRadioPanel = createSectionPanel(
                "Board colour",
                UI.toPercent(55, w), UI.toPercent(20, h),
                UI.toPercent(30, w), UI.toPercent(16, h)
        );

        boardColourGroup = new ButtonGroup();

        JRadioButton dark = createStyledRadio("Dark", "0");
        dark.setBounds(20, UI.toPercent(4, h), UI.toPercent(24, w), UI.toPercent(4, h));

        JRadioButton green = createStyledRadio("Green", "1");
        green.setBounds(20, UI.toPercent(9, h), UI.toPercent(24, w), UI.toPercent(4, h));

        boardRadioPanel.add(dark);
        boardRadioPanel.add(green);

        boardColourGroup.add(dark);
        boardColourGroup.add(green);

        if (UIconfiguration.boardColours == 0) {
            dark.setSelected(true);
        } else if (UIconfiguration.boardColours == 1) {
            green.setSelected(true);
        }

        add(boardRadioPanel);

        // Výběr barvy vody
        // --------------------------------------------------

        JPanel waterRadioPanel = createSectionPanel(
                "Water colour",
                UI.toPercent(55, w), UI.toPercent(40, h),
                UI.toPercent(30, w), UI.toPercent(12, h)
        );

        waterColourGroup = new ButtonGroup();

        JRadioButton waterClassic = createStyledRadio("Classic", "0");
        waterClassic.setBounds(20, UI.toPercent(4, h), UI.toPercent(24, w), UI.toPercent(4, h));

        waterRadioPanel.add(waterClassic);
        waterColourGroup.add(waterClassic);

        if (UIconfiguration.waterColours == 0) {
            waterClassic.setSelected(true);
        }

        add(waterRadioPanel);

        // Režim okna / rozlišení
        // --------------------------------------------------

        JPanel windowModePanel = createSectionPanel(
                "Window mode",
                UI.toPercent(10, w), UI.toPercent(20, h),
                UI.toPercent(22, w), UI.toPercent(52, h)
        );

        windowModeGroup = new ButtonGroup();

        // Popisky odpovídají PŮVODNÍM indexům (musí se shodovat s UIconfiguration.windowMode
        // a s tím, jak s nimi pracuje zbytek kódu — proto se pole samo neřadí).
        String[] modeLabels = {
                "Fullscreen window (with borders)", // 0
                "Fullscreen (borderless)",           // 1
                "Window 1600x900",                   // 2
                "Window 1280x720",                   // 3
                "Window 1920x1080",                  // 4
                "Window 2560x1440",                  // 5
                "Window 3840x2160"                   // 6
        };

        // Pořadí ZOBRAZENÍ od nejmenšího po největší rozlišení.
        // Fullscreen varianty (0, 1) nemají pevnou velikost, proto jsou na konci.
        int[] displayOrder = {3, 2, 4, 5, 6, 0, 1};

        JRadioButton[] modeButtons = new JRadioButton[modeLabels.length]; // indexováno PŮVODNÍM indexem

        for (int displayRow = 0; displayRow < displayOrder.length; displayRow++) {
            int originalIndex = displayOrder[displayRow];

            JRadioButton mode = createStyledRadio(modeLabels[originalIndex], String.valueOf(originalIndex));
            mode.setBounds(
                    UI.toPercent(1, w),
                    UI.toPercent(6, h) + displayRow * UI.toPercent(6, h),
                    UI.toPercent(20, w),
                    UI.toPercent(5, h)
            );
            windowModePanel.add(mode);
            windowModeGroup.add(mode);
            modeButtons[originalIndex] = mode;
        }

        if (UIconfiguration.windowMode >= 0 && UIconfiguration.windowMode < modeButtons.length) {
            modeButtons[UIconfiguration.windowMode].setSelected(true);
        }

        add(windowModePanel);

        // upozornění, že režim okna se projeví až po restartu
        restartNoticeLabel = new JLabel(" ", SwingConstants.CENTER);
        restartNoticeLabel.setFont(new Font("SansSerif", Font.BOLD, UI.toPercent(1.4, h)));
        restartNoticeLabel.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(74, h),
                UI.toPercent(80, w),
                UI.toPercent(5, h)
        );
        restartNoticeLabel.setForeground(new Color(0xE0, 0x5A, 0x5A));
        add(restartNoticeLabel);

        // Hlasitost zvuků
        // --------------------------------------------------

        JPanel volumePanel = createSectionPanel(
                "Sound effects volume",
                UI.toPercent(10, w), UI.toPercent(82, h),
                UI.toPercent(80, w), UI.toPercent(13, h)
        );

        int currentVolumePercent = Math.round(UIconfiguration.soundEfectsVolume * 100);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, currentVolumePercent);
        volumeSlider.setBounds(
                UI.toPercent(2, w),
                UI.toPercent(4, h),
                UI.toPercent(76, w),
                UI.toPercent(8, h)
        );
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(TEXT_MAIN);
        volumeSlider.setFont(new Font("SansSerif", Font.PLAIN, UI.toPercent(1.1, h)));

        volumePanel.add(volumeSlider);
        add(volumePanel);

        // Save tlačítko
        // --------------------------------------------------

        JButton saveB = new JButton("Save");
        saveB.setBounds(
                UI.toCenter(20, w),
                UI.toPercent(60, h),
                UI.toPercent(20, w),
                UI.toPercent(9, h)
        );
        styleAccentButton(saveB);

        add(saveB);

        saveB.addActionListener(e -> {
            String boardSelected = boardColourGroup.getSelection() != null
                    ? boardColourGroup.getSelection().getActionCommand()
                    : null;

            String waterSelected = waterColourGroup.getSelection() != null
                    ? waterColourGroup.getSelection().getActionCommand()
                    : null;

            String windowModeSelected = windowModeGroup.getSelection() != null
                    ? windowModeGroup.getSelection().getActionCommand()
                    : null;

            int previousWindowMode = UIconfiguration.windowMode;

            if (boardSelected != null) {
                UIconfiguration.boardColours = Integer.parseInt(boardSelected);
            }

            if (waterSelected != null) {
                UIconfiguration.waterColours = Integer.parseInt(waterSelected);
            }

            if (windowModeSelected != null) {
                UIconfiguration.windowMode = Integer.parseInt(windowModeSelected);
            }

            UIconfiguration.soundEfectsVolume = volumeSlider.getValue() / 100f;

            UIconfiguration.saveSettings();
            UIconfiguration.loadSettings(); // přepočítá barvy podle nových indexů

            if (previousWindowMode != UIconfiguration.windowMode) {
                restartNoticeLabel.setText("Nastavení okna se projeví až po restartu hry.");
            } else {
                restartNoticeLabel.setText(" ");
            }

            System.out.println("Settings saved");
        });

        // ESC → MENU / LOOP
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "backTo");

        getActionMap().put("backTo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (openedInLoop) {
                    frame.showScene("LOOP");
                } else {
                    frame.showScene("MENU");
                }
            }
        });
    }

    // ------------------------------------------------------------
    // Pomocné metody pro jednotný vzhled
    // ------------------------------------------------------------

    private JPanel createSectionPanel(String title, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(BG_PANEL);
        panel.setBounds(x, y, width, height);

        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COL, 1, true),
                title
        );
        border.setTitleColor(TEXT_MUTED);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, UI.toPercent(1.3, getFrameHeight())));
        panel.setBorder(border);

        return panel;
    }

    private JRadioButton createStyledRadio(String text, String actionCommand) {
        JRadioButton radio = new JRadioButton(text);
        radio.setActionCommand(actionCommand);
        radio.setOpaque(false);
        radio.setForeground(TEXT_MAIN);
        radio.setFont(new Font("SansSerif", Font.PLAIN, UI.toPercent(1.3, getFrameHeight())));
        radio.setFocusPainted(false);
        return radio;
    }

    private void styleAccentButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, UI.toPercent(2, getFrameHeight())));
        button.setBackground(ACCENT);
        button.setForeground(new Color(0x1A, 0x1C, 0x25));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(ACCENT_DARK, 2, true));
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.getModel().addChangeListener(e -> {
            if (button.getModel().isRollover()) {
                button.setBackground(ACCENT_DARK);
            } else {
                button.setBackground(ACCENT);
            }
        });
    }

    private int getFrameHeight() {
        return frame != null ? frame.getHeight() : 1080;
    }
}