package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.orsoncharts.Range;
import com.orsoncharts.renderer.RainbowScale;

import cdf.Map;
import cdf.Variable;
import utils.NumeralString;
import utils.Preference;

public final class TableViewRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Color backgroundCell = UIManager.getLookAndFeel().getDefaults().getColor("Tree.selectionBackground");

	private RainbowScale rainbowScale;
	private Map map = null;
	private boolean setMapColor;

	public TableViewRenderer() {

	}

	public final void colorMap(Variable var) {

		setMapColor = false;

		if (Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP).equals("true") && var instanceof Map) {
			map = (Map) var;
			if (map.getMinZValue() - map.getMaxZValue() != 0) {
				rainbowScale = new RainbowScale(new Range(map.getMinZValue(), map.getMaxZValue()),
						(map.getValues().getDimX() - 1) * (map.getValues().getDimY() - 1), RainbowScale.BLUE_TO_RED_RANGE);
				setMapColor = true;
			}
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setHorizontalAlignment(SwingConstants.CENTER);

		setForeground(Color.BLACK);

		final String stringValue = value.toString();

		if (NumeralString.isNumber(stringValue) && row > 0 && column > 0 && map != null && setMapColor == true) {
			setBackground(rainbowScale.valueToColor(Double.parseDouble(stringValue)));
		} else {
			setBackground(Color.WHITE);
		}

		if (stringValue.indexOf(" | ") > -1) // Comparaison
		{
			setBorder(new LineBorder(Color.BLACK, 1));
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
		}

		if (isSelected) {
			setBackground(backgroundCell);
			setForeground(Color.WHITE);
		}

		if (table.getColumnCount() * table.getRowCount() == 1)
			return this;

		if (table.getColumnCount() * table.getRowCount() == 2 * table.getColumnCount()) {
			if (row == 0)
				setFont(new Font(null, Font.BOLD, getFont().getSize()));
			return this;
		}

		if (row == 0 | column == 0) {
			setFont(new Font(null, Font.BOLD, getFont().getSize()));
			return this;
		}

		return this;
	}
}
