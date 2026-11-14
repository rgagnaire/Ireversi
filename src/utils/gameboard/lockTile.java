package utils.gameboard;

public class LockTile extends Tile {
    public LockTile(int row, int col) {
        super(row, col, "Lock", 0);
        this.tileType = "Lock"; // Set the tile type to "Lock"
    }

    @Override
    public void setDisc(Disc disc) {
        super.setDisc(disc);
        setDicsLock();            // tout disque posé ici devient non retournable
    }

    public void setDicsLock() {
        if (this.disc != null) {
            this.disc.setCanFlipped(false); // Lock the disc on this tile
        }
    }

    
}
