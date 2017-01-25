package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import lab.Lab;

public final class ListLabRenderer extends JLabel implements ListCellRenderer<Lab> {

    private static final long serialVersionUID = 1L;

    public ListLabRenderer() {
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Lab> list, Lab value, int index, boolean isSelected, boolean cellHasFocus) {

        setText(value.getName());
        setBorder(new EmptyBorder(2, 0, 2, 0));
        setToolTipText("Nombre de label(s) : " + value.getListVariable().size());

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
