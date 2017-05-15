package paco;

import javax.swing.table.AbstractTableModel;

public final class TableModelView extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private String[] entete = new String[0];
    private String[][] values = new String[0][0];

    // public TableModelView() {
    // for (int i = 0; i < entete.length; i++)
    // entete[i] = "";
    // }

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
        return values[row][col];
    }

    public void setData(String[][] data) {
        this.values = data;
        this.entete = new String[values[0].length];
        for (int i = 0; i < values[0].length; i++)
            this.entete[i] = "";
        fireTableStructureChanged();
        fireTableDataChanged();
    }
}
