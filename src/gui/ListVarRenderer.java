package gui;

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

    private static final String SCALAIRE = "/variable/SCALAIRE.png";
    private static final String CURVE = "/variable/CURVE.png";
    private static final String MAP = "/variable/MAP.png";
    private static final String INCONNU = "/variable/INCONNU.png";
    private static final String VALUEBLOCK = "/variable/VALUEBLOCK.png";
    private static final String AXIS = "/variable/AXIS.png";
    private static final String ASCII = "/variable/ASCII.png";

    // En chargeant les icones dans un tableau le gain de temps pour afficher la liste est considerable
    private final ImageIcon[] icons = new ImageIcon[] { new ImageIcon(getClass().getResource(SCALAIRE)), new ImageIcon(getClass().getResource(CURVE)),
            new ImageIcon(getClass().getResource(MAP)), new ImageIcon(getClass().getResource(INCONNU)),
            new ImageIcon(getClass().getResource(VALUEBLOCK)), new ImageIcon(getClass().getResource(AXIS)),
            new ImageIcon(getClass().getResource(ASCII)) };

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
            setIcon(icons[0]);
            break;
        case "CURVE":
            setIcon(icons[1]);
            break;
        case "MAP":
            setIcon(icons[2]);
            break;
        case "VALUEBLOCK":
            setIcon(icons[4]);
            break;
        case "ASCII":
            setIcon(icons[6]);
            break;
        case "AXIS":
            setIcon(icons[5]);
            break;
        default:
            setIcon(icons[3]);
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
