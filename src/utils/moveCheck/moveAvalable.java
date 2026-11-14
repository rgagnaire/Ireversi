package utils.moveCheck;
import utils.gameboard.*;
import java.util.ArrayList;
import java.util.List;

public class MoveAvalable {

    private static int[][] direction = {{-1, -1}, {-1, 0}, {-1, 1},
                                        {0, -1}, {0, 1}, {1, -1}, 
                                        {1, 0}, {1, 1}};

    public static boolean isValidMove(Board board, int row, int col, int playerIndex) {
        if (row < 0 || row >= board.getTotalRows() || col < 0 || col >= board.getTotalCols()
        || board.getTile(row, col).getDisc() != null) {
    return false;
}
        for (int[] dir : direction) {
            int r = row + dir[0];
            int c = col + dir[1];
            boolean hasOpponentDiscBetween = false;

            while (r >= 0 && r < board.getTotalRows() && c >= 0 && c < board.getTotalCols()) {
                Disc disc = board.getTile(r, c).getDisc();
                if (disc == null) {
                    break; // Empty tile, stop searching in this direction
                } else if (disc.getColorIndex() != playerIndex) {
                    hasOpponentDiscBetween = true; // Found an opponent's disc
                } else {
                    if (hasOpponentDiscBetween) {
                        return true; // Found a valid move
                    }
                    break; // Found own disc without any opponent's disc in between
                }
                r += dir[0];
                c += dir[1];
            }
        }
        return false; // No valid move found
    }

    public static List<int[]> getAvailableMoves(Board board, int playerIndex) {  

        
        List<int[]> availableMoves = new ArrayList<>();
        int rows = board.getTotalRows();
        int cols = board.getTotalCols();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (board.getTile(row, col).getDisc() == null) { // Only check empty tiles
                    if (isValidMove(board, row, col, playerIndex)) {
                        availableMoves.add(new int[]{row, col});
                    }
                }
            }
        }
        return availableMoves;
    }   
    
    public static List<int[]> getDiscsToFlip(Board board, int row, int col, int playerIndex) {
    List<int[]> toFlip = new ArrayList<>();
    if (!isValidMove(board, row, col, playerIndex)) {
        return toFlip;
    }

    for (int[] dir : direction) {
        List<int[]> line = new ArrayList<>();
        int r = row + dir[0];
        int c = col + dir[1];

        while (r >= 0 && r < board.getTotalRows() && c >= 0 && c < board.getTotalCols()) {
            Disc disc = board.getTile(r, c).getDisc();
            if (disc == null) {
                break;
            }
            if (disc.getColorIndex() != playerIndex) {
                if (disc.isCanFlipped()) { 
                    line.add(new int[]{r, c});
                }
            } else {
                toFlip.addAll(line);
                break;
            }
            r += dir[0];
            c += dir[1];
        }
    }
    return toFlip;
}
       
}
