package visu;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public final class TableViewRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	private Component component;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		return component;
	}

}
