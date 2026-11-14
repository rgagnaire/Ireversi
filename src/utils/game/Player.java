package utils.game;

public class Player {
    private String name;
    private final int idPlayer;
    private int bonusPoints = 0;

    public Player(String name, int idPlayer) {
        this.name = name;
        this.idPlayer = idPlayer;
    }

    public void addPoints(int points){
        this.bonusPoints += points;
    }

//============= getters and seetters ==============
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }
    
}
