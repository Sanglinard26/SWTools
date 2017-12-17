package paco;

import javax.swing.table.AbstractTableModel;

public final class TableModelView extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] entete = new String[0];
    private String[][] values = new String[0][0];

    @Override
    public String getColumnName(int column) {
        return entete[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getColumnCount() {
        return entete.length;
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

    public void setData(String[][] data) {
        this.values = data;

        final int nbCol = values[0].length;

        this.entete = new String[nbCol];
        for (short i = 0; i < nbCol; i++)
            this.entete[i] = "";
        fireTableStructureChanged();
    }
}
