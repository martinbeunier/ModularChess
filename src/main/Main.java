package main;

import java.util.ArrayList;
import java.util.Scanner;

import gui.MainFrame;
//import gui.Menu;
import gui.UIconfiguration;
import logic.ChessBoard;
import logic.Colour;
import logic.DebugConfiguration;
import logic.*;
import org.w3c.dom.ls.LSOutput;
import pieces.*;
import gameloop.GameLoop;
import pieces.PoweUps.Lifebuoy;
import pieces.PoweUps.OverClocker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import pieces.utilities.RestartPiece;

public class Main/*extends Application */{

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
       // launch(args);

        DebugConfiguration.getInstance() ;
        UIconfiguration.getInstance();

        switch (choice) {

            case 0: //spuštění ui
                MainFrame mainFrame = new MainFrame();
                break;
            case 1: //spuštění herní smyčky
                GameLoop gameLoop = new GameLoop();
                gameLoop.run("standard", null);
                break;
            case 2: //spuštění krokové simulace hry
                //inicializace hráčů

                Player player1 = new Player("Bílý", Colour.White, 600);
                Player player2 = new Player("Černý", Colour.Black, 600);





//konec inicializace hráčů

//inicializace figur


                King wking = new King("w king",7,13,Colour.White,0);
                King bking = new King("b king",7,0,Colour.Black,0);

                Guardian bguardan1 = new Guardian("guardian",8,0,Colour.Black);
                Guardian bguardan2 = new Guardian("guardian",5,0,Colour.Black);
                Guardian wguardan1 = new Guardian("guardian",8,13,Colour.White);
                Guardian wguardan2 = new Guardian("guardian",5,13,Colour.White);

                Rook brook1 = new Rook("rook", 0, 0, Colour.Black);
                Rook brook2 = new Rook("rook", 13, 0, Colour.Black);
                Rook wrook1 = new Rook("rook", 0, 13, Colour.White);
                Rook wrook2 = new Rook("rook", 13, 13, Colour.White);

                ArciBishop barcibishop1 = new ArciBishop("arcibishop", 3, 0, Colour.Black);
                ArciBishop warcibishop1 = new ArciBishop("arcibishop", 3, 13, Colour.White);

                Bishop bbishop1 = new Bishop("bishop", 2, 0, Colour.Black);
                Bishop bbishop2 = new Bishop("bishop", 11, 0, Colour.Black);
                Bishop wbishop1 = new Bishop("bishop", 2, 13, Colour.White);
                Bishop wbishop2 = new Bishop("bishop", 11, 13, Colour.White);

                Empress bempress1 = new Empress("empress", 6, 0, Colour.Black);
                Empress wempress1 = new Empress("empress", 6, 13, Colour.White);

                Knight bknight1 = new Knight("knight", 1, 0, Colour.Black);
                Knight bknight2 = new Knight("knight", 12, 0, Colour.Black);
                Knight wknight1 = new Knight("knight", 1, 13, Colour.White);
                Knight wknight2 = new Knight("knight", 12, 13, Colour.White);

                HexaRook whexarook = new HexaRook("hexarook",10,13,Colour.White);
                HexaRook bhexarook = new HexaRook("hexarook",10,0,Colour.Black);

                HobbyHorse bhobbyhorse1 = new HobbyHorse("hobbyhorse", 4, 0, Colour.Black);
                HobbyHorse bhobbyhorse2 = new HobbyHorse("hobbyhorse", 9, 0, Colour.Black);
                HobbyHorse whobbyhorse1 = new HobbyHorse("hobbyhorse", 4, 13, Colour.White);
                HobbyHorse whobbyhorse2 = new HobbyHorse("hobbyhorse", 9, 13, Colour.White);

//konec inicializace figur


//inicializace šachovnice

                ChessBoard chessBoard = new ChessBoard(14,14);

                chessBoard.addPlayer(player1);
                chessBoard.addPlayer(player2);


                for(int i = 0;i<14;i++){
                    chessBoard.addPiece(new Pawn("w pawn",i,12,Colour.White,0));
                    chessBoard.addPiece(new Pawn("b pawn",i,1,Colour.Black,2));
                    chessBoard.addPromotionSquares(i,6,Colour.White);
                    chessBoard.addPromotionSquares(i,7,Colour.Black);
                }

                chessBoard.addPiece(wking);
                chessBoard.addPiece(bking);

                chessBoard.addPiece(wguardan1);
                chessBoard.addPiece(wguardan2);
                chessBoard.addPiece(bguardan1);
                chessBoard.addPiece(bguardan2);

                chessBoard.addPiece(wrook1);
                chessBoard.addPiece(wrook2);
                chessBoard.addPiece(brook1);
                chessBoard.addPiece(brook2);

                chessBoard.addPiece(warcibishop1);
                chessBoard.addPiece(barcibishop1);

                chessBoard.addPiece(bbishop1);
                chessBoard.addPiece(bbishop2);
                chessBoard.addPiece(wbishop1);
                chessBoard.addPiece(wbishop2);

                chessBoard.addPiece(bempress1);
                chessBoard.addPiece(wempress1);

                chessBoard.addPiece(bknight1);
                chessBoard.addPiece(wknight1);
                chessBoard.addPiece(wknight2);
                chessBoard.addPiece(bknight2);

                chessBoard.addPiece(whexarook);
                chessBoard.addPiece(bhexarook);

                chessBoard.addPiece(whobbyhorse1);
                chessBoard.addPiece(whobbyhorse2);
                chessBoard.addPiece(bhobbyhorse1);
                chessBoard.addPiece(bhobbyhorse2);


//konec inicializace šachovnice


        chessBoard.printBoard();

        chessBoard.savePosition("test 2",player1);


        break;



}


    }}

/*TODO

tutorial piece
restart piece
přidat ,ať můžu vybrat(označit piece), než odehraje bot
dát do cache hint obrázky
kolize carriers
záznamy her


nepodstatné :

map editor
piece editor



//překreslit letadlo

*/



    //konec kódu
