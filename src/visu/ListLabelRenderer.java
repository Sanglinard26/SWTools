package visu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import cdf.Cdf;
import cdf.Variable;
import graph.PieChart;

public final class ListLabelRenderer extends JPanel implements ListCellRenderer<Variable> {

    private static final long serialVersionUID = 1L;

    private static final String SCALAIRE = "/variable/SCALAIRE.gif";
    private static final String CURVE = "/variable/CURVE.gif";
    private static final String MAP = "/variable/MAP.gif";
    private static final String INCONNU = "/variable/INCONNU.gif";
    private static final String VALUEBLOCK = "/variable/VALUEBLOCK.gif";
    private static final String AXIS = "/variable/AXIS.gif";
    private static final String ASCII = "/variable/ASCII.gif";

    private final JLabel variableName = new JLabel();
    private final PieChart score = new PieChart(new Dimension(30, 30));

    public ListLabelRenderer() {

        setLayout(new BorderLayout());

        variableName.setHorizontalAlignment(SwingConstants.LEFT);
        variableName.setVerticalAlignment(SwingConstants.CENTER);

        add(variableName, BorderLayout.CENTER);
        add(score, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Variable> list, Variable value, int index, boolean isSelected,
            boolean cellHasFocus) {

        setBorder(new EmptyBorder(2, 0, 2, 0));

        variableName.setText(value.getShortName());
        score.setValue(value.getLastScore());

        switch (value.getCategory()) {
        case Cdf.VALUE:
            variableName.setIcon(new ImageIcon(getClass().getResource(SCALAIRE)));
            break;
        case Cdf.CURVE_INDIVIDUAL:
            variableName.setIcon(new ImageIcon(getClass().getResource(CURVE)));
            break;
        case Cdf.CURVE_GROUPED:
            variableName.setIcon(new ImageIcon(getClass().getResource(CURVE)));
            break;
        case Cdf.CURVE_FIXED:
            variableName.setIcon(new ImageIcon(getClass().getResource(CURVE)));
            break;
        case Cdf.MAP_INDIVIDUAL:
            variableName.setIcon(new ImageIcon(getClass().getResource(MAP)));
            break;
        case Cdf.MAP_GROUPED:
            variableName.setIcon(new ImageIcon(getClass().getResource(MAP)));
            break;
        case Cdf.MAP_FIXED:
            variableName.setIcon(new ImageIcon(getClass().getResource(MAP)));
            break;
        case Cdf.VALUE_BLOCK:
            variableName.setIcon(new ImageIcon(getClass().getResource(VALUEBLOCK)));
            break;
        case Cdf.AXIS_VALUES:
            variableName.setIcon(new ImageIcon(getClass().getResource(AXIS)));
            break;
        case Cdf.ASCII:
            variableName.setIcon(new ImageIcon(getClass().getResource(ASCII)));
            break;
        default:
            variableName.setIcon(new ImageIcon(getClass().getResource(INCONNU)));
            break;
        }

        if (isSelected) {
            setBackground(Color.getHSBColor(100, 100, 100));
            variableName.setFont(new Font(null, Font.BOLD, 12));
            setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            setBackground(Color.WHITE);
            variableName.setFont(new Font(null, Font.PLAIN, 12));
        }

        return this;
    }

}
