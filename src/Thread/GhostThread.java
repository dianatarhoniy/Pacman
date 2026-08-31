package Thread;

import GameView.View;
import Model.Board;
import Model.Cell;
import Model.Direction;
import Model.Ghost;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GhostThread extends Thread {

    private final Board board;
    private final View  view;
    private final Random rng = new Random();
    private static final int STEP_MS = 500;

    public GhostThread(Board board, View view) {
        this.board = board;
        this.view  = view;
    }

    @Override public void run() {
        try {
            while (!isInterrupted()) {
                if (board.ghostsFrozen()) {
                    Thread.sleep(STEP_MS);
                    continue;
                }
                moveEveryGhostOnce();
                Thread.sleep(STEP_MS);
            }
        } catch (InterruptedException ignored) {} catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveEveryGhostOnce() throws InterruptedException, InvocationTargetException {

        for (Ghost ghost : board.ghosts()) {

            Direction dir = pickStep(ghost);
            if (dir == Direction.NONE) continue;

            int oldRow = ghost.row(), oldCol = ghost.col();

            ghost.move(dir);
            ghost.setDir(dir);
            board.setCell(oldRow, oldCol, ghost.under());

            long now = System.currentTimeMillis();
            if (now - ghost.lastRoll() >= 25_000) {
                ghost.resetRollTimer(now);
                if (rng.nextDouble() < 0.25 &&
                        board.locateUpgrade(oldRow, oldCol))
                {
                    SwingUtilities.invokeLater(() -> view.updateCell(oldRow, oldCol));
                }
            }
            Cell current = board.at(ghost.row(), ghost.col());


            if (current.name().startsWith("GHOST")) current = Cell.PATH;
            if (current.name().startsWith("PACKMAN") && board.pacInvisible())
                current = Cell.PATH;

            ghost.setUnder(current);

            if (!board.pacInvisible() && current.name().startsWith("PACKMAN")) {
                int left = board.loseAndReturnLives();

                ghost.setUnder(Cell.PATH);
                board.setCell(ghost.row(), ghost.col(), ghost.sprite());

                SwingUtilities.invokeAndWait(() -> {
                    view.updateCell(oldRow, oldCol);
                    view.updateCell(ghost.row(), ghost.col());
                    view.showLives(left);
                });
                continue;
            }


            board.setCell(ghost.row(), ghost.col(), ghost.sprite());

            SwingUtilities.invokeLater(() -> {
                view.updateCell(oldRow, oldCol);
                view.updateCell(ghost.row(), ghost.col());
            });
        }
    }


    private Direction pickStep(Ghost g) {

        Direction straight = g.dir();
        List<Direction> open = new ArrayList<>();

        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;
            int r = g.row() + d.rowStep;
            int c = g.col() + d.colStep;
            if (r < 0 || r >= board.rows() || c < 0 || c >= board.cols()) continue;
            if (board.at(r, c).name().startsWith("GHOST")) continue;
            if (board.at(r, c) != Cell.WALL) open.add(d);
        }

        if (open.contains(straight)) return straight;
        open.remove(opposite(straight));
        return open.isEmpty() ? opposite(straight)
                : open.get(rng.nextInt(open.size()));
    }

    private Direction opposite(Direction d) {
        return switch (d) {
            case UP    -> Direction.DOWN;
            case DOWN  -> Direction.UP;
            case LEFT  -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            default    -> Direction.NONE;
        };
    }
}