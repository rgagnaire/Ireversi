import utils.gameboard.*;
import utils.moveCheck.moveAvalable;

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
        testMovesAtStart();
        testOccupiedTileRefused();
        testOutsideBoardRefused();
        testTileWithoutNeighbourRefused();
        testHorizontalAndVertical();
        testDiagonalMove();
        testNoMoveForBlockedPlayer();

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

        // ---------- tests : validation d'un coup (moveAvalable) ----------

    private static int bool(boolean b) {
        return b ? 1 : 0;
    }

    /** Au depart, chaque joueur a exactement 4 coups possibles, toujours les memes. */
    private static void testMovesAtStart() {
        Board board = new Board(8, 8);

        check("depart : 4 coups pour le joueur 1", 4, moveAvalable.getAvailableMoves(board, 0).size());
        check("depart : 4 coups pour le joueur 2", 4, moveAvalable.getAvailableMoves(board, 1).size());

        // joueur 1 : (2,4) (3,5) (4,2) (5,3)
        check("depart : (2,4) valide pour le joueur 1", 1, bool(moveAvalable.isValidMove(board, 2, 4, 0)));
        check("depart : (3,5) valide pour le joueur 1", 1, bool(moveAvalable.isValidMove(board, 3, 5, 0)));
        check("depart : (4,2) valide pour le joueur 1", 1, bool(moveAvalable.isValidMove(board, 4, 2, 0)));
        check("depart : (5,3) valide pour le joueur 1", 1, bool(moveAvalable.isValidMove(board, 5, 3, 0)));
        // joueur 2 : (2,3) (3,2) (4,5) (5,4)
        check("depart : (2,3) valide pour le joueur 2", 1, bool(moveAvalable.isValidMove(board, 2, 3, 1)));
        check("depart : (5,4) valide pour le joueur 2", 1, bool(moveAvalable.isValidMove(board, 5, 4, 1)));
        // une case valide pour l'un ne l'est pas pour l'autre
        check("depart : (2,4) refuse pour le joueur 2", 0, bool(moveAvalable.isValidMove(board, 2, 4, 1)));
    }

    /** Jouer sur une case deja occupee est refuse (le cas qui posait probleme avant la garde). */
    private static void testOccupiedTileRefused() {
        Board board = new Board(8, 8);

        check("case occupee (3,3) refusee", 0, bool(moveAvalable.isValidMove(board, 3, 3, 0)));
        check("case occupee (3,4) refusee", 0, bool(moveAvalable.isValidMove(board, 3, 4, 0)));
        check("case occupee (4,3) refusee pour le joueur 2", 0, bool(moveAvalable.isValidMove(board, 4, 3, 1)));
    }

    /** Une case hors du plateau ne plante pas et est refusee. */
    private static void testOutsideBoardRefused() {
        Board board = new Board(8, 8);

        check("ligne -1 refusee", 0, bool(moveAvalable.isValidMove(board, -1, 0, 0)));
        check("colonne -1 refusee", 0, bool(moveAvalable.isValidMove(board, 0, -1, 0)));
        check("ligne 8 refusee", 0, bool(moveAvalable.isValidMove(board, 8, 0, 0)));
        check("colonne 8 refusee", 0, bool(moveAvalable.isValidMove(board, 0, 8, 0)));
    }

    /** Une case vide loin des disques n'entoure rien. */
    private static void testTileWithoutNeighbourRefused() {
        Board board = new Board(8, 8);

        check("coin (0,0) refuse", 0, bool(moveAvalable.isValidMove(board, 0, 0, 0)));
        check("coin (7,7) refuse", 0, bool(moveAvalable.isValidMove(board, 7, 7, 1)));
        check("(2,3) refuse pour le joueur 1 (rien a entourer)", 0, bool(moveAvalable.isValidMove(board, 2, 3, 0)));
    }

    /** Ligne horizontale et colonne verticale, avec plusieurs adversaires alignes. */
    private static void testHorizontalAndVertical() {
        Board board = new Board(8, 8);
        // ligne 0 : [joueur 1] [joueur 2] [joueur 2] [ vide en (0,3) ]
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1));
        // colonne 7 : [joueur 2] [joueur 1] [joueur 1] [ vide en (3,7) ]
        board.getTile(0, 7).setDisc(new Disc(1, true, 1));
        board.getTile(1, 7).setDisc(new Disc(0, true, 0));
        board.getTile(2, 7).setDisc(new Disc(0, true, 0));

        check("horizontal : (0,3) valide pour le joueur 1", 1, bool(moveAvalable.isValidMove(board, 0, 3, 0)));
        check("horizontal : (0,3) refuse pour le joueur 2", 0, bool(moveAvalable.isValidMove(board, 0, 3, 1)));
        check("vertical : (3,7) valide pour le joueur 2", 1, bool(moveAvalable.isValidMove(board, 3, 7, 1)));
        check("vertical : (3,7) refuse pour le joueur 1", 0, bool(moveAvalable.isValidMove(board, 3, 7, 0)));
    }

    /** Un coup peut entourer en diagonale. */
    private static void testDiagonalMove() {
        Board board = new Board(8, 8);
        // diagonale : (0,0) joueur 1, (1,1) joueur 2, puis on joue en (2,2)
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(1, 1).setDisc(new Disc(1, true, 1));

        check("diagonale : (2,2) valide pour le joueur 1", 1, bool(moveAvalable.isValidMove(board, 2, 2, 0)));
        check("diagonale : (2,2) refuse pour le joueur 2", 0, bool(moveAvalable.isValidMove(board, 2, 2, 1)));
    }

    /** Une ligne d'adversaires NON fermee par un de nos disques ne compte pas. */
    private static void testNoMoveForBlockedPlayer() {
        Board board = new Board(8, 8);
        // ligne 0 : [vide] [joueur 2] [joueur 2] [vide] : personne pour fermer la ligne
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1));

        check("ligne non fermee : (0,0) refuse", 0, bool(moveAvalable.isValidMove(board, 0, 0, 0)));
        check("ligne non fermee : (0,3) refuse", 0, bool(moveAvalable.isValidMove(board, 0, 3, 0)));
    }
}