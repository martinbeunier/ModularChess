package gameloop;

/**
 * Společný kontrakt pro všechny typy botů. Každý konkrétní bot (RandomBot,
 * GreedyBot, ...) implementuje vlastní strategii výběru tahu i promoce,
 * ale GameLoop s nimi pracuje jednotně přes tohle rozhraní — nemusí vědět,
 * o jaký konkrétní typ jde.
 */
public interface Bot {

    /**
     * Vybere a ROVNOU provede jeden platný tah pro aktuálního hráče (přes
     * gameLoop.tryMove(...)) — konkrétní strategie výběru záleží na implementaci.
     *
     * @return {fromX, fromY, toX, toY} provedeného tahu (u rotace from==to),
     *         nebo null, pokud bot nemá žádnou platnou možnost.
     */
    int[] chooseAndPlayMove(GameLoop gameLoop);

    /**
     * Vybere index promoční figurky z nabízených možností.
     * @param pieceNames jména dostupných možností
     * @return index vybrané figurky
     */
    int choosePromotion(String[] pieceNames);
}