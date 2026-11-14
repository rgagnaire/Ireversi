import java.util.EnumSet;
import java.util.Random;
import utils.game.BoardFactory;
import utils.game.Game;
import utils.game.GameSettings;
import utils.game.SpecialTileType;
import utils.gameboard.Board;
import utils.gameboard.BombTile;
import utils.gameboard.Tile;

/** Tests de GameSettings et de la generation du plateau (BoardFactory). */
public class TestSettings {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testDefaults();
        testBoardSizeLimits();
        testTimeLimits();
        testSpecialPercentLimits();
        testValidate();
        testClassicBoardHasNoSpecialTile();
        testSpecialTileCountAndCenter();
        testOnlyBombSelected();
        testBonusValues();
        testSameSeedSameBoard();
        testGameFromSettings();

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

    /** Vrai si l'appel leve une IllegalArgumentException. */
    private static boolean refused(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static int countSpecial(Board board) {
        int count = 0;
        for (int i = 0; i < board.getTotalRows(); i++) {
            for (int j = 0; j < board.getTotalCols(); j++) {
                if (!board.getTile(i, j).getTileType().equals("Normal")) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void testDefaults() {
        GameSettings s = new GameSettings();

        check("defaut : taille 8", 8, s.getBoardSize());
        check("defaut : 5 minutes", 5, s.getTimeLimit());
        check("defaut : 0 % de cases speciales", 0, s.getSpecialTilePercent());
        check("defaut : pas de cases speciales", false, s.hasSpecialTiles());
        check("defaut : reglages coherents", null == s.validate(), true);
    }

    private static void testBoardSizeLimits() {
        GameSettings s = new GameSettings();

        check("taille 7 refusee", true, refused(() -> s.setBoardSize(7)));
        check("taille 33 refusee", true, refused(() -> s.setBoardSize(33)));
        s.setBoardSize(8);
        s.setBoardSize(32);
        check("taille 32 acceptee", 32, s.getBoardSize());
    }

    private static void testTimeLimits() {
        GameSettings s = new GameSettings();

        check("temps 0 refuse", true, refused(() -> s.setTimeLimit(0)));
        check("temps 11 refuse", true, refused(() -> s.setTimeLimit(11)));
        s.setTimeLimit(10);
        check("temps 10 accepte", 10, s.getTimeLimit());
    }

    private static void testSpecialPercentLimits() {
        GameSettings s = new GameSettings();

        check("pourcentage 3 refuse", true, refused(() -> s.setSpecialTilePercent(3)));
        check("pourcentage 80 refuse", true, refused(() -> s.setSpecialTilePercent(80)));
        s.setSpecialTilePercent(5);
        s.setSpecialTilePercent(75);
        s.setSpecialTilePercent(0);
        check("pourcentage 0 accepte", 0, s.getSpecialTilePercent());
    }

    private static void testValidate() {
        GameSettings s = new GameSettings();

        s.setSpecialTilePercent(20);
        check("cases speciales sans effet coche : probleme", false, null == s.validate());
        s.setSpecialTileTypes(EnumSet.of(SpecialTileType.BOMB));
        check("avec un effet coche : coherent", true, null == s.validate());

        s.setPlayerDiscColors("#ff0000", "#FF0000");
        check("memes couleurs de disque : probleme", false, null == s.validate());
    }

    private static void testClassicBoardHasNoSpecialTile() {
        Board board = BoardFactory.create(new GameSettings(), new Random(1));

        check("classique : plateau 8x8", 8, board.getTotalRows());
        check("classique : aucune case speciale", 0, countSpecial(board));
        check("classique : disque central present", true, board.getTile(3, 3).getDisc() != null);
    }

    /** 10x10 a 20 % = 20 cases speciales, jamais au centre. */
    private static void testSpecialTileCountAndCenter() {
        GameSettings s = new GameSettings();
        s.setBoardSize(10);
        s.setSpecialTilePercent(20);
        s.setSpecialTileTypes(EnumSet.allOf(SpecialTileType.class));

        for (long seed = 0; seed < 50; seed++) {
            Board board = BoardFactory.create(s, new Random(seed));
            boolean centerOk = true;
            for (int i = 4; i <= 5; i++) {
                for (int j = 4; j <= 5; j++) {
                    Tile t = board.getTile(i, j);
                    centerOk &= t.getTileType().equals("Normal") && t.getDisc() != null;
                }
            }
            if (seed == 0) {
                check("10x10 a 20 % : 20 cases speciales", 20, countSpecial(board));
            }
            if (!centerOk || countSpecial(board) != 20) {
                check("graine " + seed + " : 20 cases, centre intact", true, false);
                return;
            }
        }
        check("50 graines : toujours 20 cases speciales et centre intact", true, true);
    }

    private static void testOnlyBombSelected() {
        GameSettings s = new GameSettings();
        s.setSpecialTilePercent(75);
        s.setSpecialTileTypes(EnumSet.of(SpecialTileType.BOMB));
        Board board = BoardFactory.create(s, new Random(7));

        int bombs = 0;
        int others = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile t = board.getTile(i, j);
                if (t instanceof BombTile) {
                    bombs++;
                } else if (!t.getTileType().equals("Normal")) {
                    others++;
                }
            }
        }
        check("8x8 a 75 % : 48 bombes", 48, bombs);
        check("8x8 a 75 % : aucune autre case speciale", 0, others);
    }

    private static void testBonusValues() {
        GameSettings s = new GameSettings();
        s.setBoardSize(20);
        s.setSpecialTilePercent(50);
        s.setSpecialTileTypes(EnumSet.of(SpecialTileType.BONUS));
        Board board = BoardFactory.create(s, new Random(3));

        int min = 999;
        int max = 0;
        int bonusTiles = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Tile t = board.getTile(i, j);
                if (t.getTileType().equals("BonusPoint")) {
                    bonusTiles++;
                    min = Math.min(min, t.getBonusPoint());
                    max = Math.max(max, t.getBonusPoint());
                }
            }
        }
        check("20x20 a 50 % : 200 cases +N", 200, bonusTiles);
        check("bonus minimum >= 3", true, min >= 3);
        check("bonus maximum <= 20", true, max <= 20);
    }

    /** La meme graine donne le meme plateau (utile pour le reseau : le serveur envoie la graine). */
    private static void testSameSeedSameBoard() {
        GameSettings s = new GameSettings();
        s.setSpecialTilePercent(30);
        s.setSpecialTileTypes(EnumSet.allOf(SpecialTileType.class));

        Board a = BoardFactory.create(s, new Random(42));
        Board b = BoardFactory.create(s, new Random(42));
        Board c = BoardFactory.create(s, new Random(43));

        boolean sameAB = true;
        boolean sameAC = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sameAB &= a.getTile(i, j).getTileType().equals(b.getTile(i, j).getTileType())
                        && a.getTile(i, j).getBonusPoint() == b.getTile(i, j).getBonusPoint();
                sameAC &= a.getTile(i, j).getTileType().equals(c.getTile(i, j).getTileType())
                        && a.getTile(i, j).getBonusPoint() == c.getTile(i, j).getBonusPoint();
            }
        }
        check("meme graine : meme plateau", true, sameAB);
        check("autre graine : plateau different", false, sameAC);
    }

    private static void testGameFromSettings() {
        GameSettings s = new GameSettings();
        s.setBoardSize(12);
        Game game = new Game(s, "Alice", "Bob");

        check("partie 12x12 : 12 lignes", 12, game.getBoard().getTotalRows());
        check("partie 12x12 : score de depart 2", 2, game.getScore(0));
        check("partie 12x12 : le joueur 1 commence", 0, game.getCurrentPlayer());
    }
}