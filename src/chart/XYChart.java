package chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import cdf.Curve;
import cdf.Map;
import cdf.Variable;
import utils.NumeralString;

public final class XYChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final JLabel labelNoGraph = new JLabel("Graphique non disponible");

    static {
        labelNoGraph.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public XYChart(Variable variable) {

        setBorder(new LineBorder(Color.BLACK));

        if (variable instanceof Curve) {
            this.setLayout(new BorderLayout());
            createXYLine((Curve) variable);

        } else if (variable instanceof Map) {
            this.setLayout(new GridLayout(1, 2));
            createIsoX((Map) variable);
            createIsoY((Map) variable);
        } else {
            this.setLayout(new BorderLayout());
            this.add(labelNoGraph, BorderLayout.CENTER);
        }

    }

    private final void createXYLine(Curve curve) {

        final String TITRE = "Y = f(X)";
        final SeriesCollection seriesCollection = new SeriesCollection();
        final Serie serie = new Serie("Serie");

        String xValue;
        String zValue;

        for (short x = 0; x < curve.getValues().getDimX(); x++) {

            xValue = curve.getValue(0, x);
            zValue = curve.getValue(1, x);

            if (NumeralString.isNumber(xValue) && NumeralString.isNumber(zValue)) {
                serie.addPoint(Double.parseDouble(xValue), Double.parseDouble(zValue));
            } else {
                if (NumeralString.isNumber(zValue)) {
                    serie.addPoint(x, Double.parseDouble(zValue));
                } else {
                    serie.addPoint(x, Float.NaN);
                }
            }
        }

        seriesCollection.addSerie(serie);

        this.add(new PanelXYChart(
                new PanelXYPlot(TITRE, ("X [" + curve.getUnitX() + "]").intern(), ("Y [" + curve.getUnitZ() + "]").intern(), seriesCollection),
                PanelXYChart.RIGHT_POSITION, false));
    }

    private final void createIsoX(Map map) {

        final String TITRE = "Z = f(Y)";
        final SeriesCollection seriesCollection = new SeriesCollection();
        Serie serie;

        String xValue;
        String zValue;

        for (short x = 1; x < map.getValues().getDimX(); x++) {

            serie = new Serie(map.getValue(0, x));

            for (short y = 1; y < map.getValues().getDimY(); y++) {

                xValue = map.getValue(y, 0);
                zValue = map.getValue(y, x);

                if (NumeralString.isNumber(xValue) && NumeralString.isNumber(zValue)) {
                    serie.addPoint(Double.parseDouble(xValue), Double.parseDouble(zValue));
                } else {
                    if (NumeralString.isNumber(zValue)) {
                        serie.addPoint(y, Double.parseDouble(zValue));
                    } else {
                        serie.addPoint(y, Float.NaN);
                    }
                }
            }
            seriesCollection.addSerie(serie);
        }

        this.add(new PanelXYChart(
                new PanelXYPlot(TITRE, ("Y [" + map.getUnitY() + "]").intern(), ("Z [" + map.getUnitZ()).intern() + "]", seriesCollection),
                PanelXYChart.RIGHT_POSITION, true));
    }

    private final void createIsoY(Map map) {

        final String TITRE = "Z = f(X)";
        final SeriesCollection seriesCollection = new SeriesCollection();
        Serie serie;

        String xValue;
        String zValue;

        for (short y = 1; y < map.getValues().getDimY(); y++) {

            serie = new Serie(map.getValue(y, 0));

            for (short x = 1; x < map.getValues().getDimX(); x++) {

                xValue = map.getValue(0, x);
                zValue = map.getValue(y, x);

                if (NumeralString.isNumber(xValue) && NumeralString.isNumber(zValue)) {
                    serie.addPoint(Double.parseDouble(xValue), Double.parseDouble(zValue));
                } else {
                    if (NumeralString.isNumber(zValue)) {
                        serie.addPoint(x, Double.parseDouble(zValue));
                    } else {
                        serie.addPoint(x, Float.NaN);
                    }
                }
            }
            seriesCollection.addSerie(serie);
        }

        this.add(new PanelXYChart(
                new PanelXYPlot(TITRE, ("X [" + map.getUnitX() + "]").intern(), ("Z [" + map.getUnitZ() + "]").intern(), seriesCollection),
                PanelXYChart.LEFT_POSITION, true));
    }

}
