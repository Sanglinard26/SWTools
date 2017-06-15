/*
 * Creation : 15 juin 2017
 */
package chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class ListLegend extends JList<String> implements ListSelectionListener {

    private static final long serialVersionUID = 1L;

    public ListLegend(String[] data) {
        super(data);
        setCellRenderer(new ListLegendRenderer());

        addListSelectionListener(this);
    }

    private final class ListLegendRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        private JLabel label;
        private float hue;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(60, 20));
            label.setBackground(Color.WHITE);
            label.setFont(new Font(null, Font.BOLD, 12));

            hue = (float) (index) / (float) (list.getModel().getSize());

            label.setForeground(Color.getHSBColor(hue, 1, 1));

            if (isSelected) {
                label.setBorder(new LineBorder(Color.BLACK, 1));
            } else {
                label.setBorder(new LineBorder(Color.BLACK, 0));
            }

            label.setText((String) value);

            return label;
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() & !this.isSelectionEmpty()) {
            for (int i = 0; i < this.getSelectedValuesList().size(); i++) {
                System.out.println(this.getModel().getElementAt(i));
            }
        }
    }

}
