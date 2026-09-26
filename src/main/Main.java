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

// inicializace hráčů
                Player player1 = new Player("Bílý", Colour.White, 600);
                Player player2 = new Player("Černý", Colour.Black, 600);

// inicializace šachovnice

                ChessBoard chessBoard = new ChessBoard(8,10 );
                chessBoard.addPlayer(player1);
                chessBoard.addPlayer(player2);

                for (int j= 0;j<10;j++){for (int i= 0;i<8;i++){ chessBoard.addWaterSquares(i,j);}}


                for (int i = 0; i < 8; i++) {
                    chessBoard.addPromotionSquares(i, 0, Colour.White);
                    chessBoard.addPromotionSquares(i, 1, Colour.White);

                    chessBoard.addPromotionSquares(i, 8, Colour.Black);
                    chessBoard.addPromotionSquares(i, 9, Colour.Black);

                }


// inicializace figur
                //white
                chessBoard.addPiece(new King("piece",1,8,Colour.White,0));
                chessBoard.addPiece(new ShipCarrier("piece",1,7,Colour.White,0));
                chessBoard.addPiece(new Helicopter("Piece",0,7,Colour.White));

                chessBoard.addPiece(new Bishop("Piece",2,9,Colour.White));
                chessBoard.addPiece(new Torpedo("Piece",2,7,Colour.White,0));

                chessBoard.addPiece(new Pawn("Piece",0,6,Colour.White,0));
                chessBoard.addPiece(new Pawn("Piece",1,6,Colour.White,0));
                chessBoard.addPiece(new Pawn("Piece",2,6,Colour.White,0));
                chessBoard.addPiece(new Pawn("Piece",1,5,Colour.White,0));

                chessBoard.addPiece(new ArciBishop("Piece",0,8,Colour.White));
                chessBoard.addPiece(new HexaRook("Piece",0,9,Colour.White));

                chessBoard.addPiece(new Airplane("Piece",1,9,Colour.White,0));
                chessBoard.addPiece(new Airplane("Piece",2,8,Colour.White,0));

                chessBoard.addPiece(new RaftCarrier("Piece",6,9,Colour.White,0));
                chessBoard.addPiece(new Bishop("Piece",6,8,Colour.White));
// black
                chessBoard.addPiece(new King("piece",6,1,Colour.Black,2));
                chessBoard.addPiece(new ShipCarrier("piece",6,2,Colour.Black,2));
                chessBoard.addPiece(new Helicopter("Piece",7,2,Colour.Black));

                chessBoard.addPiece(new Bishop("Piece",5,0,Colour.Black));
                chessBoard.addPiece(new Torpedo("Piece",5,2,Colour.Black,2));

                chessBoard.addPiece(new Pawn("Piece",7,3,Colour.Black,2));
                chessBoard.addPiece(new Pawn("Piece",6,3,Colour.Black,2));
                chessBoard.addPiece(new Pawn("Piece",5,3,Colour.Black,2));
                chessBoard.addPiece(new Pawn("Piece",6,4,Colour.Black,2));

                chessBoard.addPiece(new ArciBishop("Piece",7,1,Colour.Black));
                chessBoard.addPiece(new HexaRook("Piece",7,0,Colour.Black));

                chessBoard.addPiece(new Airplane("Piece",5,1,Colour.Black,2));
                chessBoard.addPiece(new Airplane("Piece",6,0,Colour.Black,2));

                chessBoard.addPiece(new RaftCarrier("Piece",1,0,Colour.Black,2));
                chessBoard.addPiece(new Bishop("Piece",1,1,Colour.Black));
//end inicializace figur
                chessBoard.printBoard();

                chessBoard.savePosition("level 4", player1);





        break;



}


    }}

/*TODO


zobrazit v map select dohrané mapy .
zobrazit lépe linebreaker move i jako threat warning.


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

/*

Seznámení hráče s :

            case "helicopter": scale = 2.17; break;

            case "arcibishop": scale = 1.4; break;


            case "linebreakerrook": scale = 1.76; break;
            case "torpedo": scale = 1.25; break;


            case "empress": scale = 1.15; break;
            case "hexarook": scale = 1.1; break;
            case "hobbyhorse": scale = 2.0; break;




            case "wasp": scale = 1.5; break;
            case "carbide" : scale = 1.37;break;

            case "lifebuoy": scale = 1.85; break;
            case "overclocker": scale = 1.25; break;
            case "blocade": scale = 1.05; break;

            case "restartpiece": scale = 1.5; break;
            case "tutorialpiece": scale = 1.92; break;

priority :
1.
arcibishop ,linebreaker rook ,helicopter
2.
empress , hexarook ,hobbyhorse
3.
lifebuyoy , torpedo
*/



    //konec kódu

/*
Player player1 = new Player("Bílý", Colour.White, 600);
Player player2 = new Player("Černý", Colour.Black, 600);



// Black
Pawn bp0 = new Pawn("Black Pawn", 0, 1, Colour.Black, 2);
Pawn bp1 = new Pawn("Black Pawn", 1, 1, Colour.Black, 2);
Pawn bp2 = new Pawn("Black Pawn", 2, 1, Colour.Black, 2);
Pawn bp3 = new Pawn("Black Pawn", 3, 1, Colour.Black, 2);
Pawn bp4 = new Pawn("Black Pawn", 4, 1, Colour.Black, 2);
Pawn bp5 = new Pawn("Black Pawn", 5, 1, Colour.Black, 2);
Pawn bp6 = new Pawn("Black Pawn", 6, 1, Colour.Black, 2);
Pawn bp7 = new Pawn("Black Pawn", 7, 1, Colour.Black, 2);

Rook br1 = new Rook("Black Rook", 0, 0, Colour.Black);
Knight bn1 = new Knight("Black Knight", 1, 0, Colour.Black);
Bishop bb1 = new Bishop("Black Bishop", 2, 0, Colour.Black);
Queen bq = new Queen("Black Queen", 3, 0, Colour.Black);
King bk = new King("Black King", 4, 0, Colour.Black, 0);
Bishop bb2 = new Bishop("Black Bishop", 5, 0, Colour.Black);
Knight bn2 = new Knight("Black Knight", 6, 0, Colour.Black);
Rook br2 = new Rook("Black Rook", 7, 0, Colour.Black);

//white
King wk = new King("k",4,7,Colour.White,0) ;
LinebreakerRook lbr = new LinebreakerRook("lbr",6,5,Colour.White);
Helicopter h1 = new Helicopter("h",7,6,Colour.White);
Helicopter h2 = new Helicopter("h",1,6,Colour.White);
ArciBishop ab1 = new ArciBishop("ab",2,6,Colour.White);
ArciBishop ab2 = new ArciBishop("ab",3,6,Colour.White);
ArciBishop ab3 = new ArciBishop("ab",5,6,Colour.White);
ArciBishop ab4 = new ArciBishop("ab",6,6,Colour.White);





ChessBoard chessBoard = new ChessBoard(8, 8);

                chessBoard.addPiece(wk);
                chessBoard.addPiece(lbr);
                chessBoard.addPiece(h1);
                chessBoard.addPiece(h2);
                chessBoard.addPiece(ab1);
                chessBoard.addPiece(ab2);
                chessBoard.addPiece(ab3);
                chessBoard.addPiece(ab4);


                chessBoard.addPiece(bp0); chessBoard.addPiece(bp1); chessBoard.addPiece(bp2);
                chessBoard.addPiece(bp3); chessBoard.addPiece(bp4); chessBoard.addPiece(bp5);
                chessBoard.addPiece(bp6); chessBoard.addPiece(bp7);


                chessBoard.addPiece(br1); chessBoard.addPiece(bn1); chessBoard.addPiece(bb1);
                chessBoard.addPiece(bq);  chessBoard.addPiece(bk);  chessBoard.addPiece(bb2);
                chessBoard.addPiece(bn2); chessBoard.addPiece(br2);

                chessBoard.addPlayer(player1);
                chessBoard.addPlayer(player2);

                for (int i = 0; i < 8; i++) chessBoard.addPromotionSquares(i, 0, Colour.White);
                for (int i = 0; i < 8; i++) chessBoard.addPromotionSquares(i, 7, Colour.Black);*/