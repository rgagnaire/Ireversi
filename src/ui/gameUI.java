package ui;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;
import utils.game.Game;
import utils.game.MoveResult;
import utils.gameboard.Board;
import utils.gameboard.Disc;
import utils.gameboard.Tile;
import utils.moveCheck.MoveAvalable;

/**
 * Ecran de jeu minimal, dans une seule classe : un JPanel qui dessine lui-meme
 * l'en-tete (scores, tour, message) et le plateau, et qui recoit les clics.
 */
public class GameUI extends JPanel {
    private static final Color[] DISC_COLORS = {Color.BLACK, Color.WHITE};
    private static final int HEADER_HEIGHT = 90; // hauteur de la zone de texte, au-dessus du plateau

    private final Game game;
    private final int size;
    private final int cellSize;
    private String message = ""; // petit message d'erreur / de passe / de fin

    public GameUI(Game game) {
        this.game = game;
        this.size = game.getBoard().getTotalRows();
        this.cellSize = Math.max(18, 560 / size); // les grands plateaux ont des cases plus petites

        setPreferredSize(new Dimension(cellSize * size, HEADER_HEIGHT + cellSize * size));
        setBackground(new Color(0xEEEEEE));
        enableEvents(AWTEvent.MOUSE_EVENT_MASK); // pour recevoir les clics dans processMouseEvent
    }

    // ------------------------------------------------------------------ clics

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getID() != MouseEvent.MOUSE_CLICKED) {
            return;
        }
        int row = (e.getY() - HEADER_HEIGHT) / cellSize;
        int col = e.getX() / cellSize;
        if (e.getY() >= HEADER_HEIGHT && row < size && col >= 0 && col < size) {
            playAt(row, col);
        }
    }

    /** Joue le coup demande et prepare le message a afficher. */
    private void playAt(int row, int col) {
        MoveResult result = game.playMove(row, col);
        switch (result) {
            case INVALID:
                message = game.isOver() ? "La partie est terminee" : "Coup impossible ici";
                break;
            case OPPONENT_PASSED:
                String passed = game.getPlayer(1 - game.getCurrentPlayer()).getName();
                message = passed + " n'a aucun coup possible : il passe son tour";
                break;
            case GAME_OVER:
                int winner = game.getWinner();
                message = (winner == Game.DRAW) ? "Egalite !" : game.getPlayer(winner).getName() + " gagne !";
                break;
            default:
                message = "";
        }
        repaint();
    }

    // ------------------------------------------------------------------ dessin

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawHeader(g2);

        Board board = game.getBoard();
        List<int[]> validMoves = game.isOver()
                ? List.of()
                : MoveAvalable.getAvailableMoves(board, game.getCurrentPlayer());

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                boolean isValid = false;
                for (int[] move : validMoves) {
                    if (move[0] == row && move[1] == col) {
                        isValid = true;
                    }
                }
                drawCell(g2, board.getTile(row, col), col * cellSize, HEADER_HEIGHT + row * cellSize, isValid);
            }
        }
    }

    /** Les trois lignes de texte du haut : scores, tour en cours, message. */
    private void drawHeader(Graphics2D g2) {
        String scores = game.getPlayer(0).getName() + " (noir) : " + game.getScore(0)
                + "     " + game.getPlayer(1).getName() + " (blanc) : " + game.getScore(1);
        String turn = game.isOver()
                ? "Partie terminee"
                : "Au tour de " + game.getPlayer(game.getCurrentPlayer()).getName();

        drawCentered(g2, scores, new Font("SansSerif", Font.PLAIN, 20), Color.DARK_GRAY, 28);
        drawCentered(g2, turn, new Font("SansSerif", Font.PLAIN, 18), Color.DARK_GRAY, 54);
        drawCentered(g2, message, new Font("SansSerif", Font.PLAIN, 14), new Color(0x8B0000), 78);
    }

    private void drawCentered(Graphics2D g2, String text, Font font, Color color, int baselineY) {
        g2.setFont(font);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, baselineY);
    }

    /** Une case : fond selon son type, texte du type, disque eventuel, point si coup possible. */
    private void drawCell(Graphics2D g2, Tile tile, int x, int y, boolean isValidMove) {
        // fond + bordure
        g2.setColor(tileColor(tile.getTileType()));
        g2.fillRect(x, y, cellSize, cellSize);
        g2.setColor(new Color(0x1D5C3A));
        g2.drawRect(x, y, cellSize, cellSize);

        // texte du type de case, en haut pour ne pas cacher le disque
        String label = tileLabel(tile);
        if (!label.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, cellSize * 3 / 10)));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(255, 255, 255, 210));
            g2.drawString(label, x + (cellSize - fm.stringWidth(label)) / 2, y + fm.getAscent() + 2);
        }

        Disc disc = tile.getDisc();
        int margin = cellSize * 12 / 100;
        if (disc != null) {
            g2.setColor(DISC_COLORS[disc.getColorIndex()]);
            g2.fillOval(x + margin, y + margin, cellSize - 2 * margin, cellSize - 2 * margin);
            // contour dore epais = disque verrouille
            g2.setStroke(new BasicStroke(disc.isCanFlipped() ? 1f : 3f));
            g2.setColor(disc.isCanFlipped() ? Color.GRAY : new Color(0xFFD700));
            g2.drawOval(x + margin, y + margin, cellSize - 2 * margin, cellSize - 2 * margin);
            g2.setStroke(new BasicStroke(1f));
        } else if (isValidMove) {
            int d = cellSize / 4; // petit point blanc = coup possible
            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillOval(x + (cellSize - d) / 2, y + (cellSize - d) / 2, d, d);
        }
    }

    private static Color tileColor(String tileType) {
        switch (tileType) {
            case "Bomb":
                return new Color(0xB5443A);
            case "Lock":
                return new Color(0x6B7A8F);
            case "BonusPoint":
                return new Color(0xC99A14);
            default:
                return new Color(0x2E8B57);
        }
    }

    private static String tileLabel(Tile tile) {
        switch (tile.getTileType()) {
            case "Bomb":
                return "B";
            case "Lock":
                return "L";
            case "BonusPoint":
                return "+" + tile.getBonusPoint();
            default:
                return "";
        }
    }
}