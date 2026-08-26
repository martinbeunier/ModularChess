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

        switch (choice){

    case 0: //spuštění ui
        MainFrame mainFrame = new MainFrame();
        break;
    case 1: //spuštění herní smyčky
        GameLoop gameLoop = new GameLoop();
        gameLoop.run("standard",null);
        break;
    case 2: //spuštění krokové simulace hry
        //inicializace hráčů

        Player player1 = new Player("Bílý",Colour.White,600);
        Player player2 = new Player("Černý",Colour.Black,600);


//konec inicializace hráčů

//inicializace figur

        ArrayList<Pawn> pawns= new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            pawns.add(new Pawn("White Pawn", i, 6, Colour.White, 0));
            pawns.add(new Pawn("Black Pawn", i, 2, Colour.Black, 2));
        }

        Bishop wbishop1 = new Bishop("White Bishop",1,8,Colour.White);
        Bishop wbishop2 = new Bishop("White Bishop",2,8,Colour.White);
        Bishop wbishop3 = new Bishop("White Bishop",6,8,Colour.White);
        Bishop wbishop4 = new Bishop("White Bishop",7,8,Colour.White);

        Bishop bbishop1 = new Bishop("Black Bishop",1,0,Colour.Black);
        Bishop bbishop2 = new Bishop("Black Bishop",2,0,Colour.Black);
        Bishop bbishop3 = new Bishop("Black Bishop",6,0,Colour.Black);
        Bishop bbishop4 = new Bishop("Black Bishop",7,0,Colour.Black);

        King wking = new King("White king",4,8,Colour.White,2);
        King bking = new King("Black king",4,0,Colour.Black,0);

        LandCarrier wlandCarrier1 = new LandCarrier("w Land Carrier",1,7,Colour.White);
        LandCarrier wlandCarrier2 = new LandCarrier("w Land Carrier",7,7,Colour.White);

        LandCarrier blandCarrier1 = new LandCarrier("b Land Carrier",1,1,Colour.Black);
        LandCarrier blandCarrier2 = new LandCarrier("b Land Carrier",7,1,Colour.Black);

        Queen wqueen = new Queen("White Queen",4,7,Colour.White);
        Queen bqueen = new Queen("White Queen",4,1,Colour.Black);

        Lifebuoy lifebuoy = new Lifebuoy("ability",4,4);

//konec inicializace figur


//inicializace šachovnice

        ChessBoard chessBoard = new ChessBoard(9,9);

        chessBoard.addPlayer(player1);
        chessBoard.addPlayer(player2);


        for(Pawn p : pawns){
            chessBoard.addPiece(p);
        }

        chessBoard.addPiece(wbishop1);
        chessBoard.addPiece(wbishop2);
        chessBoard.addPiece(wbishop3);
        chessBoard.addPiece(wbishop4);

        chessBoard.addPiece(bbishop1);
        chessBoard.addPiece(bbishop2);
        chessBoard.addPiece(bbishop3);
        chessBoard.addPiece(bbishop4);

        chessBoard.addPiece(wking);
        chessBoard.addPiece(bking);

        chessBoard.addPiece(wlandCarrier1);
        chessBoard.addPiece(wlandCarrier2);
        chessBoard.addPiece(blandCarrier1);
        chessBoard.addPiece(blandCarrier2);

        chessBoard.addPiece(wqueen);
        chessBoard.addPiece(bqueen);

        chessBoard.addPiece(lifebuoy);



        for (int i = 0; i < 9; i++) {
            chessBoard.addWaterSquares(i, 3);
            chessBoard.addWaterSquares(i, 4);
            chessBoard.addWaterSquares(i, 5);
        }

        for (int i = 0; i < 9; i++) {
            chessBoard.addPromotionSquares(i, 0,Colour.White);
            chessBoard.addPromotionSquares(i, 8,Colour.Black);

        }



        chessBoard.printBoard();
//konec inicializace šachovnice

        chessBoard.movePiece(4,4,4,0,player1);
        chessBoard.printBoard();

        chessBoard.savePosition("WaterFight",player1);


        break;



}


    }}

/*TODO


2.
funkční výběr map a botů
přidat výběr barvy
3.
udělat otočení mapy
udělat bota
4.
settings soubor
escape menu ve hře
5.
pieceology + grafika figur

nepodstatné :
map editor
piece editor
záznamy her





*/



    //konec kódu
