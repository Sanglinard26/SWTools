package visu;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableCellEditor;

public final class TableHistoryEditor extends AbstractCellEditor implements TableCellEditor {
	
	private static final long serialVersionUID = 1L;
	
	private final JTextPane textPane = new JTextPane(); 
	private final JScrollPane scrollPane = new JScrollPane(textPane);
	
	public TableHistoryEditor() {
		textPane.setEditable(false);
	}

	@Override
	public Object getCellEditorValue() {
		return textPane.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			textPane.setText(value.toString());
		return scrollPane;
	}
}
