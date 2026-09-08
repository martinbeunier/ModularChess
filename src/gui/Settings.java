package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class Settings extends JPanel {


    public static boolean openedInLoop;
    private MainFrame frame;
    private ButtonGroup boardColourGroup;
    private ButtonGroup waterColourGroup;
    private JSlider volumeSlider;

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
                UI.toPercent(15, w),
                UI.toPercent(60, h),
                UI.toPercent(30, w),
                UI.toPercent(15, h)
        );

        boardColourGroup = new ButtonGroup();

        JRadioButton dark = new JRadioButton("dark");
        dark.setBounds(0, 0, UI.toPercent(15, w), UI.toPercent(3, h));
        dark.setActionCommand("0");

        JRadioButton green = new JRadioButton("green");
        green.setBounds(0, UI.toPercent(3, h), UI.toPercent(15, w), UI.toPercent(3, h));
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

        // Hlasitost zvuků
        // --------------------------------------------------

        // JSlider pracuje s int, takže 0.0-1.0 mapujeme na 0-100
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
                UI.toPercent(20, h),
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

            if (boardSelected != null) {
                UIconfiguration.boardColours = Integer.parseInt(boardSelected);
            }

            if (waterSelected != null) {
                UIconfiguration.waterColours = Integer.parseInt(waterSelected);
            }

            UIconfiguration.soundEfectsVolume = volumeSlider.getValue() / 100f;

            UIconfiguration.saveSettings();
            UIconfiguration.loadSettings(); // přepočítá barvy podle nových indexů
            System.out.println("Settings saved");
        });

        // ESC → MENU
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "backTo");

        getActionMap().put("backTo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(openedInLoop){
                    frame.showScene("LOOP");
                }else
                {
                    frame.showScene("MENU");
                }

            }
        });
    }
}