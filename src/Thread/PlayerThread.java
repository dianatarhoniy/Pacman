package Thread;

import Controller.GameController;
import GameView.View;
import Model.Board;
import Model.Cell;
import Model.Power;

import javax.swing.*;

public class PlayerThread extends Thread {

    private final Board board;
    private final View  view;
    private static final int FPS = 15;
    private static final int FRAME_MS = 50;
    private long lastFrame = System.currentTimeMillis();
    private boolean mouthOpen = true;
    private final GameController game;

    public PlayerThread(Board board, View view, GameController game) {
        this.board = board;
        this.view  = view;
        this.game = game;
    }

    @Override public void run() {
        int prevRow = board.getPacRow();
        int prevCol = board.getPacCol();

        try {
            while (!isInterrupted()) {
                board.step();
                if (board.lives() == 0) {
                    SwingUtilities.invokeLater(() -> view.showLives(0));
                    game.stopClock();
                    SwingUtilities.invokeLater(game::endGame);
                    return;
                }
                Power justPicked = board.consumeLastPower();
                if (justPicked != null) {
                    SwingUtilities.invokeLater(() ->
                            view.showHint(justPicked.name() + " activated!")
                    );
                }

                int r = board.getPacRow();
                int c = board.getPacCol();

                final int FRAME_MS = 150;
                boolean moved      = (r != prevRow || c != prevCol);
                long    now        = System.currentTimeMillis();

                if (moved) {
                    if (now - lastFrame > FRAME_MS) {
                        mouthOpen = !mouthOpen;
                        lastFrame = now;
                    }
                } else {
                    mouthOpen = true;
                }

                Cell sprite = mouthOpen
                        ? board.spriteFor(board.currentDir())
                        : Cell.PAC_CLOSED;

                board.setCell(r, c, sprite);
                view.updateCell(r, c);

                if (moved) {
                    int oldR = prevRow, oldC = prevCol;
                    SwingUtilities.invokeLater(() -> {
                        view.updateCell(oldR, oldC);
                        view.showScore(board.score());
                        view.showLives(board.lives());
                    });
                    prevRow = r; prevCol = c;
                } else {
                    SwingUtilities.invokeLater(() -> {
                        view.showScore(board.score());
                        view.showLives(board.lives());
                    });
                }
                if (board.levelCleared()){
                    board.clearLevelFlag();
                    board.clearUpgrades();
                    SwingUtilities.invokeLater(() -> {
                        view.refresh();
                        JOptionPane.showMessageDialog(
                                view, "Level complete!",
                                "Pac-Man", JOptionPane.INFORMATION_MESSAGE);
                        game.nextLevel();
                    });
                    return;
                }
                int baseDelay = 2000 / (FPS * board.speedMul());
                Thread.sleep(baseDelay);
            }
        } catch (InterruptedException ignored) { }
    }
}