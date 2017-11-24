package graph;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cdf.Curve;
import cdf.Map;
import cdf.Variable;
import chart.PanelXYChart;
import chart.PanelXYPlot;
import chart.Serie;
import chart.SeriesCollection;
import tools.Utilitaire;

public final class XYChart extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final JLabel labelNoGraph = new JLabel("Graphique non disponible");

    public XYChart(Variable variable) {

        if (variable instanceof Curve) {
            this.setLayout(new BorderLayout());
            createXYLine((Curve) variable);

        } else if (variable instanceof Map) {
            this.setLayout(new GridLayout(1, 2));
            createIsoX((Map) variable);
            createIsoY((Map) variable);
        } else {
            this.setLayout(new BorderLayout());
            labelNoGraph.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(labelNoGraph, BorderLayout.CENTER);
        }

    }

    private void createXYLine(Curve curve) {

        SeriesCollection seriesCollection = new SeriesCollection();
        Serie serie;

        serie = new Serie("Serie");
        for (short x = 0; x < curve.getDimX(); x++) {
            try {
                serie.addPoint(Double.parseDouble(curve.getValue(0, x)), Double.parseDouble(curve.getValue(1, x)));
            } catch (NumberFormatException nfe) {
                if (Utilitaire.isNumber(curve.getValue(1, x))) {
                    serie.addPoint(x, Double.parseDouble(curve.getValue(1, x)));
                } else {
                    serie.addPoint(x, Float.NaN);
                }

            }
        }

        seriesCollection.addSerie(serie);

        this.add(new PanelXYChart(new PanelXYPlot("Y = f(X)", "X [" + curve.getUnitX() + "]", "Y [" + curve.getUnitZ() + "]", seriesCollection, true),
                PanelXYChart.RIGHT_POSITION, false));
    }

    private void createIsoX(Map map) {

        SeriesCollection seriesCollection = new SeriesCollection();
        Serie serie;

        for (short x = 1; x < map.getDimX(); x++) {

            serie = new Serie(map.getValue(0, x)); // serie = new Serie(map.getxValues()[x]);

            for (short y = 1; y < map.getDimY(); y++) {

                try {
                    serie.addPoint(Double.parseDouble(map.getValue(y, 0)), Double.parseDouble(map.getValue(y, x))); // serie.addPoint(Double.parseDouble(map.getyValues()[y]),
                                                                                                                    // Double.parseDouble(map.getzValue(y,
                                                                                                                    // x)));
                } catch (NumberFormatException nfe) {
                    if (Utilitaire.isNumber(map.getValue(y, x))) {
                        serie.addPoint(y, Double.parseDouble(map.getValue(y, x)));
                    } else {
                        serie.addPoint(y, Float.NaN);
                    }
                }
            }
            seriesCollection.addSerie(serie);
        }

        this.add(new PanelXYChart(new PanelXYPlot("Z = f(Y)", "Y [" + map.getUnitY() + "]", "Z [" + map.getUnitZ() + "]", seriesCollection, true),
                PanelXYChart.RIGHT_POSITION, true));
    }

    private void createIsoY(Map map) {

        SeriesCollection seriesCollection = new SeriesCollection();
        Serie serie;

        for (short y = 1; y < map.getDimY(); y++) {

            serie = new Serie(map.getValue(y, 0)); // serie = new Serie(map.getyValues()[y]);

            for (short x = 1; x < map.getDimX(); x++) {
                try {
                    serie.addPoint(Double.parseDouble(map.getValue(0, x)), Double.parseDouble(map.getValue(y, x))); // serie.addPoint(Double.parseDouble(map.getxValues()[x]),
                                                                                                                    // Double.parseDouble(map.getzValue(y,
                                                                                                                    // x)));
                } catch (NumberFormatException nfe) {
                    if (Utilitaire.isNumber(map.getValue(y, x))) {
                        serie.addPoint(x, Double.parseDouble(map.getValue(y, x)));
                    } else {
                        serie.addPoint(x, Float.NaN);
                    }
                }
            }
            seriesCollection.addSerie(serie);
        }

        this.add(new PanelXYChart(new PanelXYPlot("Z = f(X)", "X [" + map.getUnitX() + "]", "Z [" + map.getUnitZ() + "]", seriesCollection, true),
                PanelXYChart.LEFT_POSITION, true));
    }

}
