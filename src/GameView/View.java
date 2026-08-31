package GameView;

import Model.Board;
import Model.Cell;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    private final JLabel score = new JLabel("Score: 0");
    private final JLabel lives = new JLabel("Lives: 3");
    private final JLabel hint   = new JLabel(" ");
    private final JLabel timer = new JLabel("00:00");

    private final GameTableModel model;
    private final JTable         table;
    private static final int MIN_CELL = 8;
    public View(Board board) {
        super("Pac-Man");
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(score);
        top.add(Box.createHorizontalStrut(20));
        top.add(lives);
        top.add(Box.createHorizontalStrut(20));
        top.add(timer);
        hint.setForeground(Color.PINK);
        hint.setPreferredSize(new Dimension(180, 16));
        top.add(hint);
        add(top, BorderLayout.NORTH);
        model = new GameTableModel(board);
        table = new JTable(model);
        table.setDefaultRenderer(Cell.class, new CellRenderer());
        table.setTableHeader(null);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setFocusable(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        add(table);
        int rows = board.rows();
        int cols = board.cols();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int cell = Math.max(
                MIN_CELL,
                Math.min((int) (screen.width  * 0.90) / cols,
                        (int) (screen.height * 0.90) / rows));
        table.setRowHeight(cell);
        for (int c = 0; c < cols; c++)
            table.getColumnModel().getColumn(c).setPreferredWidth(cell);
        ImageLoader.setTileSize(cell);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        requestFocusInWindow();
    }
    public void refresh()      { model.fireTableDataChanged(); }
    public JTable boardTable() { return table; }
    public void updateCell(int row, int col) {
        model.fireTableCellUpdated(row, col);
        Rectangle r = table.getCellRect(row, col, false);
        table.repaint(r);
    }
    public void showScore(int value) {

        score.setText("Score: " + value);
    }
    public void showLives(int val) {
        lives.setText("Lives: " + val);
    }
    public void showHint(String msg) {
        hint.setText(msg);
        new javax.swing.Timer(2000, e -> hint.setText(" ")) {{
            setRepeats(false);
            start();
        }};
    }
    public void showTime(int sec) {
        timer.setText(String.format("%02d:%02d", sec / 60, sec % 60));
    }

}