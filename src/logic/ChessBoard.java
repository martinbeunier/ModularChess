package logic;
import javafx.scene.shape.Path;
import main.Main;
import pieces.*;
import pieces.PoweUps.PowerUp;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;






public class ChessBoard {
    private Piece[][] board;
    private Tile[][] tiles;
    int[] enPassantTarget = null;
    private ArrayList<Player> players;
    private PromotionChooser promotionChooser = null;
    private int snapShotCount ;

    private HashMap<String, Integer> positionCounts = new HashMap<>();

    //region capsulation

    public int getWidth() {
        return board[0].length;
    }
    public int getHeight() {
        return board.length;
    }

    public Piece getPiece(int x, int y) {
        if (!inBoard(x, y, x, y)) return null;
        return board[x][y];
    }
    public ArrayList<Piece> getPieces() {
        // Pokud ještě nemáte samostatný seznam figurek,
        // můžete ho vygenerovat z pole board[][]:
        ArrayList<Piece> piecesList = new ArrayList<>();
        if (board != null) {
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[r].length; c++) {
                    Piece p = board[r][c];
                    // Přidáme jen pokud figurka není null a neobsahuje duplicity u více-políčkových figurek
                    if (p != null && !piecesList.contains(p)) {
                        piecesList.add(p);
                    }
                }
            }
        }
        return piecesList;
    }



    public Tile[][] getTiles() {
        return tiles;
    }

    public void setPromotionChooser(PromotionChooser chooser) {
        this.promotionChooser = chooser;
    }

    // Přidej tuto metodu do ChessBoard.java (např. hned vedle movePiece).
// Používá stejný výpočet jako movePiece(), jen bez provedení tahu — pro zvýraznění v GUI.

    public ArrayList<int[]> getPossibleTargets(int startX, int startY, Player player) {
        ArrayList<int[]> targets = new ArrayList<>();

        if (inBoard(startX, startY, startX, startY) == false) return targets;
        if (isPiece(startX, startY) == false) return targets;
        if (isEnemyPiece(startX, startY, player) == true) return targets;

        ArrayList<MoveType> clasicalMoves = getMovesByClass(startX, startY, MoveClass.LEAP);
        ArrayList<MoveType> bigMoves = getMovesByClass(startX, startY, MoveClass.BIG);
        ArrayList<MoveType> castleMoves = getMovesByClass(startX, startY, MoveClass.CASTLE);
        ArrayList<MoveType> carierMoves = getMovesByClass(startX, startY, MoveClass.CARRIER);
        ArrayList<MoveType> torpedoMoves = getMovesByClass(startX, startY, MoveClass.TORPEDO);

        ArrayList<MoveType> generatedMoves = getGeneratedMovesByClass(startX, startY, MoveClass.REPEAT);
        ArrayList<MoveType> linebreakerMoves = getGeneratedMovesByClass(startX, startY, MoveClass.LINEBREAKER);

        ArrayList<MoveType> validClasicalMoves = validateLeapMoves(startX, startY, clasicalMoves, player);
        ArrayList<MoveType> validBigMoves = validateBigMoves(startX, startY, bigMoves, player);
        ArrayList<MoveType> validCastleMoves = validateCastleMoves(startX, startY, castleMoves, player);
        ArrayList<MoveType> validTorpedoMoves = validateTorpedoMoves(startX, startY, torpedoMoves, player);
        ArrayList<MoveType> validCarierMoves = validateCarrierMoves(startX, startY, carierMoves, player);
        ArrayList<MoveType> validGeneratedMoves = validateGeneratedMoves(startX, startY, generatedMoves, player);
        ArrayList<MoveType> validLinebreakerMoves = validateLinebreakerMoves(startX, startY, linebreakerMoves, player);

        ArrayList<MoveType> allValidMoves = new ArrayList<>();
        allValidMoves.addAll(validClasicalMoves);
        allValidMoves.addAll(validGeneratedMoves);
        allValidMoves.addAll(validBigMoves);
        allValidMoves.addAll(validCastleMoves);
        allValidMoves.addAll(validCarierMoves);
        allValidMoves.addAll(validTorpedoMoves);
        allValidMoves.addAll(validLinebreakerMoves);

        for (MoveType m : allValidMoves) {
            int tx = startX + m.getX();
            int ty = startY + m.getY();

            if (!inBoard(startX, startY, tx, ty)) continue;

            boolean alreadyThere = false;
            for (int[] t : targets) {
                if (t[0] == tx && t[1] == ty) {
                    alreadyThere = true;
                    break;
                }
            }
            if (!alreadyThere) {
                targets.add(new int[]{tx, ty});
            }
        }

        return targets;
    }

    // Přidej tyto tři metody do ChessBoard.java (např. vedle countHeads()):

    /**
     * Vrátí pozice všech Head figurek dané barvy na desce.
     */
    public ArrayList<int[]> getHeadPositions(Colour colour) {
        ArrayList<int[]> heads = new ArrayList<>();

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] instanceof Head && board[x][y].getColour() == colour) {
                    heads.add(new int[]{x, y});
                }
            }
        }

        return heads;
    }

    /**
     * Vrátí pozice všech nepřátelských figurek (jiné barvy než targetColour),
     * které mohou právě teď platně táhnout na pole [targetX, targetY].
     * Používá se ke zjištění, kdo útočí na danou Head figurku.
     */
    public ArrayList<int[]> getAttackersOfSquare(int targetX, int targetY, Colour targetColour) {
        ArrayList<int[]> attackers = new ArrayList<>();

        for (Player enemyPlayer : players) {
            if (enemyPlayer.getColor() == targetColour) continue; // to je "naše" barva, ne útočník

            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    Piece piece = board[x][y];
                    if (piece == null) continue;
                    if (piece.getColour() != enemyPlayer.getColor()) continue;

                    ArrayList<int[]> targets = getPossibleTargets(x, y, enemyPlayer);
                    for (int[] t : targets) {
                        if (t[0] == targetX && t[1] == targetY) {
                            attackers.add(new int[]{x, y});
                            break;
                        }
                    }
                }
            }
        }

        return attackers;
    }

    /**
     * Zjistí barvu jediné zbývající Head figurky na desce (= vítěz).
     * Vrátí null, pokud zbývá 0 hlav (remíza) nebo víc než 1 (hra ještě neskončila).
     */
    public Colour getSurvivingHeadColour() {
        Colour found = null;
        int count = 0;

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] instanceof Head) {
                    found = board[x][y].getColour();
                    count++;
                }
            }
        }

        return (count == 1) ? found : null;
    }
    public String getPositionSignature(Colour toMove) {
        StringBuilder sb = new StringBuilder();
        sb.append(toMove).append("|");

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                Piece p = board[x][y];

                if (p == null) {
                    sb.append(".");
                } else {
                    sb.append(p.getClass().getSimpleName())
                            .append(p.getColour());

                    if (p instanceof OrientedPiece) {
                        sb.append("r").append(((OrientedPiece) p).getRotation());
                    }
                }
                sb.append(";");
            }
        }

        return sb.toString();
    }

    /**
     * Zaznamená aktuální pozici (zvýší její počítadlo v HashMapě) a vrátí true,
     * pokud se tahle PŘESNĚ STEJNÁ pozice (včetně toho, kdo je na tahu)
     * objevila už potřetí. O(1) místo procházení celé historie.
     */
    public boolean recordPositionAndCheckRepetition(Colour toMove) {
        String signature = getPositionSignature(toMove);

        int newCount = positionCounts.getOrDefault(signature, 0) + 1;
        positionCounts.put(signature, newCount);

        return newCount >= 3;
    }

     public boolean saveGameHistorySnapshot(String gameHistoryFilePath,Player currentPlayer,int movesWithoutCapture){
         //System.out.println(gameHistoryFilePath);

         try {
             File myObj = new File(gameHistoryFilePath);
             if (myObj.createNewFile()) {
                 System.out.println("File created: " + myObj.getName());
             } else {
                 System.out.println("File already exists.");
             }
         } catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
         }

         try {
             FileWriter myWriter = new FileWriter(gameHistoryFilePath, true);
             StringBuilder sb = new StringBuilder();

             snapShotCount++;
             sb.append("\n========================================\n");
             sb.append("GAME HISTORY SNAPSHOT "+snapShotCount+"\n");
             sb.append("========================================\n");


             if (DebugConfiguration.savePosition) System.out.println("\nWidth : " + board.length + " Height: " + board[0].length);
             sb.append("Width : ").append(board.length).append(" Height: ").append(board[0].length).append("\n");

             if (DebugConfiguration.savePosition) System.out.println("\nTiles promotion colours :");
             sb.append("\nTiles promotion colours :\n");
             for (int j = 0; j < tiles.length; j++) {
                 for (int i = 0; i < tiles[j].length; i++) {
                     if (!tiles[i][j].getPromotionColours().isEmpty()) {
                         if (DebugConfiguration.savePosition) System.out.print("promotion colours of tile : " + i + " " + j);
                         sb.append("promotion colours of tile : ").append(i).append(" ").append(j);

                         for (Colour colour : tiles[i][j].getPromotionColours()) {
                             if (DebugConfiguration.savePosition) System.out.print(" " + colour.name());
                             sb.append(" ").append(colour.name());
                         }
                         if (DebugConfiguration.savePosition) System.out.println();
                         sb.append("\n");
                     }
                 }
             }

             if (DebugConfiguration.savePosition) System.out.println("\nTiles water :");
             sb.append("\nTiles water :\n");
             for (int j = 0; j < tiles.length; j++) {
                 for (int i = 0; i < tiles[j].length; i++) {
                     if (tiles[i][j].getWater()) {
                         if (DebugConfiguration.savePosition) System.out.println("water : " + i + " " + j + "  ");
                         sb.append("water : ").append(i).append(" ").append(j).append("\n");
                     }
                 }
             }

             if (enPassantTarget != null) {
                 if (DebugConfiguration.savePosition) System.out.println("\nenPassant : " + enPassantTarget[0] + " " + enPassantTarget[1]);
                 sb.append("\nenPassant : ").append(enPassantTarget[0]).append(" ").append(enPassantTarget[1]).append("\n");
             }

             if (DebugConfiguration.savePosition) System.out.println("\nPieces :");
             sb.append("\nPieces :\n");
             for (int j = 0; j < board.length; j++) {
                 for (int i = 0; i < board[j].length; i++) {
                     if (board[i][j] != null) {
                         String pieceStr = "piece : "+board[i][j].getClass() +" "+ board[i][j].myToString2();
                         if (board[i][j] instanceof OrientedPiece) {
                             pieceStr += " " + ((OrientedPiece) board[i][j]).getRotation();
                         }
                         if (DebugConfiguration.savePosition) System.out.println(pieceStr);
                         sb.append(pieceStr).append("\n");
                     }
                 }
             }

             if (DebugConfiguration.savePosition) System.out.println("\nPlayers :");
             sb.append("\nPlayers :\n");
             for (Player player : players) {
                 String playerStr = player.myToString() + " PowerUps :";
                 for (PowerUpName powerUp : player.getPowerUps()) {
                     playerStr += " " + powerUp.name();
                 }
                 if (DebugConfiguration.savePosition) System.out.println(playerStr);
                 sb.append(playerStr).append("\n");
             }

             String currentStr = "\ncurrent player : " + currentPlayer.getColor();
             if (DebugConfiguration.savePosition) System.out.println(currentStr);
             sb.append(currentStr).append("\n");

             myWriter.write(sb.toString());
             myWriter.close();
             System.out.println("\nSuccessfully wrote to the file.");
         } catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
         }

        return true;
     }


    //  Vrátí všechny možné surové tahy na desce bez kontextu podmínek
    //  @return

  /* public ArrayList<MoveType> getPosibleMoves()
    {
        ArrayList<MoveType> goodMoves =  new ArrayList<MoveType>();
        ArrayList<MoveType> generatedMoves = new ArrayList<MoveType>();
        for (int j = 0; j < board.length; j++) {

            for (int i = 0; i < board[0].length; i++) {

                if(board[j][i] != null) {


                    //System.out.println("\nFigura : " + board[j][i].getName() + " Barva : " + board[j][i].getColour() + " X : " + board[j][i].getX() + " Y : " + board[j][i].getY());


                    for (MoveType m : board[j][i].getMoves()) {
                      //  System.out.println("1 move");
                        if (m.getRepeat() == true){



                            int x = i;
                            int y = j;

                            while(x - m.getX() >= 0  && y + m.getY() >= 0 && x + m.getX() < board.length && y + m.getY() < board[0].length)
                            {

                                x = x + m.getX();
                                y = y + m.getY();

                                System.out.println("x : " + x+ " y : "+y);
                                //generatedMoves.add(new MoveType(x-i,y-j,false));


                            }

                        }else {

                            System.out.println("Defined moves");
                            if (inBoard(i,j,i + m.getX(),j + m.getY()) == true) {
                                 System.out.println("x : " + (i+ m.getX() )+ " y : "+( j+ m.getY()));
                                 goodMoves.add(m);
                            }


                        }
                    }


                  //  System.out.println("\n");
                    for (MoveType m2 : generatedMoves) {
                        if (inBoard(i,j,i + m2.getX(),j + m2.getY()) == true) {
                            System.out.println("x : " + (i+ m2.getX() )+ " y : "+( j+ m2.getY()));
                            goodMoves.add(m2);
                        }

                    }



                }
            }
        }
return goodMoves;
    }


      //Vrátí všechny možné tahy figury na desce bez kontextu podmínek
     // @return


    public ArrayList<MoveType> getPosibleMovesOnePiece( int j,int i )
    {
        ArrayList<MoveType> goodMoves =  new ArrayList<MoveType>();

                if(board[j][i] != null) {

                    ArrayList<MoveType> generatedMoves = new ArrayList<MoveType>();
                    System.out.println("\nFigura : " + board[j][i].getName() + " Barva : " + board[j][i].getColour() + " X : " + board[j][i].getX() + " Y : " + board[j][i].getY());


                    for (MoveType m : board[j][i].getMoves()) {




                        //  System.out.println("1 move");
                        if (m.getRepeat() == true){



                            int x = i;
                            int y = j;

                            while(x - m.getX() >= 0  && y + m.getY() >= 0 && x + m.getX() < board.length && y + m.getY() < board[0].length)
                            {

                                x = x + m.getX();
                                y = y + m.getY();

                                System.out.println("x : " + x+ " y : "+y);
                                generatedMoves.add(new MoveType(x-i,y-j,false));


                            }

                        }else {

                            //System.out.println("Defined moves");
                            if (inBoard(i,j,i + m.getX(),j + m.getY()) == true) {
                                System.out.println("x : " + (i+ m.getX() )+ " y : "+( j+ m.getY()));
                                goodMoves.add(m);
                            }


                        }
                    }


                    System.out.println("\n");
                    for (MoveType m2 : generatedMoves) {
                        if (inBoard(i,j,i + m2.getX(),j + m2.getY()) == true) {
                            System.out.println("x : " + (i+ m2.getX() )+ " y : "+( j+ m2.getY()));
                            goodMoves.add(m2);
                        }

                    }





        }
        return goodMoves;
    }
*/
    //endregion


    //region constuctor and other
    public ChessBoard(int width, int height) {
        this.board = new Piece[width][height];
        this.tiles = new Tile[width][height];
        this.players = new ArrayList<>();

        for (int i = 0; i <width ; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = new Tile();
            }
        }

    }



    public boolean movePiece(int startX, int startY, int endX, int endY, Player player) {
        ArrayList<MoveType> clasicalMoves = new ArrayList<>();
        ArrayList<MoveType> generatedMoves = new ArrayList<>();
        ArrayList<MoveType> bigMoves = new ArrayList<>();
        ArrayList<MoveType> castleMoves = new ArrayList<>();
        ArrayList<MoveType> carierMoves = new ArrayList<>();
        ArrayList<MoveType> torpedoMoves = new ArrayList<>();
        ArrayList<MoveType> linebreakerMoves = new ArrayList<>();


        ArrayList<MoveType> validClasicalMoves = new ArrayList<>();
        ArrayList<MoveType> validGeneratedMoves = new ArrayList<>();
        ArrayList<MoveType> validBigMoves = new ArrayList<>();
        ArrayList<MoveType> validCastleMoves = new ArrayList<>();
        ArrayList<MoveType> validCarierMoves = new ArrayList<>();
        ArrayList<MoveType> validTorpedoMoves = new ArrayList<>();
        ArrayList<MoveType> validLinebreakerMoves = new ArrayList<>();


        ArrayList<MoveType> allValidMoves = new ArrayList<>();

        if (inBoard(startX, startY, endX, endY) == false) {
            System.out.println("\nnemůžeš se přesunout mimo šachovnici");
            return false;
        }
        if (isPiece(startX, startY) == false) {
            System.out.println("\ntam není figura");
            return false;
        }
        if (isEnemyPiece(startX, startY, player) == true) {
            System.out.println("\nTo není tvoje figura");
            return false;
        }

        System.out.println("\n\nfigura : " + board[startX][startY].getName() + " z (" + startX + ", " + startY + ") na (" + endX + ", " + endY + ")");





        clasicalMoves = getMovesByClass(startX, startY,MoveClass.LEAP);
        bigMoves = getMovesByClass(startX, startY,MoveClass.BIG);
        castleMoves = getMovesByClass(startX, startY,MoveClass.CASTLE);
        carierMoves = getMovesByClass(startX, startY,MoveClass.CARRIER);
        torpedoMoves = getMovesByClass(startX, startY,MoveClass.TORPEDO);

        generatedMoves = getGeneratedMovesByClass(startX, startY,MoveClass.REPEAT);
        linebreakerMoves = getGeneratedMovesByClass(startX, startY,MoveClass.LINEBREAKER);

     /*   for (MoveType move : carierMoves) {
            System.out.println("k"+move.getMoveClass());

        }*/

        validClasicalMoves = validateLeapMoves(startX, startY, clasicalMoves, player);
        validBigMoves = validateBigMoves(startX, startY, bigMoves, player);
        validCastleMoves = validateCastleMoves(startX, startY, castleMoves, player);
        validTorpedoMoves = validateTorpedoMoves(startX, startY, torpedoMoves, player);

        validCarierMoves = validateCarrierMoves(startX,startY,carierMoves,player);



        validGeneratedMoves = validateGeneratedMoves(startX, startY, generatedMoves, player);
        validLinebreakerMoves = validateLinebreakerMoves(startX,startY,linebreakerMoves,player);

        allValidMoves.addAll(validClasicalMoves);
        allValidMoves.addAll(validGeneratedMoves);
        allValidMoves.addAll(validBigMoves);




if(executeStandardMoves(startX,startY,endX,endY,player,allValidMoves) ==true)return  true;
if(executeCastleMoves(startX,startY,endX,endY,player,validCastleMoves) ==true)return  true;
if(executeCarrierMoves(startX,startY,endX,endY,player,validCarierMoves) == true ) return true;
if(executeTorpedoMoves(startX,startY,endX,endY,player,validTorpedoMoves) == true ) return true;
if(executeLinebreakerMoves(startX,startY,endX,endY,player,validLinebreakerMoves) == true ) return true;






        //for(MoveType move:carierMoves){





        System.out.println("pohyb neodpovídá typu figury");
        return false;


    }

    public boolean movePiece(int startX, int startY, int rotation, Player player) {
        ArrayList<MoveType> rotateMoves = new ArrayList<>();
        ArrayList<MoveType> validRotatMoves = new ArrayList<>();



        if (isPiece(startX, startY) == false) {
            System.out.println("\ntam není figura");
            return false;
        }
        if (isEnemyPiece(startX, startY, player) == true) {
            System.out.println("\nTo není tvoje figura");
            return false;
        }


        rotateMoves = getRotateMoves(startX, startY);

        validRotatMoves = validRotateMoves(startX,startY,rotateMoves);




        for (MoveType move : validRotatMoves) {


            if(move.getRotate() == rotation) {



                System.out.println("otočil ses");

                rotatePiece(startX,startY,rotation,player);







                return true;

            }

        }

        System.out.println("nemůžeš se otočit tímto způsobem");
        return false;
    }



    public void addPromotionSquares(int x ,int y ,Colour colour){
if(inBoard(x,y,x,y)) tiles[x][y].addPromotionColour(colour);
    }

    public void addWaterSquares(int x ,int y ){
        if(inBoard(x,y,x,y))tiles[x][y].setWater(true);
    }

    public void addPiece(Piece p) {
        if(inBoard(p.getX(), p.getY(),p.getX(), p.getY() )) board[p.getX()][p.getY()] = p;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void printBoard() {
        System.out.println("\n   Výpis šachovnice:\n");

        // horní osa (0 → width-1)
        System.out.print("     ");
        for (int x = 0; x < board.length; x++) {
            System.out.print(x + "    ");
        }
        System.out.println();


        for (int y = 0; y < board[0].length; y++) {

            // levá osa (0 → height-1)
            System.out.print(y + " ");

            for (int x = 0; x < board.length; x++) {

                if (board[x][y] == null) {
                    System.out.print(" ....");
                } else {
                    System.out.print(" " + board[x][y] + "");
                }
            }

            System.out.println();

        }
    }

    public String toStringPlayer(){

        return players.toString();
    }





    //endregion

    //region boolean methods

    //region basic boolean methods

    public boolean inBoard(int startX, int startY, int endX, int endY) {
        if (endX >= 0 && endY >= 0 && endX < board.length && endY < board[0].length
                && startX >= 0 && startY >= 0 && startX < board.length && startY < board[0].length) {
            return true;

        } else {

            return false;
        }
    }


    public boolean isPiece(int startX, int startY) {
        if (board[startX][startY] != null) {

            return true;
        } else {
            return false;
        }
    }

    public boolean isEnemyPiece(int startX, int startY, Player player) {
        if (board[startX][startY].getColour().equals(player.getColor())) {
            return false;
        } else {

            return true;
        }

    }

    public boolean isFriendlyPiece(int endX, int endY, Player player) {
        if (isPiece(endX, endY) == false) {

            return true;
        } else {


            if (board[endX][endY].getColour().equals(player.getColor())) {


                return false;
            } else {
                return true;
            }
        }

    }

    public boolean isBlocade(int endX, int endY) {
        if (isPiece(endX, endY) == false) {

            return true;
        } else {
            if (board[endX][endY].getColour().equals(Colour.Blockade)) {
                return false;

            } else {
                return true;
            }
        }
    }
    //endregion

    public boolean emptyLine(int startX, int startY, int endX, int endY) {

        int stepX = Integer.signum(endX - startX);
        int stepY = Integer.signum(endY - startY);

        int x = startX + stepX;
        int y = startY + stepY;

        while (x != endX || y != endY) {
            if (board[x][y] != null) {

                return false;
            }

            x += stepX;
            y += stepY;
        }
        return true;

    }

    public boolean noBlocadeLine(int startX, int startY, int endX, int endY) {

        int stepX = Integer.signum(endX - startX);
        int stepY = Integer.signum(endY - startY);

        int x = startX + stepX;
        int y = startY + stepY;

        while (x != endX || y != endY && inBoard(startX,startY,endX,endY)) {

            if(board[x][y] != null) {
                if (board[x][y].getColour().equals(Colour.Blockade)) {

                    return false;
                }
            }

            x += stepX;
            y += stepY;
        }
        return true;

    }

    public boolean emptyLineWithRook(int startX, int startY, int endX, int endY) {

        int stepX = Integer.signum(endX - startX);
        int stepY = Integer.signum(endY - startY);

        int x = startX + stepX;
        int y = startY + stepY;

        //dodělat ověření na first move king

        boolean firstTime = true;

        if (board[startX][startY].getFirstMove() == false) {
//dodělat ověření na first move rook

            return false;
        }

        while (inBoard(x,y,startX,startY)) {

if (firstTime == false)
{
    if (board[x][y] instanceof Rook)
    {
        if (board[x][y].getFirstMove()) {
//dodělat ověření na first move rook

            return true;
        }
    }
}
            firstTime = false;

            if (board[x][y] != null) {

                return false;
            }

            x += stepX;
            y += stepY;
        }

        return false;
    }

    /**
     * Pro leap ,repeat , big
     * @param endX
     * @param endY
     * @param m
     * @return
     */
    public boolean validBehaviour( int endX, int endY , MoveType m){
        if (m.getBehaviour().equals(MoveBehaviour.BOTH)) {

            return true;
        }


        if (m.getBehaviour().equals(MoveBehaviour.MOVE)) {
            if (board[endX][endY] == null) {
                return true ;
            }
        }
        if (m.getBehaviour().equals(MoveBehaviour.TAKE)) {
            if (board[endX][endY] != null) {
                return true;


            }
        }
        return false;
    }

    /**
     * Pro validate leap ,generated ,big , castle moves
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param player
     * @return
     */
    public boolean standardValidations(int startX, int startY, int endX, int endY,Player player) {

        if (inBoard(startX, startY, endX, endY) == false)   return false;
        if (isFriendlyPiece(endX, endY, player)==false) return false;
        if (isBlocade(endX, endY)==false)return false;

                    return true;



    }



//endregion




    //region moves operations



//region getters

    public ArrayList<MoveType> getGeneratedMovesByClass(int startX, int startY ,MoveClass moveClass) {
        ArrayList<MoveType> goodMoves = new ArrayList<>();



        if(DebugConfiguration.definedRepeatMoves) System.out.println("Defined generative moves " + moveClass);
        if (board[startX][startY] == null)
            return goodMoves;

        Piece piece = board[startX][startY];
        for (MoveType m : piece.getMoves()) {
            int dx = m.getX();
            int dy = m.getY();
            int step = 1;

            do {
                int endX = startX + dx * step;
                int endY = startY + dy * step;

                if (!inBoard(startX, startY, endX, endY))
                    break;


                 if (m.getMoveClass() == moveClass) {


                         goodMoves.add(new MoveType(dx * step, dy * step, m.getRequiresFirstMove(), m.getBehaviour(), MoveClass.LEAP));




                    if(DebugConfiguration.definedRepeatMoves) System.out.println("x : " + (dx * step) +" y : " + (dy * step));
                    step++;
                }
            } while (m.getMoveClass() == moveClass);
        }

        // Optional: filter moves that are inside board (already done above)
        return goodMoves;
    }




    public ArrayList<MoveType> getRotateMoves(int startX, int startY) {


        ArrayList<MoveType> goodMoves = new ArrayList<>();
       if(DebugConfiguration.definedRotateMoves) System.out.println("Defined Rotate Moves ");
        if (board[startX][startY] != null) {


            Piece piece = board[startX][startY];
            for (MoveType m : piece.getMoves()) {
                //  System.out.println("1 move");
                if (m.getMoveClass().equals(MoveClass.ROTATE)) {

                    if(DebugConfiguration.definedRotateMoves) System.out.println("r : " + m.getRotate() );
                   goodMoves.add(m);



                }
            }
        }
        return goodMoves;

    }

    /**
     * for leap ,big , castle moves
     * @param j
     * @param i
     * @param moveClass
     * @return
     */
    public ArrayList<MoveType> getMovesByClass(int j, int i,MoveClass moveClass) {
        ArrayList<MoveType> goodMoves = new ArrayList<>();

        if (board[j][i] != null) {

            if(DebugConfiguration.definedMovesByClass) System.out.println("Defined moves " + moveClass);

            for (MoveType m : board[j][i].getMoves()) {


                if (m.getMoveClass().equals(moveClass)) {



                    if (inBoard(j, i, j + m.getX(), i + m.getY()) == true) {
                        if(DebugConfiguration.definedMovesByClass)  System.out.println("x : " + (j+ m.getX() )+ " y : "+( i+ m.getY()));
                        goodMoves.add(m);
                    }


                }
            }


        }
        return goodMoves;
    }

    //endregion

    //region validations
    public ArrayList<MoveType> validateLeapMoves(int startX, int startY, ArrayList<MoveType> moves, Player player) {
        ArrayList<MoveType> validMoves = new ArrayList<>();
       if(DebugConfiguration.validLeapMoves) System.out.println("Valid Leap moves");
        for (MoveType m : moves) {


            int endX = startX + m.getX();
            int endY = startY + m.getY();


  if(standardValidations(startX, startY, endX, endY,player))
  {

      // pro TAKE tahy — normálně musí být nepřítel, ale en passant je výjimka
      if (board[startX][startY] instanceof Pawn) {
          if (m.getBehaviour() == MoveBehaviour.TAKE) {
              if (board[endX][endY] == null) {
                  // en passant — zachycený pěšec je na (endX, startY)
                  if (enPassantTarget != null &&
                          enPassantTarget[0] == endX &&
                          enPassantTarget[1] == startY) {
                      validMoves.add(m);
                  }
                  continue;
              }
          }
      }

      if (validBehaviour(endX, endY, m)) {
          if (!m.getRequiresFirstMove() || board[startX][startY].getFirstMove()) {
              validMoves.add(m);
              if(DebugConfiguration.validLeapMoves)System.out.println("x : " + endX + " y : " + endY);
          }
      }


  }
        }


        return validMoves;
    }




    public ArrayList<MoveType> validateGeneratedMoves(int startX, int startY, ArrayList<MoveType> moves, Player player) {
        ArrayList<MoveType> validMoves = new ArrayList<>();
        if(DebugConfiguration.validGeneratedMoves) System.out.println("Valid generated moves");
        for (MoveType m : moves) {

                int dx = m.getX() > 0 ? 1 : m.getX() < 0 ? -1 : 0;
                int dy = m.getY() > 0 ? 1 : m.getY() < 0 ? -1 : 0;

                int x = startX + dx;
                int y = startY + dy;

                int endX = startX + m.getX();
                int endY = startY + m.getY();

                boolean valid = true;

                while (x != endX || y != endY) {
                    if (board[x][y] != null) {
                        valid = false;
                        break;
                    }
                    x += dx;
                    y += dy;
                }

                if (valid && validBehaviour(endX, endY, m) && standardValidations(startX, startY, endX, endY, player)) {
                    validMoves.add(m);
                    if(DebugConfiguration.validGeneratedMoves) System.out.println("x : " + endX + " y : " + endY);
                }

        }
        return validMoves;
    }


    public ArrayList<MoveType> validateBigMoves(int startX, int startY, ArrayList<MoveType> moves, Player player) {
        ArrayList<MoveType> validMoves = new ArrayList<>();
        if(DebugConfiguration.validBigMoves) System.out.println("Valid big moves");
        for (MoveType m : moves) {


            int endX = startX + m.getX();
            int endY = startY + m.getY();


            if(standardValidations(startX, startY, endX, endY,player))
            {
                        if(emptyLine(startX, startY, endX, endY)) {
                            if (validBehaviour(endX, endY, m))
                            {
                                if(!m.getRequiresFirstMove() || board[startX][startY].getFirstMove())
                                {
                                validMoves.add(m);
                                    if(DebugConfiguration.validBigMoves) System.out.println("x : " + endX + " y : " + endY);
                                }
                            }
                        }


            }
        }


        return validMoves;
    }



    public ArrayList<MoveType> validateCastleMoves(int startX, int startY, ArrayList<MoveType> moves, Player player) {
        ArrayList<MoveType> validMoves = new ArrayList<>();
        if(DebugConfiguration.validCastleMoves) System.out.println("Valid castle moves");
        for (MoveType m : moves) {


            int endX = startX + m.getX();
            int endY = startY + m.getY();


            if(standardValidations(startX, startY, endX, endY,player))
            {
                        if(emptyLineWithRook(startX, startY,endX,endY)) {
                            if (validBehaviour(endX, endY, m))
                            {
                                if(!m.getRequiresFirstMove() || board[startX][startY].getFirstMove())
                                {
                                    validMoves.add(m);
                                    if(DebugConfiguration.validCastleMoves)System.out.println("x : " + endX + " y : " + endY);
                                }
                            }
                        }


            }
        }


        return validMoves;
    }

    public ArrayList<MoveType> validateCarrierMoves(
            int startX,
            int startY,
            ArrayList<MoveType> carrierMoves,
            Player player)
    {
        ArrayList<MoveType> validMoves = new ArrayList<>();
        if(DebugConfiguration.validCarrierMoves)System.out.println("Valid Carrier moves");
        if(board[startX][startY] instanceof Carrier) {

            Carrier carrier = (Carrier) board[startX][startY];
            ArrayList<OcupationSquare> squares = carrier.getOcupationSquares();

            // Sestav set vlastních pozic carrieru (absolutní souřadnice)
            HashSet<String> ownSquares = new HashSet<>();
            ownSquares.add(startX + "," + startY); // střed
            for (OcupationSquare square : squares) {
                int ox = startX + square.getX();
                int oy = startY + square.getY();
                ownSquares.add(ox + "," + oy);
            }

            for (MoveType move : carrierMoves) {
                boolean valid = true;

                int dx = move.getX();
                int dy = move.getY();

                int endX = startX + dx;
                int endY = startY + dy;

                for (OcupationSquare square : squares) {
                    int targetX = endX + square.getX();
                    int targetY = endY + square.getY();

                    // 1) kontrola hranic
                    if (!inBoard(targetX, targetY, targetX, targetY)) {
                        valid = false;
                        break;
                    }

                    // 2) přeskočit vlastní čtverce carrieru
                    if (ownSquares.contains(targetX + "," + targetY)) {
                        continue;
                    }

                    // 3) kolize jen na předních hranových čtvercích
                    if (isFrontSquare(square, squares, dx, dy)) {
                        Piece piece = board[targetX][targetY];

                        if (piece != null) {
                            if(DebugConfiguration.validCarrierMoves){System.out.println(
                                    "KOLIZE na " + targetX + " " + targetY +
                                            " s " + piece.getName()
                            );}
                            valid = false;
                            break;
                        }
                    }
                }

                if (valid) {
                    validMoves.add(move);
                    if(DebugConfiguration.validCarrierMoves)System.out.println("x : " + endX + " y : " + endY);
                }
            }
        }
        return validMoves;
    }

    public ArrayList<MoveType> validateTorpedoMoves( int startX,int startY,ArrayList<MoveType> moves,Player player) {
        ArrayList<MoveType> validMoves = new ArrayList<>();
        if (DebugConfiguration.validTorpedoMoves) System.out.println("Valid torpedo moves");
        for (MoveType m : moves) {


            int endX = startX + m.getX();
            int endY = startY + m.getY();


           if(inBoard(endX, endY, endX, endY)) {
               if (noBlocadeLine(startX, startY, endX, endY)) {

                   if (!m.getRequiresFirstMove() || board[startX][startY].getFirstMove()) {
                       validMoves.add(m);
                       if (DebugConfiguration.validTorpedoMoves) System.out.println("x : " + endX + " y : " + endY);
                   }

               }

           }

        }


        return validMoves;
    }

    public ArrayList<MoveType> validateLinebreakerMoves(int startX, int startY, ArrayList<MoveType> moves, Player player)//TODO opravit validaci
    {

        ArrayList<MoveType> validMoves = new ArrayList<>();
        if(DebugConfiguration.validLinebreakerMoves) System.out.println("Valid linebreaker moves");
        for (MoveType m : moves) {


            int dx = m.getX() > 0 ? 1 : m.getX() < 0 ? -1 : 0;
            int dy = m.getY() > 0 ? 1 : m.getY() < 0 ? -1 : 0;

            int endX = startX + m.getX();
            int endY = startY + m.getY();

            // první figura
            if (!inBoard(endX, endY, endX, endY)) continue;
            if (board[endX][endY] == null) continue;
            if (board[endX][endY].getColour() == player.getColor()) continue;
            if (board[endX][endY].getColour() == Colour.Blockade) continue;

            // druhá figura za ní
            int behindX = endX - dx;
            int behindY = endY - dy;

            if (!inBoard(behindX, behindY, behindX, behindY)) continue;
            if (board[behindX][behindY] == null) continue;
            if (board[behindX][behindY].getColour() == player.getColor()) continue;
            if (board[behindX][behindY].getColour() == Colour.Blockade) continue;



            int checkX = startX + dx;
            int checkY = startY + dy;

            boolean valid = true;

            while (checkX != behindX || checkY != behindY) {

                if (board[checkX][checkY] != null) {
                    valid = false;
                    break;
                }

                checkX += dx;
                checkY += dy;
            }


/*
            System.out.println("start x : " + startX + " y : " + startY);
            System.out.println("end x : " + endX + " y : " + endY);
            System.out.println("behind x : " + behindX + " y : " + behindY);
            System.out.println("d x : " + dx + " y : " + dy);*/


if (valid) {
    if (DebugConfiguration.validLinebreakerMoves) System.out.println("x : " + endX + " y : " + endY);
    validMoves.add(m);
}




        }



return validMoves;
    }

    public ArrayList<MoveType> validRotateMoves(int startX,int startY, ArrayList<MoveType> moves){
        ArrayList<MoveType> validMoves = new ArrayList<>();

        if(DebugConfiguration.validRotateMoves)System.out.println("valid rotate moves");
for (MoveType m : moves) {
    System.out.println(m.getRotate());
    if(!m.getRequiresFirstMove() || board[startX][startY].getFirstMove())
    {
        validMoves.add(m);
        if(DebugConfiguration.validRotateMoves)System.out.println("x : " + startX + " y : " + startY + " r : " + m.getRotate());
    }

}

        return validMoves;


    }

    private boolean isFrontSquare(
            OcupationSquare square,
            ArrayList<OcupationSquare> squares,
            int dx,
            int dy)
    {
        int nextX = square.getX() + dx;
        int nextY = square.getY() + dy;

        for (OcupationSquare other : squares)
        {
            if (other.getX() == nextX &&
                    other.getY() == nextY)
            {
                return false; // není hrana
            }
        }

        return true; // je hrana ve směru pohybu
    }
    //endregion

    //region executions

    public void switchRook (int startX ,int startY,int endX,int endY) {
        int stepX = Integer.signum(endX - startX);
        int stepY = Integer.signum(endY - startY);

        int x = startX + stepX;
        int y = startY + stepY;

        startX = x;
        startY = y;

        while (inBoard(x,y,startX,startY))
        {


                if (board[x][y] instanceof Rook)
                {


                    board[startX][startY] = board[x][y];
                    board[x][y] = null;


                }
            x += stepX;
            y += stepY;
            }


  }




    public void rotatePiece(int endX, int endY,int rotation, Player player)
    {
       RotablePiece piece =  (RotablePiece)  board[endX][endY];

       int endRotation = ((piece.getRotation()+rotation) %4);

        ArrayList<RotateFactory> options =
                piece.getRotatePieces();

        if( options.isEmpty() == false) {


            int choice = 0;


            RotateFactory selected = options.get(choice);

            board[endX][endY] =
                    selected.create(
                            piece.getName(),
                            endX,
                            endY,
                            endRotation,
                            player.getColor()

                    );


        }
    }



    public boolean executeStandardMoves(int startX,int startY,int endX, int endY ,Player player , ArrayList<MoveType> allValidMoves){
        for (MoveType move : allValidMoves) {
            int targetX = startX + move.getX();
            int targetY = startY + move.getY();

            if (targetX == endX && targetY == endY) {

                // Uložíme si referenci na pohybující se figurku PŘED tím, než board[startX][startY] přepíšeme na null.
                Piece movingPiece = board[startX][startY];

                addPowerUp(endX, endY, player);
                board[endX][endY] = movingPiece;
                board[endX][endY].setFirstMove(false);
                board[startX][startY] = null;

                // En passant smí provést JEDINĚ pěšec — proto instanceof Pawn navíc ke geometrickým podmínkám.
                if (movingPiece instanceof Pawn &&
                        enPassantTarget != null &&
                        endX == enPassantTarget[0] &&
                        startY == enPassantTarget[1]) {
                    board[enPassantTarget[0]][enPassantTarget[1]] = null;
                    enPassantTarget = null;
                }

                if (board[endX][endY] instanceof Pawn && Math.abs(endX - startX) == 0 && Math.abs(endY - startY) == 2) {
                    enPassantTarget = new int[]{endX, endY}; // pozice pěšce který skočil
                } else {
                    enPassantTarget = null; // každý jiný tah en passant zruší
                }

                promotion(endX,endY);

                return true;
            }
        }
        return false;
    }

    public boolean executeCastleMoves(int startX,int startY,int endX, int endY ,Player player , ArrayList<MoveType> validCastleMoves){
        for (MoveType move : validCastleMoves) {
            int targetX = startX + move.getX();
            int targetY = startY + move.getY();

            if (targetX == endX && targetY == endY) {

                addPowerUp(endX, endY, player);
                board[endX][endY] = board[startX][startY];
                board[endX][endY].setFirstMove(false);
                board[startX][startY] = null;
                switchRook(startX,startY,endX,endY);

                return true;

            }
        }
        return false;
    }



    public boolean executeCarrierMoves(int startX,int startY,int endX, int endY ,Player player , ArrayList<MoveType> validCarierMoves){

        boolean firstTime = true;
        for(MoveType move: validCarierMoves){

           // if(firstTime){System.out.println("moves v exekuci");firstTime=false;}

            int targetX = startX + move.getX();
            int targetY = startY + move.getY();

            System.out.println((move.getX() + startX )+" " +( move.getY()+ startY) );

            if (targetX == endX && targetY == endY) {
                if( board[startX][startY] instanceof Carrier) {

                    Carrier carrier = (Carrier) board[startX][startY];
                    ArrayList<OcupationSquare> OcupationSquares = new ArrayList<>();
                    OcupationSquares = carrier.getOcupationSquares();


                    ArrayList<TempPiece> tempPieces = new ArrayList<>();

                    board[startX][startY] = null;

                    for (OcupationSquare square : OcupationSquares) {
                        if (isPiece(startX + square.getX(), startY + square.getY())) {
                            tempPieces.add(new TempPiece(startX + square.getX(), startY + square.getY(), board[startX + square.getX()][startY + square.getY()]));

                        }
                    }

                    for (OcupationSquare square : OcupationSquares) {
                        board[startX + square.getX()][startY + square.getY()] = null;
                    }

                    for (TempPiece piece : tempPieces) {
                        // System.out.println(piece.getX() + " " + piece.getY());
                        board[move.getX() + piece.getX()][move.getY() + piece.getY()] = piece.getPiece();
                             promotion(move.getX() + piece.getX(),move.getY() + piece.getY());
                    }

                    board[startX + move.getX()][startY + move.getY()] = carrier;


                    return true;
                }
            }
        }



return false;

    }

    public boolean executeTorpedoMoves(int startX,int startY,int endX, int endY ,Player player , ArrayList<MoveType> validTorpedoMoves){
        for (MoveType move : validTorpedoMoves) {
            int targetX = startX + move.getX();
            int targetY = startY + move.getY();

            if (targetX == endX && targetY == endY) {

                // Krok posunu v každém směru: -1, 0 nebo +1 (stejný princip jako emptyLine()/switchRook())
                int stepX = Integer.signum(endX - startX);
                int stepY = Integer.signum(endY - startY);

                // Zničíme VŠECHNY figurky na políčkách MEZI start a cíl (obě krajní pole zatím necháváme)
                int x = startX + stepX;
                int y = startY + stepY;

                while (x != endX || y != endY) {
                    addPowerUp(x, y, player);
                    board[x][y] = null;
                    x += stepX;
                    y += stepY;
                }

                // Přesun figurky na cílové pole (případně sebere figurku, co tam stála)
                addPowerUp(endX, endY, player);
                board[endX][endY] = board[startX][startY];
                board[endX][endY].setFirstMove(false);
                board[startX][startY] = null;

                promotion(endX, endY);

                return true;
            }
        }

        return false;
    }

    public boolean executeLinebreakerMoves(int startX,int startY,int endX, int endY ,Player player , ArrayList<MoveType> validLinebreakerMoves){


        for (MoveType move : validLinebreakerMoves)
        {

            int targetX = startX + move.getX();
            int targetY = startY + move.getY();

            int stepX = startX;
            int stepY = startY;

            if (targetX == endX && targetY == endY) {


                if (stepX > targetX) {
                    stepX = stepX - 1;
                }
                if (stepY > targetY) {
                    stepY = stepY - 1;
                }
                if (stepX < targetX) {
                    stepX = stepX + 1;
                }
                if (stepY < targetY) {
                    stepY = stepY + 1;
                }


                addPowerUp(endX, endY, player);

              //  System.out.println(endX + " " + endY + " " + stepX + " " + stepY);


                board[endX][endY ] = null;
                board[startX][startY] = null;

               int deleteX = endX;
               int deleteY = endY;

                if(stepX > endX){deleteX = endX + 1;}
                if(stepX < endX){deleteX = endX - 1;}
          //      if(stepX == endX){deleteX = endX;}

               if(stepY > endY){deleteY = endY + 1;}
                if(stepY < endY){deleteY = endY - 1;}
             //   if(stepY == endY){deleteY = endY;}

               // System.out.println(deleteX + " " + deleteY);

                board[deleteX][deleteY] = null;

                addPowerUp(deleteX, deleteY, player);
                addPowerUp(endX, endY, player);


                return true;

            }


        }

        return false;
    }



//endregion





        //endregion

    //region interactions

 /*   public void promotion(int endX, int endY ) stará metoda bez gui
    {
        Piece piece = board[endX][endY];

        ArrayList<PromotionFactory> options =
                piece.getPromotionPieces();

        if( options.isEmpty() == false) {


            ArrayList<Colour> promotionColours = new ArrayList<>();
            promotionColours = tiles[endX][endY].getPromotionColours();

            for (Colour colour : promotionColours)
            {


                if (colour == board[endX][endY].getColour()) {


                    int choice = 0;


                    do {
                        System.out.println("\nVyber si promotion :");
                        for (int i = 0; i < options.size(); i++) {
                            Piece p = options.get(i).create(0, 0, board[endX][endY].getColour());

                            System.out.println(i + " " + p.getName());
                        }
                        choice = Main.scanner.nextInt();
                    }
                    while (choice < 0 || choice >= options.size());


                    PromotionFactory selected = options.get(choice);

                    board[endX][endY] =
                            selected.create(
                                    endX,
                                    endY,
                                    board[endX][endY].getColour()

                            );
                }
            }
        }
    }*/


    public void promotion(int endX, int endY)
    {
        Piece piece = board[endX][endY];

        ArrayList<PromotionFactory> options = piece.getPromotionPieces();

        if (options.isEmpty() == false) {

            ArrayList<Colour> promotionColours = tiles[endX][endY].getPromotionColours();

            for (Colour colour : promotionColours) {

                if (colour == board[endX][endY].getColour()) {

                    // Připravíme si jména figurek pro nabídku (GUI i konzole je použijí stejně)
                    String[] names = new String[options.size()];
                    int[] values = new int[options.size()];

                    for (int i = 0; i < options.size(); i++) {
                        Piece p = options.get(i).create(0, 0, board[endX][endY].getColour());
                        names[i] = p.getName();
                        values[i] = p.getValue();
                    }

                    int choice;

                    if (promotionChooser != null) {
                        // GUI (nebo jiný) chooser — NEBLOKUJE konzoli, jen zavolá dodanou implementaci
                        choice = promotionChooser.choosePromotion(endX, endY, names);
                        if (choice < 0 || choice >= options.size()) {
                            choice = 0; // pojistka, kdyby chooser vrátil neplatnou hodnotu
                        }
                    } else {
                        // Fallback pro spuštění bez GUI (např. run() metoda v GameLoop) — staré chování
                        do {
                            System.out.println("\nVyber si promotion :");
                            for (int i = 0; i < names.length; i++) {
                                System.out.println(i + " " + names[i]);
                            }
                            choice = Main.scanner.nextInt();
                        }
                        while (choice < 0 || choice >= options.size());
                    }

                    PromotionFactory selected = options.get(choice);

                    board[endX][endY] =
                            selected.create(
                                    endX,
                                    endY,
                                    board[endX][endY].getColour()
                            );
                }
            }
        }
    }

    public boolean waterInterAction()
    {
boolean drowned = false;

        boolean[][] saveSquares = new boolean[board.length][board[0].length] ;
        for (int y=0;y<board.length;y++)
        {
            for(int x=0;x<board[y].length;x++)
            {
                if( board[x][y] instanceof  Carrier){

                    for(OcupationSquare square  :((Carrier) board[x][y]).getOcupationSquares())
                    {
                        saveSquares[square.getX()+x][square.getY()+y] = true;
                    }
                }

            }
        }

        for (int y=0;y<board.length;y++){
            for(int x=0;x<board[y].length;x++){

                if(board[x][y] != null)
                {
                    //místo Pawn parametr boolean drownable
                    if (board[x][y] instanceof Pawn && tiles[x][y].getWater() == true)
                    {
                        if(saveSquares[x][y] == false)
                        {
                            for(Player player : players)
                            {
                                if(board[x][y] != null)
                                {
                                    if (player.getColor() == board[x][y].getColour()) {
                                        if (!player.getPowerUps().contains(PowerUpName.LIFEBUOY)) {
                                            if (drowned == false){printBoard()  ; drowned = true;}
                                            board[x][y] = null;
                                        }
                                    }
                                }
                            }



                        }
                    }
                }

            }
        }

return drowned;
    }


    /**
     * adds power up to player list power ups.
     * @param endX
     * @param endY
     * @param player
     */
    public void addPowerUp(int endX, int endY, Player player) {


        Piece piece = board[endX][endY];

        if (piece instanceof PowerUp powerUp) {
            player.addPowerUp(powerUp.getPowerUpName());
            System.out.println(player.toString());

        }


    }

    public int countHeads(){

        int count = 0;

        for(int y=0;y<board.length;y++){
            for(int x=0;x<board[y].length;x++){
                if(board[x][y] != null){

                    if(board[x][y] instanceof Head){
                        count++;
                    }
                }

            }
        }



        return count;
    }

    //endregion




//region file Management

        public void savePosition(String filename,Player currentPlayer)
        {



            try {
                File myObj = new File("files\\positions\\" + filename + ".chess");
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            try {
                FileWriter myWriter = new FileWriter("files\\positions\\" + filename + ".chess");
                StringBuilder sb = new StringBuilder();

                if (DebugConfiguration.savePosition) System.out.println("\nWidth : " + board.length + " Height: " + board[0].length);
                sb.append("Width : ").append(board.length).append(" Height: ").append(board[0].length).append("\n");

                if (DebugConfiguration.savePosition) System.out.println("\nTiles promotion colours :");
                sb.append("\nTiles promotion colours :\n");
                for (int j = 0; j < tiles.length; j++) {
                    for (int i = 0; i < tiles[j].length; i++) {
                        if (!tiles[i][j].getPromotionColours().isEmpty()) {
                            if (DebugConfiguration.savePosition) System.out.print("promotion colours of tile : " + i + " " + j);
                            sb.append("promotion colours of tile : ").append(i).append(" ").append(j);

                            for (Colour colour : tiles[i][j].getPromotionColours()) {
                                if (DebugConfiguration.savePosition) System.out.print(" " + colour.name());
                                sb.append(" ").append(colour.name());
                            }
                            if (DebugConfiguration.savePosition) System.out.println();
                            sb.append("\n");
                        }
                    }
                }

                if (DebugConfiguration.savePosition) System.out.println("\nTiles water :");
                sb.append("\nTiles water :\n");
                for (int j = 0; j < tiles.length; j++) {
                    for (int i = 0; i < tiles[j].length; i++) {
                        if (tiles[i][j].getWater()) {
                            if (DebugConfiguration.savePosition) System.out.println("water : " + i + " " + j + "  ");
                            sb.append("water : ").append(i).append(" ").append(j).append("\n");
                        }
                    }
                }

                if (enPassantTarget != null) {
                    if (DebugConfiguration.savePosition) System.out.println("\nenPassant : " + enPassantTarget[0] + " " + enPassantTarget[1]);
                    sb.append("\nenPassant : ").append(enPassantTarget[0]).append(" ").append(enPassantTarget[1]).append("\n");
                }

                if (DebugConfiguration.savePosition) System.out.println("\nPieces :");
                sb.append("\nPieces :\n");
                for (int j = 0; j < board.length; j++) {
                    for (int i = 0; i < board[j].length; i++) {
                        if (board[i][j] != null) {
                            String pieceStr = "piece : "+board[i][j].getClass() +" "+ board[i][j].myToString2();
                            if (board[i][j] instanceof OrientedPiece) {
                                pieceStr += " " + ((OrientedPiece) board[i][j]).getRotation();
                            }
                            if (DebugConfiguration.savePosition) System.out.println(pieceStr);
                            sb.append(pieceStr).append("\n");
                        }
                    }
                }

                if (DebugConfiguration.savePosition) System.out.println("\nPlayers :");
                sb.append("\nPlayers :\n");
                for (Player player : players) {
                    String playerStr = player.myToString() + " PowerUps :";
                    for (PowerUpName powerUp : player.getPowerUps()) {
                        playerStr += " " + powerUp.name();
                    }
                    if (DebugConfiguration.savePosition) System.out.println(playerStr);
                    sb.append(playerStr).append("\n");
                }

                String currentStr = "\ncurrent player : " + currentPlayer.getColor();
                if (DebugConfiguration.savePosition) System.out.println(currentStr);
                sb.append(currentStr).append("\n");

                myWriter.write(sb.toString());
                myWriter.close();
                System.out.println("\nSuccessfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

    public static ChessBoard loadPosition(String filename, ArrayList<Player> playersOutput) {
        ChessBoard chessBoard = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader("files\\positions\\" + filename + ".chess"));
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Width")) {
                    // "Width : 8 Height: 8"
                    String[] nums = line.replaceAll("[^0-9]+", " ").trim().split("\\s+");
                    int width = Integer.parseInt(nums[0]);
                    int height = Integer.parseInt(nums[1]);
                    chessBoard = new ChessBoard(width, height);
                }

                else if (chessBoard == null) {
                    // Width musí být první řádek v souboru, jinak nemáme kam ukládat
                    throw new IllegalStateException("Soubor neobsahuje řádek 'Width' jako první nastavovací řádek.");
                }

                else if (line.startsWith("promotion colours of tile")) {
                    String[] parts = line.split(" : ")[1].trim().split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    for (int i = 2; i < parts.length; i++) {
                        chessBoard.addPromotionSquares(x, y, Colour.valueOf(parts[i]));
                    }
                }

                else if (line.startsWith("water")) {
                    String[] parts = line.split(" : ")[1].trim().split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    chessBoard.addWaterSquares(x, y);
                }

                else if (line.startsWith("enPassant")) {
                    String[] parts = line.split(" : ")[1].trim().split(" ");
                    int[] target = new int[]{
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1])
                    };
                    chessBoard.setEnPassantTarget(target);
                }

                else if (line.startsWith("piece")) {
                    // "piece : class pieces.Pawn ;Black Pawn; 0 1 Black true 100 2"
                    String className = line.split("class ")[1].split(" ")[0].trim();
                    String simpleClass = className.substring(className.lastIndexOf('.') + 1);

                    String[] semicolons = line.split(";");
                    String name = semicolons[1].trim();
                    String[] rest = semicolons[2].trim().split(" ");
                    int x = Integer.parseInt(rest[0]);
                    int y = Integer.parseInt(rest[1]);
                    Colour colour = Colour.valueOf(rest[2]);
                    boolean firstMove = Boolean.parseBoolean(rest[3]);
                    int rotation = rest.length > 5 ? Integer.parseInt(rest[5].trim()) : 0;

                    Piece p = PieceFactory.create(simpleClass, name, x, y, colour, rotation);
                    p.setFirstMove(firstMove);
                    chessBoard.addPiece(p);
                }

                else if  (line.startsWith("Player :")) {
                    // "Player :  ;Bílý; White 600 PowerUps :"
                    String[] semicolons = line.split(";");
                    String playerName = semicolons[1].trim(); //1649
                    String[] rest = semicolons[2].trim().split(" ");
                    Colour colour = Colour.valueOf(rest[0]);
                    int elo = Integer.parseInt(rest[1]);

                    Player player = new Player(playerName, colour, elo);

                    if (line.contains("PowerUps :")) {
                        String[] powerupParts = line.split("PowerUps :", -1);
                        String afterPowerUps = powerupParts.length > 1 ? powerupParts[1].trim() : "";
                        if (!afterPowerUps.isEmpty()) {
                            for (String pu : afterPowerUps.split(" ")) {
                                if (!pu.isEmpty()) {
                                    player.addPowerUp(PowerUpName.valueOf(pu));
                                }
                            }
                        }
                    }

                    chessBoard.addPlayer(player);
                    playersOutput.add(player);
                }

                else if (line.startsWith("current player")) {
                    Colour colour = Colour.valueOf(line.split(" : ")[1].trim());
                    for (Player p : playersOutput) {
                        if (p.getColor() == colour) {
                            playersOutput.add(p); // aktuální hráč na konci seznamu
                            break;
                        }
                    }
                }
            }

            br.close();
            System.out.println("Position loaded successfully.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return chessBoard;
    }
    public void reset() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = null;
                tiles[i][j] = new Tile();
            }
        }
        players.clear();
        enPassantTarget = null;
    }

    public void setEnPassantTarget(int[] enPassantTarget){
        this.enPassantTarget = enPassantTarget;
    }

    //endregion

   /* public static Point fromFile(String filename) {
        // na začátku loadPosition přečti první řádek a zjisti velikost
// "Width : 8 Height: 8"
        String firstLine = readLine(filename);
        String[] parts = firstLine.split(" ");
        int width = Integer.parseInt(parts[2]);
        int height = Integer.parseInt(parts[4]);

Point point = new Point(width, height);
        return point;
    }*/
}
