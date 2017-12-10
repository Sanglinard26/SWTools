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
    private final TableViewRenderer renderer;

    public TableView(TableModelView model) {
        super(model);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setTableHeader(null);
        renderer = new TableViewRenderer();
        this.setDefaultRenderer(Object.class, renderer);
        this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.setCellSelectionEnabled(true);
    }

    @Override
    public TableModelView getModel() {
        return (TableModelView) super.getModel();
    }

    @Override
    public TableViewRenderer getDefaultRenderer(Class<?> paramClass) {
        return renderer;
    }

    public static final void adjustCells(JTable table) {

        TableColumnModel columnModel = table.getColumnModel();
        int maxWidth;
        TableCellRenderer cellRenderer;
        Object value;
        Component component;
        TableColumn column;

        for (int col = 0; col < columnModel.getColumnCount(); col++) {
            maxWidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                cellRenderer = table.getCellRenderer(row, col);
                value = table.getValueAt(row, col);
                component = cellRenderer.getTableCellRendererComponent(table, value, false, false, row, col);
                maxWidth = Math.max(component.getPreferredSize().width, maxWidth);
            }
            column = columnModel.getColumn(col);
            column.setPreferredWidth(maxWidth + 10);
        }

    }

}
