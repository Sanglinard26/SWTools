package visu;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import paco.TableModelView;

public final class TableView extends JTable {

	private static final long serialVersionUID = 1L;

	public TableView(TableModelView model) {
		super(model);
		adjustCellsSize();
		//this.setDefaultRenderer(Object.class, new TableViewRenderer());
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.setCellSelectionEnabled(true);
	}

	private void adjustCellsSize()
	{
		TableColumnModel columnModel = this.getColumnModel();
		for (int col = 0; col<columnModel.getColumnCount(); col++)
		{
			int maxWidth = 0;
			for (int row = 0; row<this.getRowCount(); row++)
			{
				TableCellRenderer cellRenderer = this.getCellRenderer(row, col);
				Object value = this.getValueAt(row, col);
				Component component = cellRenderer.getTableCellRendererComponent(this, value, false, false, row, col);
				maxWidth = Math.max(component.getPreferredSize().width, maxWidth);
			}
			TableColumn column = columnModel.getColumn(col);
			column.setPreferredWidth(maxWidth);
		}

	}

}
