package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.orsoncharts.Range;
import com.orsoncharts.renderer.RainbowScale;

import cdf.Map;
import cdf.Variable;
import tools.Preference;
import tools.Utilitaire;

public final class TableViewRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private JLabel component;
    private RainbowScale rainbowScale;
    private Map map = null;
    private Boolean setMapColor = false;

    public TableViewRenderer() {

    }

    public void colorMap(Variable var) {
        if (Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP).equals("true")) {
            if (var instanceof Map) {
                map = (Map) var;
                if (map.getMinZValue() - map.getMaxZValue() != 0) {
                    rainbowScale = new RainbowScale(new Range(map.getMinZValue(), map.getMaxZValue()), (map.getDimX() - 1) * (map.getDimY() - 1),
                            RainbowScale.BLUE_TO_RED_RANGE);
                    setMapColor = true;
                } else {
                    setMapColor = false;
                }
            } else {
                setMapColor = false;
            }
        } else {
            setMapColor = false;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setHorizontalAlignment(SwingConstants.CENTER);

        component.setForeground(Color.BLACK);

        if (Utilitaire.isNumber(value.toString()) & row > 0 & column > 0 & map != null & setMapColor == true) {
            component.setBackground(rainbowScale.valueToColor(Double.parseDouble(value.toString())));
        } else {
            component.setBackground(Color.WHITE);
        }

        if (isSelected) {
            component.setBackground(Color.LIGHT_GRAY);
            component.setForeground(Color.WHITE);
        }

        if (value.toString().indexOf(" => ") > -1) // Comparaison
        {
            component.setBorder(new LineBorder(Color.BLACK, 1));
            component.setBackground(Color.WHITE);
            component.setForeground(Color.BLACK);
        }

        if (table.getColumnCount() * table.getRowCount() == 1)
            return component;

        if (table.getColumnCount() * table.getRowCount() == 2 * table.getColumnCount()) {
            if (row == 0)
                component.setFont(new Font(null, Font.BOLD, component.getFont().getSize()));
            return component;
        }

        if (row == 0 | column == 0) {
            component.setFont(new Font(null, Font.BOLD, component.getFont().getSize()));
            return component;
        }

        return component;
    }
}
