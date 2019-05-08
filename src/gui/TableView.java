package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cdf.TableModelView;

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
        this.setGridColor(Color.GRAY);
        this.setBorder(new LineBorder(Color.BLACK));
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

        final TableColumnModel columnModel = table.getColumnModel();
        final int nbCol = columnModel.getColumnCount();
        final int nbRow = table.getRowCount();
        int maxWidth;
        TableCellRenderer cellRenderer;
        Object value;
        Component component;
        TableColumn column;

        for (short col = 0; col < nbCol; col++) {
            maxWidth = 0;
            for (short row = 0; row < nbRow; row++) {
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
