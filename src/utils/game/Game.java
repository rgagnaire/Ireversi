package utils.game;

import utils.gameboard.*;
import utils.moveCheck.*;
import java.util.Random;


public class Game {
    public static final int DRAW = -1;          // egalite
    public static final int NO_WINNER_YET = -2; // partie pas finie

    private final Board board;
    private final Player[] players = new Player[2];
    private int currentPlayer = 0; // le joueur 1 commence
    private boolean over = false;

    public Game(GameSettings settings, String name1, String name2) {
    this(BoardFactory.create(settings, new Random()), name1, name2);
}

    public Game(int boardSize, String name1, String name2) {
        this(new Board(boardSize, boardSize), name1, name2);
    }

    /** Pratique pour les tests : on fournit un plateau deja prepare. */
    public Game(Board board, String name1, String name2) {
        this.board = board;
        this.players[0] = new Player(name1, 0);
        this.players[1] = new Player(name2, 1);
    }

    /** Le joueur courant essaye de jouer en (row, col). */
    public MoveResult playMove(int row, int col) {
        if (over) {
            return MoveResult.INVALID;
        }
        int bonus = MovePlayer.playMove(board, row, col, currentPlayer);
        if (bonus < 0) {
            return MoveResult.INVALID;
        }
        players[currentPlayer].addPoints(bonus);
        return nextTurn();
    }

    /** Decide qui joue ensuite : adversaire, meme joueur (passe) ou fin de partie. */
    private MoveResult nextTurn() {
        int opponent = 1 - currentPlayer;

        if (board.isFullBoard()) {
            over = true;
            return MoveResult.GAME_OVER;
        }
        if (hasMove(opponent)) {
            currentPlayer = opponent;
            return MoveResult.OK;
        }
        if (hasMove(currentPlayer)) {
            return MoveResult.OPPONENT_PASSED; // l'adversaire passe, currentPlayer ne change pas
        }
        over = true; // personne ne peut jouer
        return MoveResult.GAME_OVER;
    }

    private boolean hasMove(int playerIndex) {
        return !MoveAvalable.getAvailableMoves(board, playerIndex).isEmpty();
    }

    /** Score = nombre de disques de la couleur du joueur + ses points bonus. */
    public int getScore(int playerIndex) {
        int discs = 0;
        for (int i = 0; i < board.getTotalRows(); i++) {
            for (int j = 0; j < board.getTotalCols(); j++) {
                Disc d = board.getTile(i, j).getDisc();
                if (d != null && d.getColorIndex() == playerIndex) {
                    discs++;
                }
            }
        }
        return discs + players[playerIndex].getBonusPoints();
    }

    /** 0 ou 1 = indice du gagnant, DRAW = egalite, NO_WINNER_YET = partie en cours. */
    public int getWinner() {
        if (!over) {
            return NO_WINNER_YET;
        }
        int s0 = getScore(0);
        int s1 = getScore(1);
        if (s0 == s1) {
            return DRAW;
        }
        return s0 > s1 ? 0 : 1;
    }

    //============ getters ============
    public Board getBoard() {
        return board;
    }

    public Player getPlayer(int index) {
        return players[index];
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isOver() {
        return over;
    }
}