package gui;

import logic.*;
import pieces.*;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;
import profile.PlayerManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.awt.datatransfer.StringSelection;
import java.time.format.DateTimeFormatter;

public class GamePlayer extends JPanel {

    private MainFrame frame;

    private List<ChessBoard> snapshots = new ArrayList<>();
    private List<Colour> moverColours = new ArrayList<>(); // moverColours.get(i) = kdo tahne PO snapshotu i (mover tahu i)
    private List<MoveEntry> moves = new ArrayList<>();
    private int currentSnapshotIndex = 0;
    private boolean boardFlipped = false;

    private JLabel titleLabel;
    private JLabel snapshotLabel;
    private JLabel playerInfoLabel;
    private JTable moveTable;
    private DefaultTableModel tableModel;
    private Integer[][] cellSnapshotIndex = new Integer[0][2];

    private static final double BOARD_LEFT_FRACTION = 0.04;
    private static final double BOARD_RIGHT_FRACTION = 0.62;
    private static final int MARGIN = 60;

    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private String pgnWhiteName = "White";
    private String pgnBlackName = "Black";
    private String pgnResultTag = "*";
    private String pgnDateTag = "????.??.??";


    public GamePlayer(MainFrame frame) {
        this.frame = frame;

        int w = frame.getWidth();
        int h = frame.getHeight();

        setLayout(null);
        setBackground(new Color(26, 28, 37));

        titleLabel = new JLabel("Přehrávání partie");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, UI.toPercent(2.2, h)));
        titleLabel.setBounds(UI.toPercent(64, w), UI.toPercent(69, h), UI.toPercent(32, w), UI.toPercent(4, h));
        add(titleLabel);

        snapshotLabel = new JLabel("");
        snapshotLabel.setForeground(Color.WHITE);
        snapshotLabel.setFont(new Font("SansSerif", Font.PLAIN, UI.toPercent(1.6, h)));
        snapshotLabel.setBounds(UI.toPercent(64, w), UI.toPercent(72, h), UI.toPercent(32, w), UI.toPercent(3, h));
        add(snapshotLabel);

        // ------------------------------------------------------------
        // Pravý panel: údaje o hráčích, seznam tahů, tlačítka
        // ------------------------------------------------------------

        playerInfoLabel = new JLabel("");
        playerInfoLabel.setForeground(Color.WHITE);
        playerInfoLabel.setVerticalAlignment(SwingConstants.TOP);
        playerInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, UI.toPercent(1.6, h)));
        playerInfoLabel.setBounds(UI.toPercent(64, w), UI.toPercent(3, h), UI.toPercent(32, w), UI.toPercent(10, h));
        add(playerInfoLabel);

        String[] columns = {"Bílý", "Černý"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moveTable = new JTable(tableModel);
        moveTable.setRowHeight(UI.toPercent(3.5, h));
        moveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        moveTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = moveTable.rowAtPoint(e.getPoint());
                int col = moveTable.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0 || row >= cellSnapshotIndex.length) return;

                Integer snapshotIndex = cellSnapshotIndex[row][col];
                if (snapshotIndex != null) {
                    currentSnapshotIndex = snapshotIndex;
                    refreshView();
                }
            }
        });

        JScrollPane moveScroll = new JScrollPane(moveTable);
        moveScroll.setBounds(UI.toPercent(64, w), UI.toPercent(14, h), UI.toPercent(32, w), UI.toPercent(55, h));
        add(moveScroll);

        JButton prevButton = new JButton("◀ Předchozí");
        prevButton.setBounds(UI.toPercent(64, w), UI.toPercent(76, h), UI.toPercent(15, w), UI.toPercent(7, h));
        prevButton.addActionListener(e -> goToPreviousSnapshot());
        add(prevButton);

        JButton nextButton = new JButton("Další ▶");
        nextButton.setBounds(UI.toPercent(81, w), UI.toPercent(76, h), UI.toPercent(15, w), UI.toPercent(7, h));
        nextButton.addActionListener(e -> goToNextSnapshot());
        add(nextButton);

        JButton flipButton = new JButton("Otočit desku");
        flipButton.setBounds(UI.toPercent(64, w), UI.toPercent(85, h), UI.toPercent(15, w), UI.toPercent(7, h));
        flipButton.addActionListener(e -> {
            boardFlipped = !boardFlipped;
            repaint();
        });
        add(flipButton);

        JButton backButton = new JButton("Zpět na historii");
        backButton.setBounds(UI.toPercent(81, w), UI.toPercent(85, h), UI.toPercent(15, w), UI.toPercent(7, h));
        backButton.addActionListener(e -> frame.showScene("GAMEHISTORY"));
        add(backButton);

        JButton pgnButton = new JButton("Generovat PGN");
        pgnButton.setBounds(UI.toPercent(64, w), UI.toPercent(96, h) - UI.toPercent(7, h), UI.toPercent(32, w), UI.toPercent(7, h));
        pgnButton.addActionListener(e -> showPgnDialog());
        add(pgnButton);


        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "prevSnapshot");
        getActionMap().put("prevSnapshot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToPreviousSnapshot();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "nextSnapshot");
        getActionMap().put("nextSnapshot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToNextSnapshot();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backToHistory");
        getActionMap().put("backToHistory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showScene("GAMEHISTORY");
            }
        });
    }

    /** Načte a rozparsuje soubor s historií partie, zobrazí úvodní snapshot. */
    public void loadFromFile(File historyFile) {
        snapshots.clear();
        moverColours.clear();
        moves.clear();
        imageCache.clear();
        currentSnapshotIndex = 0;
        boardFlipped = false;
        pgnResultTag = "*";
        pgnDateTag = "????.??.??";

        List<List<String>> blocks = splitIntoSnapshotBlocks(historyFile);

        for (List<String> block : blocks) {
            ChessBoard board = ChessBoard.loadPositionFromLines(block, new ArrayList<>());
            if (board != null) {
                snapshots.add(board);
                moverColours.add(extractCurrentPlayerColour(block));
            }
        }

        if (!snapshots.isEmpty()) {
            for (Player p : snapshots.get(0).getPlayers()) {
                if (p.getColor() == Colour.White) pgnWhiteName = p.getName();
                if (p.getColor() == Colour.Black) pgnBlackName = p.getName();
            }
        }

        computeAllMoves();
        buildMoveTable();
        refreshView();
    }


    /** Volitelné obohacení PGN hlavičky o výsledek a datum, pokud je známý HistoryEntry. */
    /** Volitelné obohacení PGN hlavičky o výsledek a datum, pokud je známý HistoryEntry. */
    public void applyHistoryMetadata(String resultText, String rawTimestamp) {
        if (resultText != null) {
            String humanName = PlayerManager.getCurrentHumanPlayer().getName();
            Colour humanColour = null;
            for (int i = 0; i < Math.min(snapshots.size(), 1); i++) {
                for (Player p : snapshots.get(i).getPlayers()) {
                    if (p.getName().equals(humanName)) humanColour = p.getColor();
                }
            }

            if (resultText.contains("DRAW")) {
                pgnResultTag = "1/2-1/2";
            } else if (humanColour != null && resultText.contains("WIN")) {
                pgnResultTag = (humanColour == Colour.White) ? "1-0" : "0-1";
            } else if (humanColour != null && resultText.contains("LOSS")) {
                pgnResultTag = (humanColour == Colour.White) ? "0-1" : "1-0";
            }
        }

        if (rawTimestamp != null) {
            try {
                java.time.LocalDateTime dt = java.time.LocalDateTime.parse(rawTimestamp);
                pgnDateTag = dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            } catch (Exception ignored) {
            }
        }
    }

    // ================================================================
    // PARSOVÁNÍ SOUBORU NA JEDNOTLIVÉ SNAPSHOTY
    // ================================================================
    private List<List<String>> splitIntoSnapshotBlocks(File file) {
        List<List<String>> blocks = new ArrayList<>();
        List<String> currentBlock = null;
        boolean inResultBlock = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();

                if (trimmed.startsWith("GAME HISTORY SNAPSHOT")) {
                    currentBlock = new ArrayList<>();
                    blocks.add(currentBlock);
                    inResultBlock = false;
                    continue;
                }
                if (trimmed.startsWith("GAME RESULT")) {
                    inResultBlock = true;
                    currentBlock = null;
                    continue;
                }
                if (trimmed.equals("========================================")) {
                    continue;
                }
                if (!inResultBlock && currentBlock != null) {
                    currentBlock.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return blocks;
    }

    /** Vytáhne barvu z řádku "current player : X" daného snapshotu — kdo tahne PO tomto snapshotu. */
    private Colour extractCurrentPlayerColour(List<String> block) {
        for (String rawLine : block) {
            String line = rawLine.trim();
            if (line.startsWith("current player")) {
                try {
                    return Colour.valueOf(line.split(" : ")[1].trim());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    // ================================================================
    // VÝPOČET TAHŮ (porovnání sousedních snapshotů)
    // ================================================================

    private static class PieceSnapshot {
        String className;
        String colour;
        int x, y;
        Integer rotation;
    }

    private static class MoveEntry {
        String description;
        Integer fromX, fromY, toX, toY; // pro zvýraznění na desce; null = nezvýrazňovat
    }

    private void computeAllMoves() {
        moves.clear();
        for (int i = 1; i < snapshots.size(); i++) {
            moves.add(computeMove(snapshots.get(i - 1), snapshots.get(i)));
        }
    }

    private List<PieceSnapshot> extractPieces(ChessBoard board) {
        List<PieceSnapshot> list = new ArrayList<>();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Piece p = board.getPiece(x, y);
                if (p == null) continue;

                PieceSnapshot ps = new PieceSnapshot();
                ps.className = p.getClass().getSimpleName();
                ps.colour = p.getColour().name();
                ps.x = x;
                ps.y = y;
                ps.rotation = (p instanceof OrientedPiece) ? ((OrientedPiece) p).getRotation() : null;
                list.add(ps);
            }
        }
        return list;
    }

    private MoveEntry computeMove(ChessBoard prev, ChessBoard curr) {
        List<PieceSnapshot> prevRemaining = extractPieces(prev);
        List<PieceSnapshot> currRemaining = extractPieces(curr);

        for (Iterator<PieceSnapshot> itPrev = prevRemaining.iterator(); itPrev.hasNext();) {
            PieceSnapshot p = itPrev.next();
            Iterator<PieceSnapshot> itCurr = currRemaining.iterator();
            boolean matched = false;
            while (itCurr.hasNext()) {
                PieceSnapshot c = itCurr.next();
                if (isSameState(p, c)) {
                    itCurr.remove();
                    matched = true;
                    break;
                }
            }
            if (matched) itPrev.remove();
        }

        Map<String, List<PieceSnapshot>> prevGroups = groupByClassColour(prevRemaining);
        Map<String, List<PieceSnapshot>> currGroups = groupByClassColour(currRemaining);

        for (String key : new ArrayList<>(prevGroups.keySet())) {
            List<PieceSnapshot> prevList = prevGroups.get(key);
            List<PieceSnapshot> currList = currGroups.get(key);

            if (prevList.size() == 1 && currList != null && currList.size() == 1) {
                PieceSnapshot from = prevList.get(0);
                PieceSnapshot to = currList.get(0);

                prevGroups.remove(key);
                currGroups.remove(key);

                String capturedDesc = findCaptureAt(prevGroups, to.x, to.y);
                return buildMoveDescription(from, to, capturedDesc);
            }
        }

        PieceSnapshot promFrom = null, promTo = null;
        outer:
        for (List<PieceSnapshot> prevList : prevGroups.values()) {
            for (PieceSnapshot from : prevList) {
                for (List<PieceSnapshot> currList : currGroups.values()) {
                    for (PieceSnapshot to : currList) {
                        if (from.colour.equals(to.colour) && !from.className.equals(to.className)) {
                            promFrom = from;
                            promTo = to;
                            break outer;
                        }
                    }
                }
            }
        }

        if (promFrom != null) {
            MoveEntry entry = new MoveEntry();
            entry.fromX = promFrom.x;
            entry.fromY = promFrom.y;
            entry.toX = promTo.x;
            entry.toY = promTo.y;
            entry.description = promFrom.className + " " + promFrom.x + "," + promFrom.y +
                    " -> " + promTo.className + " " + promTo.x + "," + promTo.y + " (povýšení)";
            return entry;
        }

        MoveEntry fallback = new MoveEntry();
        fallback.description = "Více změn na desce";
        return fallback;
    }

    private boolean isSameState(PieceSnapshot a, PieceSnapshot b) {
        return a.className.equals(b.className)
                && a.colour.equals(b.colour)
                && a.x == b.x
                && a.y == b.y
                && Objects.equals(a.rotation, b.rotation);
    }

    private Map<String, List<PieceSnapshot>> groupByClassColour(List<PieceSnapshot> list) {
        Map<String, List<PieceSnapshot>> map = new LinkedHashMap<>();
        for (PieceSnapshot p : list) {
            String key = p.className + "|" + p.colour;
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }
        return map;
    }

    private String findCaptureAt(Map<String, List<PieceSnapshot>> prevGroups, int x, int y) {
        for (List<PieceSnapshot> list : prevGroups.values()) {
            Iterator<PieceSnapshot> it = list.iterator();
            while (it.hasNext()) {
                PieceSnapshot p = it.next();
                if (p.x == x && p.y == y) {
                    it.remove();
                    return p.className;
                }
            }
        }
        return null;
    }

    private MoveEntry buildMoveDescription(PieceSnapshot from, PieceSnapshot to, String capturedDesc) {
        MoveEntry entry = new MoveEntry();
        entry.fromX = from.x;
        entry.fromY = from.y;
        entry.toX = to.x;
        entry.toY = to.y;

        StringBuilder sb = new StringBuilder();
        sb.append(from.className).append(" ");

        if (from.x == to.x && from.y == to.y) {
            sb.append(from.x).append(",").append(from.y).append(" -> r").append(to.rotation);
        } else {
            sb.append(from.x).append(",").append(from.y)
                    .append(" -> ").append(to.x).append(",").append(to.y);
        }

        if (capturedDesc != null) {
            sb.append(" (brání: ").append(capturedDesc).append(")");
        }

        entry.description = sb.toString();
        return entry;
    }

    // ================================================================
    // TABULKA TAHŮ — sloupec určuje SKUTEČNÝ mover (z "current player"), ne parita indexu
    // ================================================================

    private void buildMoveTable() {
        tableModel.setRowCount(0);
        List<Integer[]> rowSnapshotIndices = new ArrayList<>();

        // Řádek 0: inicializační pozice — chyběla v původní verzi
        tableModel.addRow(new Object[]{"Počáteční pozice", ""});
        rowSnapshotIndices.add(new Integer[]{0, null});

        Integer[] pendingRow = null;
        String pendingWhiteText = null;
        String pendingBlackText = null;

        for (int i = 0; i < moves.size(); i++) {
            MoveEntry move = moves.get(i);
            int snapshotIndex = i + 1; // tah i vytváří snapshot i+1
            int snapshotNumber = snapshotIndex + 1; // odpovídá "GAME HISTORY SNAPSHOT N"
            String text = snapshotNumber + ": " + move.description;

            Colour mover = (i < moverColours.size()) ? moverColours.get(i) : null;

            if (mover == Colour.White) {
                if (pendingRow != null && pendingRow[0] != null) {
                    tableModel.addRow(new Object[]{nz(pendingWhiteText), nz(pendingBlackText)});
                    rowSnapshotIndices.add(pendingRow);
                    pendingRow = null;
                }
                if (pendingRow == null) pendingRow = new Integer[]{null, null};
                pendingRow[0] = snapshotIndex;
                pendingWhiteText = text;
                pendingBlackText = (pendingBlackText != null && pendingRow[1] == null) ? null : pendingBlackText;

            } else if (mover == Colour.Black) {
                if (pendingRow != null && pendingRow[1] != null) {
                    tableModel.addRow(new Object[]{nz(pendingWhiteText), nz(pendingBlackText)});
                    rowSnapshotIndices.add(pendingRow);
                    pendingRow = null;
                    pendingWhiteText = null;
                }
                if (pendingRow == null) pendingRow = new Integer[]{null, null};
                pendingRow[1] = snapshotIndex;
                pendingBlackText = text;

            } else {
                // Neznámý mover (chybí/nerozpoznaný "current player") — samostatný řádek
                if (pendingRow != null) {
                    tableModel.addRow(new Object[]{nz(pendingWhiteText), nz(pendingBlackText)});
                    rowSnapshotIndices.add(pendingRow);
                    pendingRow = null;
                    pendingWhiteText = null;
                    pendingBlackText = null;
                }
                tableModel.addRow(new Object[]{text, ""});
                rowSnapshotIndices.add(new Integer[]{snapshotIndex, null});
            }
        }

        if (pendingRow != null) {
            tableModel.addRow(new Object[]{nz(pendingWhiteText), nz(pendingBlackText)});
            rowSnapshotIndices.add(pendingRow);
        }

        cellSnapshotIndex = new Integer[rowSnapshotIndices.size()][2];
        for (int r = 0; r < rowSnapshotIndices.size(); r++) {
            cellSnapshotIndex[r][0] = rowSnapshotIndices.get(r)[0];
            cellSnapshotIndex[r][1] = rowSnapshotIndices.get(r)[1];
        }
    }

    private String nz(String s) {
        return (s == null) ? "" : s;
    }

    // ================================================================
    // NAVIGACE
    // ================================================================

    private void goToPreviousSnapshot() {
        if (currentSnapshotIndex > 0) {
            currentSnapshotIndex--;
            refreshView();
        }
    }

    private void goToNextSnapshot() {
        if (currentSnapshotIndex < snapshots.size() - 1) {
            currentSnapshotIndex++;
            refreshView();
        }
    }

    private void refreshView() {
        updateSnapshotLabel();
        updatePlayerInfo();
        repaint();
    }

    private void updateSnapshotLabel() {
        if (snapshots.isEmpty()) {
            snapshotLabel.setText("Žádná data k zobrazení.");
        } else {
            snapshotLabel.setText("Pozice " + (currentSnapshotIndex + 1) + " / " + snapshots.size());
        }
    }

    private void updatePlayerInfo() {
        if (snapshots.isEmpty()) {
            playerInfoLabel.setText("");
            return;
        }

        ChessBoard board = snapshots.get(currentSnapshotIndex);
        StringBuilder sb = new StringBuilder("<html>");

        for (Player player : board.getPlayers()) {
            String colourLabel = (player.getColor() == Colour.White) ? "Bílý" : "Černý";
            sb.append("<b>").append(colourLabel).append(":</b> ")
                    .append(player.getName())
                    .append(" (").append(player.getElo()).append(")<br>");
        }

        sb.append("</html>");
        playerInfoLabel.setText(sb.toString());
    }

    // ================================================================
    // VYKRESLENÍ DESKY (s podporou otočení)
    // ================================================================

    private int getTileSize() {
        if (snapshots.isEmpty()) return 1;
        ChessBoard board = snapshots.get(currentSnapshotIndex);
        if (board.getWidth() == 0 || board.getHeight() == 0) return 1;

        int bandWidth = (int) (getWidth() * (BOARD_RIGHT_FRACTION - BOARD_LEFT_FRACTION));
        int availableWidth = Math.max(1, bandWidth - MARGIN);
        int availableHeight = Math.max(1, getHeight() - MARGIN);

        return Math.min(availableWidth / board.getWidth(), availableHeight / board.getHeight());
    }

    private int getOffsetX(int tileSize) {
        if (snapshots.isEmpty()) return 0;
        ChessBoard board = snapshots.get(currentSnapshotIndex);

        int boardWidth = board.getWidth() * tileSize;
        int bandLeft = (int) (getWidth() * BOARD_LEFT_FRACTION);
        int bandWidth = (int) (getWidth() * (BOARD_RIGHT_FRACTION - BOARD_LEFT_FRACTION));

        return bandLeft + (bandWidth - boardWidth) / 2;
    }

    private int getOffsetY(int tileSize) {
        if (snapshots.isEmpty()) return 0;
        ChessBoard board = snapshots.get(currentSnapshotIndex);
        return (getHeight() - (board.getHeight() * tileSize)) / 2;
    }

    private int displayX(int x, int cols) {
        return boardFlipped ? (cols - 1 - x) : x;
    }

    private int displayY(int y, int rows) {
        return boardFlipped ? (rows - 1 - y) : y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (snapshots.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ChessBoard board = snapshots.get(currentSnapshotIndex);
        int cols = board.getWidth();
        int rows = board.getHeight();
        if (cols == 0 || rows == 0) return;

        int tileSize = getTileSize();
        int offsetX = getOffsetX(tileSize);
        int offsetY = getOffsetY(tileSize);

        Tile[][] tiles = board.getTiles();

        MoveEntry currentMove = (currentSnapshotIndex > 0 && currentSnapshotIndex - 1 < moves.size())
                ? moves.get(currentSnapshotIndex - 1)
                : null;

        // 1. Dlaždice
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int screenX = displayX(x, cols);
                int screenY = displayY(y, rows);
                int posX = offsetX + screenX * tileSize;
                int posY = offsetY + screenY * tileSize;

                Tile tile = null;
                if (tiles != null && x < tiles.length && tiles[x] != null && y < tiles[x].length) {
                    tile = tiles[x][y];
                }

                if ((x + y) % 2 == 0) {
                    g2d.setColor(UIconfiguration.boardColorLight);
                } else {
                    g2d.setColor(UIconfiguration.boardColorDark);
                }

                if (tile != null && tile.getWater()) {
                    if ((x + y) % 2 == 0) {
                        g2d.setColor(UIconfiguration.waterColorLight);
                    } else {
                        g2d.setColor(UIconfiguration.waterColorDark);
                    }
                }

                g2d.fillRect(posX, posY, tileSize, tileSize);

                if (currentMove != null && currentMove.fromX != null) {
                    if ((x == currentMove.fromX && y == currentMove.fromY)
                            || (x == currentMove.toX && y == currentMove.toY)) {
                        g2d.setColor(new Color(255, 255, 0, 80));
                        g2d.fillRect(posX, posY, tileSize, tileSize);
                    }
                }

                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.drawRect(posX, posY, tileSize, tileSize);
            }
        }

        // 2. Figurky
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece == null) continue;

                int screenX = displayX(x, cols);
                int screenY = displayY(y, rows);
                int posX = offsetX + screenX * tileSize;
                int posY = offsetY + screenY * tileSize;

                drawPieceAt(g2d, piece, posX, posY, tileSize);
            }
        }

        // 3. Souřadnice
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(10, tileSize / 4)));
        FontMetrics fm = g2d.getFontMetrics();

        for (int x = 0; x < cols; x++) {
            String label = String.valueOf(x);
            int textWidth = fm.stringWidth(label);
            int screenX = displayX(x, cols);
            int textX = offsetX + screenX * tileSize + (tileSize - textWidth) / 2;
            g2d.drawString(label, textX, offsetY - 6);
            g2d.drawString(label, textX, offsetY + rows * tileSize + fm.getAscent() + 2);
        }

        for (int y = 0; y < rows; y++) {
            String label = String.valueOf(y);
            int screenY = displayY(y, rows);
            int textY = offsetY + screenY * tileSize + (tileSize + fm.getAscent()) / 2 - 2;
            g2d.drawString(label, offsetX - fm.stringWidth(label) - 8, textY);
            g2d.drawString(label, offsetX + cols * tileSize + 8, textY);
        }
    }

    private void drawPieceAt(Graphics2D g2d, Piece piece, int posX, int posY, int tileSize) {
        boolean svgDrawn = false;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        String colorPrefix = "";
        switch (piece.getColour()) {
            case Colour.White : colorPrefix = "white"; break;
            case Colour.Black : colorPrefix = "black"; break;
        }

        String pieceType = piece.getClass().getSimpleName().toLowerCase();
        String imagePath = "src/files/images/skins/" + colorPrefix + pieceType + ".svg";

        double scale = PieceVisuals.getScaleByClass(pieceType);
        int svgSize = (int) Math.round(tileSize * scale);

        String cacheKey = imagePath + "_" + svgSize;
        BufferedImage cachedImg = imageCache.get(cacheKey);

        if (cachedImg == null && svgSize > 0) {
            try {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    SVGLoader loader = new SVGLoader();
                    SVGDocument svgDocument = loader.load(imgFile.toURI().toURL());

                    if (svgDocument != null) {
                        int renderSize = svgSize * 2;
                        BufferedImage newImg = new BufferedImage(renderSize, renderSize, BufferedImage.TYPE_INT_ARGB);

                        Graphics2D gImg = newImg.createGraphics();
                        gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        gImg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        gImg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                        svgDocument.render(null, gImg, new ViewBox(0, 0, renderSize, renderSize));
                        gImg.dispose();

                        cachedImg = newImg;
                        imageCache.put(cacheKey, cachedImg);
                    }
                }
            } catch (Exception e) {
                cachedImg = null;
            }
        }

        if (cachedImg != null) {
            int svgX = posX - (svgSize - tileSize) / 2;
            int svgY = posY - (svgSize - tileSize) / 2;
            g2d.drawImage(cachedImg, svgX, svgY, svgSize, svgSize, null);
            svgDrawn = true;
        }

        if (!svgDrawn) {
            int padding = tileSize / 8;
            int drawX = posX + padding;
            int drawY = posY + padding;
            int drawSize = tileSize - 2 * padding;

            g2d.setColor(piece.getColour() == Colour.White ? Color.WHITE : Color.BLACK);
            g2d.fillOval(drawX, drawY, drawSize, drawSize);
            g2d.setColor(Color.GRAY);
            g2d.drawOval(drawX, drawY, drawSize, drawSize);
        }

        if (piece instanceof OrientedPiece) {
            OrientedPiece orientedPiece = (OrientedPiece) piece;
            int rotation = orientedPiece.getRotation();

            int centerX = posX + tileSize / 2;
            int centerY = posY + tileSize / 2;
            int pointerLength = (tileSize - (tileSize / 3)) / 2;

            double angleRad = Math.toRadians((rotation * 90) - 90);
            if (boardFlipped) {
                angleRad += Math.PI;
            }

            int targetX = centerX + (int) (Math.cos(angleRad) * pointerLength);
            int targetY = centerY + (int) (Math.sin(angleRad) * pointerLength);

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(centerX, centerY, targetX, targetY);
            g2d.setStroke(new BasicStroke(1));
        }

        if (!svgDrawn) {
            String className = piece.getClass().getSimpleName();
            String symbol = (className.length() >= 4) ? className.substring(0, 4).toUpperCase() : className;

            g2d.setColor(piece.getColour() == Colour.White ? Color.BLACK : Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, tileSize / 4));

            FontMetrics fm = g2d.getFontMetrics();
            int textX = posX + (tileSize - fm.stringWidth(symbol)) / 2;
            int textY = posY + (tileSize + fm.getAscent() - fm.getDescent()) / 2;

            g2d.drawString(symbol, textX, textY);
        }
    }
    // ================================================================
// GENEROVÁNÍ PGN (jen standardní figurky: Pawn, Knight, Bishop, Rook, Queen, King)
// ================================================================

    private void showPgnDialog() {
        String pgn = generatePgnText();

        JTextArea textArea = new JTextArea(pgn);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(500, 400));

        JButton copyButton = new JButton("Kopírovat");
        copyButton.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(pgn), null);
            copyButton.setText("Zkopírováno!");
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(copyButton, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "PGN partie", JOptionPane.PLAIN_MESSAGE);
    }

    private String generatePgnText() {
        if (snapshots.size() < 1) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("[Event \"ModularChess partie\"]\n");
        sb.append("[Site \"?\"]\n");
        sb.append("[Date \"").append(pgnDateTag).append("\"]\n");
        sb.append("[White \"").append(pgnWhiteName).append("\"]\n");
        sb.append("[Black \"").append(pgnBlackName).append("\"]\n");
        sb.append("[Result \"").append(pgnResultTag).append("\"]\n\n");

        StringBuilder movetext = new StringBuilder();
        int moveNumber = 1;
        boolean waitingForBlack = false;

        for (int i = 1; i < snapshots.size(); i++) {
            Colour mover = (i - 1 < moverColours.size()) ? moverColours.get(i - 1) : null;
            if (mover == null) continue;

            ChessBoard prev = snapshots.get(i - 1);
            ChessBoard curr = snapshots.get(i);
            String moveStr = computePgnMove(prev, curr, mover, prev.getHeight());
            if (moveStr == null) moveStr = "?";

            if (mover == Colour.White) {
                movetext.append(moveNumber).append(". ").append(moveStr).append(" ");
                waitingForBlack = true;
            } else {
                if (!waitingForBlack) {
                    movetext.append(moveNumber).append("... ");
                }
                movetext.append(moveStr).append(" ");
                moveNumber++;
                waitingForBlack = false;
            }
        }

        movetext.append(pgnResultTag);
        sb.append(movetext.toString().trim());
        return sb.toString();
    }

    /**
     * Vytvoří PGN notaci jednoho tahu porovnáním prev/curr snapshotů pro danou barvu na tahu.
     * Zvládá: běžný tah, braní, povýšení, rošádu. NEZVLÁDÁ: šach/mat symboly, en passant jako speciální případ.
     * Nestandardní figurky (mimo Pawn/Knight/Bishop/Rook/Queen/King) vrátí "?" jako písmeno figurky.
     */
    private String computePgnMove(ChessBoard prev, ChessBoard curr, Colour moverColour, int boardHeight) {
        List<PieceSnapshot> prevAll = extractPieces(prev);
        List<PieceSnapshot> currAll = extractPieces(curr);

        for (Iterator<PieceSnapshot> itPrev = prevAll.iterator(); itPrev.hasNext();) {
            PieceSnapshot p = itPrev.next();
            Iterator<PieceSnapshot> itCurr = currAll.iterator();
            boolean matched = false;
            while (itCurr.hasNext()) {
                PieceSnapshot c = itCurr.next();
                if (isSameState(p, c)) {
                    itCurr.remove();
                    matched = true;
                    break;
                }
            }
            if (matched) itPrev.remove();
        }

        List<PieceSnapshot> moverPrev = filterColour(prevAll, moverColour);
        List<PieceSnapshot> moverCurr = filterColour(currAll, moverColour);
        List<PieceSnapshot> oppPrev = filterColour(prevAll, opposite(moverColour));

        // --- Rošáda: král se posunul o 2 pole na stejné řadě ---
        PieceSnapshot kingFrom = findSingleByClass(moverPrev, "King");
        PieceSnapshot kingTo = findSingleByClass(moverCurr, "King");
        if (kingFrom != null && kingTo != null && kingFrom.y == kingTo.y && Math.abs(kingFrom.x - kingTo.x) == 2) {
            return (kingTo.x > kingFrom.x) ? "O-O" : "O-O-O";
        }

        if (moverPrev.isEmpty() || moverCurr.isEmpty()) return null;

        PieceSnapshot from = null, to = null;
        outer:
        for (PieceSnapshot p : moverPrev) {
            for (PieceSnapshot c : moverCurr) {
                if (p.className.equals(c.className)) {
                    from = p;
                    to = c;
                    break outer;
                }
            }
        }

        boolean promotion = false;
        String promotedClass = null;
        if (from == null) {
            // Různé třídy = povýšení pěšce
            from = moverPrev.get(0);
            to = moverCurr.get(0);
            promotion = true;
            promotedClass = to.className;
        }

        boolean capture = false;
        for (PieceSnapshot op : oppPrev) {
            if (op.x == to.x && op.y == to.y) {
                capture = true;
                break;
            }
        }

        String destSquare = squareOf(to.x, to.y, boardHeight);
        StringBuilder sb = new StringBuilder();

        if (from.className.equals("Pawn")) {
            if (capture) {
                sb.append((char) ('a' + from.x)).append("x").append(destSquare);
            } else {
                sb.append(destSquare);
            }
            if (promotion) {
                sb.append("=").append(pgnLetter(promotedClass));
            }
        } else {
            sb.append(pgnLetter(from.className));
            sb.append(computeDisambiguation(prev, from, to, moverColour));
            if (capture) sb.append("x");
            sb.append(destSquare);
        }

        return sb.toString();
    }

    private List<PieceSnapshot> filterColour(List<PieceSnapshot> list, Colour colour) {
        List<PieceSnapshot> result = new ArrayList<>();
        for (PieceSnapshot p : list) {
            if (p.colour.equals(colour.name())) result.add(p);
        }
        return result;
    }

    private PieceSnapshot findSingleByClass(List<PieceSnapshot> list, String className) {
        PieceSnapshot found = null;
        int count = 0;
        for (PieceSnapshot p : list) {
            if (p.className.equals(className)) {
                found = p;
                count++;
            }
        }
        return (count == 1) ? found : null;
    }

    private Colour opposite(Colour colour) {
        return (colour == Colour.White) ? Colour.Black : Colour.White;
    }

    private String squareOf(int x, int y, int boardHeight) {
        char file = (char) ('a' + x);
        int rank = boardHeight - y;
        return "" + file + rank;
    }

    private String pgnLetter(String className) {
        switch (className) {
            case "King": return "K";
            case "Queen": return "Q";
            case "Rook": return "R";
            case "Bishop": return "B";
            case "Knight": return "N";
            case "Pawn": return "";
            default: return "?"; // nestandardní figurka
        }
    }

    /** Standardní PGN pravidlo pro rozlišení tahu, když by ke stejnému cíli mohla dojít i jiná figurka stejné třídy. */
    private String computeDisambiguation(ChessBoard prev, PieceSnapshot from, PieceSnapshot to, Colour colour) {
        Player movingPlayer = null;
        for (Player p : prev.getPlayers()) {
            if (p.getColor() == colour) {
                movingPlayer = p;
                break;
            }
        }
        if (movingPlayer == null) return "";

        List<PieceSnapshot> ambiguous = new ArrayList<>();

        for (int x = 0; x < prev.getWidth(); x++) {
            for (int y = 0; y < prev.getHeight(); y++) {
                if (x == from.x && y == from.y) continue;

                Piece p = prev.getPiece(x, y);
                if (p == null) continue;
                if (!p.getClass().getSimpleName().equals(from.className)) continue;
                if (p.getColour() != colour) continue;

                ArrayList<int[]> targets = prev.getPossibleTargets(x, y, movingPlayer);
                for (int[] t : targets) {
                    if (t[0] == to.x && t[1] == to.y) {
                        PieceSnapshot other = new PieceSnapshot();
                        other.className = from.className;
                        other.colour = colour.name();
                        other.x = x;
                        other.y = y;
                        ambiguous.add(other);
                        break;
                    }
                }
            }
        }

        if (ambiguous.isEmpty()) return "";

        boolean sameFileExists = false, sameRankExists = false;
        for (PieceSnapshot other : ambiguous) {
            if (other.x == from.x) sameFileExists = true;
            if (other.y == from.y) sameRankExists = true;
        }

        if (!sameFileExists) return String.valueOf((char) ('a' + from.x));
        if (!sameRankExists) return String.valueOf(prev.getHeight() - from.y);
        return squareOf(from.x, from.y, prev.getHeight());
    }
}