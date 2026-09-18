package main;

import java.util.ArrayList;
import java.util.Scanner;

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

                Emperor wemperor = new Emperor("White Emperor",4,8,Colour.White);
                Emperor bemperor = new Emperor("Black Emperor",4,0,Colour.Black);

                LandCarrier wlandCarrier1 = new LandCarrier("w Land Carrier",1,7,Colour.White);
                LandCarrier wlandCarrier2 = new LandCarrier("w Land Carrier",7,7,Colour.White);

                LandCarrier blandCarrier1 = new LandCarrier("b Land Carrier",1,1,Colour.Black);
                LandCarrier blandCarrier2 = new LandCarrier("b Land Carrier",7,1,Colour.Black);

                Queen wqueen = new Queen("White Queen",4,7,Colour.White);
                Queen bqueen = new Queen("Black Queen",4,1,Colour.Black);

                Torpedo wtorpedo = new Torpedo("w Torpedo",3,8,Colour.White,0);
                Torpedo btorpedo = new Torpedo("b Torpedo",3,0,Colour.Black,2);

                Wasp wwasp = new Wasp("white wasp",0,7,Colour.White);
                Wasp bwasp = new Wasp("black wasp",0,0,Colour.Black);

                Lifebuoy lifebuoy = new Lifebuoy("ability",4,4);
                OverClocker overClocker = new OverClocker("OverClocker",1,4);

                Blocade blocade = new Blocade("blocade",7,4);




//konec inicializace figur


//inicializace šachovnice

                ChessBoard chessBoard = new ChessBoard(13,9);

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

                chessBoard.addPiece(wemperor);
                chessBoard.addPiece(bemperor);

                chessBoard.addPiece(wlandCarrier1);
                chessBoard.addPiece(wlandCarrier2);
                chessBoard.addPiece(blandCarrier1);
                chessBoard.addPiece(blandCarrier2);

                chessBoard.addPiece(wqueen);
                chessBoard.addPiece(bqueen);

                chessBoard.addPiece(wtorpedo);
                chessBoard.addPiece(btorpedo);

                chessBoard.addPiece(wwasp);
                chessBoard.addPiece(bwasp);

                chessBoard.addPiece(lifebuoy);
                chessBoard.addPiece(overClocker);
                chessBoard.addPiece(blocade);



                for (int i = 0; i < 9; i++) {
                    chessBoard.addWaterSquares(i, 3);
                    chessBoard.addWaterSquares(i, 4);
                    chessBoard.addWaterSquares(i, 5);
                }

                for (int i = 0; i < 9; i++) {
                    chessBoard.addPromotionSquares(i, 0,Colour.White);
                    chessBoard.addPromotionSquares(i, 8,Colour.Black);

                }
                chessBoard.addPromotionSquares(10,5,Colour.White);
                chessBoard.addPromotionSquares(10,5,Colour.Black);
                chessBoard.addPromotionSquares(11,5,Colour.White);
                chessBoard.addPromotionSquares(11,5,Colour.Black);




//konec inicializace šachovnice

                chessBoard.printBoard();

                chessBoard.savePosition("test 2",player1);





        break;



}


    }}

/*TODO






Přidat campaign .

Přidat progress systém .



přidat mazání starých her
přidat konvezri na pgn
přidat zvuk do gamePlayer
přidat speciální moves do záznamu

tutorial piece

nepodstatné :
přidat ,ať můžu vybrat(označit piece), než odehraje bot
map editor
piece editor
dát zápis hry do vlastního vlákna



//překreslit letadlo

*/


    //konec kódu


/*
```
@Override
public String toString() {
    return "x  x";
}

public String myToString() {
    return "Piece{" +
            "name='" + name + '\'' +
            ", x=" + x +
            ", y=" + y +
            ", colour=" + colour +
            ", moves=" + moves +
            '}';
}




public String myToString2() {
    return
             ";"+name  + "; "+
             x + " "
             + y + " "
             + colour + " "
             + firstMove + " "
             + value
            ;
}
```

Ano pawn je oriented piece , airplane je rotable piece , Vzhledem k tomu ,že se povedlo poskládat mapy do souboru a pak je načíst jako inicializační pozice ,nemněl by ten přehrávač být snad takový problém .
 */

/*
 Sezam tahů :
„sudá snapshot“	Bílý 	„lichý snapshot“	černý
2	ArciBishop x,y -> x,y	3	ArciBishop x,y -> x,y
4	Airplane x,y -> r	5
…		…





*/