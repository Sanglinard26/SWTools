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
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setTableHeader(null);
        this.setDefaultRenderer(Object.class, new TableViewRenderer());
        this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        this.setCellSelectionEnabled(true);
        // this.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseReleased(MouseEvent e) {
        // if (e.isPopupTrigger()) {
        // final JPopupMenu menu = new JPopupMenu();
        // final JMenu menuCopy = new JMenu("Copier dans le presse-papier");
        // JMenuItem subMenu = new JMenuItem("Format image");
        // subMenu.addActionListener(new ActionListener() {
        //
        // @Override
        // public void actionPerformed(ActionEvent e) {
        //
        // StringBuilder sb = new StringBuilder();
        //
        // for (int row : TableView.this.getSelectedRows()) {
        // for (int col : TableView.this.getSelectedColumns()) {
        // sb.append(TableView.this.getModel().getValueAt(row, col) + "\t");
        // }
        // sb.append("\n");
        // }
        // copyTxtToClipboard(sb.toString());
        // }
        // });
        // menuCopy.add(subMenu);
        // menu.add(menuCopy);
        // menu.show(e.getComponent(), e.getX(), e.getY());
        // }
        // }
        // });
    }

    @Override
    public TableModelView getModel() {
        return (TableModelView) super.getModel();
    }

    // private void copyTxtToClipboard(String s) {
    // final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // clipboard.setContents(new TxtTransfert(s), null);
    // }
    //
    // final class TxtTransfert implements Transferable {
    // private String s;
    //
    // public TxtTransfert(String s) {
    // this.s = s;
    // }
    //
    // @Override
    // public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    // return s;
    // }
    //
    // @Override
    // public DataFlavor[] getTransferDataFlavors() {
    // return new DataFlavor[] { DataFlavor.stringFlavor };
    // }
    //
    // @Override
    // public boolean isDataFlavorSupported(DataFlavor flavor) {
    // return DataFlavor.stringFlavor.equals(flavor);
    // }
    //
    // }

    public static void adjustCellsSize(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int col = 0; col < columnModel.getColumnCount(); col++) {
            int maxWidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Object value = table.getValueAt(row, col);
                Component component = cellRenderer.getTableCellRendererComponent(table, value, false, false, row, col);
                maxWidth = Math.max(component.getPreferredSize().width, maxWidth);
            }
            TableColumn column = columnModel.getColumn(col);
            column.setPreferredWidth(maxWidth + 10);
        }

    }

}
