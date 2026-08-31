package Controller;

import GameView.View;
import Model.Board;
import Model.Direction;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {
    View view;
    Board board;
    public Controller(View view, Board model) {
        this.view = view;
        this.board = model;
        view.addKeyListener(this);
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP    -> board.requestDirection(Direction.UP);
            case KeyEvent.VK_DOWN  -> board.requestDirection(Direction.DOWN);
            case KeyEvent.VK_LEFT  -> board.requestDirection(Direction.LEFT);
            case KeyEvent.VK_RIGHT -> board.requestDirection(Direction.RIGHT);
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
}
