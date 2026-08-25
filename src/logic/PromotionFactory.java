package logic;

import pieces.*;

@FunctionalInterface
public interface PromotionFactory {
    Piece create(int x, int y, Colour colour);
}