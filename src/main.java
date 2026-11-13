import ui.*;
import utils.*;
import utils.gameboard.Board;
import utils.gameboard.Disc;
import utils.gameboard.lockTile;

public class main {
    public static void main(String[] args) {
        Board b = new Board(8, 8);
b.setTile(2, 2, new lockTile(2, 2));
b.getTile(2, 2).setDisc(new Disc(0, true, 0));   // disque du joueur 1
b.getTile(2, 2).getDisc().flipDisc();
System.out.println(b.getTile(2, 2).getDisc().getColorIndex()); // doit afficher 0 (non retourné)
    }
}