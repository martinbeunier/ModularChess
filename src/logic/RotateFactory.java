package logic;

import pieces.*;



    @FunctionalInterface
    public interface RotateFactory {
        Piece create(String name,int x, int y, int rotation, Colour colour);
    }
