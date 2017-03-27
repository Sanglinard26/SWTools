package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import paco.TableModelHistory;

public final class TableHistory extends JTable {

	private static final long serialVersionUID = 1L;

	public TableHistory(TableModelHistory model) {
		super(model);
		this.setDefaultRenderer(Object.class, new TableHistoryRenderer());
		this.setRowSelectionAllowed(false);
		this.setRowHeight(40);

		for(int i = 0; i<this.getColumnModel().getColumnCount();i++)
		{
			this.getColumnModel().getColumn(i).setHeaderRenderer(new ColumnRender());
			switch (i) {
			case 1:
				this.getColumnModel().getColumn(i).setMaxWidth(150);
				break;
			case 3:
				this.getColumnModel().getColumn(i).setMaxWidth(900);
				this.getColumnModel().getColumn(i).setCellEditor(new TableHistoryEditor());
				break;

			default:
				this.getColumnModel().getColumn(i).setMaxWidth(200);
				break;
			}
		}
	}

	@Override
	public TableModelHistory getModel() {
		return (TableModelHistory) super.getModel();
	}

	private class ColumnRender extends JLabel implements TableCellRenderer
	{
		private static final long serialVersionUID = 1L;

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

