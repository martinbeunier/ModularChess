package logic;

import pieces.*;
import pieces.PoweUps.*; // Import pro PowerUpy

import java.lang.reflect.Constructor;

public class PieceFactory {

    public static Piece create(String className, String name, int x, int y, Colour colour, int rotation) {
        try {
            Class<?> clazz;
            // Zkusi najit tridu v 'pieces.', pokud neselze, zkusi 'pieces.PoweUps.'
            try {
                clazz = Class.forName("pieces." + className);
            } catch (ClassNotFoundException e) {
                clazz = Class.forName("pieces.PoweUps." + className);
            }

            // 1. Konstruktor pro PowerUp (String, int, int)
            try {
                Constructor<?> constructor = clazz.getConstructor(String.class, int.class, int.class);
                return (Piece) constructor.newInstance(name, x, y);
            } catch (NoSuchMethodException ignored) {}

            // 2. Konstruktor s rotation (Pawn, King)
            try {
                Constructor<?> constructor = clazz.getConstructor(String.class, int.class, int.class, Colour.class, int.class);
                return (Piece) constructor.newInstance(name, x, y, colour, rotation);
            } catch (NoSuchMethodException ignored) {}

            // 3. Klasicky konstruktor bez rotation (Rook, Queen...)
            Constructor<?> constructor = clazz.getConstructor(String.class, int.class, int.class, Colour.class);
            return (Piece) constructor.newInstance(name, x, y, colour);

        } catch (Exception e) {
            throw new IllegalArgumentException("Neznámá třída figurky nebo power-upu: " + className, e);
        }
    }
}