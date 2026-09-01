package gui;

import gameloop.Bot;
import gameloop.BotFactory;
import gameloop.GameLoop;
import logic.*;
import logic.Tile;
import logic.ChessBoard;
import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;

public class Loop extends JPanel {
    private MainFrame frame;
    private String selectedMap;
    private String selectedOpponent;
    private String selectedColour;
    private GameLoop gameLoop;

    // Držení stavu pro Drag & Drop i Click & Click
    private Piece selectedPiece = null;
    private int startGridX = -1;
    private int startGridY = -1;
    private int dragX = 0;
    private int dragY = 0;
    private boolean isDragging = false;
    private boolean playSound = false;


    // Tlačítka pro rotaci — aktivní jen když je vybraná figurka, která umí rotovat daným směrem
    private JButton rotateLeftButton;
    private JButton rotateRightButton;
    private JButton rotateAroundButton;

    // Zvýraznění možných tahů vybrané figurky (seznam polí [x, y])
    private ArrayList<int[]> possibleMoves = new ArrayList<>();

    // Pole, na kterém právě probíhá promoce (zobrazený modální dialog) — -1 = nic
    private int promotionHighlightX = -1;
    private int promotionHighlightY = -1;

    // Ohrožené Head figurky a figurky, které na ně útočí — přepočítává se po každém tahu
    private ArrayList<int[]> threatenedHeadSquares = new ArrayList<>();
    private ArrayList<int[]> attackingPieceSquares = new ArrayList<>();

    // Uchování souřadnic posledního proveditelného tahu
    private int lastMoveFromX = -1;
    private int lastMoveFromY = -1;
    private int lastMoveToX = -1;
    private int lastMoveToY = -1;

    private boolean boardFlipped = false; // true = hraje se za černého, otočíme pohled na desku

    private Image currentPreviewImage;
    private Image defaultNoPieceImage = safeLoadImage("files\\images\\icon.png");
    private Image defaultUnknownPieceImage =safeLoadImage("files\\images\\img19.jpg");

    // Cache pro uložení již načtených SVG obrázků v paměti RAM
    private final Map<String, BufferedImage> imageCache = new HashMap<>();

    //private final Bot bot = new Bot();

    public Loop(MainFrame frame) {
        this.frame = frame;
        this.setLayout(null);
        this.setBackground(new Color(26, 28, 37));

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "backTo"            ); //TODO vyskakovací menu
        getActionMap().put("backTo", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
               // frame.showScene("MENU");
            }
        });


        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left"            );
        getActionMap().put("left", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRotate(3);
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right"            );
        getActionMap().put("right", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRotate(1);
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up"            );
        getActionMap().put("up", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRotate(2);
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down"            );
        getActionMap().put("down", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRotate(2);
            }
        });


        // ------------------------------------------------------------
        // Tlačítka pro rotaci (šipky vedle šachovnice)
        // ------------------------------------------------------------
        int w = frame.getWidth();
        int h = frame.getHeight();


        rotateLeftButton = new JButton(
                "<html><div style='text-align:center'>" +
                        "<font color='black' size='12'>&#8630;</font><br>" +
                        "<font color='black' size='4'>Left</font>" +
                        "</div></html>"
        );

        rotateRightButton = new JButton(
                "<html><div style='text-align:center'>" +
                        "<font size='12'>&#8631;</font><br>" +
                        "<font size='4'>Right</font>" +
                        "</div></html>"
        );

        rotateAroundButton = new JButton(
                "<html><div style='text-align:center'>" +
                        "<font size='12'>&#8645;</font><br>" +
                        "<font size='4'>Backwards</font>" +
                        "</div></html>"
        );

        rotateLeftButton.setHorizontalAlignment(SwingConstants.CENTER);
        rotateRightButton.setHorizontalAlignment(SwingConstants.CENTER);
        rotateAroundButton.setHorizontalAlignment(SwingConstants.CENTER);


      //  rotateLeftButton.setFont(new Font("Noto Sans", Font.BOLD, 16));
       // rotateRightButton.setFont(new Font("Noto Sans", Font.BOLD, 16));
        //rotateAroundButton.setFont(new Font("Noto Sans", Font.BOLD, 16));

        rotateLeftButton.setBounds(UI.toPercent(71, w), UI.toPercent(65, h), UI.toPercent(7, w), UI.toPercent(12, h));
        rotateRightButton.setBounds(UI.toPercent(87, w), UI.toPercent(65, h), UI.toPercent(7, w), UI.toPercent(12, h));
        rotateAroundButton.setBounds(UI.toPercent(79, w), UI.toPercent(65, h), UI.toPercent(7, w), UI.toPercent(12, h));

        // Na začátku nic vybráno -> všechna tlačítka neaktivní
        rotateLeftButton.setEnabled(false);
        rotateRightButton.setEnabled(false);
        rotateAroundButton.setEnabled(false);

        rotateLeftButton.addActionListener(e -> attemptRotate(3));   // delta 3 = -90°
        rotateRightButton.addActionListener(e -> attemptRotate(1));  // delta 1 = +90°
        rotateAroundButton.addActionListener(e -> attemptRotate(2)); // delta 2 = 180°

        add(rotateLeftButton);
        add(rotateRightButton);
        add(rotateAroundButton);



        // ------------------------------------------------------------
        // Myš — pohyb figurek (drag & drop i click & click)
        // ------------------------------------------------------------
        MouseAdapter inputHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameLoop == null) return;
                if (gameLoop.isVsBot() && gameLoop.getCurrentPlayer().getColor() == gameLoop.getBotColour()) {
                    return; // je na tahu bot — ignorujeme klik hráče
                }

                ChessBoard board = gameLoop.getChessBoard();
                if (board == null) return;

                int tileSize = getTileSize();
                int offsetX = getOffsetX(tileSize);
                int offsetY = getOffsetY(tileSize);

                int clickedDisplayX = (int) Math.floor((double) (e.getX() - offsetX) / tileSize);
                int clickedDisplayY = (int) Math.floor((double) (e.getY() - offsetY) / tileSize);

                int gridX = displayX(clickedDisplayX, board.getWidth());
                int gridY = displayY(clickedDisplayY, board.getHeight());

                // Kontrola kliknutí mimo šachovnici
                if (gridX < 0 || gridX >= board.getWidth() || gridY < 0 || gridY >= board.getHeight()) {
                    resetSelection();
                    repaint();
                    return;
                }



                // ----------------------------------------------------
                // 2. OVLÁDÁNÍ LEVÝM TLAČÍTKEM (Drag & Drop + Click & Click)
                // ----------------------------------------------------
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Piece clickedPiece = board.getPiece(gridX, gridY);

                    // A) Už máme vybranou figurku a klikáme na cílové pole (CLICK & CLICK)
                    if (selectedPiece != null && (gridX != startGridX || gridY != startGridY)) {
                        boolean moved = gameLoop.tryMove(startGridX, startGridY, gridX, gridY);
                        resetSelection();
                        if (!moved) {
                            // Pokud tah neprošel, ale klikli jsme na jinou vlastní figurku, vybereme ji
                            if (clickedPiece != null && clickedPiece.getColour() == gameLoop.getCurrentPlayer().getColor()) {
                                selectPieceAt(clickedPiece, gridX, gridY, e.getX(), e.getY());
                            }
                        }
                    }
                    // B) První výběr figurky
                    else if (clickedPiece != null && clickedPiece.getColour() == gameLoop.getCurrentPlayer().getColor()) {
                        selectPieceAt(clickedPiece, gridX, gridY, e.getX(), e.getY());
                    }
                    // C) Kliknutí na prázdné pole bez předchozího výběru
                    else {
                        resetSelection();
                    }
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null && SwingUtilities.isLeftMouseButton(e)) {
                    isDragging = true;
                    dragX = e.getX();
                    dragY = e.getY();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Drag & Drop se vyhodnotí pouze tehdy, pokud hráč figurku reálně přetahoval
                if (selectedPiece != null && isDragging && SwingUtilities.isLeftMouseButton(e)) {
                    ChessBoard board = gameLoop.getChessBoard();
                    int tileSize = getTileSize();
                    int offsetX = getOffsetX(tileSize);
                    int offsetY = getOffsetY(tileSize);

                    int releasedDisplayX = (int) Math.floor((double) (e.getX() - offsetX) / tileSize);
                    int releasedDisplayY = (int) Math.floor((double) (e.getY() - offsetY) / tileSize);

                    int targetGridX = displayX(releasedDisplayX, board.getWidth());
                    int targetGridY = displayY(releasedDisplayY, board.getHeight());

                    if (targetGridX >= 0 && targetGridX < board.getWidth() &&
                            targetGridY >= 0 && targetGridY < board.getHeight()) {

                        if (targetGridX != startGridX || targetGridY != startGridY) {
                            gameLoop.tryMove(startGridX, startGridY, targetGridX, targetGridY);
                        }
                    }
                    resetSelection();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoverPreview(e.getX(), e.getY());
            }
        };


        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
        addMouseWheelListener(inputHandler);
    }
    // Hlídání předchozí pozice kurzoru pro zabránění zbytečnému repaintu
    private int lastHoverX = -2;
    private int lastHoverY = -2;

    private void updateHoverPreview(int mouseX, int mouseY) {
        if (gameLoop == null || gameLoop.getChessBoard() == null) return;

        int tileSize = getTileSize();
        int offsetX = getOffsetX(tileSize);
        int offsetY = getOffsetY(tileSize);
        ChessBoard board = gameLoop.getChessBoard();

        int hoveredDisplayX = (mouseX - offsetX) / tileSize;
        int hoveredDisplayY = (mouseY - offsetY) / tileSize;

        int boardX = displayX(hoveredDisplayX, board.getWidth());
        int boardY = displayY(hoveredDisplayY, board.getHeight());

        // Pokud je kurzor stále na stejném políčku, nic nepřekreslujeme
        if (boardX == lastHoverX && boardY == lastHoverY) return;

        lastHoverX = boardX;
        lastHoverY = boardY;

        if (boardX >= 0 && boardX < board.getWidth() && boardY >= 0 && boardY < board.getHeight()) {
            Piece piece = board.getPiece(boardX, boardY);

            if (piece != null) {
                Image piecePreview = loadPreviewForPiece(piece);
                currentPreviewImage = (piecePreview != null) ? piecePreview : defaultUnknownPieceImage;
            } else {
                currentPreviewImage = defaultNoPieceImage;
            }
        } else {
            currentPreviewImage = defaultNoPieceImage;
        }

        repaint();
    }
    private Image safeLoadImage(String path) {
        java.io.File file = new java.io.File(path);
        if (!file.exists()) {
            System.err.println("Obrázek nenalezen: " + file.getAbsolutePath());
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    private Image loadPreviewForPiece(Piece piece) {
        // Načte obrázek podle násobení násobené třídy figury (např. "card_pawn.png", "card_linebreaker.png")
        String className = piece.getClass().getSimpleName().toLowerCase();
        String path = "files/images/" + className + ".png";

        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            return new ImageIcon(path).getImage();
        }

        return null; // Pokud soubor neexistuje, použije se defaultUnknownPieceImage
    }
    private void selectPieceAt(Piece piece, int gridX, int gridY, int mouseX, int mouseY) {
        selectedPiece = piece;
        startGridX = gridX;
        startGridY = gridY;
        dragX = mouseX;
        dragY = mouseY;
        isDragging = false;
        updateRotationButtons();
        updatePossibleMoves();
    }

    private void resetSelection() {
        selectedPiece = null;
        startGridX = -1;
        startGridY = -1;
        isDragging = false;
        updateRotationButtons();
        updatePossibleMoves();
    }

    /**
     * Přepočítá seznam polí, na která může aktuálně vybraná figurka platně táhnout,
     * přes ChessBoard.getPossibleTargets(...). Prázdný seznam = nic zvýrazněné.
     */
    private void updatePossibleMoves() {
        if (selectedPiece != null && gameLoop != null && startGridX != -1) {
            possibleMoves = gameLoop.getChessBoard()
                    .getPossibleTargets(startGridX, startGridY, gameLoop.getCurrentPlayer());
        } else {
            possibleMoves = new ArrayList<>();
        }
    }

    /**
     * Zjistí, které rotace jsou pro aktuálně vybranou figurku platné
     * (přes ChessBoard.getRotateMoves + validRotateMoves), a podle toho
     * zapne/vypne jednotlivá tlačítka.
     */
    private void updateRotationButtons() {
        boolean canLeft = false;
        boolean canRight = false;
        boolean canAround = false;

        if (selectedPiece != null && gameLoop != null && startGridX != -1) {
            ChessBoard board = gameLoop.getChessBoard();

            ArrayList<MoveType> rawMoves = board.getRotateMoves(startGridX, startGridY);
            ArrayList<MoveType> validMoves = board.validRotateMoves(startGridX, startGridY, rawMoves);

            for (MoveType m : validMoves) {
                int delta = ((m.getRotate() % 4) + 4) % 4; // normalizace na 0..3

                if (delta == 1) canRight = true;
                else if (delta == 3) canLeft = true;
                else if (delta == 2) canAround = true;
            }
        }

        rotateLeftButton.setEnabled(canLeft);
        rotateRightButton.setEnabled(canRight);
        rotateAroundButton.setEnabled(canAround);
    }

    /**
     * Pokusí se otočit vybranou figurku o zadanou deltu (1 = doprava, 3 = doleva, 2 = 180°).
     * Voláno z tlačítek.
     */
    private void attemptRotate(int delta) {
        if (gameLoop == null || selectedPiece == null || startGridX == -1) return;

        boolean moved = gameLoop.tryMove(startGridX, startGridY, delta);

        if (moved) {
            // rotatePiece() v ChessBoard vytváří NOVÝ objekt Piece na daném poli
            // (stejně jako promotion), takže je potřeba si znovu načíst čerstvou referenci.
            selectedPiece = gameLoop.getChessBoard().getPiece(startGridX, startGridY);
            updateRotationButtons();
            updatePossibleMoves();
        }

        repaint();
    }

    private void checkGameOver() {
        if (gameLoop.isGameOver()) {
            String message;

            if (gameLoop.isDrawByRepetition()) {
                message = "Hra skončila!\nRemíza — stejná pozice nastala potřetí.";

            } else if (gameLoop.isDrawByNoCapture()) {
                message = "Hra skončila!\nRemíza — 70 tahů bez sežrání figurky.";

            } else {
                Colour winner = gameLoop.getChessBoard().getSurvivingHeadColour();

                if (winner == Colour.White) {
                    message = "Hra skončila!\nVítěz: Bílý";
                } else if (winner == Colour.Black) {
                    message = "Hra skončila!\nVítěz: Černý";
                } else {
                    message = "Hra skončila!\nRemíza — oba Headi byli zničeni.";
                }
            }

            JOptionPane.showMessageDialog(this, message, "Konec hry", JOptionPane.INFORMATION_MESSAGE);
            frame.showScene("MAPSELECT");
        }
    }


    /**
     * Po každém tahu (i rotaci) zjistí, jestli je nějaká Head figurka ohrožená
     * (nepřátelská figurka na ni může platně táhnout), a pokud ano:
     *  - zvýrazní ohroženou Head figurku i všechny útočníky na desce (v paintComponent),
     *  - přehraje varovný zvuk (systémový beep — nevyžaduje žádný externí soubor).
     */
    private void updateThreatWarnings() {
        threatenedHeadSquares = new ArrayList<>();
        attackingPieceSquares = new ArrayList<>();

        if (gameLoop == null) return;
        ChessBoard board = gameLoop.getChessBoard();
        if (board == null) return;

        boolean anyThreatFound = false;

        for (Colour colour : new Colour[]{Colour.White, Colour.Black}) {
            ArrayList<int[]> heads = board.getHeadPositions(colour);
            for (int[] head : heads) {
                ArrayList<int[]> attackers = board.getAttackersOfSquare(head[0], head[1], colour);
                if (!attackers.isEmpty()) {
                    threatenedHeadSquares.add(head);
                    attackingPieceSquares.addAll(attackers);
                    anyThreatFound = true;
                }
            }
        }

        if (anyThreatFound) {
            SoundPlayer.playWav(UIconfiguration.THREAT_SOUND_PATH, UIconfiguration.soundEfectsVolume);
            if (gameLoop.didLastMoveCauseDrowning()) {
                SoundPlayer.playWav("files\\sounds\\waterSound.wav", UIconfiguration.soundEfectsVolume);
            }
        } else {
            if (gameLoop.didLastMoveCauseDrowning()) {
                SoundPlayer.playWav("files\\sounds\\waterSound.wav", UIconfiguration.soundEfectsVolume);
            } else {
                SoundPlayer.playWav(UIconfiguration.MOVE_SOUND_PATH, UIconfiguration.soundEfectsVolume);
            }
        }
    }
    private void registerLastMove(int fromX, int fromY, int toX, int toY) {
        this.lastMoveFromX = fromX;
        this.lastMoveFromY = fromY;
        this.lastMoveToX = toX;
        this.lastMoveToY = toY;
    }
    private void resetLastMove() {
        lastMoveFromX = -1;
        lastMoveFromY = -1;
        lastMoveToX = -1;
        lastMoveToY = -1;
    }

    public void setSelectedMap(String selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void setSelectedOpponent(String selectedOpponent) {
        this.selectedOpponent = selectedOpponent;
    }

    public void setSelectedColour(String selectedColour) {
        this.selectedColour = selectedColour;
    }

    public void startGame() {
        System.out.println("Načítám hrací plochu s mapou: " + selectedMap + " a botem: " + selectedOpponent);

        boardFlipped = "Black".equalsIgnoreCase(selectedColour);

        gameLoop = new GameLoop();
        gameLoop.initGame(selectedMap);

        gameLoop.setMoveListener((fromX, fromY, toX, toY) -> {
            SwingUtilities.invokeLater(() -> {
                registerLastMove(fromX, fromY, toX, toY);
                updateThreatWarnings();
                gameLoop.saveGame();
                checkGameOver();
                repaint();
            });
        });

        gameLoop.getChessBoard().setPromotionChooser((x, y, pieceNames) -> {
            Bot currentBot = gameLoop.getBot();
            if (gameLoop.isVsBot() && currentBot != null
                    && gameLoop.getCurrentPlayer().getColor() == gameLoop.getBotColour()) {
                return currentBot.choosePromotion(pieceNames);
            }
            return showPromotionDialog(x, y, pieceNames);
        });

        Bot bot = BotFactory.createBot(selectedOpponent); // null, pokud "Against yourself"
        boolean vsBot = (bot != null);
        Colour botColour = boardFlipped ? Colour.White : Colour.Black;
        gameLoop.setVsBot(vsBot, botColour, bot);

        resetSelection();
        repaint();
    }

    /**
     * Zobrazí modální GUI okno se seznamem (tlačítky) figurek pro proměnu pěšce.
     * Blokuje, dokud hráč nevybere, ale běží to celé na EDT přes vlastní
     * event loop JDialogu — nezamrzne to zbytek GUI.
     *
     * Souřadnice (x, y) se zobrazí v titulku a zároveň se dané pole
     * zvýrazní přímo na šachovnici, aby bylo jasné, KTERÝ pěšec (u vícenásobné
     * promoce např. z Carrieru) se právě promuje.
     */
    private int showPromotionDialog(int x, int y, String[] pieceNames) {
        // Zvýrazníme pole na desce a hned překreslíme, ať je vidět i pod/za dialogem
        promotionHighlightX = x;
        promotionHighlightY = y;
        repaint();

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Vyber figurku pro proměnu pěšce na poli (" + x + ", " + y + ")",
                true
        );
        dialog.setLayout(new GridLayout(pieceNames.length, 1, 5, 5));

        int[] result = {0};

        for (int i = 0; i < pieceNames.length; i++) {
            final int idx = i;
            JButton btn = new JButton(pieceNames[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.addActionListener(ev -> {
                result[0] = idx;
                dialog.dispose();
            });
            dialog.add(btn);
        }

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(260, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);

        // Zrušíme zvýraznění (další promoce v řadě si nastaví svoje vlastní)
        promotionHighlightX = -1;
        promotionHighlightY = -1;
        repaint();

        return result[0];
    }

    private int getTileSize() {
        if (gameLoop == null || gameLoop.getChessBoard() == null) return 1;
        ChessBoard board = gameLoop.getChessBoard();
        if (board.getWidth() == 0 || board.getHeight() == 0) return 1;

        int margin = 60; // Rezerva pro souřadnice okolo šachovnice (px)
        int availableWidth = Math.max(1, getWidth() - margin);
        int availableHeight = Math.max(1, getHeight() - margin);

        return Math.min(availableWidth / board.getWidth(), availableHeight / board.getHeight());
    }


    private int getOffsetX(int tileSize) {
        if (gameLoop == null || gameLoop.getChessBoard() == null) return 0;

        // Vycentruje šachovnici v prostoru od 0 do 90 % šířky okna (zbylých 10 % vpravo zůstane pro tlačítka)
       // int availableWidth = (int) (getWidth() * 0.7);
        int boardWidth = gameLoop.getChessBoard().getWidth() * tileSize;

        //return Math.max(40, (availableWidth - boardWidth) / 2);
        return (int)UI.toPercent(30,boardWidth);
    }
    private int getOffsetY(int tileSize) {
        if (gameLoop == null || gameLoop.getChessBoard() == null) return 0;
        return (getHeight() - (gameLoop.getChessBoard().getHeight() * tileSize)) / 2;
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

        UIconfiguration.printMemoryUsage();

        if (gameLoop == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        ChessBoard board = gameLoop.getChessBoard();
        if (board == null) return;

        int cols = board.getWidth();
        int rows = board.getHeight();

        if (cols == 0 || rows == 0) return;

        int tileSize = getTileSize();
        int offsetX = getOffsetX(tileSize);
        int offsetY = getOffsetY(tileSize);

        Tile[][] tiles = board.getTiles();

        // =========================================================
        // 1. VYKRESLENÍ DLAŽDIC
        // =========================================================
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                int screenX = displayX(x, cols);
                int screenY = displayY(y, rows);

                int posX = offsetX + (screenX * tileSize);
                int posY = offsetY + (screenY * tileSize);

                // Tile[][] je [x][y]
                Tile tile = null;

                if (tiles != null &&
                        x < tiles.length &&
                        tiles[x] != null &&
                        y < tiles[x].length) {

                    tile = tiles[x][y];
                }

                // Základní barva šachovnice
                if ((x + y) % 2 == 0) {
                    g2d.setColor(UIconfiguration.boardColorLight);
                } else {
                    g2d.setColor(UIconfiguration.boardColorDark);
                }

                // Speciální vlastnosti dlaždice
                if (tile != null) {

                    if (tile.getWater()) {
                        if ((x + y) % 2 == 0) {
                            g2d.setColor(UIconfiguration.waterColorLight);
                        } else {
                            g2d.setColor(UIconfiguration.waterColorDark);
                        }


                    } else if (tile.getPromotionColours() != null
                            && !tile.getPromotionColours().isEmpty()) {

                        boolean hasWhite =
                                tile.getPromotionColours().contains(Colour.White);

                        boolean hasBlack =
                                tile.getPromotionColours().contains(Colour.Black);

                        if (hasWhite && hasBlack) {
                            g2d.setColor(new Color(186, 85, 211, 200));

                        } else if (hasWhite) {

                            if ((x + y) % 2 == 0) {

                                g2d.setColor(new Color(225, 212, 157));

                            } else {
                                g2d.setColor(new Color(204, 177, 93));
                            }

                        } else if (hasBlack) {

                            if ((x + y) % 2 == 0) {
                                g2d.setColor(new Color(211, 134, 134));

                            } else {
                                g2d.setColor(new Color(218, 94, 94));
                            }
                        }
                    }
                }

                g2d.fillRect(
                        posX,
                        posY,
                        tileSize,
                        tileSize
                );

                // Zvýraznění posledního tahu (odkud -> kam)
                if ((x == lastMoveFromX && y == lastMoveFromY) || (x == lastMoveToX && y == lastMoveToY)) {
                    g2d.setColor(new Color(255, 255, 0, 80)); // Poloprůhledná žlutá
                    g2d.fillRect(posX, posY, tileSize, tileSize);

                    // Volitelné: Čárkovaný nebo tenký rámeček pro cílové pole
                    if (x == lastMoveToX && y == lastMoveToY) {
                        g2d.setColor(new Color(200, 200, 0, 180));
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRect(posX + 1, posY + 1, tileSize - 2, tileSize - 2);
                        g2d.setStroke(new BasicStroke(1));
                    }
                }

                // Zvýraznění vybraného pole
                if (x == startGridX && y == startGridY) {
                    g2d.setColor(new Color(0, 255, 0, 100));

                    g2d.fillRect(
                            posX,
                            posY,
                            tileSize,
                            tileSize
                    );
                }

                // Zvýraznění pole, na kterém právě probíhá promoce (otevřený dialog)
                if (x == promotionHighlightX && y == promotionHighlightY) {
                    g2d.setColor(new Color(255, 140, 0, 170));
                    g2d.fillRect(posX, posY, tileSize, tileSize);

                    g2d.setColor(new Color(255, 90, 0, 255));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRect(posX + 2, posY + 2, tileSize - 4, tileSize - 4);
                    g2d.setStroke(new BasicStroke(1));
                }
                // Zvýraznění ohrožené Head figurky (červená výplň)
                for (int[] h : threatenedHeadSquares) {
                    if (h[0] == x && h[1] == y) {
                        g2d.setColor(new Color(255, 0, 0, 130));

                        g2d.fillRect(
                                posX,
                                posY,
                                tileSize,
                                tileSize
                        );
                    }
                }

                // Zvýraznění figurky, která na Head útočí (červený rámeček)
                for (int[] a : attackingPieceSquares) {
                    if (a[0] == x && a[1] == y) {
                        g2d.setColor(new Color(255, 0, 0, 255));
                        g2d.setStroke(new BasicStroke(4));

                        g2d.drawRect(
                                posX + 2,
                                posY + 2,
                                tileSize - 4,
                                tileSize - 4
                        );

                        g2d.setStroke(new BasicStroke(1));
                    }
                }
                // Ohraničení dlaždice
                g2d.setColor(new Color(0, 0, 0, 40));

                g2d.drawRect(
                        posX,
                        posY,
                        tileSize,
                        tileSize
                );
            }

        }
        // =========================================================
        // 1c. ZVÝRAZNĚNÍ POLÍ OBSAZENÝCH CARRIEREM (hlavní pole + occupation squares)
        // Počítá se znovu při KAŽDÉM překreslení přímo z board.getPiece(x,y),
        // takže se automaticky aktualizuje po přesunu i po zničení Carrieru —
        // nic si nepamatujeme mezi kresleními.
        // =========================================================
        boolean[][] carrierSquares = new boolean[cols][rows];

        for (int cx = 0; cx < cols; cx++) {
            for (int cy = 0; cy < rows; cy++) {
                Piece p = board.getPiece(cx, cy);
                if (p instanceof Carrier) {
                    Carrier carrier = (Carrier) p;

                    carrierSquares[cx][cy] = true;

                    for (OcupationSquare sq : carrier.getOcupationSquares()) {
                        int ox = cx + sq.getX();
                        int oy = cy + sq.getY();
                        if (ox >= 0 && ox < cols && oy >= 0 && oy < rows) {
                            carrierSquares[ox][oy] = true;
                        }
                    }
                }
            }
        }

        g2d.setColor(new Color(120, 120, 120, 110)); // šedá poloprůhledná
        for (int cx = 0; cx < cols; cx++) {
            for (int cy = 0; cy < rows; cy++) {
                if (carrierSquares[cx][cy]) {


                    int screenX = displayX(cx, cols);
                    int screenY = displayY(cy, rows);

                    int posX = offsetX + screenX * tileSize;
                    int posY = offsetY + screenY * tileSize;

                    g2d.fillRect(
                            posX,
                            posY,
                            tileSize,
                            tileSize
                    );
                }
            }
        }

        // =========================================================
        // 1b. ZVÝRAZNĚNÍ MOŽNÝCH TAHŮ VYBRANÉ FIGURKY
        // =========================================================
        if (!possibleMoves.isEmpty()) {
            for (int[] target : possibleMoves) {
                int tx = target[0];
                int ty = target[1];

                int screenX = displayX(tx, cols);
                int screenY = displayY(ty, rows);

                int posX = offsetX + (screenX * tileSize);
                int posY = offsetY + (screenY * tileSize);

                boolean isCapture = board.getPiece(tx, ty) != null; // pořád tx,ty (raw) — beze změny

                if (isCapture) {
                    // pole s nepřátelskou figurkou -> zvýrazníme prstencem (braní)
                    g2d.setColor(new Color(220, 20, 60, 170));
                    g2d.setStroke(new BasicStroke(4));
                    int pad = tileSize / 10;

                    g2d.drawOval(
                            posX + pad,
                            posY + pad,
                            tileSize - 2 * pad,
                            tileSize - 2 * pad
                    );

                    g2d.setStroke(new BasicStroke(1));
                } else {
                    // volné pole -> tečka uprostřed
                    g2d.setColor(new Color(30, 130, 30, 170));
                    int dotSize = tileSize / 4;
                    int dotX = posX + (tileSize - dotSize) / 2;
                    int dotY = posY + (tileSize - dotSize) / 2;

                    g2d.fillOval(
                            dotX,
                            dotY,
                            dotSize,
                            dotSize
                    );
                }
            }
        }

        // =========================================================
        // 2. VYKRESLENÍ FIGUREK
        // =========================================================
        for (int px = 0; px < cols; px++) {
            for (int py = 0; py < rows; py++) {

                Piece piece = board.getPiece(px, py); // raw — beze změny

                if (piece == null) continue;
                if (isDragging && piece == selectedPiece) continue;

                int screenX = displayX(px, cols);
                int screenY = displayY(py, rows);

                int posX = offsetX + (screenX * tileSize);
                int posY = offsetY + (screenY * tileSize);

                if (piece == null) continue;

                // Pokud figurku přetahujeme,
                // vykreslí se až pod kurzorem.
                if (isDragging && piece == selectedPiece) {
                    continue;
                }


                drawPieceAt(
                        g2d,
                        piece,
                        posX,
                        posY,
                        tileSize
                );
            }
        }

        // =========================================================
        // 3. VYKRESLENÍ FIGURKY POD KURZOREM
        // =========================================================
        if (isDragging && selectedPiece != null) {

            drawPieceAt(
                    g2d,
                    selectedPiece,
                    dragX - tileSize / 2,
                    dragY - tileSize / 2,
                    tileSize
            );
        }
        // =========================================================
        // VYKRESLENÍ SOUŘADNIC PO OBVODU ŠACHOVNICE
        // =========================================================
        g2d.setColor(Color.yellow); // Barva textu souřadnic
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(24, tileSize / 4)));

        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString("X", offsetX , offsetY - 5);
        g2d.drawString("Y", offsetX - 20, offsetY + 15);
        // 1. Horizontální souřadnice (sloupce 0..cols-1)

        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(10, tileSize / 4)));

        for (int x = 0; x < cols; x++) {
            String label = String.valueOf(x);
            int textWidth = fm.stringWidth(label);

            int screenX = displayX(x, cols);
            int textX = offsetX + (screenX * tileSize) + (tileSize - textWidth) / 2;

            int textYTop = offsetY - 6;
            g2d.drawString(label, textX, textYTop);

            int textYBottom = offsetY + (rows * tileSize) + fm.getAscent() + 2;
            g2d.drawString(label, textX, textYBottom);
        }

        // 2. Vertikální souřadnice (řádky 0..rows-1)
        for (int y = 0; y < rows; y++) {
            String label = String.valueOf(y);
            int textHeight = fm.getAscent();

            int screenY = displayY(y, rows);
            int textY = offsetY + (screenY * tileSize) + (tileSize + textHeight) / 2 - 2;

            int textXLeft = offsetX - fm.stringWidth(label) - 8;
            g2d.drawString(label, textXLeft, textY);

            int textXRight = offsetX + (cols * tileSize) + 8;
            g2d.drawString(label, textXRight, textY);
        }




        if (currentPreviewImage != null) {
            int cardWidth = UI.toPercent(25, getWidth());
            int cardHeight = UI.toPercent(50, getHeight());

            // Pozice v pravém horním rohu (X: 82 % šířky, Y: 4 % výšky)
            int cardX = UI.toPercent(70, getWidth());
            int cardY = UI.toPercent(4, getHeight());

            g2d.drawImage(currentPreviewImage, cardX, cardY, cardWidth, cardHeight, this);
        }

    }

    private double getScaleByClass(String className){
        double scale = 1;
        switch (className){
            case "pawn":
                scale = 2.3;
                break;

            case "airplane":
                scale = 2;
                break;

            case "fighter":
                scale = 1.5;
                break;
            case "helicopter":
                scale = 2.17;
                break;
            case "knight":
                scale = 1.8;
                break;
        }


        return scale;
    }

    private void drawPieceAt(Graphics2D g2d, Piece piece, int posX, int posY, int tileSize) {

        // --- 1. VRSTVA DOLE: Vykreslení těla figurky (obrázek z Cache nebo Kolečko) ---
        boolean svgDrawn = false;

        // Nastavení vyhlazování pro hlavní plátno
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        String colorPrefix = (piece.getColour() == Colour.White) ? "white" : "black";
        String pieceType = piece.getClass().getSimpleName().toLowerCase();
        String imagePath = "files/images/skins/" + colorPrefix + pieceType + ".svg";

        double baseScale = 1.0;
        double finalScale = baseScale * getScaleByClass(pieceType);
        int svgSize = (int) Math.round(tileSize * finalScale);

        // Unikátní klíč pro cache zahrnuje i velikost políčka (pokud se okno nezmění, použije se cache)
        String cacheKey = imagePath + "_" + svgSize;

        // 1. Zkusíme vytáhnout HOTOVÝ BufferedImage z paměti
        BufferedImage cachedImg = imageCache.get(cacheKey);

        // 2. Pokud v paměti ještě NENÍ, načteme SVG a jednou ho vykreslíme do BufferedImage
        if (cachedImg == null && svgSize > 0) {
            try {
                java.io.File imgFile = new java.io.File(imagePath);
                if (imgFile.exists()) {
                    SVGLoader loader = new SVGLoader();
                    SVGDocument svgDocument = loader.load(imgFile.toURI().toURL());

                    if (svgDocument != null) {
                        int qualityMultiplier = 2; // Super-sampling pro vyhlazení
                        int renderSize = svgSize * qualityMultiplier;

                        BufferedImage newImg = new BufferedImage(
                                renderSize, renderSize, BufferedImage.TYPE_INT_ARGB
                        );

                        Graphics2D gImg = newImg.createGraphics();
                        gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        gImg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        gImg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                        // Vykreslení SVG do plátna
                        svgDocument.render(null, gImg, new ViewBox(0, 0, renderSize, renderSize));
                        gImg.dispose();

                        // Uložení HOTOVÉHO vyhlazeného obrázku do cache
                        cachedImg = newImg;
                        imageCache.put(cacheKey, cachedImg);
                    }
                }
            } catch (Exception e) {
                cachedImg = null;
            }
        }

        // 3. Bleskové vykreslení hotového obrázku z paměti RAM
        if (cachedImg != null) {
            int svgX = posX - (svgSize - tileSize) / 2;
            int svgY = posY - (svgSize - tileSize) / 2;

            g2d.drawImage(cachedImg, svgX, svgY, svgSize, svgSize, null);
            svgDrawn = true;
        }

        // Fallback kolečko v případě, že soubor neexistuje
        if (!svgDrawn) {
            int padding = tileSize / 8;
            int drawX = posX + padding;
            int drawY = posY + padding;
            int drawWidth = tileSize - (2 * padding);
            int drawHeight = tileSize - (2 * padding);

            g2d.setColor(piece.getColour() == Colour.White ? Color.WHITE : Color.BLACK);
            g2d.fillOval(drawX, drawY, drawWidth, drawHeight);

            g2d.setColor(Color.GRAY);
            g2d.drawOval(drawX, drawY, drawWidth, drawHeight);
        }

// --- 2. VRSTVA UPROSTŘED: Červená směrová čárka ---
        if (piece instanceof OrientedPiece) {
            OrientedPiece orientedPiece = (OrientedPiece) piece;
            int rotation = orientedPiece.getRotation();

            int centerX = posX + tileSize / 2;
            int centerY = posY + tileSize / 2;
            int pointerLength = (tileSize - (tileSize / 3)) / 2;

            double angleRad = Math.toRadians((rotation * 90) - 90);

            // Při pohledu za černého je celá šachovnice otočená o 180°
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

        // --- 3. VRSTVA NAHOŘE: Text (pouze bez SVG) ---
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


}





