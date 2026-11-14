package utils.moveCheck;

import java.util.List;
import utils.gameboard.*;



public class MovePlayer {

    /**
     * Joue un coup : pose le disque, retourne les disques encadres,
     * puis declenche l'effet de la case (bombe, verrou, bonus).
     *
     * @return les points bonus gagnes (0 si la case n'en donne pas),
     *         ou -1 si le coup est invalide (le plateau n'est alors pas modifie).
     */
    public static int playMove(Board board, int row, int col, int playerIndex) {
        // 1. Les disques a retourner se calculent AVANT de poser le disque
        //    (isValidMove exige une case vide).
        if (!MoveAvalable.isValidMove(board, row, col, playerIndex)) {
            return -1;
        }
        List<int[]> toFlip = MoveAvalable.getDiscsToFlip(board, row, col, playerIndex);

        // 2. Poser le disque. Sur une lockTile, setDisc le verrouille tout seul.
        Tile tile = board.getTile(row, col);
        tile.setDisc(new Disc(playerIndex, true, playerIndex));

        // 3. Retourner les disques encadres
        for (int[] cell : toFlip) {
            board.getTile(cell[0], cell[1]).getDisc().flipDisc();
        }

        // 4. Effet de la case : la bombe explose apres les retournements normaux
        if (tile instanceof BombTile) {
            ((BombTile) tile).explode(board);
        }

        // 5. Bonus : a ajouter au score du joueur par la partie (Game)
        return tile.getBonusPoint();
    }
}