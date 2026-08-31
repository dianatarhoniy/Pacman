package GameView;

import Model.Board;
import Model.Cell;

import javax.swing.table.AbstractTableModel;

public class GameTableModel extends AbstractTableModel {
    private Board board;
    public GameTableModel(Board board) {
        this.board = board;
    }
    @Override
    public int getRowCount() {
        return board.rows();
    }

    @Override
    public int getColumnCount() {
        return board.cols();
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Cell.class;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return board.at(rowIndex,columnIndex);
    }

}
