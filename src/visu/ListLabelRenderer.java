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

import paco.PaCo;
import paco.Variable;

public final class ListLabelRenderer extends JLabel implements ListCellRenderer<Variable> {

    private static final long serialVersionUID = 1L;

    private static final String SCALAIRE = "/variable/SCALAIRE.gif";
    private static final String CURVE = "/variable/CURVE.gif";
    private static final String MAP = "/variable/MAP.gif";
    private static final String INCONNU = "/variable/INCONNU.gif";
    private static final String VALUEBLOCK = "/variable/VALUEBLOCK.gif";
    private static final String AXIS = "/variable/AXIS.gif";
    private static final String ASCII = "/variable/ASCII.gif";

    public ListLabelRenderer() {
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Variable> list, Variable value, int index, boolean isSelected,
            boolean cellHasFocus) {

        setText(value.getShortName());
        setBorder(new EmptyBorder(2, 0, 2, 0));

        switch (value.getCategory()) {
        case PaCo._C:
            setIcon(new ImageIcon(getClass().getResource(SCALAIRE)));
            break;
        case PaCo._T:
            setIcon(new ImageIcon(getClass().getResource(CURVE)));
            break;
        case PaCo._T_GROUPED:
            setIcon(new ImageIcon(getClass().getResource(CURVE)));
            break;
        case PaCo._M:
            setIcon(new ImageIcon(getClass().getResource(MAP)));
            break;
        case PaCo._M_GROUPED:
            setIcon(new ImageIcon(getClass().getResource(MAP)));
            break;
        case PaCo._T_CA:
            setIcon(new ImageIcon(getClass().getResource(VALUEBLOCK)));
            break;
        case PaCo._A:
            setIcon(new ImageIcon(getClass().getResource(AXIS)));
            break;
        case PaCo.ASCII:
            setIcon(new ImageIcon(getClass().getResource(ASCII)));
            break;
        default:
            setIcon(new ImageIcon(getClass().getResource(INCONNU)));
            break;
        }

        if (isSelected) {
            setBackground(Color.getHSBColor(100, 100, 100));
            setFont(new Font(null, Font.BOLD, 12));
            setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            setBackground(Color.WHITE);
            setFont(new Font(null, Font.PLAIN, 12));
        }

        return this;
    }

}
