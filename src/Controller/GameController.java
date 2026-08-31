package Controller;

import GameView.View;
import Model.Board;
import Thread.PlayerThread;
import Thread.GhostThread;
import Util.HighScoreManager;
import Thread.GameTimer;


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class GameController {

    private MainMenu  menu;
    private View      gameView;
    private Board     board;
    private PlayerThread pacThread;
    private GhostThread ghostThread;
    private GameTimer clock;
    private final HighScoreManager scores = new HighScoreManager();

    public void launch(){
        menu = new MainMenu();
        menu.onStart(e -> askBoardSize());
        menu.onScores(e -> scores.showDialog(menu));
        menu.onExit (e -> System.exit(0));
        menu.setVisible(true);
    }

    private void askBoardSize(){
        StartDialog d = new StartDialog(menu);
        d.setVisible(true);
        if (!d.accepted()) return;
        startGame(d.rows(), d.cols());
    }

    private void startGame(int r,int c){
        if (menu!=null){ menu.dispose(); menu=null; }

        board    = new Board(r,c);
        gameView = new View(board);
        clock = new GameTimer(gameView);
        clock.start();
        new Controller(gameView, board);
        gameView.requestFocusInWindow();
        gameView.addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent e){ endGame(); }
        });

        pacThread   = new PlayerThread(board, gameView, this);
        ghostThread = new GhostThread(board, gameView);
        pacThread.start(); ghostThread.start();
    }

    public void endGame() {
        endThreadsOnly();

        if (clock != null) {
            clock.stop();
            clock = null;
        }
        scores.addWithDialog(gameView, board.score());

        SwingUtilities.invokeLater(() -> {
            if (gameView != null) {
                gameView.dispose();
                gameView = null;
            }
            menu = new MainMenu();
            menu.onStart(e -> askBoardSize());
            menu.onScores(e -> scores.showDialog(menu));
            menu.onExit (e -> System.exit(0));
            menu.setVisible(true);
        });
    }

    public void nextLevel(){
        if (clock != null) clock.stop();
        clock = null;

        clock = new GameTimer(gameView);
        clock.start();
        int nRows = Math.min(board.rows()+2, 59);
        int nCols = Math.min(board.cols()+2, 59);

        int keepScore = board.score();
        int keepLives = board.lives();

        endThreadsOnly();

        board = new Board(nRows, nCols);
        board.addScore(keepScore);
        while (board.lives() > keepLives) board.loseLife();

        gameView.dispose();
        gameView = new View(board);
        new Controller(gameView, board);
        gameView.requestFocusInWindow();


        pacThread   = new PlayerThread(board, gameView, this);
        ghostThread = new GhostThread(board, gameView);
        pacThread.start(); ghostThread.start();
    }

    private void endThreadsOnly(){
        if (pacThread  !=null) pacThread.interrupt();
        if (ghostThread!=null) ghostThread.interrupt();
    }
    public void stopClock(){ if (clock!=null) clock.stop(); }

}