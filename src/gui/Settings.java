package gui;

import javax.swing.*;
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

    public Settings(MainFrame frame) {
        this.frame = frame;

        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        int w = frame.getWidth();
        int h = frame.getHeight();

        // Výběr barvy šachovnice
        // --------------------------------------------------

        JPanel boardRadioPanel = new JPanel();
        boardRadioPanel.setLayout(null);

        boardRadioPanel.setBounds(
                UI.toPercent(80, w),
                UI.toPercent(60, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );

        boardColourGroup = new ButtonGroup();

        JRadioButton dark = new JRadioButton("dark");
        dark.setBounds(20, 0, UI.toPercent(15, w), UI.toPercent(3, h));
        dark.setActionCommand("0");

        JRadioButton green = new JRadioButton("green");
        green.setBounds(20, UI.toPercent(3, h), UI.toPercent(15, w), UI.toPercent(3, h));
        green.setActionCommand("1");

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

        JPanel waterRadioPanel = new JPanel();
        waterRadioPanel.setLayout(null);

        waterRadioPanel.setBounds(
                UI.toPercent(55, w),
                UI.toPercent(60, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );

        waterColourGroup = new ButtonGroup();

        JRadioButton waterClassic = new JRadioButton("classic");
        waterClassic.setBounds(0, 0, UI.toPercent(15, w), UI.toPercent(3, h));
        waterClassic.setActionCommand("0");

        waterRadioPanel.add(waterClassic);

        waterColourGroup.add(waterClassic);

        if (UIconfiguration.waterColours == 0) {
            waterClassic.setSelected(true);
        }

        add(waterRadioPanel);

        // Režim okna / rozlišení
        // --------------------------------------------------

        JPanel windowModePanel = new JPanel();
        windowModePanel.setLayout(null);

        windowModePanel.setBounds(
                UI.toPercent(15, w),
                UI.toPercent(20, h),
                UI.toPercent(15, w),
                UI.toPercent(45, h)
        );

        windowModeGroup = new ButtonGroup();

        JRadioButton mode0 = new JRadioButton("Okno na celou obrazovku (s okraji)");
        mode0.setBounds(0, 0, UI.toPercent(15, w), UI.toPercent(6, h));
        mode0.setActionCommand("0");

        JRadioButton mode1 = new JRadioButton("Fullscreen (bez okrajů)");
        mode1.setBounds(0, UI.toPercent(6, h), UI.toPercent(15, w), UI.toPercent(6, h));
        mode1.setActionCommand("1");

        JRadioButton mode2 = new JRadioButton("Okno 1600x900");
        mode2.setBounds(0, UI.toPercent(12, h), UI.toPercent(15, w), UI.toPercent(6, h));
        mode2.setActionCommand("2");

        JRadioButton mode3 = new JRadioButton("Okno 1280x720");
        mode3.setBounds(0, UI.toPercent(18, h), UI.toPercent(15, w), UI.toPercent(6, h));
        mode3.setActionCommand("3");

        JRadioButton mode4 = new JRadioButton("Okno 1920×1080");
        mode4.setBounds(0, UI.toPercent(24, h), UI.toPercent(15, w), UI.toPercent(6, h));
        mode4.setActionCommand("4");

        JRadioButton mode5 = new JRadioButton("Okno 2560x1440");
        mode5.setBounds(0, UI.toPercent(30, h), UI.toPercent(15, w), UI.toPercent(6, h));
        mode5.setActionCommand("5");

        JRadioButton mode6 = new JRadioButton("Okno 3840×2160");
        mode6.setBounds(0, UI.toPercent(36, h), UI.toPercent(15, w), UI.toPercent(6, h));
        mode6.setActionCommand("6");

        windowModePanel.add(mode0);
        windowModePanel.add(mode1);
        windowModePanel.add(mode2);
        windowModePanel.add(mode3);
        windowModePanel.add(mode4);
        windowModePanel.add(mode5);
        windowModePanel.add(mode6);


        windowModeGroup.add(mode0);
        windowModeGroup.add(mode1);
        windowModeGroup.add(mode2);
        windowModeGroup.add(mode3);
        windowModeGroup.add(mode4);
        windowModeGroup.add(mode5);
        windowModeGroup.add(mode6);

        switch (UIconfiguration.windowMode) {
            case 0: mode0.setSelected(true); break;
            case 1: mode1.setSelected(true); break;
            case 2: mode2.setSelected(true); break;
            case 3: mode3.setSelected(true); break;
            case 4: mode4.setSelected(true); break;
            case 5: mode5.setSelected(true); break;
            case 6: mode6.setSelected(true); break;
        }

        add(windowModePanel);

        // upozornění, že režim okna se projeví až po restartu
        restartNoticeLabel = new JLabel(" ");
        restartNoticeLabel.setBounds(
                UI.toPercent(15, w),
                UI.toPercent(50, h),
                UI.toPercent(20, w),
                UI.toPercent(6, h)
        );
        restartNoticeLabel.setForeground(Color.RED);
        add(restartNoticeLabel);

        // Hlasitost zvuků
        // --------------------------------------------------

        int currentVolumePercent = Math.round(UIconfiguration.soundEfectsVolume * 100);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, currentVolumePercent);
        volumeSlider.setBounds(
                UI.toPercent(15, w),
                UI.toPercent(80, h),
                UI.toPercent(70, w),
                UI.toPercent(8, h)
        );
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        add(volumeSlider);

        // Save tlačítko
        // --------------------------------------------------

        JButton saveB = new JButton("save");
        saveB.setBounds(
                UI.toCenter(20, w),
                UI.toPercent(30, h),
                UI.toPercent(20, w),
                UI.toPercent(10, h)
        );

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
}