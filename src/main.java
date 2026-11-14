import utils.gameboard.*;
import utils.moveCheck.*;

/** Tests temporaires du plateau : bombe, verrou, validation, capture et coup joue. */
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
        testFlipListAtStart();
        testFlipListSeveralDirections();
        testFlipListInvalidMove();
        testFlipListSkipsLockedDisc();
        testPlayMoveAtStart();
        testPlayMoveInvalid();
        testPlayMoveSeveralDirections();
        testPlayMoveLockTile();
        testPlayMoveBombTile();
        testPlayMoveBonusTile();

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

    // ---------- tests : bombe et verrou ----------

    /** Un disque pose sur un verrou reste de la meme couleur apres flipDisc(). */
    private static void testLockedDiscCannotFlip() {
        Board board = new Board(8, 8);
        board.setTile(2, 2, new LockTile(2, 2));
        board.getTile(2, 2).setDisc(new Disc(0, true, 0));

        board.getTile(2, 2).getDisc().flipDisc();

        check("verrou : le disque ne se retourne pas", 0, board.getTile(2, 2).getDisc().getColorIndex());
    }

    /** Bombe au centre : 5x5 - 1 = 24 disques retournes. */
    private static void testBombInTheMiddle() {
        Board board = new Board(8, 8);
        BombTile bomb = new BombTile(4, 4, "Bomb", 0);
        board.setTile(4, 4, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe au centre : 24 disques retournes", 24, countPlayer2(board));
        check("bombe au centre : le disque de la bombe reste", 0, board.getTile(4, 4).getDisc().getColorIndex());
    }

    /** Bombe dans le coin (0,0) : 3x3 - 1 = 8 disques retournes, sans sortir du plateau. */
    private static void testBombInTheCorner() {
        Board board = new Board(8, 8);
        BombTile bomb = new BombTile(0, 0, "Bomb", 0);
        board.setTile(0, 0, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe dans le coin : 8 disques retournes", 8, countPlayer2(board));
    }

    /** Bombe au bord (0,4) : 3 lignes x 5 colonnes - 1 = 14 disques retournes. */
    private static void testBombOnTheEdge() {
        Board board = new Board(8, 8);
        BombTile bomb = new BombTile(0, 4, "Bomb", 0);
        board.setTile(0, 4, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe au bord : 14 disques retournes", 14, countPlayer2(board));
    }

    /** Un verrou dans la zone de la bombe : 24 - 1 = 23 retournes, le verrouille reste. */
    private static void testBombDoesNotFlipLockedDisc() {
        Board board = new Board(8, 8);
        board.setTile(2, 2, new LockTile(2, 2));
        BombTile bomb = new BombTile(4, 4, "Bomb", 0);
        board.setTile(4, 4, bomb);
        fillWithPlayer1(board);

        bomb.explode(board);

        check("bombe + verrou : 23 disques retournes", 23, countPlayer2(board));
        check("bombe + verrou : le disque verrouille reste", 0, board.getTile(2, 2).getDisc().getColorIndex());
    }

    // ---------- tests : validation d'un coup (MoveAvalable) ----------

    private static int bool(boolean b) {
        return b ? 1 : 0;
    }

    /** Au depart, chaque joueur a exactement 4 coups possibles, toujours les memes. */
    private static void testMovesAtStart() {
        Board board = new Board(8, 8);

        check("depart : 4 coups pour le joueur 1", 4, MoveAvalable.getAvailableMoves(board, 0).size());
        check("depart : 4 coups pour le joueur 2", 4, MoveAvalable.getAvailableMoves(board, 1).size());

        // joueur 1 : (2,4) (3,5) (4,2) (5,3)
        check("depart : (2,4) valide pour le joueur 1", 1, bool(MoveAvalable.isValidMove(board, 2, 4, 0)));
        check("depart : (3,5) valide pour le joueur 1", 1, bool(MoveAvalable.isValidMove(board, 3, 5, 0)));
        check("depart : (4,2) valide pour le joueur 1", 1, bool(MoveAvalable.isValidMove(board, 4, 2, 0)));
        check("depart : (5,3) valide pour le joueur 1", 1, bool(MoveAvalable.isValidMove(board, 5, 3, 0)));
        // joueur 2 : (2,3) (3,2) (4,5) (5,4)
        check("depart : (2,3) valide pour le joueur 2", 1, bool(MoveAvalable.isValidMove(board, 2, 3, 1)));
        check("depart : (5,4) valide pour le joueur 2", 1, bool(MoveAvalable.isValidMove(board, 5, 4, 1)));
        // une case valide pour l'un ne l'est pas pour l'autre
        check("depart : (2,4) refuse pour le joueur 2", 0, bool(MoveAvalable.isValidMove(board, 2, 4, 1)));
    }

    /** Jouer sur une case deja occupee est refuse. */
    private static void testOccupiedTileRefused() {
        Board board = new Board(8, 8);

        check("case occupee (3,3) refusee", 0, bool(MoveAvalable.isValidMove(board, 3, 3, 0)));
        check("case occupee (3,4) refusee", 0, bool(MoveAvalable.isValidMove(board, 3, 4, 0)));
        check("case occupee (4,3) refusee pour le joueur 2", 0, bool(MoveAvalable.isValidMove(board, 4, 3, 1)));
    }

    /** Une case hors du plateau ne plante pas et est refusee. */
    private static void testOutsideBoardRefused() {
        Board board = new Board(8, 8);

        check("ligne -1 refusee", 0, bool(MoveAvalable.isValidMove(board, -1, 0, 0)));
        check("colonne -1 refusee", 0, bool(MoveAvalable.isValidMove(board, 0, -1, 0)));
        check("ligne 8 refusee", 0, bool(MoveAvalable.isValidMove(board, 8, 0, 0)));
        check("colonne 8 refusee", 0, bool(MoveAvalable.isValidMove(board, 0, 8, 0)));
    }

    /** Une case vide loin des disques n'entoure rien. */
    private static void testTileWithoutNeighbourRefused() {
        Board board = new Board(8, 8);

        check("coin (0,0) refuse", 0, bool(MoveAvalable.isValidMove(board, 0, 0, 0)));
        check("coin (7,7) refuse", 0, bool(MoveAvalable.isValidMove(board, 7, 7, 1)));
        check("(2,3) refuse pour le joueur 1 (rien a entourer)", 0, bool(MoveAvalable.isValidMove(board, 2, 3, 0)));
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

        check("horizontal : (0,3) valide pour le joueur 1", 1, bool(MoveAvalable.isValidMove(board, 0, 3, 0)));
        check("horizontal : (0,3) refuse pour le joueur 2", 0, bool(MoveAvalable.isValidMove(board, 0, 3, 1)));
        check("vertical : (3,7) valide pour le joueur 2", 1, bool(MoveAvalable.isValidMove(board, 3, 7, 1)));
        check("vertical : (3,7) refuse pour le joueur 1", 0, bool(MoveAvalable.isValidMove(board, 3, 7, 0)));
    }

    /** Un coup peut entourer en diagonale. */
    private static void testDiagonalMove() {
        Board board = new Board(8, 8);
        // diagonale : (0,0) joueur 1, (1,1) joueur 2, puis on joue en (2,2)
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(1, 1).setDisc(new Disc(1, true, 1));

        check("diagonale : (2,2) valide pour le joueur 1", 1, bool(MoveAvalable.isValidMove(board, 2, 2, 0)));
        check("diagonale : (2,2) refuse pour le joueur 2", 0, bool(MoveAvalable.isValidMove(board, 2, 2, 1)));
    }

    /** Une ligne d'adversaires NON fermee par un de nos disques ne compte pas. */
    private static void testNoMoveForBlockedPlayer() {
        Board board = new Board(8, 8);
        // ligne 0 : [vide] [joueur 2] [joueur 2] [vide] : personne pour fermer la ligne
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1));

        check("ligne non fermee : (0,0) refuse", 0, bool(MoveAvalable.isValidMove(board, 0, 0, 0)));
        check("ligne non fermee : (0,3) refuse", 0, bool(MoveAvalable.isValidMove(board, 0, 3, 0)));
    }

    // ---------- tests : disques a retourner (getDiscsToFlip) ----------

    /** Vrai si la liste contient la case (row, col). */
    private static boolean contains(java.util.List<int[]> cells, int row, int col) {
        for (int[] cell : cells) {
            if (cell[0] == row && cell[1] == col) {
                return true;
            }
        }
        return false;
    }

    /** Au depart, jouer en (2,4) avec le joueur 1 retourne uniquement le disque (3,4). */
    private static void testFlipListAtStart() {
        Board board = new Board(8, 8);
        java.util.List<int[]> flips = MoveAvalable.getDiscsToFlip(board, 2, 4, 0);

        check("depart (2,4) : 1 disque a retourner", 1, flips.size());
        check("depart (2,4) : c'est le disque (3,4)", 1, bool(contains(flips, 3, 4)));
    }

    /** Jouer en (0,3) entoure dans trois directions : 2 + 1 + 1 = 4 disques. */
    private static void testFlipListSeveralDirections() {
        Board board = new Board(8, 8);
        // horizontal : (0,0) joueur 1, (0,1) et (0,2) joueur 2
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1));
        // vertical : (1,3) joueur 2, (2,3) joueur 1
        board.getTile(1, 3).setDisc(new Disc(1, true, 1));
        board.getTile(2, 3).setDisc(new Disc(0, true, 0));
        // diagonale bas-gauche : (1,2) joueur 2, (2,1) joueur 1
        board.getTile(1, 2).setDisc(new Disc(1, true, 1));
        board.getTile(2, 1).setDisc(new Disc(0, true, 0));

        java.util.List<int[]> flips = MoveAvalable.getDiscsToFlip(board, 0, 3, 0);

        check("3 directions : 4 disques a retourner", 4, flips.size());
        check("3 directions : (0,1) present", 1, bool(contains(flips, 0, 1)));
        check("3 directions : (0,2) present", 1, bool(contains(flips, 0, 2)));
        check("3 directions : (1,3) present", 1, bool(contains(flips, 1, 3)));
        check("3 directions : (1,2) present", 1, bool(contains(flips, 1, 2)));
    }

    /** Un coup invalide (rien a entourer, ou case occupee) ne retourne rien. */
    private static void testFlipListInvalidMove() {
        Board board = new Board(8, 8);

        check("coup invalide (0,0) : liste vide", 0, MoveAvalable.getDiscsToFlip(board, 0, 0, 0).size());
        check("case occupee (3,3) : liste vide", 0, MoveAvalable.getDiscsToFlip(board, 3, 3, 0).size());
        check("hors plateau : liste vide", 0, MoveAvalable.getDiscsToFlip(board, -1, 0, 0).size());
    }

    /** Un disque verrouille est entoure mais n'est pas dans la liste. */
    private static void testFlipListSkipsLockedDisc() {
        Board board = new Board(8, 8);
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.setTile(0, 2, new LockTile(0, 2));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1)); // verrouille automatiquement

        java.util.List<int[]> flips = MoveAvalable.getDiscsToFlip(board, 0, 3, 0);

        check("verrou : le coup reste valide", 1, bool(MoveAvalable.isValidMove(board, 0, 3, 0)));
        check("verrou : 1 seul disque a retourner", 1, flips.size());
        check("verrou : (0,1) present", 1, bool(contains(flips, 0, 1)));
        check("verrou : (0,2) absent", 0, bool(contains(flips, 0, 2)));
    }

    // ---------- tests : jouer un coup (MovePlayer.playMove) ----------

    /** Couleur du disque en (row, col), -1 s'il n'y en a pas. */
    private static int colorAt(Board board, int row, int col) {
        Disc d = board.getTile(row, col).getDisc();
        return d == null ? -1 : d.getColorIndex();
    }

    /** Au depart, le joueur 1 joue (2,4) : le disque est pose et (3,4) change de couleur. */
    private static void testPlayMoveAtStart() {
        Board board = new Board(8, 8);
        int result = MovePlayer.playMove(board, 2, 4, 0);

        check("jouer (2,4) : pas de bonus (0)", 0, result);
        check("jouer (2,4) : disque pose pour le joueur 1", 0, colorAt(board, 2, 4));
        check("jouer (2,4) : (3,4) retourne pour le joueur 1", 0, colorAt(board, 3, 4));
        check("jouer (2,4) : (3,3) inchange (joueur 1)", 0, colorAt(board, 3, 3));
        check("jouer (2,4) : (4,3) inchange (joueur 2)", 1, colorAt(board, 4, 3));
        check("jouer (2,4) : (4,4) inchange (joueur 1)", 0, colorAt(board, 4, 4));
    }

    /** Un coup invalide renvoie -1 et ne change rien. */
    private static void testPlayMoveInvalid() {
        Board board = new Board(8, 8);

        check("coup invalide (0,0) : renvoie -1", -1, MovePlayer.playMove(board, 0, 0, 0));
        check("coup invalide (0,0) : rien n'est pose", -1, colorAt(board, 0, 0));
        check("case occupee (3,3) : renvoie -1", -1, MovePlayer.playMove(board, 3, 3, 0));
        check("case occupee (3,3) : disque inchange", 0, colorAt(board, 3, 3));
        check("hors plateau : renvoie -1", -1, MovePlayer.playMove(board, -1, 0, 0));
    }

    /** Meme situation que le test des 3 directions : les 4 disques changent de couleur. */
    private static void testPlayMoveSeveralDirections() {
        Board board = new Board(8, 8);
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1));
        board.getTile(1, 3).setDisc(new Disc(1, true, 1));
        board.getTile(2, 3).setDisc(new Disc(0, true, 0));
        board.getTile(1, 2).setDisc(new Disc(1, true, 1));
        board.getTile(2, 1).setDisc(new Disc(0, true, 0));

        MovePlayer.playMove(board, 0, 3, 0);

        check("3 directions : (0,1) retourne", 0, colorAt(board, 0, 1));
        check("3 directions : (0,2) retourne", 0, colorAt(board, 0, 2));
        check("3 directions : (1,3) retourne", 0, colorAt(board, 1, 3));
        check("3 directions : (1,2) retourne", 0, colorAt(board, 1, 2));
        check("3 directions : (0,3) pose", 0, colorAt(board, 0, 3));
    }

    /** Un disque pose sur une case verrou ne pourra plus jamais etre retourne. */
    private static void testPlayMoveLockTile() {
        Board board = new Board(8, 8);
        board.setTile(2, 4, new LockTile(2, 4));

        MovePlayer.playMove(board, 2, 4, 0);

        check("case verrou : disque pose", 0, colorAt(board, 2, 4));
        check("case verrou : disque verrouille (0 = non retournable)",
                0, bool(board.getTile(2, 4).getDisc().isCanFlipped()));
        check("case verrou : le coup a bien retourne (3,4)", 0, colorAt(board, 3, 4));
    }

    /** Bombe en (0,3) : retournements normaux, puis explosion de tout le carre 5x5. */
    private static void testPlayMoveBombTile() {
        Board board = new Board(8, 8);
        board.setTile(0, 3, new BombTile(0, 3, "Bomb", 0));
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(0, 2).setDisc(new Disc(1, true, 1));
        board.getTile(1, 4).setDisc(new Disc(1, true, 1)); // dans le carre, sera retourne
        board.getTile(2, 2).setDisc(new Disc(0, true, 0)); // dans le carre, sera retourne aussi
        Disc locked = new Disc(1, false, 1);
        board.getTile(2, 5).setDisc(locked);               // dans le carre mais verrouille

        MovePlayer.playMove(board, 0, 3, 0);

        check("bombe : le disque de la bombe reste au joueur 1", 0, colorAt(board, 0, 3));
        check("bombe : (0,1) retourne par le coup puis par la bombe", 1, colorAt(board, 0, 1));
        check("bombe : (0,2) retourne par le coup puis par la bombe", 1, colorAt(board, 0, 2));
        check("bombe : (0,0) hors du carre, inchange", 0, colorAt(board, 0, 0));
        check("bombe : (1,4) retourne par la bombe", 0, colorAt(board, 1, 4));
        check("bombe : (2,2) retourne par la bombe", 1, colorAt(board, 2, 2));
        check("bombe : le disque verrouille (2,5) reste", 1, colorAt(board, 2, 5));
        check("bombe : hors du carre, (3,3) inchange", 0, colorAt(board, 3, 3));
    }

    /** Une case +3 renvoie 3 points. */
    private static void testPlayMoveBonusTile() {
        Board board = new Board(8, 8);
        board.getTile(2, 4).setBonusPoint(3);

        check("case +3 : renvoie 3", 3, MovePlayer.playMove(board, 2, 4, 0));
        check("case +3 : le disque est pose", 0, colorAt(board, 2, 4));
    }
}