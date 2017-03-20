package graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import paco.Curve;
import paco.Map;
import paco.Variable;
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

    private void createXYLine(Curve variable) {

        final String chartTitle = "Y = f(X)";
        final String xAxisLabel = "X [" + variable.getUnitX() + "]";
        final String yAxisLabel = "Y [" + variable.getUnitZ() + "]";

        final XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries series1 = new XYSeries(variable.getShortName());

        final String[][] variableValues = variable.getValues();

        for (int i = 0; i < variable.getDimX(); i++) {
            try {
                series1.add(Double.parseDouble(variableValues[0][i]), Double.parseDouble(variableValues[1][i]));
            } catch (NumberFormatException e) {
                series1.add(i, Double.parseDouble(variableValues[1][i]));
            }

        }

        dataset.addSeries(series1);

        final JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        chart.getXYPlot().setRenderer(new XYLineAndShapeRenderer(true, true));
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        chart.getXYPlot().setDomainGridlinePaint(Color.BLACK);
        chart.removeLegend();

        this.add(new ChartPanel(chart));
    }

    private void createIsoX(Map variable) {
        final JPanel panel = new JPanel(new BorderLayout());
        final JList<String> listSeries = new JList<String>();
        listSeries.setPreferredSize(new Dimension(50, 100));
        listSeries.setBackground(Color.LIGHT_GRAY);

        final String chartTitle = "Iso X";
        final String xAxisLabel = "Y [" + variable.getUnitY() + "]";
        final String yAxisLabel = "Z [" + variable.getUnitZ() + "]";

        final XYSeriesCollection dataset = new XYSeriesCollection();
        final JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);
        // chart.getXYPlot().getRenderer().setSeriesVisible(0, false); //A voir si utile plutot que de supprimer toutes les series � chaque selection
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        chart.getXYPlot().setRenderer(new XYLineAndShapeRenderer(true, true));
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        chart.getXYPlot().setDomainGridlinePaint(Color.BLACK);
        final XYSeries[] series = new XYSeries[variable.getDimX() - 1]; // -1 pour ne pas tenir compte de la case "X\Y"
        final String[] seriesName = new String[variable.getDimX() - 1];
        final int[] indexSerie = new int[variable.getDimX() - 1];

        for (int x = 0; x < variable.getDimX() - 1; x++) {
            series[x] = new XYSeries(variable.getxValues()[x]);
            seriesName[x] = variable.getxValues()[x];
            indexSerie[x] = x;

            for (int y = 0; y < variable.getDimY() - 1; y++) {
                try {
                    series[x].add(Double.parseDouble(variable.getyValues()[y]), Double.parseDouble(variable.getzValue(y, x)));
                } catch (NumberFormatException e) {
                    if (Utilitaire.isNumber(variable.getzValue(y, x))) {
                        series[x].add(y, Double.parseDouble(variable.getzValue(y, x)));
                    } else {
                        series[x].add(y, Double.NaN);
                    }

                }

            }
            dataset.addSeries(series[x]);
        }

        listSeries.setListData(seriesName);
        listSeries.setSelectedIndices(indexSerie);

        listSeries.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() & listSeries.getModel().getSize() > 0) {
                    dataset.removeAllSeries();
                    for (int indice : listSeries.getSelectedIndices()) {
                        dataset.addSeries(series[indice]);
                    }
                }

            }
        });

        panel.add(new JScrollPane(listSeries), BorderLayout.WEST);
        panel.add(new ChartPanel(chart), BorderLayout.CENTER);
        this.add(panel);
    }

    private void createIsoY(Map variable) {
        final JPanel panel = new JPanel(new BorderLayout());
        final JList<String> listSeries = new JList<String>();
        listSeries.setPreferredSize(new Dimension(50, 100));
        listSeries.setBackground(Color.LIGHT_GRAY);

        final String chartTitle = "Iso Y";
        final String xAxisLabel = "X [" + variable.getUnitX() + "]";
        final String yAxisLabel = "Z [" + variable.getUnitZ() + "]";

        final XYSeriesCollection dataset = new XYSeriesCollection();
        final JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        chart.getXYPlot().setRenderer(new XYLineAndShapeRenderer(true, true));
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setRangeGridlinePaint(Color.BLACK);
        chart.getXYPlot().setDomainGridlinePaint(Color.BLACK);
        final XYSeries[] series = new XYSeries[variable.getDimY() - 1];
        final String[] seriesName = new String[variable.getDimY() - 1];
        final int[] indexSerie = new int[variable.getDimY() - 1];

        for (int y = 0; y < variable.getDimY() - 1; y++) {
            indexSerie[y] = y;
            series[y] = new XYSeries(variable.getyValues()[y]);
            seriesName[y] = variable.getyValues()[y];

            for (int x = 0; x < variable.getDimX() - 1; x++) {
                try {
                    series[y].add(Double.parseDouble(variable.getxValues()[x]), Double.parseDouble(variable.getzValue(y, x)));
                } catch (NumberFormatException e) {
                    if (Utilitaire.isNumber(variable.getzValue(y, x))) {
                        series[y].add(x, Double.parseDouble(variable.getzValue(y, x)));
                    } else {
                        series[y].add(x, Double.NaN);
                    }

                }

            }
            dataset.addSeries(series[y]);
        }
        listSeries.setListData(seriesName);
        listSeries.setSelectedIndices(indexSerie);

        listSeries.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() & listSeries.getModel().getSize() > 0) {
                    dataset.removeAllSeries();
                    for (int indice : listSeries.getSelectedIndices()) {
                        dataset.addSeries(series[indice]);
                    }
                }

            }
        });

        panel.add(new JScrollPane(listSeries), BorderLayout.EAST);
        panel.add(new ChartPanel(chart), BorderLayout.CENTER);
        this.add(panel);
    }

}
