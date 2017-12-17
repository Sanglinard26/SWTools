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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

public final class PanelXYPlot extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final int padding = 25; // Espace par rapport au bord de la fenetre
    private static final int labelPadding = 25; // Espace pour les etiquettes
    private static final Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private static final int pointWidth = 4;
    private int numberYDivisions;
    private int numberXDivisions;

    private List<SerieScale> listSeries;

    private SerieScale serieScale;

    private final String title;
    private final String xAxisLabel;
    private final String yAxisLabel;

    private SeriesCollection seriesCollection;

    private Rectangle clip;

    public PanelXYPlot(String title, String xAxisLabel, String yAxisLabel, SeriesCollection seriesCollection, Boolean autoRangeAxe) {

        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.seriesCollection = seriesCollection;

        ToolTipManager.sharedInstance().setInitialDelay(0);

        setPreferredSize(new Dimension(500, 500));

        if (autoRangeAxe) {
            this.numberXDivisions = 10;
            this.numberYDivisions = 10;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (getMaxXValue() - getMinXValue());
        double yScale;

        if (getMaxYValue() - getMinYValue() != 0) {
            yScale = ((double) getHeight() - (2 * padding) - labelPadding) / (getMaxYValue() - getMinYValue());
        } else {
            yScale = (double) getHeight() - (2 * padding) - labelPadding;
        }

        final int nbSerie = seriesCollection.getSeriesCount();
        listSeries = new ArrayList<SerieScale>(nbSerie);

        int x0, y0, x1, y1, x2, y2; // Point de coordonnees

        Serie serie;

        for (short nSerie = 0; nSerie < nbSerie; nSerie++) {

            if (!Double.isNaN(yScale)) {

                serie = seriesCollection.getSerie(nSerie);

                serieScale = new SerieScale(serie.getName());

                for (int nPoint = 0; nPoint < serie.getPointsCount(); nPoint++) {

                    x1 = (int) ((serie.getPoints().get(nPoint).getX() - getMinXValue()) * xScale + padding + labelPadding);

                    y1 = (int) ((getMaxYValue() - serie.getPoints().get(nPoint).getY()) * yScale + padding);

                    serieScale.add(new Point(x1, y1));
                }
                listSeries.add(serieScale);
            }

        }

        // draw title
        if (title != null) {
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(title);
            g2.setColor(Color.WHITE);
            g2.fillRect(getWidth() / 2 - ((labelWidth + 10) / 2), 2, labelWidth + 10, 20);
            g2.setColor(Color.BLACK);
            g2.drawRect(getWidth() / 2 - ((labelWidth + 10) / 2), 2, labelWidth + 10, 20);
            g2.drawString(title, getWidth() / 2 - ((labelWidth) / 2), metrics.getHeight() + 2);
        }

        // draw xAxislabel
        if (xAxisLabel != null) {
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(xAxisLabel);
            g2.setColor(Color.WHITE);
            g2.fillRect(getWidth() / 2 - ((labelWidth + 10) / 2), getHeight() - 20 - 5, labelWidth + 10, 20);
            g2.setColor(Color.BLACK);
            g2.drawRect(getWidth() / 2 - ((labelWidth + 10) / 2), getHeight() - 20 - 5, labelWidth + 10, 20);
            g2.drawString(xAxisLabel, getWidth() / 2 - ((labelWidth) / 2), getHeight() - metrics.getHeight() + 2);
        }

        // draw yAxislabel
        if (yAxisLabel != null) {
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(yAxisLabel);

            g2.setColor(Color.WHITE);
            g2.fillRect(2, getHeight() / 2 - ((labelWidth + 10) / 2), 20, labelWidth + 10);
            g2.setColor(Color.BLACK);
            g2.drawRect(2, getHeight() / 2 - ((labelWidth + 10) / 2), 20, labelWidth + 10);

            AffineTransform orig = g2.getTransform();
            g2.rotate(-Math.PI / 2);
            // g2.drawString(yAxisLabel, -(metrics.getHeight() + 2), (getHeight() / 2 - ((labelWidth) / 2)));
            g2.drawString(yAxisLabel, -(getHeight() / 2) - (labelWidth / 2), metrics.getHeight() + 2);
            g2.setTransform(orig);
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        String xLabel, yLabel;
        FontMetrics metrics;
        int labelWidth;

        // create hatch marks and grid lines for y axis.
        for (byte i = 0; i < numberYDivisions + 1; i++) {
            x0 = padding + labelPadding;
            x1 = pointWidth + padding + labelPadding;
            y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            y1 = y0;
            if (getNbXPoints() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1); // Dessin de la grille
                g2.setColor(Color.BLACK);
                yLabel = ((int) ((getMinYValue() + (getMaxYValue() - getMinYValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                metrics = g2.getFontMetrics();
                labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // create hatch marks and grid lines for x axis.
        for (byte i = 0; i < numberXDivisions + 1; i++) {
            x0 = i * (getWidth() - padding * 2 - labelPadding) / numberXDivisions + padding + labelPadding;
            x1 = x0;
            y0 = getHeight() - padding - labelPadding;
            y1 = y0 - pointWidth;
            if (getNbXPoints() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding); // Dessin de la grille
                g2.setColor(Color.BLACK);
                xLabel = ((int) ((getMinXValue() + (getMaxXValue() - getMinXValue()) * ((i * 1.0) / numberXDivisions)) * 100)) / 100.0 + "";
                metrics = g2.getFontMetrics();
                labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        float hue = 0;
        int xMark, yMark;
        int ovalW, ovalH;

        for (short nSerie = 0; nSerie < nbSerie; nSerie++) {

            hue = (float) (nSerie) / (float) (listSeries.size());

            g2.setColor(Color.getHSBColor(hue, 1, 1));

            listSeries.get(nSerie).setSerieColor(nSerie, Color.getHSBColor(hue, 1, 1));

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
                xMark = listSeries.get(nSerie).get(i).x - pointWidth / 2;
                yMark = listSeries.get(nSerie).get(i).y - pointWidth / 2;
                ovalW = pointWidth;
                ovalH = pointWidth;
                g2.fillOval(xMark, yMark, ovalW, ovalH);
            }
        }

        g2.dispose();

    }

    public void markPoint(Graphics g, Point p) {
        Graphics2D g2 = (Graphics2D) g;

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

    public int getNbXPoints() {
        int nbMaxXpoints = 0;
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            nbMaxXpoints = Math.max(nbMaxXpoints, seriesCollection.getSerie(i).getPointsCount());
        }
        return nbMaxXpoints;
    }

    public double getMaxYValue() {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            maxValue = Math.max(maxValue, seriesCollection.getSerie(i).getMaxYValue());
        }
        return maxValue;
    }

    public double getMaxXValue() {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            maxValue = Math.max(maxValue, seriesCollection.getSerie(i).getMaxXValue());
        }
        return maxValue;
    }

    public double getMinYValue() {
        double minValue = Double.POSITIVE_INFINITY;
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            minValue = Math.min(minValue, seriesCollection.getSerie(i).getMinYValue());
        }
        return minValue;
    }

    public double getMinXValue() {
        double minValue = Double.POSITIVE_INFINITY;
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            minValue = Math.min(minValue, seriesCollection.getSerie(i).getMinXValue());
        }
        return minValue;
    }

    public SeriesCollection getSeriesCollection() {
        return seriesCollection;
    }

    public void setSeriesCollection(SeriesCollection seriesCollection) {
        this.seriesCollection = seriesCollection;
        invalidate();
        this.repaint();
    }

    private final class SerieScale extends ArrayList<Point> {

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        private String serieName;
        @SuppressWarnings("unused")
        private Color serieColor;

        public SerieScale(String serieName) {
            this.serieName = serieName;
        }

        public void setSerieColor(int index, Color serieColor) {
            this.serieColor = serieColor;
            seriesCollection.getSerie(index).setColor(serieColor);
        }
    }

    @Override
    public boolean contains(int x, int y) {

        for (short idxSerie = 0; idxSerie < listSeries.size(); idxSerie++) {
            for (short idxPoint = 0; idxPoint < listSeries.get(idxSerie).size(); idxPoint++) {
                if (Math.abs(x - listSeries.get(idxSerie).get(idxPoint).getX()) < 4
                        & Math.abs(y - listSeries.get(idxSerie).get(idxPoint).getY()) < 4) {
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
