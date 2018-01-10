package chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.UIManager;

public final class PieChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private final Dimension dim;
    private final static int padding = 2;
    private static final Color emptySlice = UIManager.getLookAndFeel().getDefaults().getColor("ProgressBar.background");
    private static final Color fullSlice = UIManager.getLookAndFeel().getDefaults().getColor("ProgressBar.foreground"); //

    private static final Slice[] slices = new Slice[2];

    public PieChart(Dimension dim) {
        this.dim = dim;
    }

    @Override
    public Dimension getPreferredSize() {
        return dim;
    }

    @Override
    public void paint(Graphics g) {
        drawPie((Graphics2D) g, new Rectangle(dim.width, dim.height), slices);
    }

    public final void setValue(int value) {
        slices[0] = new Slice(100 - value, emptySlice);
        slices[1] = new Slice(value, fullSlice);
        repaint();
    }

    private final void drawPie(Graphics2D g, Rectangle area, Slice[] slices) {

        int curValue = 25;
        int startAngle = 0;
        int arcAngle;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.BLACK);
        g.drawOval(area.x + padding, area.y + padding, area.width - (padding * 2), area.height - (padding * 2));

        for (byte i = 0; i < 2; i++) {
            startAngle = curValue * 360 / 100;
            arcAngle = slices[i].value * 360 / 100;

            g.setColor(slices[i].color);
            g.fillArc(area.x + padding + 1, area.y + padding + 1, area.width - (padding * 2) - 1, area.height - (padding * 2) - 1, startAngle,
                    arcAngle);
            curValue += slices[i].value;
        }
    }

    private final class Slice {

        private final int value;
        private final Color color;

        public Slice(int value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
