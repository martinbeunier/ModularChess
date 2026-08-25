package logic;

/**
 * Umožňuje ChessBoard zeptat se "něčeho vnějšího" (GUI, konzole, testu...),
 * kterou figurku si hráč vybral při promoci pěšce, aniž by ChessBoard
 * musel znát Swing / JOptionPane / cokoliv GUI-specifického.
 *
 * Loop (GUI) si vytvoří vlastní implementaci (např. přes lambda) a předá ji
 * do ChessBoard.setPromotionChooser(...).
 */
@FunctionalInterface
public interface PromotionChooser {
    /**
     * @param x souřadnice pole, na kterém se figurka promuje (pro zobrazení/zvýraznění v GUI)
     * @param y souřadnice pole, na kterém se figurka promuje (pro zobrazení/zvýraznění v GUI)
     * @param pieceNames jména dostupných figurek k výběru (v pořadí odpovídajícím indexům)
     * @return index vybrané figurky v poli pieceNames
     */
    int choosePromotion(int x, int y, String[] pieceNames);
}