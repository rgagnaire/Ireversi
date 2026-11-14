package utils.game;

import java.util.EnumSet;
import java.util.Set;

/** Les regles d'une partie : taille du plateau, temps par coup, couleurs, cases speciales. */
public class GameSettings {
    public static final int MIN_TIME_LIMIT = 1;   // minutes par coup
    public static final int MAX_TIME_LIMIT = 10;
    public static final int DEFAULT_TIME_LIMIT = 5;
    public static final int MIN_BOARD_SIZE = 8;
    public static final int MAX_BOARD_SIZE = 32;
    public static final int DEFAULT_BOARD_SIZE = 8;
    public static final int MIN_SPECIAL_PERCENT = 5;
    public static final int MAX_SPECIAL_PERCENT = 75;
    public static final int MIN_BONUS = 3;        // valeur d'une case +N : de 3 a 20
    public static final int MAX_BONUS = 20;

    private int timeLimit = DEFAULT_TIME_LIMIT;
    private int boardSize = DEFAULT_BOARD_SIZE;
    private int specialTilePercent = 0;           // 0 = pas de cases speciales
    private String boardColor = "Classic";
    private String player1DiscColor = "#000000";
    private String player2DiscColor = "#FFFFFF";
    private final Set<SpecialTileType> specialTileTypes = EnumSet.noneOf(SpecialTileType.class);

    /** Partie classique : 8x8, 5 minutes, aucune case speciale. */
    public GameSettings() {
    }

    public void setTimeLimit(int minutes) {
        if (minutes < MIN_TIME_LIMIT || minutes > MAX_TIME_LIMIT) {
            throw new IllegalArgumentException("Le temps par coup doit etre entre "
                    + MIN_TIME_LIMIT + " et " + MAX_TIME_LIMIT + " minutes");
        }
        this.timeLimit = minutes;
    }

    public void setBoardSize(int size) {
        if (size < MIN_BOARD_SIZE || size > MAX_BOARD_SIZE) {
            throw new IllegalArgumentException("La taille du plateau doit etre entre "
                    + MIN_BOARD_SIZE + " et " + MAX_BOARD_SIZE);
        }
        this.boardSize = size;
    }

    /** 0 = desactive, sinon un pourcentage de cases entre 5 et 75. */
    public void setSpecialTilePercent(int percent) {
        if (percent != 0 && (percent < MIN_SPECIAL_PERCENT || percent > MAX_SPECIAL_PERCENT)) {
            throw new IllegalArgumentException("Les cases speciales : 0 ou entre "
                    + MIN_SPECIAL_PERCENT + " et " + MAX_SPECIAL_PERCENT + " pourcent");
        }
        this.specialTilePercent = percent;
    }

    public void setSpecialTileTypes(Set<SpecialTileType> types) {
        specialTileTypes.clear();
        specialTileTypes.addAll(types);
    }

    public void setBoardColor(String boardColor) {
        this.boardColor = boardColor;
    }

    public void setPlayerDiscColors(String player1Hex, String player2Hex) {
        this.player1DiscColor = player1Hex.toUpperCase();
        this.player2DiscColor = player2Hex.toUpperCase();
    }

    /** Renvoie null si les reglages sont coherents, sinon un message qui explique le probleme. */
    public String validate() {
        if (specialTilePercent > 0 && specialTileTypes.isEmpty()) {
            return "Choisissez au moins un effet de case speciale";
        }
        if (player1DiscColor.equalsIgnoreCase(player2DiscColor)) {
            return "Les deux joueurs doivent avoir des couleurs de disque differentes";
        }
        return null;
    }

    /** Vrai si des cases speciales doivent etre placees sur le plateau. */
    public boolean hasSpecialTiles() {
        return specialTilePercent > 0 && !specialTileTypes.isEmpty();
    }

    //============ getters ============
    public int getTimeLimit() {
        return timeLimit;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getSpecialTilePercent() {
        return specialTilePercent;
    }

    public Set<SpecialTileType> getSpecialTileTypes() {
        return EnumSet.copyOf(specialTileTypes.isEmpty() ? EnumSet.noneOf(SpecialTileType.class) : specialTileTypes);
    }

    public String getBoardColor() {
        return boardColor;
    }

    public String getPlayer1DiscColor() {
        return player1DiscColor;
    }

    public String getPlayer2DiscColor() {
        return player2DiscColor;
    }
}