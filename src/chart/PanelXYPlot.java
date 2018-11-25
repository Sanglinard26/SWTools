/*
 * Creation : 6 juin 2017
 */
package chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import utils.NumeralString;

public final class PanelXYPlot extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int PADDING = 30; // Espace par rapport au bord de la fenetre
    private static final int LABEL_PADDING = 40; // Espace pour les etiquettes
    private static final Color GRID_COLOR = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private static final int POINT_WIDTH = 4;
    private int NB_X_DIV;
    private int NB_Y_DIV;

    private static final NumberFormat nbf = NumberFormat.getInstance();

    private final String title;
    private final String xAxisLabel;
    private final String yAxisLabel;
    private final double[] rangeXY;
    private final HashMap<String, Color> mapColor;

    private final double minXvalue;
    private final double maxXvalue;
    private double minYvalue;
    private double maxYvalue;
    private final double diffXvalue;
    private double diffYvalue;

    private List<SerieScale> listSeries;
    private SeriesCollection seriesCollection;

    private Rectangle clip;

    static {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        nbf.setMaximumFractionDigits(3);
    }

    public PanelXYPlot(String title, String xAxisLabel, String yAxisLabel, SeriesCollection seriesCollection) {

        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.seriesCollection = seriesCollection;

        this.rangeXY = new double[4];

        fillRangeXY();

        minXvalue = this.rangeXY[0];
        maxXvalue = this.rangeXY[1];
        minYvalue = this.rangeXY[2];
        maxYvalue = this.rangeXY[3];
        diffXvalue = maxXvalue - minXvalue;
        diffYvalue = maxYvalue - minYvalue;

        final float yAdjust = 0.1f;
        double it;
        int nbItX = 0;

        if (getNbXPoints() - 1 > 20) {
            nbItX = 20;
        } else {
            nbItX = getNbXPoints() - 1;
        }

        for (int i = nbItX; i > 0; i--) {
            it = diffXvalue % (diffXvalue / i);
            if (it == 0) {
                NB_X_DIV = i;
                break;
            }
        }

        if (diffYvalue > 0) {
            for (int i = 20; i > 0; i--) {
                it = diffYvalue % (diffYvalue / i);
                if (it == 0) {
                    NB_Y_DIV = i;
                    break;
                }
            }
        } else {
            NB_Y_DIV = 2;
            minYvalue -= yAdjust;
            maxYvalue += yAdjust;
            diffYvalue = maxYvalue - minYvalue;
        }

        int nbSerie = seriesCollection.getSeriesCount();

        this.mapColor = new HashMap<String, Color>(nbSerie);

        for (int i = 0; i < nbSerie; i++) {
            mapColor.put(seriesCollection.getSerie(i).getName(), Color.getHSBColor((float) (i) / (float) (nbSerie), 1, 1));
        }

        setPreferredSize(new Dimension(500, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;

        final int height = getHeight();
        final int width = getWidth();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final double xScale = ((double) width - (2 * PADDING) - LABEL_PADDING) / (diffXvalue);
        final double yScale;

        if (diffYvalue != 0) {
            yScale = ((double) height - (2 * PADDING) - LABEL_PADDING) / (diffYvalue);
        } else {
            yScale = (double) height - (2 * PADDING) - LABEL_PADDING;
        }

        final int nbSerie = seriesCollection.getSeriesCount();
        listSeries = new ArrayList<SerieScale>(nbSerie);

        int x0, y0, x1, y1, x2, y2; // Point de coordonnees

        Serie serie;
        SerieScale serieScale;

        for (short nSerie = 0; nSerie < nbSerie; nSerie++) {

            if (!Double.isNaN(yScale)) {

                serie = seriesCollection.getSerie(nSerie);

                serieScale = new SerieScale(serie.getName());

                for (int nPoint = 0; nPoint < serie.getPointsCount(); nPoint++) {

                    x1 = (int) ((serie.getPoints().get(nPoint).getX() - minXvalue) * xScale + PADDING + LABEL_PADDING);

                    y1 = (int) ((maxYvalue - serie.getPoints().get(nPoint).getY()) * yScale + PADDING);

                    serieScale.add(new Point(x1, y1));
                }
                listSeries.add(serieScale);
            }

        }

        String xLabel, yLabel;
        FontMetrics metrics;
        int labelWidth;

        // draw title
        if (title != null) {
            metrics = g2.getFontMetrics();
            labelWidth = metrics.stringWidth(title);
            g2.setColor(Color.WHITE);
            g2.fillRect(width / 2 - ((labelWidth + 10) / 2), 2, labelWidth + 10, 20);
            g2.setColor(Color.BLACK);
            g2.drawRect(width / 2 - ((labelWidth + 10) / 2), 2, labelWidth + 10, 20);
            g2.drawString(title, width / 2 - ((labelWidth) / 2), metrics.getHeight() + 2);
        }

        // draw xAxislabel
        if (xAxisLabel != null) {
            metrics = g2.getFontMetrics();
            labelWidth = metrics.stringWidth(xAxisLabel);
            g2.setColor(Color.WHITE);
            g2.fillRect(width / 2 - ((labelWidth + 10) / 2), height - 20 - 5, labelWidth + 10, 20);
            g2.setColor(Color.BLACK);
            g2.drawRect(width / 2 - ((labelWidth + 10) / 2), height - 20 - 5, labelWidth + 10, 20);
            g2.drawString(xAxisLabel, width / 2 - ((labelWidth) / 2), height - metrics.getHeight() + 2);
        }

        // draw yAxislabel
        final AffineTransform orig = g2.getTransform();
        if (yAxisLabel != null) {
            metrics = g2.getFontMetrics();
            labelWidth = metrics.stringWidth(yAxisLabel);

            g2.setColor(Color.WHITE);
            g2.fillRect(2, height / 2 - ((labelWidth + 10) / 2), 20, labelWidth + 10);
            g2.setColor(Color.BLACK);
            g2.drawRect(2, height / 2 - ((labelWidth + 10) / 2), 20, labelWidth + 10);

            g2.rotate(-Math.PI / 2);
            g2.drawString(yAxisLabel, -(height / 2) - (labelWidth / 2), metrics.getHeight() + 2);
            g2.setTransform(orig);
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(PADDING + LABEL_PADDING, PADDING, width - (2 * PADDING) - LABEL_PADDING, height - 2 * PADDING - LABEL_PADDING);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (byte i = 0; i < NB_Y_DIV + 1; i++) {
            x0 = PADDING + LABEL_PADDING;
            x1 = POINT_WIDTH + PADDING + LABEL_PADDING;
            y0 = height - ((i * (height - PADDING * 2 - LABEL_PADDING)) / NB_Y_DIV + PADDING + LABEL_PADDING);
            y1 = y0;
            if (getNbXPoints() > 0) {
                g2.setColor(GRID_COLOR);
                g2.drawLine(PADDING + LABEL_PADDING + 1 + POINT_WIDTH, y0, width - PADDING, y1); // Dessin de la grille
                g2.setColor(Color.BLACK);
                yLabel = NumeralString.formatStringNumber((minYvalue + (diffYvalue) * ((i * 1.0d) / NB_Y_DIV)));
                metrics = g2.getFontMetrics();
                labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // create hatch marks and grid lines for x axis.

        for (byte i = 0; i < NB_X_DIV + 1; i++) {
            x0 = i * (width - PADDING * 2 - LABEL_PADDING) / NB_X_DIV + PADDING + LABEL_PADDING;
            x1 = x0;
            y0 = height - PADDING - LABEL_PADDING;
            y1 = y0 - POINT_WIDTH;
            if (getNbXPoints() > 0) {
                g2.setColor(GRID_COLOR);
                g2.drawLine(x0, height - PADDING - LABEL_PADDING - 1 - POINT_WIDTH, x1, PADDING); // Dessin de la grille
                g2.setColor(Color.BLACK);
                xLabel = NumeralString.formatStringNumber((minXvalue + (diffXvalue) * ((i * 1.0) / NB_X_DIV)));
                metrics = g2.getFontMetrics();
                labelWidth = metrics.stringWidth(xLabel);
                g2.rotate(-Math.PI / 2);
                g2.drawString(xLabel, -height + (PADDING + LABEL_PADDING) - labelWidth - 5, x0 + metrics.getHeight() / 2);
                g2.setTransform(orig);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // create x and y axes
        g2.drawLine(PADDING + LABEL_PADDING, height - PADDING - LABEL_PADDING, PADDING + LABEL_PADDING, PADDING);
        g2.drawLine(PADDING + LABEL_PADDING, height - PADDING - LABEL_PADDING, width - PADDING, height - PADDING - LABEL_PADDING);

        int xMark, yMark;
        int ovalW, ovalH;

        if (!listSeries.isEmpty()) {
            for (short nSerie = 0; nSerie < nbSerie; nSerie++) {

                g2.setColor(mapColor.get(listSeries.get(nSerie).getSerieName()));

                g2.setStroke(GRAPH_STROKE);
                for (short i = 0; i < listSeries.get(nSerie).size() - 1; i++) {
                    x1 = listSeries.get(nSerie).get(i).x;
                    y1 = listSeries.get(nSerie).get(i).y;
                    x2 = listSeries.get(nSerie).get(i + 1).x;
                    y2 = listSeries.get(nSerie).get(i + 1).y;
                    g2.drawLine(x1, y1, x2, y2);
                }

                g2.setColor(Color.BLACK);
                for (short i = 0; i < listSeries.get(nSerie).size(); i++) {
                    xMark = listSeries.get(nSerie).get(i).x - POINT_WIDTH / 2;
                    yMark = listSeries.get(nSerie).get(i).y - POINT_WIDTH / 2;
                    ovalW = POINT_WIDTH;
                    ovalH = POINT_WIDTH;
                    g2.fillOval(xMark, yMark, ovalW, ovalH);
                }
            }
        }

        g2.dispose();

    }

    public final void markPoint(Graphics g, Point p) {

        final Graphics2D g2 = (Graphics2D) g;

        if (clip != null) {
            if ((p.x - 10 / 2) != clip.x | (p.y - 10 / 2) != clip.y)
                repaint(clip);
        }

        g2.setColor(Color.RED);
        final int x = p.x - 10 / 2;
        final int y = p.y - 10 / 2;
        final int ovalW = 10;
        final int ovalH = 10;
        g2.fillOval(x, y, ovalW, ovalH);

        clip = new Rectangle(x, y, 10, 10);

        g2.dispose();
    }

    public final int getNbXPoints() {

        final int nbSerie = seriesCollection.getSeriesCount();
        int nbMaxXpoints = 0;

        for (int i = 0; i < nbSerie; i++) {
            nbMaxXpoints = Math.max(nbMaxXpoints, seriesCollection.getSerie(i).getPointsCount());
        }
        return nbMaxXpoints;
    }

    private final void fillRangeXY() {
        double minXValue = Double.POSITIVE_INFINITY;
        double maxXValue = Double.NEGATIVE_INFINITY;
        double minYValue = Double.POSITIVE_INFINITY;
        double maxYValue = Double.NEGATIVE_INFINITY;

        double[] rangeSerie;

        final int nbSerie = seriesCollection.getSeriesCount();

        for (int i = 0; i < nbSerie; i++) {

            rangeSerie = seriesCollection.getSerie(i).getRangeXY();

            minXValue = Math.min(minXValue, rangeSerie[0]);
            maxXValue = Math.max(maxXValue, rangeSerie[1]);
            minYValue = Math.min(minYValue, rangeSerie[2]);
            maxYValue = Math.max(maxYValue, rangeSerie[3]);
        }

        this.rangeXY[0] = minXValue;
        this.rangeXY[1] = maxXValue;
        this.rangeXY[2] = minYValue;
        this.rangeXY[3] = maxYValue;
    }

    public final SeriesCollection getSeriesCollection() {
        return seriesCollection;
    }

    public final void setSeriesCollection(SeriesCollection seriesCollection) {
        this.seriesCollection = seriesCollection;
        invalidate();
        this.repaint();
    }

    private final class SerieScale extends ArrayList<Point> {

        private static final long serialVersionUID = 1L;

        private String serieName;

        public SerieScale(String serieName) {
            this.serieName = serieName;
        }

        public String getSerieName() {
            return serieName;
        }

    }

    @Override
    public boolean contains(int x, int y) {

        for (short idxSerie = 0; idxSerie < listSeries.size(); idxSerie++) {
            for (short idxPoint = 0; idxPoint < listSeries.get(idxSerie).size(); idxPoint++) {
                if (Math.abs(x - listSeries.get(idxSerie).get(idxPoint).getX()) < 4
                        && Math.abs(y - listSeries.get(idxSerie).get(idxPoint).getY()) < 4) {
                    markPoint(this.getGraphics(), listSeries.get(idxSerie).get(idxPoint));
                    setToolTipText(seriesCollection.getSerie(idxSerie).getPoints().get(idxPoint).toString());
                    return super.contains(x, y); // ==> Permet d'afficher la ToolTip mais plus le menu contextuel
                    // return false; ==> Permet de garder le menu contextuel d'export mais n'affiche plus la ToolTip
                }
            }
        }
        setToolTipText(null);
        if (clip != null) {
            repaint(clip);
        }
        return false;
    }

}
