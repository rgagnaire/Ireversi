package utils.gameboard;

public class lockTile extends Tile {
    public lockTile(int row, int col, String tileType, int bonusPoint) {
        super(row, col, tileType, bonusPoint);
        this.tileType = "Lock"; // Set the tile type to "Lock"
    }

    public void setDicsLock() {
        if (this.disc != null) {
            this.disc.setCanFlipped(false); // Lock the disc on this tile
        }
    }

    
}
