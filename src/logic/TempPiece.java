package logic;

import pieces.*;

class TempPiece {
    Piece piece;
    int x;
    int y;


    public TempPiece( int x, int y,Piece piece) {
        this.piece = piece;
        this.x = x;
        this.y = y;
    }

    public Piece getPiece() {
        return piece;
    }
    public int getX() {
        return x;

    }
    public int getY() {
        return y;
    }
}


