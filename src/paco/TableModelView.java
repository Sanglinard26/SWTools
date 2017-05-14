package paco;

import javax.swing.table.AbstractTableModel;

public final class TableModelView extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    public final String[] entete;
    private final String[][] values;

    public TableModelView(String[][] values) {
		this.values = values;
		
		entete = new String[values[0].length];
		for (int i = 0; i<entete.length; i++) entete[i] = "";
	}
    
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
}
