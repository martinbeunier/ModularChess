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





//konec inicializace šachovnice

        chessBoard.printBoard();

        chessBoard.savePosition("XXL chess (rip of)",player1);


        break;



}


    }}

/*TODO


vylepšit výběr map
udělat tutorial mapu
záznamy her


nepodstatné :

map editor
piece editor



//překreslit pawna
//překreslit letadlo

*/



    //konec kódu
