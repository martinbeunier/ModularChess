/*public class RandomKod {
}

Velký pawn move
 int x = startX + m.getX();
int y = startY + m.getY();

while (inBoard(x, y)) {

    if (board[x][y] == null) {
        if (m.getBehaviour() == MoveBehaviour.MOVE || m.getBehaviour() == MoveBehaviour.BOTH) {
            validMoves.add(new MoveType(x - startX, y - startY, false, m.getBehaviour()));
        }
    }
    else {
        if (board[x][y].getColour() != player.getColour()) {
            if (m.getBehaviour() == MoveBehaviour.TAKE || m.getBehaviour() == MoveBehaviour.BOTH) {
                validMoves.add(new MoveType(x - startX, y - startY, false, MoveBehaviour.TAKE));
            }
        }
        break; // ❗ zastaví sliding
    }

    if (!m.isRepeat()) break;

    x += stepX;
    y += stepY;
}













 */