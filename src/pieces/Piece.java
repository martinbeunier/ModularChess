package pieces;
import logic.*;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Piece {

    private String name;
    private int x;
    private int y;
    private Colour colour;
    private ArrayList<MoveType> moves;
    private boolean firstMove;
    private int value;
    private ArrayList<PromotionFactory> promotionPieces;





    public Piece(String name,int x, int y, Colour colour) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.colour = colour;
        moves = new ArrayList<MoveType>();
        firstMove = true;
        value = 0;
        promotionPieces = new ArrayList<>();


    }

    public Piece(String name,int x, int y, Colour colour , int value) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.colour = colour;
        moves = new ArrayList<MoveType>();
        firstMove = true;
        this.value = value;
        promotionPieces = new ArrayList<>();


    }



    public void addMove(MoveType m) {
        moves.add(m);
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Colour getColour() {
        return colour;
    }

    public ArrayList<MoveType> getMoves() {
        return moves;
    }

    public boolean getFirstMove() {
        return firstMove;
    }



    public ArrayList<PromotionFactory> getPromotionPieces()
    {
        return promotionPieces;
    }

    public void addPromotionPiece(PromotionFactory factory)
    {
        promotionPieces.add(factory);
    }


    public int getValue() {
        return value;
    }

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
}
