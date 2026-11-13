package utils.gameboard;

public class bombTile extends Tile {
    public bombTile(int row, int col, String tileType, int bonusPoint) {
        super(row, col, tileType, bonusPoint);
        this.tileType = "Bomb"; // Set the tile type to "Bomb"

    }

    public void explode(Board board) {
        int row = this.row - 1;
        int col = this.col - 1;
        for (int i = row - 2 ; i <= row +2; i++){
            for (int j = col - 2; j <= col + 2; j++){
                if (((i >=0) && (j>=0)) &&(Math.abs(i - j) == 1) || (Math.abs(i) == 2 && j == col - 1) || (Math.abs(j) == 2 && i == row - 1) && (i != row - 1 && j != col - 1)) {
                    if (board.getTile(i, j).disc != null) {
                        board.getTile(i, j).disc.flipDisc();
                    }
                }
            }
        }
    }
}
