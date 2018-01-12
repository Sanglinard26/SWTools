package chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.UIManager;

public final class PieChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private final static int padding = 2;
    private static final Color emptySlice = Color.WHITE;
    private static final Color fullSlice = UIManager.getLookAndFeel().getDefaults().getColor("textHighlight");
    private static final Color score0 = Color.RED;
    private static final Color score25 = Color.ORANGE;
    private static final Color score50 = Color.YELLOW;
    private static final Color score75 = Color.GREEN;
    private static final Color score100 = Color.BLUE;

    private static final int SCORE_MAX = 100;

    private final Dimension dim;
    private int nbSlice;
    private final Slice[] slices;
    private final int sumValues;

    public PieChart(Dimension dim) {
        this(dim, 2, SCORE_MAX);
    }

    public PieChart(Dimension dim, int nbSlice, int sumValues) {
        this.dim = dim;
        this.nbSlice = nbSlice;
        this.slices = new Slice[nbSlice];
        this.sumValues = sumValues;
    }

    @Override
    public Dimension getPreferredSize() {
        return dim;
    }

    @Override
    public void paintComponent(Graphics g) {
        drawPie((Graphics2D) g, new Rectangle(dim.width, dim.height), slices);
    }

    public final void setValue(int value) {
        slices[0] = new Slice(SCORE_MAX - value, emptySlice);
        slices[1] = new Slice(value, fullSlice);
        repaint();
    }

    public final void setValue(HashMap<Integer, Integer> score) {
        slices[0] = new Slice(score.get(0), score0);
        slices[1] = new Slice(score.get(25), score25);
        slices[2] = new Slice(score.get(50), score50);
        slices[3] = new Slice(score.get(75), score75);
        slices[4] = new Slice(score.get(100), score100);
        repaint();
    }

    private final void drawPie(Graphics2D g, Rectangle area, Slice[] slices) {

        int curValue = 25;
        int startAngle = 0;
        int arcAngle;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        g.drawOval(area.x + padding, area.y + padding, area.width - (padding * 2), area.height - (padding * 2));

        for (byte i = 0; i < nbSlice; i++) {
            startAngle = curValue * 360 / sumValues;
            arcAngle = slices[i].getValue() * 360 / sumValues + 1; // +1 pour eviter d'avoir un blanc a cause des arrondis (calcul avec int)

            g.setColor(slices[i].getColor());
            g.fillArc(area.x + padding + 1, area.y + padding + 1, area.width - (padding * 2) - 1, area.height - (padding * 2) - 1, startAngle,
                    arcAngle);
            curValue += slices[i].getValue();
        }
    }

    private final class Slice {

        private final int value;
        private final Color color;

        public Slice(int value, Color color) {
            this.value = value;
            this.color = color;
        }

        public int getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }
}
