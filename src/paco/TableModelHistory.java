package paco;

import javax.swing.table.AbstractTableModel;

public final class TableModelHistory extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    public static final String[] ENTETE = new String[] { "DATE", "AUTEUR", "SCORE", "COMMENTAIRES" };
    private String[][] history = new String[0][0];

    @Override
    public String getColumnName(int column) {
        return ENTETE[column];
    }

    @Override
    public int getColumnCount() {
        return ENTETE.length;
    }

    @Override
    public int getRowCount() {
        return history.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return history[row][col];
    }

    public void setData(String[][] data) {
        this.history = data;
        fireTableDataChanged();
    }

}
