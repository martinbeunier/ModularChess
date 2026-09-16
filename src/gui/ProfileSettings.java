package gui;

import profile.PlayerManager;
import logic.Player;

import javax.swing.*;
import java.awt.*;

public class ProfileSettings extends JPanel {

    private MainFrame frame;
    private JTextField nameField;
    private JTextField cheatField;
    private JLabel statusLabel;

    public ProfileSettings(MainFrame frame) {
        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);

        JButton backButton = new JButton("Zpět do menu");
        backButton.addActionListener(e -> frame.showScene("MENU"));
        backButton.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(10, h),
                UI.toPercent(20, w),
                UI.toPercent(20, h)
        );
        add(backButton);

        nameInputbox(w, h);
        cheatInputbox(w, h);
        escape();
    }

    private void saveName() {
        String newName = nameField.getText().trim();

        if (newName.isEmpty()) {
            statusLabel.setText("Jméno nesmí být prázdné.");
            return;
        }

        if (newName.contains(";")) {
            statusLabel.setText("Jméno nesmí obsahovat ';'.");
            return;
        }

        PlayerManager.setName(newName);
        statusLabel.setText("Jméno bylo uloženo.");
    }

    private void nameInputbox(int w, int h) {

        JLabel titleLabel = new JLabel("Nastavení profilu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(25, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(titleLabel);

        JLabel nameLabel = new JLabel("Jméno hráče:");
        nameLabel.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(35, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(nameLabel);

        Player currentPlayer = PlayerManager.getCurrentHumanPlayer();

        nameField = new JTextField(currentPlayer.getName(), 20);
        nameField.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(40, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(nameField);

        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> saveName());
        saveButton.setBounds(
                UI.toPercent(45, w),
                UI.toPercent(47, h),
                UI.toPercent(10, w),
                UI.toPercent(5, h)
        );
        add(saveButton);

        statusLabel = new JLabel(" ");
        statusLabel.setBounds(
                UI.toPercent(40, w),
                UI.toPercent(54, h),
                UI.toPercent(20, w),
                UI.toPercent(30, h)
        );
        add(statusLabel);
    }

    private void cheatInputbox(int w, int h) {

        JLabel cheatLabel = new JLabel("Cheat code:");
        cheatLabel.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(65, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(cheatLabel);

        JButton cheatButton = new JButton("Aktivovat cheat");
        cheatButton.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(70, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );



        add(cheatButton);

        cheatField = new JTextField();
        cheatField.setBounds(
                UI.toPercent(10, w),
                UI.toPercent(40, h),
                UI.toPercent(20, w),
                UI.toPercent(5, h)
        );
        add(cheatField);

        cheatButton.addActionListener(e -> {
            if(cheatField.getText().equals("174 bpm")) {

                PlayerManager.cheatcodeActivated = true;
                System.out.println("Cheat code Activated");

                statusLabel.setText(
                        "<html>" +
                                "Cheat code is activated.<br>" +
                                "Your progress will not be written.<br>" +
                                "You have unlocked all maps.<br>" +
                                "Cheat code will be reseted after turning off the game." +
                                "</html>"
                );
            }
        });
    }

    private void escape() {

        getInputMap(
                WHEN_IN_FOCUSED_WINDOW
        ).put(
                KeyStroke.getKeyStroke("ESCAPE"),
                "backTo"
        );

        getActionMap().put(
                "backTo",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        frame.showScene("MENU");
                    }
                }
        );
    }
}