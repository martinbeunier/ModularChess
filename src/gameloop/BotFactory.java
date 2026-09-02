package gameloop;

/**
 * Vybere konkrétní implementaci Bot podle názvu (z GUI, ActionCommand tlačítka apod.).
 * Jednoduchý switch — pro pár pevně daných typů botů je tohle mnohem
 * spolehlivější a čitelnější než reflexe (Class.forName).
 */
public class BotFactory {

    public static Bot createBot(String opponentName) {
        if (opponentName == null) return null;

        switch (opponentName) {
            case "Bot 1":
                return new gameloop.RandomBot();
            case "Bot 2":
                return new gameloop.GreedyBot();
            case "Bot 3":
                return new gameloop.TacticalBot();
            default:
                return null; // "Against yourself" nebo neznámá hodnota -> žádný bot
        }
    }
}