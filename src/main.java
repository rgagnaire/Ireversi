import utils.gameboard.Board;
import utils.gameboard.Disc;
import utils.gameboard.bombTile;
import utils.gameboard.lockTile;

/** Tests temporaires du plateau : bombe et verrou. */
public class Main {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testLockedDiscCannotFlip();
        testBombInTheMiddle();
        testBombInTheCorner();
        testBombDoesNotFlipLockedDisc();
        testBombOnTheEdge();

        System.out.println();
        System.out.println(passed + " test(s) reussi(s), " + failed + " echoue(s)");
    }

    // ---------- outils ----------

    /** Remplit tout le plateau de disques du joueur 1 (colorIndex 0). */
    private static void fillWithPlayer1(Board board) {
        for (int i = 0; i < board.getTotalRows(); i++) {
            for (int j = 0; j < board.getTotalCols(); j++) {
                board.getTile(i, j).setDisc(new Disc(0, true, 0));
            }
        }
    }

    /** Nombre de disques du joueur 2 (colorIndex 1) sur le plateau. */
    private static int countPlayer2(Board board) {
        int count = 0;
        for (int i = 0; i < board.getTotalRows(); i++) {
            for (int j = 0; j < board.getTotalCols(); j++) {
                Disc d = board.getTile(i, j).getDisc();
                if (d != null && d.getColorIndex() == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void check(String name, int expected, int actual) {
        if (expected == actual) {
            passed++;
            System.out.println("OK      " + name + " (" + actual + ")");
        } else {
            failed++;
            System.out.println("ECHEC   " + name + " : attendu " + expected + ", obtenu " + actual);
        }
    }

    // ---------- tests ----------

    /** Un disque pose sur un verrou reste de la meme couleur apres flipDisc(). */
    private static void testLockedDiscCannotFlip() {
        Board board = new Board(8, 8);
        board.setTile(2, 2, new lockTile(2, 2));
        board.getTile(2, 2).setDisc(new Disc(0, true, 0));

        board.getTile(2, 2).getDisc().flipDisc();

        check("verrou : le disque ne se retourne pas", 0, board.getTile(2, 2).getDisc().getColorIndex());
    }

    /** Bombe au centre : 5x5 - 1 = 24 disques retournes. */
    private static void testBombInTheMiddle() {
        Board board = new Board(8, 8);
        bombTile bomb = new bombTile(4, 4, "Bomb", 0);
        board.setTile(4, 4, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe au centre : 24 disques retournes", 24, countPlayer2(board));
        check("bombe au centre : le disque de la bombe reste", 0, board.getTile(4, 4).getDisc().getColorIndex());
    }

    /** Bombe dans le coin (0,0) : 3x3 - 1 = 8 disques retournes, sans sortir du plateau. */
    private static void testBombInTheCorner() {
        Board board = new Board(8, 8);
        bombTile bomb = new bombTile(0, 0, "Bomb", 0);
        board.setTile(0, 0, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe dans le coin : 8 disques retournes", 8, countPlayer2(board));
    }

    /** Bombe au bord (0,4) : 3 lignes x 5 colonnes - 1 = 14 disques retournes. */
    private static void testBombOnTheEdge() {
        Board board = new Board(8, 8);
        bombTile bomb = new bombTile(0, 4, "Bomb", 0);
        board.setTile(0, 4, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe au bord : 14 disques retournes", 14, countPlayer2(board));
    }

    /** Un verrou dans la zone de la bombe : 24 - 1 = 23 retournes, le verrouille reste. */
    private static void testBombDoesNotFlipLockedDisc() {
        Board board = new Board(8, 8);
        board.setTile(2, 2, new lockTile(2, 2));
        bombTile bomb = new bombTile(4, 4, "Bomb", 0);
        board.setTile(4, 4, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe + verrou : 23 disques retournes", 23, countPlayer2(board));
        check("bombe + verrou : le disque verrouille reste", 0, board.getTile(2, 2).getDisc().getColorIndex());
    }
}