package visu;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public final class TableHistoryRenderer implements TableCellRenderer {

    private Component component;
    private final JLabel label = new JLabel();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JTextPane textPane = new JTextPane();
    private final JScrollPane scrollPane = new JScrollPane(textPane);

    private static final HashMap<String, Integer> maturite = new HashMap<String, Integer>();

    public TableHistoryRenderer() {
        label.setOpaque(true);
        label.setBackground(Color.WHITE);

        maturite.put("changed", 0);
        maturite.put("prelimcalibrated", 25);
        maturite.put("calibrated", 50);
        maturite.put("checked", 75);
        maturite.put("completed", 100);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        switch (column) {
        case 0:
            label.setText(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            component = label;
            break;
        case 1:
            label.setText(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            component = label;
            break;
        case 2:
            progressBar.setValue(maturite.get(value.toString().toLowerCase()));
            progressBar.setString(String.valueOf(maturite.get(value.toString().toLowerCase())) + "%");
            progressBar.setStringPainted(true);
            component = progressBar;
            break;
        case 3:
            // if(value.toString().length()>200) table.setRowHeight(row, 100);
            textPane.setText(value.toString());
            // component = textPane;
            component = scrollPane;
            break;
        }

        return component;
    }

}
