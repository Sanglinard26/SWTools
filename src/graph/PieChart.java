package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public final class PieChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private final Dimension dim;
    private double value;
    private final static int padding = 2;
    private static final Color emptySlice = Color.WHITE;
    private static final Color fullSlice = Color.BLUE;

    private static final Slice[] slices = new Slice[2];

    public PieChart(Dimension dim, double value) {
        this.dim = dim;
        this.value = value;

        slices[0] = new Slice(100 - value, emptySlice);
        slices[1] = new Slice(value, fullSlice);
    }

    public PieChart(Dimension dim) {
        this.dim = dim;
        slices[0] = new Slice(100 - value, emptySlice);
        slices[1] = new Slice(value, fullSlice);
    }

    @Override
    public Dimension getPreferredSize() {
        return dim;
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        drawPie((Graphics2D) g, new Rectangle(dim.width, dim.height), slices);
    }

    public void setValue(double value) {
        this.value = value;
        slices[0] = new Slice(100 - value, emptySlice);
        slices[1] = new Slice(value, fullSlice);
        repaint();
    }

    private void drawPie(Graphics2D g, Rectangle area, Slice[] slices) {

        double curValue = 25.0D;
        int startAngle = 0;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.BLACK);
        g.drawOval(area.x + padding, area.y + padding, area.width - (padding * 2), area.height - (padding * 2));

        for (int i = 0; i < 2; i++) {
            startAngle = (int) (curValue * 360 / 100);
            int arcAngle = (int) (slices[i].value * 360 / 100);

            g.setColor(slices[i].color);
            g.fillArc(area.x + padding + 1, area.y + padding + 1, area.width - (padding * 2) - 1, area.height - (padding * 2) - 1, startAngle,
                    arcAngle);
            curValue += slices[i].value;
        }
    }

    private final class Slice {
        private double value;
        private Color color;

        public Slice(double value, Color color) {
            this.value = value;
            this.color = color;
        }
    }
}
