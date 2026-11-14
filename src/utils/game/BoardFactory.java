package utils.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import utils.gameboard.*;


/** Construit le plateau de depart d'une partie, avec les cases speciales tirees au hasard. */
public class BoardFactory {

    public static Board create(GameSettings settings, Random random) {
        int size = settings.getBoardSize();
        Board board = new Board(size, size); // le constructeur place deja les 4 disques du centre

        if (!settings.hasSpecialTiles()) {
            return board;
        }

        // 1. Toutes les cases possibles, sauf les 4 cases du centre (elles gardent leur disque)
        int mid = size / 2;
        List<int[]> candidates = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                boolean isCenter = (row == mid - 1 || row == mid) && (col == mid - 1 || col == mid);
                if (!isCenter) {
                    candidates.add(new int[]{row, col});
                }
            }
        }

        // 2. On melange, puis on prend les N premieres : N = pourcentage du plateau
        Collections.shuffle(candidates, random);
        int wanted = (int) Math.round(size * size * settings.getSpecialTilePercent() / 100.0);
        int count = Math.min(wanted, candidates.size());

        // 3. Chaque case choisie recoit un des effets coches, au hasard
        List<SpecialTileType> types = new ArrayList<>(settings.getSpecialTileTypes());
        for (int i = 0; i < count; i++) {
            int row = candidates.get(i)[0];
            int col = candidates.get(i)[1];
            SpecialTileType type = types.get(random.nextInt(types.size()));
            board.setTile(row, col, createTile(type, row, col, random));
        }
        return board;
    }

    private static Tile createTile(SpecialTileType type, int row, int col, Random random) {
        switch (type) {
            case BOMB:
                return new BombTile(row, col, "Bomb", 0);
            case LOCK:
                return new LockTile(row, col);
            default: { // BONUS
                int bonus = GameSettings.MIN_BONUS
                        + random.nextInt(GameSettings.MAX_BONUS - GameSettings.MIN_BONUS + 1);
                Tile tile = new Tile(row, col, "BonusPoint", 0);
                tile.setBonusPoint(bonus); // met aussi le type de la case a "BonusPoint"
                return tile;
            }
        }
    }
}