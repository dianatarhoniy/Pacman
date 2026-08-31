package GameView;

import Model.Cell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class CellRenderer extends DefaultTableCellRenderer {
    private static final Color FLOOR = Color.BLACK;

    @Override
    public Component getTableCellRendererComponent(
            JTable t, Object v, boolean sel, boolean foc, int r, int c) {

        JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                t, v, sel, foc, r, c);

        if (v instanceof Cell cell) {
            lbl.setIcon(ImageLoader.iconFor(cell));
            lbl.setText(null);
        }
        lbl.setOpaque(true);
        lbl.setBackground(FLOOR);
        return lbl;
    }
}