package Controller;

import GameView.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public final class MainMenu extends JFrame {

    private final JButton btnStart = new JButton("Start");
    private final JButton btnExit  = new JButton("Exit");
    private final JButton btnScores = new JButton("Best scores");

    public MainMenu() {
        super("Pac-Man");

        ImageIcon bg = ImageLoader.iconForBackground(
                "resources/poster.png", 400, 400);
        JLabel back = new JLabel(bg);
        back.setLayout(new GridBagLayout());
        setContentPane(back);

        JPanel p = new JPanel(new GridLayout(3,1,0,10));
        p.setOpaque(false);
        p.add(btnStart);
        p.add(btnScores);
        p.add(btnExit);
        back.add(p);


        pack();
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void onStart(ActionListener l) { btnStart.addActionListener(l); }
    public void onExit (ActionListener l) { btnExit .addActionListener(l); }
    public void onScores(ActionListener l){ btnScores.addActionListener(l); }
}