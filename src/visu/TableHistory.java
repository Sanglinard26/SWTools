package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.TableView.TableCell;

import paco.TableModelHistory;

public final class TableHistory extends JTable {
	
	private static final long serialVersionUID = 1L;

	public TableHistory(TableModelHistory model) {
		super(model);
		this.setDefaultRenderer(Object.class, new TableHistoryRenderer());
		this.setRowSelectionAllowed(false);
		this.setRowHeight(30);;
		
		this.getColumnModel().getColumn(0).setMaxWidth(200);
		this.getColumnModel().getColumn(1).setMaxWidth(150);
		this.getColumnModel().getColumn(2).setMaxWidth(200);
		this.getColumnModel().getColumn(3).setMaxWidth(900);
		
		this.getColumnModel().getColumn(0).setHeaderRenderer(new ColumnRender());
		this.getColumnModel().getColumn(1).setHeaderRenderer(new ColumnRender());
		this.getColumnModel().getColumn(2).setHeaderRenderer(new ColumnRender());
		this.getColumnModel().getColumn(3).setHeaderRenderer(new ColumnRender());
	}
	
	@Override
	public TableModelHistory getModel() {
		return (TableModelHistory) super.getModel();
	}
	
	private class ColumnRender extends JLabel implements TableCellRenderer
	{
		public ColumnRender() {
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setPreferredSize(new Dimension(0, 30));
			this.setOpaque(true);
			this.setBackground(Color.LIGHT_GRAY);
			this.setBorder(new LineBorder(Color.DARK_GRAY, 1));
			this.setFont(new Font(null, Font.BOLD, 14));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			
			setText(value.toString());
			
			return this;
		}
		
	}

}

