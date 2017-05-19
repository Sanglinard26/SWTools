package visu;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public final class TableViewRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private JLabel component;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setHorizontalAlignment(SwingConstants.CENTER);

        if (table.getColumnCount() * table.getRowCount() == 1)
            return component;

        if (table.getColumnCount() * table.getRowCount() == 2 * table.getColumnCount()) {
            if (row == 0)
                component.setFont(new Font(null, Font.BOLD, component.getFont().getSize()));
            return component;
        }

        if (row == 0 | column == 0) {
            component.setFont(new Font(null, Font.BOLD, component.getFont().getSize()));
            return component;
        }

        return component;
    }
}
