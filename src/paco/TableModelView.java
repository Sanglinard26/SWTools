package paco;

import javax.swing.table.AbstractTableModel;

public final class TableModelView extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private int nbCol = 0;
    private String[][] values = new String[0][0];

    @Override
    public String getColumnName(int column) {
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getColumnCount() {
        return nbCol;
    }

    @Override
    public int getRowCount() {
        return values.length;
    }

    @Override
    public Object getValueAt(int row, int col) {

        final Object value = values[row][col];

        if (value != null) {
            return value;
        }
        return "";
    }

    public final void setData(String[][] data) {
        this.values = data;
        this.nbCol = values[0].length;

        fireTableStructureChanged();
    }
}
