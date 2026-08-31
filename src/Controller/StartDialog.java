package Controller;

import javax.swing.*;

public final class StartDialog extends JDialog {

    private final JSpinner rows = new JSpinner(new SpinnerNumberModel(21,  11, 100, 1));
    private final JSpinner cols = new JSpinner(new SpinnerNumberModel(21,  11, 100, 1));
    private boolean accepted = false;

    public StartDialog(JFrame owner) {
        super(owner, "Enter dimensions", true);

        rows.setPreferredSize(cols.getPreferredSize());

        JPanel grid = new JPanel();
        grid.add(new JLabel("Rows:")); grid.add(rows);
        grid.add(Box.createHorizontalStrut(10));
        grid.add(new JLabel("Cols:")); grid.add(cols);

        JButton ok = new JButton("Start");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> { accepted = true; dispose(); });
        cancel.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.add(ok); south.add(cancel);

        add(grid,   "Center");
        add(south,  "South");
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean accepted() { return accepted; }
    public int rows() { return (Integer) rows.getValue(); }
    public int cols() { return (Integer) cols.getValue(); }
}