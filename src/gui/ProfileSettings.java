package gui;

import profile.PlayerManager;
import logic.Player;

import javax.swing.*;
import java.awt.*;

public class ProfileSettings extends JPanel {

    private MainFrame frame;
    private JTextField nameField;
    private JLabel statusLabel;

    public ProfileSettings(MainFrame frame) {
        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Nastavení profilu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, gbc);

        gbc.gridy++;
        JLabel nameLabel = new JLabel("Jméno hráče:");
        add(nameLabel, gbc);

        Player currentPlayer = PlayerManager.getCurrentHumanPlayer();

        gbc.gridy++;
        nameField = new JTextField(currentPlayer.getName(), 20);
        add(nameField, gbc);

        gbc.gridy++;
        JButton saveButton = new JButton("Uložit");
        saveButton.addActionListener(e -> saveName());
        add(saveButton, gbc);

        gbc.gridy++;
        statusLabel = new JLabel(" ");
        add(statusLabel, gbc);

        gbc.gridy++;
        JButton backButton = new JButton("Zpět do menu");
        backButton.addActionListener(e -> frame.showScene("MENU"));
        add(backButton, gbc);

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
}