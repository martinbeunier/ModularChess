package gui;

import profile.PlayerManager;
import profile.PlayerManager.HistoryEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameHistory extends JPanel {

    private MainFrame frame;

    // Uprav podle skutečné cesty, kam GameLoop.getGameHistoryFilePath() ukládá záznamy jednotlivých partií.
    private static final String GAME_HISTORY_DIR = "src/files/gameHistory";

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton replayButton;

    private List<HistoryEntry> entries = new ArrayList<>();

    GameHistory(MainFrame frame) {
        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);
        setBackground(new Color(26, 28, 37));

        JLabel title = new JLabel("Historie her");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, UI.toPercent(4, h)));
        title.setBounds(UI.toPercent(5, w), UI.toPercent(5, h), UI.toPercent(40, w), UI.toPercent(8, h));
        add(title);

        JButton backButton = new JButton("Zpět do menu");
        backButton.addActionListener(e -> frame.showScene("MENU"));
        backButton.setBounds(UI.toPercent(5, w), UI.toPercent(88, h), UI.toPercent(15, w), UI.toPercent(7, h));
        add(backButton);

        replayButton = new JButton("Replay");
        replayButton.setEnabled(false);
        replayButton.setBounds(UI.toPercent(80, w), UI.toPercent(88, h), UI.toPercent(15, w), UI.toPercent(7, h));
        replayButton.addActionListener(e -> onReplayClicked());
        add(replayButton);

        String[] columns = {"Datum", "Mapa", "Soupeř", "Výsledek", "Lze přehrát"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(UI.toPercent(4, h));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateReplayButtonState());

        // Dvojklik na řádek = zkratka pro Replay (pokud je partie přehratelná)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onReplayClicked();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(UI.toPercent(5, w), UI.toPercent(15, h), UI.toPercent(90, w), UI.toPercent(70, h));
        add(scrollPane);

        loadHistory();
    }
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadHistory();
        }
    }

    /** Znovu načte obsah map_history.txt a naplní tabulku (nejnovější záznamy nahoře). */
    public void loadHistory() {
        entries = PlayerManager.getMapHistoryEntries();
        Collections.reverse(entries); // nejnovější první

        tableModel.setRowCount(0);

        for (HistoryEntry entry : entries) {
            boolean replayable = isReplayable(entry);

            tableModel.addRow(new Object[]{
                    formatTimestamp(entry.timestamp),
                    entry.mapName,
                    entry.opponent,
                    entry.result,
                    replayable ? "Ano" : "Ne"
            });
        }

        updateReplayButtonState();
    }

    /** Partie je přehratelná, pokud máme uložený název souboru a ten soubor/složka reálně existuje na disku. */
    private boolean isReplayable(HistoryEntry entry) {
        if (entry.historyFileName == null || entry.historyFileName.isBlank()) {
            return false;
        }
        File historyFile = new File(GAME_HISTORY_DIR, entry.historyFileName);
        return historyFile.exists();
    }

    private String formatTimestamp(String rawTimestamp) {
        try {
            LocalDateTime dt = LocalDateTime.parse(rawTimestamp);
            return dt.format(DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            return rawTimestamp; // fallback — zobrazí syrový formát, ale aspoň nic nespadne
        }
    }

    private void updateReplayButtonState() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= entries.size()) {
            replayButton.setEnabled(false);
            return;
        }
        replayButton.setEnabled(isReplayable(entries.get(selectedRow)));
    }

    private void onReplayClicked() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= entries.size()) return;

        HistoryEntry entry = entries.get(selectedRow);
        if (!isReplayable(entry)) return;

        openReplay(entry);
    }

    /**
     * TODO: zde se později napojí nový JPanel pro přehrávání konkrétní partie.
     * Zatím jen placeholder, ať je vidět, který záznam by se měl přehrát.
     */
    private void openReplay(HistoryEntry entry) {
        File historyFile = new File(GAME_HISTORY_DIR, entry.historyFileName);

        JOptionPane.showMessageDialog(
                this,
                "Replay zatím není implementován.\n\n" +
                        "Mapa: " + entry.mapName + "\n" +
                        "Soupeř: " + entry.opponent + "\n" +
                        "Výsledek: " + entry.result + "\n" +
                        "Soubor: " + historyFile.getAbsolutePath(),
                "Replay",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Až bude replay JPanel hotový, tady se bude předávat historyFile (nebo jeho obsah)
        // a spouštět nová scéna, např.:
        // frame.showScene("REPLAY", historyFile);
    }
}