package gameloop;

/**
 * Umožňuje GameLoop oznámit "něčemu vnějšímu" (typicky GUI), že právě proběhl
 * tah — ať už lidský, nebo botův — aniž by GameLoop musel cokoliv vědět
 * o Swingu. Stejný princip jako logic.PromotionChooser.
 */
@FunctionalInterface
public interface MoveListener {
    void onMoveCompleted(int fromX, int fromY, int toX, int toY);
}