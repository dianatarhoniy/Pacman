package Thread;

import GameView.View;

import javax.swing.*;

public final class GameTimer {
    private int seconds = 0;
    private final View view;
    private final Timer t;

    public GameTimer(View v) {
        this.view = v;
        t = new Timer(1000, e -> {
            seconds++;
            view.showTime(seconds);
        });
    }
    public void start() { view.showTime(0); t.start(); }
    public void stop()  { t.stop(); }
}