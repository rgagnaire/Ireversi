package utils.gameboard;

public class Disc {
    private int colorIndex; // 0 for player 1 and 1 for player 2
    private boolean canFlipped; //Custom tile lock
    private  int belongsTo; //who own this disc

    public Disc(int colorIndex, boolean canFlipped, int belongsTo) {
        this.colorIndex = colorIndex;
        this.canFlipped = canFlipped;
        this.belongsTo = belongsTo;
    }

    public void flipDisc(){
        if (canFlipped) {
            this.colorIndex = (this.colorIndex == 0) ? 1 : 0;
            this.belongsTo = (this.belongsTo == 0) ? 1 : 0;
        }
    }

    public void setCanFlipped(boolean canFlipped) {
        this.canFlipped = canFlipped;
    }


    //============ getters and setters ============
    public int getColorIndex() {
        return colorIndex;
    }

    public boolean isCanFlipped() {
        return canFlipped;
    }

    public int getBelongsTo() {
        return belongsTo;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }


    public void setBelongsTo(int belongsTo) {
        this.belongsTo = belongsTo;
    }
}
