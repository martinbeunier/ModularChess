package main;

import java.util.ArrayList;
import java.util.Scanner;

import pieces.utilities.RestartPiece;
import pieces.utilities.TutorialPiece;
import profile.PlayerManager;
import gui.MainFrame;
import gui.UIconfiguration;
import logic.ChessBoard;
import logic.Colour;
import logic.DebugConfiguration;
import logic.*;
import pieces.*;
import gameloop.GameLoop;
import pieces.PoweUps.Lifebuoy;
import pieces.PoweUps.OverClocker;

public class Main {

    public static final Scanner scanner = new Scanner(System.in);



    /*
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/chess_ui.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Šachy");
        stage.setScene(scene);
        stage.show();
    }
*/

    public static void main(String[] args) {
        int choice = 0;


        DebugConfiguration.getInstance() ;
        UIconfiguration.getInstance();
        PlayerManager.getCurrentHumanPlayer(); // vynutí načtení/vytvoření profilu hned na startu


        switch (choice) {

            case 0: //spuštění ui
                MainFrame mainFrame = new MainFrame();
                break;
            case 1: //spuštění herní smyčky
                GameLoop gameLoop = new GameLoop();
                gameLoop.run("standard", null);
                break;
            case 2: //spuštění krokové simulace hry
                Player player1 = new Player("Bílý", Colour.White, 600);
                Player player2 = new Player("Černý", Colour.Black, 600);


//inicializace figur

//white
                King wk = new King("k", 3, 6, Colour.White, 0);
                Airplane a1 = new Airplane("a", 3, 5, Colour.White, 2);
                Airplane a2 = new Airplane("a", 1, 6, Colour.White, 0);
                Airplane a3 = new Airplane("a", 5, 6, Colour.White, 0);

//black
                King bk = new King("k", 3, 0, Colour.Black, 2);
                Rook br = new Rook("r", 4, 0, Colour.Black);

//other
                RestartPiece restartPiece = new RestartPiece("restart", 9, 7, Colour.White);
                TutorialPiece tutorialPiece = new TutorialPiece("Promote airplanes \\nfighter and checkmate .\\n You can use rotation .",9,5 );


//inicializace šachovnice

                ChessBoard chessBoard = new ChessBoard(10, 8);

                chessBoard.addPlayer(player1);
                chessBoard.addPlayer(player2);

// blokády
                for (int i = 0; i < 8; i++) {
                    chessBoard.addPiece(new Blocade("blocade", 7, i));
                    chessBoard.addPiece(new Blocade("blocade", 8, i));
                }
                for (int i = 0; i < 5; i++) {
                    chessBoard.addPiece(new Blocade("blocade", 9, i));
                }


                for (int i = 0; i < 7; i++) {
                    chessBoard.addPromotionSquares(i, 0, Colour.White);
                    chessBoard.addPromotionSquares(i, 7, Colour.Black);
                }

// figury
                chessBoard.addPiece(wk);
                chessBoard.addPiece(a1);
                chessBoard.addPiece(a2);
                chessBoard.addPiece(a3);

                chessBoard.addPiece(bk);
                chessBoard.addPiece(br);

                chessBoard.addPiece(restartPiece);
                chessBoard.addPiece(tutorialPiece);


                chessBoard.printBoard();

                chessBoard.savePosition("test 2", player1);





        break;



}


    }}

/*TODO


zobrazit v map select dohrané mapy .


bugnuty zvuk v loop

přidat mazání starých her
přidat zvuk do gamePlayer



nepodstatné :
přidat ,ať můžu vybrat(označit piece), než odehraje bot
map editor
piece editor
dát zápis hry do vlastního vlákna



//překreslit letadlo

*/

/*
Další koncepty :
torpedo bomber - spawne jen jednou torpedo
offset repeat moves
infiltrator z ouroboros


 */

    //konec kódu


