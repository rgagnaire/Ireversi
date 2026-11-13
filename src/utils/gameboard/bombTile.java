package utils.gameboard;

public class bombTile extends Tile {
    public bombTile(int row, int col, String tileType, int bonusPoint) {
        super(row, col, tileType, bonusPoint);
        this.tileType = "Bomb"; // Set the tile type to "Bomb"

    }

    public void explode(Board board) {
    for (int i = row - 2; i <= row + 2; i++) {
        for (int j = col - 2; j <= col + 2; j++) {
            boolean isCenter = (i == row && j == col);
            boolean insideBoard = i >= 0 && i < board.getTotalRows()
                               && j >= 0 && j < board.getTotalCols();
            if (isCenter || !insideBoard) {
                continue;
            }
            Disc disc = board.getTile(i, j).getDisc();
            if (disc != null) {
                disc.flipDisc();   // do nothing if the disc is locked
            }
        }
    }
}
}
