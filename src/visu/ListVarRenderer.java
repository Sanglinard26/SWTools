package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import lab.Variable;

public final class ListVarRenderer extends JLabel implements ListCellRenderer<Variable> {

    private static final long serialVersionUID = 1L;

    private static final String SCALAIRE = "/variable/SCALAIRE.gif";
    private static final String CURVE = "/variable/CURVE.gif";
    private static final String MAP = "/variable/MAP.gif";
    private static final String INCONNU = "/variable/INCONNU.gif";
    private static final String VALUEBLOCK = "/variable/VALUEBLOCK.gif";
    private static final String ASCII = "/variable/ASCII.gif";

    public ListVarRenderer() {
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Variable> list, Variable value, int index, boolean isSelected,
            boolean cellHasFocus) {

        setText(value.getNom());
        setBorder(new EmptyBorder(2, 0, 2, 0));
        setToolTipText("Label de type : " + value.getType());

        switch (value.getType()) {
        case "SCALAIRE":
            setIcon(new ImageIcon(getClass().getResource(SCALAIRE)));
            break;
        case "CURVE":
            setIcon(new ImageIcon(getClass().getResource(CURVE)));
            break;
        case "MAP":
            setIcon(new ImageIcon(getClass().getResource(MAP)));
            break;
        case "VALUEBLOCK":
            setIcon(new ImageIcon(getClass().getResource(VALUEBLOCK)));
            break;
        case "ASCII":
            setIcon(new ImageIcon(getClass().getResource(ASCII)));
            break;
        default:
            setIcon(new ImageIcon(getClass().getResource(INCONNU)));
            break;
        }

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
