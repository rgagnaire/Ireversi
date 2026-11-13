package utils.gameboard;

public class Board {
    private int totalRows;
    private int totalCols;
    private Tile[][] tiles;

    public Board(int totalRows, int totalCols) {
        this.totalRows = totalRows;
        this.totalCols = totalCols;
        this.tiles = new Tile[totalRows][totalCols];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                tiles[i][j] = new Tile(i, j, "Normal", 0);
            }
        }
        // setup the initial discs in the middle of the boeard 
        setupInitialDiscs();

    }

    private void setupInitialDiscs() {
        int midRow = totalRows / 2 - 1;
        int midCol = totalCols / 2 - 1;

        tiles[midRow][midCol].setDisc(new Disc(0, true, 0)); // Player 1 disc
        tiles[midRow][midCol + 1].setDisc(new Disc(1, true, 1)); // Player 2 disc
        tiles[midRow + 1][midCol].setDisc(new Disc(1, true, 1)); // Player 2 disc
        tiles[midRow + 1][midCol + 1].setDisc(new Disc(0, true, 0)); // Player 1 disc
    }

    public boolean isFullBoard() {
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                if (tiles[i][j].getDisc() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalCols() {
        return totalCols;
    }

    public void setTile(int row, int col, Tile tile) {
        tiles[row][col] = tile;
    }

}
