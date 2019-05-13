package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import cdf.Variable;
import chart.PieChart;

public final class ListLabelRenderer extends JComponent implements ListCellRenderer<Variable> {

	private static final long serialVersionUID = 1L;

	private static final String SCALAIRE = "/variable/SCALAIRE.png";
	private static final String CURVE = "/variable/CURVE.png";
	private static final String MAP = "/variable/MAP.png";
	private static final String INCONNU = "/variable/INCONNU.png";
	private static final String VALUEBLOCK = "/variable/VALUEBLOCK.png";
	private static final String AXIS = "/variable/AXIS.png";
	private static final String ASCII = "/variable/ASCII.png";

	private static final JLabel variableName = new JLabel();
	private static final PieChart score = new PieChart(new Dimension(30, 30));

	private final ImageIcon[] icons = new ImageIcon[] { new ImageIcon(getClass().getResource(SCALAIRE)), new ImageIcon(getClass().getResource(CURVE)),
			new ImageIcon(getClass().getResource(MAP)), new ImageIcon(getClass().getResource(INCONNU)),
			new ImageIcon(getClass().getResource(VALUEBLOCK)), new ImageIcon(getClass().getResource(AXIS)),
			new ImageIcon(getClass().getResource(ASCII)) };

	static {
		variableName.setHorizontalAlignment(SwingConstants.LEFT);
		variableName.setVerticalAlignment(SwingConstants.CENTER);
	}

	public ListLabelRenderer() {

		setLayout(new BorderLayout());

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
		case VALUE:
			variableName.setIcon(icons[0]);
			break;
		case CURVE:
			variableName.setIcon(icons[1]);
			break;
		case MAP:
			variableName.setIcon(icons[2]);
			break;
		case VAL_BLK:
			variableName.setIcon(icons[4]);
			break;
		case COM_AXIS:
			variableName.setIcon(icons[5]);
			break;
		case ASCII:
			variableName.setIcon(icons[6]);
			break;
		default:
			variableName.setIcon(icons[3]);
			break;
		}

		if (isSelected) {
			variableName.setFont(new Font(null, Font.BOLD, 14));
			setBorder(new LineBorder(Color.BLACK, 1));
		} else {
			setBackground(Color.WHITE);
			variableName.setFont(new Font(null, Font.PLAIN, 12));
		}

		return this;
	}

}
