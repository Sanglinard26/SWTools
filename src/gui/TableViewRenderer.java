package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import cdf.Map;
import cdf.Variable;
import net.ericaro.surfaceplotter.surface.ColorModel;
import utils.NumeralString;
import utils.Preference;

public final class TableViewRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final Color backgroundCell = UIManager.getLookAndFeel().getDefaults().getColor("Tree.selectionBackground");

    private ColorMap colorMap;
    private Map map = null;
    private boolean setMapColor;

    public final void colorMap(Variable var) {

        setMapColor = false;

        if (Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP).equals("true") && var instanceof Map) {
            map = (Map) var;
            if (map.getMinZValue() - map.getMaxZValue() != 0) {
            	colorMap = new ColorMap((byte) 1, 0.0F, 1.0F, 1.0F, 0.0F, 0.6666F);
                colorMap.setRange(map.getMinZValue(), map.getMaxZValue());
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

        if (NumeralString.isNumber(stringValue) && row > 0 && column > 0 && map != null && setMapColor) {
            setBackground(colorMap.getPolygonColor(colorMap.getColorFraction(Float.parseFloat(stringValue))));
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
    
    private final class ColorMap extends ColorModel
    {
    	
    	private float minValue = 0;
    	private float maxValue = 0;

		public ColorMap(byte mode, float hue, float sat, float bright, float min, float max) {
			super(mode, hue, sat, bright, min, max);
		}
		
		public final void setRange(float min, float max)
		{
			this.minValue = min;
			this.maxValue = max;
		}

		public final float getColorFraction(float value) {
	        return (value - this.minValue) / (this.maxValue-this.minValue);
	    }
    	
    }
}
