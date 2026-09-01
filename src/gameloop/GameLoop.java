package gameloop;

import main.Main;
import pieces.*;
import logic.*;
import pieces.PoweUps.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//import static jdk.internal.org.jline.utils.Colors.s;



import main.Main;
import pieces.*;
import logic.*;
import pieces.PoweUps.*;

import java.io.File;
import java.util.ArrayList;

public class GameLoop {
    private ChessBoard chessBoard;
    private ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer;
    private int switchPlayer = 0;

    private boolean lastMoveCausedDrowning = false;
    /** Inicializuje šachovnici a hráče — buď ze savu, nebo od nuly. Bez vstupu, bez smyčky. */
    private int movesWithoutCapture = 0;
    private boolean drawByRepetition = false;
    private boolean drawByNoCapture = false;

    private static final int NO_CAPTURE_MOVE_LIMIT = 70;

    private String gameHistoryFilePath; // uloží se JEDNOU, na začátku, pak se pořád jen přepisuje stejný soubor
    private String mapName;             // potřebujeme si zapamatovat název mapy pro název souboru


    private final Bot bot = new Bot();
    private boolean vsBot = false;
    private Colour botColour;
    private MoveListener moveListener;



    public void initGame(String fileName) {
        ArrayList<Player> loadedPlayers = new ArrayList<>();
        this.mapName = fileName;

        if (new File("files\\positions\\" + fileName + ".chess").exists()) {
            this.chessBoard = ChessBoard.loadPosition(fileName, loadedPlayers);
            players = new ArrayList<>(loadedPlayers.subList(0, loadedPlayers.size() - 1));
            currentPlayer = loadedPlayers.get(loadedPlayers.size() - 1);
            switchPlayer = players.indexOf(currentPlayer);
        } else {
            // ... celá inicializace od nuly, jen jednou

            Player player1 = new Player("Bílý", Colour.White, 600);
            Player player2 = new Player("Černý", Colour.Black, 600);
            switchPlayer = 0;
            players.add(player1);
            players.add(player2);

            // White pawns
            Pawn wp0 = new Pawn("White Pawn", 0, 6, Colour.White, 0);
            Pawn wp1 = new Pawn("White Pawn", 1, 6, Colour.White, 0);
            Pawn wp2 = new Pawn("White Pawn", 2, 6, Colour.White, 0);
            Pawn wp3 = new Pawn("White Pawn", 3, 6, Colour.White, 0);
            Pawn wp4 = new Pawn("White Pawn", 4, 6, Colour.White, 0);
            Pawn wp5 = new Pawn("White Pawn", 5, 6, Colour.White, 0);
            Pawn wp6 = new Pawn("White Pawn", 6, 6, Colour.White, 0);
            Pawn wp7 = new Pawn("White Pawn", 7, 6, Colour.White, 0);

            // Black pawns
            Pawn bp0 = new Pawn("Black Pawn", 0, 1, Colour.Black, 2);
            Pawn bp1 = new Pawn("Black Pawn", 1, 1, Colour.Black, 2);
            Pawn bp2 = new Pawn("Black Pawn", 2, 1, Colour.Black, 2);
            Pawn bp3 = new Pawn("Black Pawn", 3, 1, Colour.Black, 2);
            Pawn bp4 = new Pawn("Black Pawn", 4, 1, Colour.Black, 2);
            Pawn bp5 = new Pawn("Black Pawn", 5, 1, Colour.Black, 2);
            Pawn bp6 = new Pawn("Black Pawn", 6, 1, Colour.Black, 2);
            Pawn bp7 = new Pawn("Black Pawn", 7, 1, Colour.Black, 2);

            Rook wr1 = new Rook("White Rook", 0, 7, Colour.White);
            Knight wn1 = new Knight("White Knight", 1, 7, Colour.White);
            Bishop wb1 = new Bishop("White Bishop", 2, 7, Colour.White);
            Queen wq = new Queen("White Queen", 3, 7, Colour.White);
            King wk = new King("White King", 4, 7, Colour.White, 0);
            Bishop wb2 = new Bishop("White Bishop", 5, 7, Colour.White);
            Knight wn2 = new Knight("White Knight", 6, 7, Colour.White);
            Rook wr2 = new Rook("White Rook", 7, 7, Colour.White);

            Rook br1 = new Rook("Black Rook", 0, 0, Colour.Black);
            Knight bn1 = new Knight("Black Knight", 1, 0, Colour.Black);
            Bishop bb1 = new Bishop("Black Bishop", 2, 0, Colour.Black);
            Queen bq = new Queen("Black Queen", 3, 0, Colour.Black);
            King bk = new King("Black King", 4, 0, Colour.Black, 0);
            Bishop bb2 = new Bishop("Black Bishop", 5, 0, Colour.Black);
            Knight bn2 = new Knight("Black Knight", 6, 0, Colour.Black);
            Rook br2 = new Rook("Black Rook", 7, 0, Colour.Black);

            this.chessBoard = new ChessBoard(8, 8);
            chessBoard.addPiece(wp0); chessBoard.addPiece(wp1); chessBoard.addPiece(wp2);
            chessBoard.addPiece(wp3); chessBoard.addPiece(wp4); chessBoard.addPiece(wp5);
            chessBoard.addPiece(wp6); chessBoard.addPiece(wp7);

            chessBoard.addPiece(bp0); chessBoard.addPiece(bp1); chessBoard.addPiece(bp2);
            chessBoard.addPiece(bp3); chessBoard.addPiece(bp4); chessBoard.addPiece(bp5);
            chessBoard.addPiece(bp6); chessBoard.addPiece(bp7);

            chessBoard.addPiece(wr1); chessBoard.addPiece(wn1); chessBoard.addPiece(wb1);
            chessBoard.addPiece(wq);  chessBoard.addPiece(wk);  chessBoard.addPiece(wb2);
            chessBoard.addPiece(wn2); chessBoard.addPiece(wr2);

            chessBoard.addPiece(br1); chessBoard.addPiece(bn1); chessBoard.addPiece(bb1);
            chessBoard.addPiece(bq);  chessBoard.addPiece(bk);  chessBoard.addPiece(bb2);
            chessBoard.addPiece(bn2); chessBoard.addPiece(br2);

            chessBoard.addPlayer(player1);
            chessBoard.addPlayer(player2);

            for (int i = 0; i < 8; i++) chessBoard.addPromotionSquares(i, 0, Colour.White);
            for (int i = 0; i < 8; i++) chessBoard.addPromotionSquares(i, 7, Colour.Black);

            currentPlayer = players.get(switchPlayer % players.size());
    }

    }

    public boolean tryMove(int startX, int startY, int endX, int endY) {
        int piecesBefore = chessBoard.getPieces().size();

        boolean moved = chessBoard.movePiece(startX, startY, endX, endY, currentPlayer);

        if (moved) {
            applyPostMoveRules(piecesBefore);

            if (moveListener != null) {
                moveListener.onMoveCompleted(startX, startY, endX, endY);
            }

            maybeTriggerBotMove();
        }

        return moved;
    }

    public boolean tryMove(int startX, int startY, int rotate) {
        int piecesBefore = chessBoard.getPieces().size();

        boolean moved = chessBoard.movePiece(startX, startY, rotate, currentPlayer);

        if (moved) {
            applyPostMoveRules(piecesBefore);

            if (moveListener != null) {
                // U rotace "from" i "to" je stejné pole (otočila se figurka na místě)
                moveListener.onMoveCompleted(startX, startY, startX, startY);
            }

            maybeTriggerBotMove();
        }

        return moved;
    }

    private void applyPostMoveRules(int piecesBefore) {
        int piecesAfter = chessBoard.getPieces().size();
        boolean captured = piecesAfter < piecesBefore;

        lastMoveCausedDrowning = chessBoard.waterInterAction();

        // Utopení pěšce se počítá stejně jako braní — deska se "vyprázdnila",
        // takže nejde o "mrtvý" tah bez pokroku.
        if (captured || lastMoveCausedDrowning) {
            movesWithoutCapture = 0;
        } else {
            movesWithoutCapture++;
        }

        drawByNoCapture = movesWithoutCapture >= NO_CAPTURE_MOVE_LIMIT;

        switchPlayer++;
        currentPlayer = players.get(switchPlayer % players.size());

        drawByRepetition = chessBoard.recordPositionAndCheckRepetition(currentPlayer.getColor());
    }


    public boolean didLastMoveCauseDrowning() {
        return lastMoveCausedDrowning;
    }

    public boolean isDrawByRepetition() {
        return drawByRepetition;
    }

    public boolean isDrawByNoCapture() {
        return drawByNoCapture;
    }


    public boolean isGameOver() {
        return chessBoard.countHeads() <= 1 || drawByRepetition || drawByNoCapture;
    }

    public ChessBoard getChessBoard() { return chessBoard; }
    public Player getCurrentPlayer() { return currentPlayer; }

    /** Volitelná konzolová varianta — GUI (Loop) ji nepoužívá, jen pro testování/spuštění bez UI. */

    public void run(String fileName,String opponent) {
        initGame(fileName);
        chessBoard.printBoard();

        while (true) {
            boolean moved = false;
            do {
                System.out.println("Hraje hráč barvy : " + currentPlayer.getColor());
                System.out.print("Zadej tah (startX startY endX endY): ");

                String line = Main.scanner.nextLine();
                String[] parts = line.trim().split("\\s+");

                if (parts.length == 4) {
                    moved = tryMove(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                    chessBoard.printBoard();
                } else if (parts.length == 3) {
                    moved = tryMove(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]));
                    chessBoard.printBoard();
                } else {
                    System.out.println("Zadej 3 nebo 4 čísla");
                }
            } while (!moved);

            if (isGameOver()) break;
        }
    }
    public boolean saveGame() {
        // Cestu spočítáme jen POPRVÉ — při dalších voláních (po každém tahu)
        // se použije ta samá, uložená cesta, takže se pořád píše do STEJNÉHO souboru.
        if (gameHistoryFilePath == null) {
            gameHistoryFilePath = buildGameHistoryFilePath();
        }

        chessBoard.saveGameHistorySnapshot(gameHistoryFilePath, currentPlayer, movesWithoutCapture);
        return true;
    }

    private String buildGameHistoryFilePath() {
        File historyDir = new File("files\\gameHistory");

        // Složka na začátku hry možná ještě neexistuje
        if (!historyDir.exists()) {
            historyDir.mkdirs();
        }

        File[] existingFiles = historyDir.listFiles();
        int count = (existingFiles != null) ? existingFiles.length : 0;

        return "files\\gameHistory\\" + count + "_" + mapName + ".chess";
    }

    public void setVsBot(boolean vsBot, Colour botColour) {
        this.vsBot = vsBot;
        this.botColour = botColour;
        maybeTriggerBotMove(); // pokryje případ, kdy bot táhne jako první
    }


    public void setMoveListener(MoveListener listener) {
        this.moveListener = listener;
    }

    public boolean isVsBot() {
        return vsBot;
    }

    public Colour getBotColour() {
        return botColour;
    }

    private void maybeTriggerBotMove() {
        if (!vsBot) return;
        if (isGameOver()) return;
        if (currentPlayer.getColor() != botColour) return;

        Thread botThread = new Thread(() -> {
            try {
                Thread.sleep(400); // volitelná umělá prodleva, kosmetika
            } catch (InterruptedException ignored) {}

            bot.playRandomMove(this); // interně zavolá tryMove(...) -> celý koloběh se zopakuje
        });

        botThread.setDaemon(true);
        botThread.start();
    }

}

