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


                //Not tutorial
                King king1 = new King("w king", 9, 9, Colour.White, 0);
                Torpedo torpedo2 = new Torpedo("w torpedo", 9, 8, Colour.White, 2);

                Rook rook = new Rook("rook",7,8,Colour.Black);


//Tutorial 4
                King king5 = new King("b king", 5, 0, Colour.Black, 0);
                Torpedo torpedo = new Torpedo("torpedo",5,4, Colour.White, 0);

                Pawn pawn6 = new Pawn("pawm",5,2,Colour.Black, 2);
                Pawn pawn4 = new Pawn("pawm",5,3,Colour.White, 2);
                Pawn pawn5 = new Pawn("pawm",5,1,Colour.Black, 2);


                //Tutorial 3
                King king4 = new King("b king", 8, 0, Colour.Black, 0);
                LandCarrier landCarrier2 = new LandCarrier("carrier",8,1,Colour.Black);
                Pawn pawn1 = new Pawn("pawm",7,0,Colour.Black, 2);
                Pawn pawn2 = new Pawn("pawm",9,0,Colour.Black, 2);

                Pawn pawn3 = new Pawn("pawm",8,5,Colour.White, 0);
                LinebreakerRook linebreakerRook = new LinebreakerRook("linebreaker Rook",8,6,Colour.White);

                //Tutorial 2
                Airplane airplane = new Airplane("airplane", 0, 0, Colour.White, 0);
                King king3 = new King("b king", 1, 2, Colour.Black, 0);

//Tutorial 1
                LandCarrier landCarrier = new LandCarrier("carrier", 1, 8, Colour.White);
                Bishop bishop = new Bishop("bishop", 2, 7, Colour.White);
                King king2 = new King("w king", 5, 9, Colour.Black, 0);

//konec inicializace figur


//inicializace šachovnice

                ChessBoard chessBoard = new ChessBoard(10, 10);

                chessBoard.addPlayer(player1);
                chessBoard.addPlayer(player2);


                //Not Tutorial

                chessBoard.addPiece(torpedo2);
                chessBoard.addPiece(rook);
                chessBoard.addPiece(king1);

                chessBoard.addPiece(new Blocade("blocade", 8, 8));
                chessBoard.addPiece(new Blocade("blocade", 8,9));

                //  Tutorial 4

                chessBoard.addPiece(pawn6);
                chessBoard.addPiece(pawn4);
                chessBoard.addPiece(pawn5);
                chessBoard.addPiece(torpedo);

                chessBoard.addPiece(king5);
                //Tutorial 3

                chessBoard.addPiece(pawn1);
                chessBoard.addPiece(pawn2);
                chessBoard.addPiece(landCarrier2);

                chessBoard.addPiece(king4);

                chessBoard.addPiece(pawn3);
                chessBoard.addPiece(linebreakerRook);

                for (int j = 0; j < 5; j++) {
                    chessBoard.addPiece(new Blocade("blocade", 6, j));
                }

                for (int i = 1; i < 8; i++) {
                    chessBoard.addPiece(new Blocade("blocade", 7, i));
                    chessBoard.addPiece(new Blocade("blocade", 9, i));

                    chessBoard.addPiece(new Blocade("blocade", 7, i));
                    chessBoard.addPiece(new Blocade("blocade", 9, i));
                }
                chessBoard.addPiece(new Blocade("blocade", 8,7));

                for (int i = 7; i < 10; i++){
                    for (int j = 0; j < 5; j++) {

                        chessBoard.addWaterSquares(i,j);
                    }
                }

            //Tutorial 2
                for (int i = 0; i < 5; i++){
                    for (int j = 0; j < 5; j++) {

                        chessBoard.addPiece(new Blocade("blocade", i, j));
                    }
                }

        chessBoard.addPiece(king3);
        chessBoard.addPiece(airplane);
        chessBoard.addPromotionSquares(1,2,Colour.White);



        // Tutorial 1
        chessBoard.addPiece(landCarrier);
        chessBoard.addPiece(bishop);
        chessBoard.addPiece(king2);
        for(int j = 0;j<7;j++){
            chessBoard.addPiece(new Blocade("blocade",j,5));
        }
        for(int j = 6;j<10;j++){
            chessBoard.addPiece(new Blocade("blocade",6,j));
        }
        chessBoard.addPiece(new Blocade("blocade",3,6));
        chessBoard.addPiece(new Blocade("blocade",3,8));
        chessBoard.addPiece(new Blocade("blocade",3,9));

        chessBoard.addPiece(new Blocade("blocade",4,6));
        chessBoard.addPiece(new Blocade("blocade",4,7));
        chessBoard.addPiece(new Blocade("blocade",4,9));

        chessBoard.addPiece(new Blocade("blocade",5,6));
        chessBoard.addPiece(new Blocade("blocade",5,7));
        chessBoard.addPiece(new Blocade("blocade",5,8));


//konec inicializace šachovnice

        chessBoard.printBoard();

        chessBoard.savePosition("test2",player1);


        break;



}


    }}

/*TODO


udělat tutorial mapu
záznamy her


nepodstatné :

map editor
piece editor



//překreslit pawna
//překreslit letadlo

*/



    //konec kódu
