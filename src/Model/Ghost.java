package Model;

public class Ghost {
    private int row;
    private int col;
    private Direction dir = Direction.NONE;
    private Cell sprite;
    private Cell under = Cell.FOOD;
    private long lastUpgradeRoll = System.currentTimeMillis();
    public long lastRoll()             { return lastUpgradeRoll; }
    public void resetRollTimer(long t) { lastUpgradeRoll = t; }

    public Direction dir()              { return dir; }
    public void setDir(Direction d)     { dir = d; }
    public Ghost(int row, int col,Cell sprite) {
        this.row = row;
        this.col = col;
        this.sprite = sprite;
    }
    public int row(){
        return row;
    }
    public int col(){
        return col;
    }
    public void move(Direction dir){
        row += dir.rowStep;
        col += dir.colStep;
    }
    public Cell sprite(){
        return sprite;
    }
    public Cell under(){
        return under;
    }
    public void setUnder(Cell c){
        under = c;
    }

}
