package visu;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import paco.TableModelHistory;

public final class TableHistory extends JTable {
	
	private static final long serialVersionUID = 1L;

	public TableHistory(TableModelHistory model) {
		super(model);
		this.setDefaultRenderer(Object.class, new TableHistoryRenderer());
		this.setRowSelectionAllowed(false);
		this.setRowHeight(30);
		
		this.getColumnModel().getColumn(0).setMaxWidth(150);
		this.getColumnModel().getColumn(1).setMaxWidth(150);
		this.getColumnModel().getColumn(2).setMaxWidth(200);
		this.getColumnModel().getColumn(3).setMaxWidth(900);
	}
	
	@Override
	public TableModelHistory getModel() {
		return (TableModelHistory) super.getModel();
	}

}

