/*
 * Creation : 6 avr. 2017
 */
package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import paco.PaCo;

public class ListPacoRenderer extends JLabel implements ListCellRenderer<PaCo> {

    private static final long serialVersionUID = 1L;

    public ListPacoRenderer() {
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PaCo> list, PaCo value, int index, boolean isSelected, boolean cellHasFocus) {

        setText(value.toString());
        setBorder(new EmptyBorder(2, 0, 2, 0));

        if (isSelected) {
            setBackground(Color.WHITE);
            setFont(new Font(null, Font.BOLD, 12));
            setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            setBackground(Color.WHITE);
            setFont(new Font(null, Font.PLAIN, 12));
        }

        return this;
    }

}
