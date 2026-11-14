import utils.game.Game;
import utils.game.MoveResult;
import utils.gameboard.Board;
import utils.gameboard.Disc;

/** Tests de la classe Game : tours, passe, fin de partie, score. */
public class TestGame {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testStart();
        testTurnsAlternate();
        testInvalidMoveKeepsTurn();
        testBonusInScore();
        testPassAndGameOver();
        testNoMoveAfterGameOver();

        System.out.println();
        System.out.println(passed + " test(s) reussi(s), " + failed + " echoue(s)");
    }

    private static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("OK      " + name + " (" + actual + ")");
        } else {
            failed++;
            System.out.println("ECHEC   " + name + " : attendu " + expected + ", obtenu " + actual);
        }
    }

    /** Retire les 4 disques du centre pour partir d'un plateau vide. */
    private static Board emptyBoard() {
        Board board = new Board(8, 8);
        for (int i = 3; i <= 4; i++) {
            for (int j = 3; j <= 4; j++) {
                board.getTile(i, j).removeDisc();
            }
        }
        return board;
    }

    private static void testStart() {
        Game game = new Game(8, "Alice", "Bob");

        check("depart : le joueur 1 commence", 0, game.getCurrentPlayer());
        check("depart : score joueur 1", 2, game.getScore(0));
        check("depart : score joueur 2", 2, game.getScore(1));
        check("depart : partie en cours", false, game.isOver());
        check("depart : pas de gagnant", Game.NO_WINNER_YET, game.getWinner());
        check("depart : nom du joueur 1", "Alice", game.getPlayer(0).getName());
    }

    private static void testTurnsAlternate() {
        Game game = new Game(8, "Alice", "Bob");

        check("coup 1 : (2,4) accepte", MoveResult.OK, game.playMove(2, 4));
        check("coup 1 : au tour du joueur 2", 1, game.getCurrentPlayer());
        check("coup 1 : score joueur 1 (2 + 1 pose + 1 retourne)", 4, game.getScore(0));
        check("coup 1 : score joueur 2", 1, game.getScore(1));

        check("coup 2 : (2,3) accepte pour le joueur 2", MoveResult.OK, game.playMove(2, 3));
        check("coup 2 : au tour du joueur 1", 0, game.getCurrentPlayer());
    }

    private static void testInvalidMoveKeepsTurn() {
        Game game = new Game(8, "Alice", "Bob");

        check("coup invalide (0,0) : refuse", MoveResult.INVALID, game.playMove(0, 0));
        check("coup invalide : toujours au joueur 1", 0, game.getCurrentPlayer());
        check("coup invalide : score inchange", 2, game.getScore(0));
    }

    /** Une case +3 s'ajoute au score du joueur qui y joue. */
    private static void testBonusInScore() {
        Game game = new Game(8, "Alice", "Bob");
        game.getBoard().getTile(2, 4).setBonusPoint(3);

        game.playMove(2, 4);

        check("bonus : score joueur 1 (4 disques + 3 bonus)", 7, game.getScore(0));
        check("bonus : points bonus du joueur 1", 3, game.getPlayer(0).getBonusPoints());
        check("bonus : score joueur 2 inchange", 1, game.getScore(1));
    }

    /**
     * Plateau vide avec deux petites lignes :
     * (0,0)=J1 (0,1)=J2 et (2,0)=J1 (2,1)=J2.
     * J1 joue (0,2) : J2 n'a plus aucun coup -> il passe, J1 rejoue.
     * J1 joue (2,2) : plus aucun disque adverse -> fin de partie.
     */
    private static void testPassAndGameOver() {
        Board board = emptyBoard();
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        board.getTile(2, 0).setDisc(new Disc(0, true, 0));
        board.getTile(2, 1).setDisc(new Disc(1, true, 1));
        Game game = new Game(board, "Alice", "Bob");

        check("passe : J1 joue (0,2)", MoveResult.OPPONENT_PASSED, game.playMove(0, 2));
        check("passe : c'est encore au joueur 1", 0, game.getCurrentPlayer());
        check("passe : partie pas finie", false, game.isOver());

        check("fin : J1 joue (2,2)", MoveResult.GAME_OVER, game.playMove(2, 2));
        check("fin : partie terminee", true, game.isOver());
        check("fin : score joueur 1", 6, game.getScore(0));
        check("fin : score joueur 2", 0, game.getScore(1));
        check("fin : le joueur 1 gagne", 0, game.getWinner());
    }

    private static void testNoMoveAfterGameOver() {
        Board board = emptyBoard();
        board.getTile(0, 0).setDisc(new Disc(0, true, 0));
        board.getTile(0, 1).setDisc(new Disc(1, true, 1));
        Game game = new Game(board, "Alice", "Bob");

        check("apres la fin : J1 joue (0,2), fin", MoveResult.GAME_OVER, game.playMove(0, 2));
        check("apres la fin : tout coup est refuse", MoveResult.INVALID, game.playMove(5, 5));
    }
}