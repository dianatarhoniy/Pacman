package Model;

import java.util.*;

public class Board {

    private static final int[] destinationRow = {-2, 2, 0, 0};
    private static final int[] destinationCol = {0, 0, -2, 2};

    private final Cell[][] board;
    private final List<Ghost> ghosts = new ArrayList<>();
    private final Random rngPower    = new Random();

    private final int row, col;

    private int pacRow, pacCol;
    private int houseRow, houseCol;

    private Direction currentDir = Direction.NONE;
    private Direction wantedDir  = Direction.NONE;

    private int  score           = 0;
    private int  lives           = 3;
    private int  speedMul        = 1;
    private int  upgradesOnBoard = 0;
    private int  foodLeft        = 0;

    private boolean ghostsFrozen = false;
    private boolean pacInvisible = false;
    private boolean levelCleared = false;

    private Power active          = null;
    private Power lastPowerPicked = null;
    private long  effectEnds      = 0;

    public Board(int row, int col) {
        this.row = (row % 2 == 0) ? row - 1 : row;
        this.col = (col % 2 == 0) ? col - 1 : col;

        board = new Cell[this.row][this.col];
        generateMaze();
        solidifyBorders();
        placePacman();
        placeHouse();
        spawnGhosts();
    }

    public synchronized void setDirection(Direction dir)          { currentDir = dir; }
    public synchronized void requestDirection(Direction dir)      { wantedDir = dir;  }

    public synchronized void step() {
        if (wantedDir != Direction.NONE && canMove(wantedDir)) currentDir = wantedDir;
        if (!canMove(currentDir)) return;

        int tr = pacRow + currentDir.rowStep;
        int tc = pacCol + currentDir.colStep;
        Cell tgt = board[tr][tc];

        if (!pacInvisible && tgt.name().startsWith("GHOST_")) {
            resetPacman();
            return;
        }

        if (tgt == Cell.UPGRADE) {
            score += 50;
            triggerRandomPowerUp(tr, tc);
            if (foodLeft <= 0) levelCleared = true;
        } else if (tgt == Cell.FOOD) {
            score += 10;
            if (--foodLeft <= 0) levelCleared = true;
        }

        board[pacRow][pacCol] = Cell.PATH;
        pacRow = tr; pacCol = tc;
        board[pacRow][pacCol] = spriteFor(currentDir);

        if (active != null && System.currentTimeMillis() >= effectEnds) {
            speedMul = 1;
            ghostsFrozen = false;
            pacInvisible = false;
            active = null;
        }
    }

    private boolean canMove(Direction d) {
        if (d == Direction.NONE) return false;
        int r = pacRow + d.rowStep, c = pacCol + d.colStep;
        return r >= 0 && r < row && c >= 0 && c < col && board[r][c] != Cell.WALL;
    }

    public synchronized boolean resetPacman() {
        return loseAndReturnLives() == 0;
    }
    public synchronized int loseAndReturnLives() {
        board[pacRow][pacCol] = Cell.PATH;
        pacRow = pacCol = 1;
        board[pacRow][pacCol] = Cell.PACKMAN_RIGHT;
        wantedDir = currentDir = Direction.NONE;
        if (--lives < 0) lives = 0;
        return lives;
    }

    public synchronized boolean locateUpgrade(int r, int c) {
        if (upgradesOnBoard > 0) return false;
        if (board[r][c] == Cell.FOOD) foodLeft--;
        board[r][c] = Cell.UPGRADE;
        upgradesOnBoard++;
        checkClear();
        return true;
    }

    private void checkClear() {
        if (foodLeft == 0 && upgradesOnBoard == 0) levelCleared = true;
    }

    public synchronized void clearUpgrades() {
        for (int r = 0; r < row; r++)
            for (int c = 0; c < col; c++)
                if (board[r][c] == Cell.UPGRADE) board[r][c] = Cell.PATH;
        upgradesOnBoard = 0;
    }

    private void triggerRandomPowerUp(int r, int c) {
        board[r][c] = Cell.PATH;
        if (upgradesOnBoard > 0) upgradesOnBoard--;
        Power p = Power.values()[rngPower.nextInt(Power.values().length)];
        lastPowerPicked = p;
        switch (p) {
            case Power.BONUS       -> score += 250;
            case Power.LIFE        -> lives++;
            case Power.SPEED       -> speedMul = 2;
            case Power.FREEZE      -> ghostsFrozen = true;
            case Power.INVISIBILITY-> pacInvisible = true;
        }
        if (p.ms > 0) {
            active = p;
            effectEnds = System.currentTimeMillis() + p.ms;
        }
        checkClear();
    }

    public synchronized Power consumeLastPower() {
        Power p = lastPowerPicked;
        lastPowerPicked = null;
        return p;
    }

    public synchronized int  rows()             { return row;         }
    public synchronized int  cols()             { return col;         }
    public synchronized Cell at(int r,int c)    { return board[r][c]; }
    public synchronized void setCell(int r,int c, Cell v){ board[r][c]=v; }
    public synchronized int  getPacRow()        { return pacRow;      }
    public synchronized int  getPacCol()        { return pacCol;      }
    public synchronized List<Ghost> ghosts()    { return ghosts;      }
    public synchronized int  houseRow()         { return houseRow;    }
    public synchronized int  houseCol()         { return houseCol;    }
    public synchronized int  score()            { return score;       }
    public synchronized void addScore(int p)    { score += p;         }
    public synchronized int  lives()            { return lives;       }
    public synchronized void loseLife()         { if (--lives < 0) lives = 0; }
    public synchronized int  speedMul()         { return speedMul;    }
    public synchronized boolean ghostsFrozen()  { return ghostsFrozen; }
    public synchronized boolean pacInvisible()  { return pacInvisible; }
    public synchronized Direction currentDir()  { return currentDir;  }
    public synchronized boolean levelCleared()  { return levelCleared; }
    public synchronized void clearLevelFlag()   { levelCleared = false; }
    public synchronized int  foodLeft()         { return foodLeft;    }
    public synchronized void incFood()          { foodLeft++;         }

    private void generateMaze() {
        for (int r = 0; r < row; r++) Arrays.fill(board[r], Cell.WALL);
        boolean[][] visited = new boolean[row][col];
        Random rand = new Random();
        dfs(1, 1, visited, rand);
        removeDeadEnds(rand);
    }

    private void dfs(int r, int c, boolean[][] v, Random rand) {
        v[r][c] = true;
        board[r][c] = Cell.FOOD;
        incFood();
        List<Integer> dirs = Arrays.asList(0,1,2,3);
        Collections.shuffle(dirs, rand);
        for (int d : dirs) {
            int nr = r + destinationRow[d], nc = c + destinationCol[d];
            if (boundary(nr,nc) && !v[nr][nc]) {
                board[r + destinationRow[d]/2][c + destinationCol[d]/2] = Cell.FOOD;
                incFood();
                dfs(nr,nc,v,rand);
            }
        }
    }

    private boolean boundary(int r, int c) { return r>0 && r<row-1 && c>0 && c<col-1; }

    private void removeDeadEnds(Random rand) {
        boolean again;
        do {
            again = false;
            for (int r = 1; r < row-1; r++)
                for (int c = 1; c < col-1; c++)
                    if (board[r][c] == Cell.FOOD) {
                        int open = 0, last = -1;
                        int[] dr={-1,1,0,0}, dc={0,0,-1,1};
                        for (int d=0; d<4; d++)
                            if (board[r+dr[d]][c+dc[d]]!=Cell.WALL) open++; else last=d;
                        if (open==1 && last!=-1) {
                            int nr=r+dr[last], nc=c+dc[last];
                            board[nr][nc]=Cell.FOOD;
                            incFood();
                            again=true;
                        }
                    }
        } while(again);
    }

    private void solidifyBorders() {
        for (int i=0;i<row;i++){ board[i][0]=Cell.WALL; board[i][col-1]=Cell.WALL; }
        for (int j=0;j<col;j++){ board[0][j]=Cell.WALL; board[row-1][j]=Cell.WALL; }
    }

    private void placePacman() { pacRow=1; pacCol=1; board[pacRow][pacCol]=Cell.PACKMAN_RIGHT; }

    private void placeHouse() {
        houseRow=row/2; houseCol=col/2;
        if(board[houseRow][houseCol]==Cell.FOOD) foodLeft--;
        board[houseRow][houseCol]=Cell.HOUSE;
    }

    private void spawnGhosts() {
        ghosts.clear();
        int[][] off={{-1,0},{0,1},{1,0},{0,-1}};
        Cell[] colr={Cell.GHOST_GREEN,Cell.GHOST_YELLOW,Cell.GHOST_RED,Cell.GHOST_PINK};
        for(int i=0;i<colr.length;i++){
            int r=houseRow+off[i][0], c=houseCol+off[i][1];
            if(board[r][c]==Cell.WALL) board[r][c]=Cell.PATH;
            else if(board[r][c]==Cell.FOOD) foodLeft--;
            Ghost g=new Ghost(r,c,colr[i]);
            g.setUnder(board[r][c]);
            ghosts.add(g);
            board[r][c]=colr[i];
        }
    }

    public Cell spriteFor(Direction d) {
        return switch(d){
            case UP    -> Cell.PACKMAN_UP;
            case DOWN  -> Cell.PACKMAN_DOWN;
            case LEFT  -> Cell.PACKMAN_LEFT;
            case RIGHT -> Cell.PACKMAN_RIGHT;
            default    -> Cell.PACKMAN_RIGHT;
        };
    }
}