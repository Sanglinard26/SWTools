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

import javax.swing.JPanel;

public final class PanelXYPlot extends JPanel {

    private static final long serialVersionUID = 1L;

    private int padding = 25; // Espace par rapport au bord de la fenetre
    private int labelPadding = 25; // Espace pour les etiquettes
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
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

        setPreferredSize(new Dimension(500, 500));

        if (autoRangeAxe) {
            this.numberXDivisions = 10;
            this.numberYDivisions = 10;
        }

        // this.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseClicked(MouseEvent e) {
        // for (int i = 0; i < graphPoints.size(); i++) {
        // if (Math.abs(graphPoints.get(i).x - e.getX()) < 4 & Math.abs(graphPoints.get(i).y - e.getY()) < 4) {
        // System.out.println(scores.get(i));
        //
        // markPoint(getGraphics(), graphPoints.get(i));
        // clip = getGraphics().getClipBounds(new Rectangle(graphPoints.get(i).x - 10 / 2, graphPoints.get(i).y - 10 / 2, 10, 10));
        // }
        // }
        // }
        // });
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

        listSeries = new ArrayList<>();

        for (int nSerie = 0; nSerie < seriesCollection.getSeriesCount(); nSerie++) {

            serieScale = new SerieScale(seriesCollection.getSerie(nSerie).toString());

            for (int nPoint = 0; nPoint < seriesCollection.getSerie(nSerie).getPointsCount(); nPoint++) {

                int x1 = (int) ((seriesCollection.getSerie(nSerie).getPoints().get(nPoint).getX() - getMinXValue()) * xScale + padding
                        + labelPadding);
                int y1 = (int) ((getMaxYValue() - seriesCollection.getSerie(nSerie).getPoints().get(nPoint).getY()) * yScale + padding);

                serieScale.add(new Point(x1, y1));
            }

            listSeries.add(serieScale);
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

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (getNbXPoints() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1); // Dessin de la grille
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinYValue() + (getMaxYValue() - getMinYValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // create hatch marks and grid lines for x axis.
        for (int i = 0; i < numberXDivisions + 1; i++) {
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / numberXDivisions + padding + labelPadding;
            int x1 = x0;
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            if (getNbXPoints() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding); // Dessin de la grille
                g2.setColor(Color.BLACK);
                String xLabel = ((int) ((getMinXValue() + (getMaxXValue() - getMinXValue()) * ((i * 1.0) / numberXDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        float hue = 0;

        for (int nSerie = 0; nSerie < listSeries.size(); nSerie++) {

            hue = (float) (nSerie) / (float) (listSeries.size());

            g2.setColor(Color.getHSBColor(hue, 1, 1));

            listSeries.get(nSerie).setSerieColor(nSerie, Color.getHSBColor(hue, 1, 1));

            g2.setStroke(GRAPH_STROKE);
            for (int i = 0; i < listSeries.get(nSerie).size() - 1; i++) {
                int x1 = listSeries.get(nSerie).get(i).x;
                int y1 = listSeries.get(nSerie).get(i).y;
                int x2 = listSeries.get(nSerie).get(i + 1).x;
                int y2 = listSeries.get(nSerie).get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setColor(Color.BLACK);
            for (int i = 0; i < listSeries.get(nSerie).size(); i++) {
                int x = listSeries.get(nSerie).get(i).x - pointWidth / 2;
                int y = listSeries.get(nSerie).get(i).y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
        }

        g2.dispose();

    }

    public void markPoint(Graphics g, Point p) {
        Graphics2D g2 = (Graphics2D) g;

        if (clip != null)
            repaint(clip);

        g2.setColor(Color.RED);
        int x = p.x - 10 / 2;
        int y = p.y - 10 / 2;
        int ovalW = 10;
        int ovalH = 10;
        g2.fillOval(x, y, ovalW, ovalH);
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

    public void selectSeries(SeriesCollection seriesCollection) {
        this.seriesCollection = seriesCollection;
        invalidate();
        this.repaint();
    }

    private final class SerieScale extends ArrayList<Point> {

        private static final long serialVersionUID = 1L;

        private String serieName;
        private Color serieColor;

        public SerieScale(String serieName) {
            this.serieName = serieName;
        }

        public void setSerieColor(int index, Color serieColor) {
            this.serieColor = serieColor;
            seriesCollection.getSerie(index).setColor(serieColor);
        }
    }

}
