package utils.gameboard;

public class Tile {
    protected int row;
    protected int col;
    protected String tileType;
    protected int bonusPoint;
    protected Disc disc;

    public Tile(int row, int col, String tileType, int bonusPoint) {
        this.row = row;
        this.col = col;
        this.tileType = "Normal"; // Default tile type
        this.bonusPoint = bonusPoint;
        this.disc = null; // Initialize no disc on the tile
    }


     public void removeDisc() {
        this.disc = null;
    }

    //============ getters and setters ============
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getTileType() {
        return tileType;
    }

    public int getBonusPoint() {
        return bonusPoint;
    }

    public Disc getDisc() {
        return disc;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setTileType(String tileType) {
        this.tileType = tileType;
    }

    public void setBonusPoint(int bonusPoint) { // bonus point can be set to 0 or any positive integer
        this.bonusPoint = bonusPoint;
        if (bonusPoint > 0) {
            this.tileType = "BonusPoint";
        } else {
            this.tileType = "Normal";
        }
    }

     public void setDisc(Disc disc) {
        this.disc = disc;
    }
}
