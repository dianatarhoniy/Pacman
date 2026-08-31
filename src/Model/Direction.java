package Model;

public enum Direction {
    UP(-1,0),
    NONE(0,0),
    DOWN(1,0),
    LEFT(0,-1),
    RIGHT(0,1);

    public int rowStep;
    public int colStep;
    Direction(int rowStep, int colStep) {
        this.rowStep = rowStep;
        this.colStep = colStep;
    }

}
