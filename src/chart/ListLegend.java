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

public final class ListLegend extends JList<Serie> {

    private static final long serialVersionUID = 1L;

    public ListLegend(Serie[] data) {
        super(data);
        setCellRenderer(new ListLegendRenderer());
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
            label.setFont(new Font(null, Font.PLAIN, 12));

            hue = (float) (index) / (float) (list.getModel().getSize());

            label.setForeground(Color.getHSBColor(hue, 1, 1));

            if (isSelected) {
                label.setFont(new Font(null, Font.BOLD, 12));
                label.setForeground(Color.WHITE);
            }

            label.setText(((Serie) value).getName());

            return label;
        }

    }
}
