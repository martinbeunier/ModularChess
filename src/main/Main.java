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
                //inicializace hráčů

                Player player1 = new Player("Bílý",Colour.White,600);
                Player player2 = new Player("Černý",Colour.Black,600);


//konec inicializace hráčů

//inicializace figur




                Emperor wemperor = new Emperor("White Emperor",4,8,Colour.White);
                Emperor bemperor = new Emperor("Black Emperor",4,0,Colour.Black);
                Emperor wemperor2 = new Emperor("White Emperor",5,8,Colour.White);
                Emperor bemperor2 = new Emperor("Black Emperor",5,0,Colour.Black);




//konec inicializace figur


//inicializace šachovnice

                ChessBoard chessBoard = new ChessBoard(13,9);

                chessBoard.addPlayer(player1);
                chessBoard.addPlayer(player2);






                chessBoard.addPiece(wemperor);
                chessBoard.addPiece(bemperor);
                chessBoard.addPiece(wemperor2);
                chessBoard.addPiece(bemperor2);







//konec inicializace šachovnice


                chessBoard.printBoard();

                chessBoard.savePosition("test 2 v 2", player1);





        break;



}


    }}

/*TODO


zobrazit v map select dohrané mapy .
opravit metodu na counting kingů .


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


