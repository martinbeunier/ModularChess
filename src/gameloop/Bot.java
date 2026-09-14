package gameloop;

import logic.Player;
import profile.PlayerManager;

/**
 * Společný základ pro všechny typy botů. Každý konkrétní bot (RandomBot,
 * GreedyBot, ...) implementuje vlastní strategii výběru tahu i promoce,
 * ale nese si i svou trvalou identitu (jméno, elo, avatar) přes napojený
 * Player — ten se získává z PlayerManageru podle pevného id, takže elo a
 * statistiky zůstávají trvalé mezi hrami.
 */
public abstract class Bot {

    private final Player player;

    protected Bot(String id, String displayName, int elo, String avatarPath) {
        this.player = PlayerManager.getOrCreate(id, displayName, elo, avatarPath);
    }

    /** Vrátí trvalého (barvy-neznajícího) Player hráče reprezentujícího tohoto bota. */
    public Player getPlayer() {
        return player;
    }

    public abstract int[] chooseAndPlayMove(GameLoop gameLoop);

    public abstract int choosePromotion(String[] pieceNames);
}