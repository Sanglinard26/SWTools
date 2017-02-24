package visu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.Range;
import com.orsoncharts.axis.ValueAxis3D;
import com.orsoncharts.data.function.Function3D;
import com.orsoncharts.graphics3d.Dimension3D;
import com.orsoncharts.graphics3d.swing.DisplayPanel3D;
import com.orsoncharts.legend.LegendAnchor;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.RainbowScale;
import com.orsoncharts.renderer.xyz.SurfaceRenderer;
import com.orsoncharts.util.Orientation;

import paco.Map;
import paco.Variable;

public class PanelGraph extends JPanel {

    private JPanel pan2D = new JPanel(new BorderLayout());
    private JPanel pan3D = new JPanel(new BorderLayout());
    private CardLayout cardLayout = new CardLayout();
    private JPanel panCard = new JPanel(cardLayout);
    private JToggleButton toggleButton;

    private String[] nameCard = new String[] { "2D", "3D" };
    private int numCard = 0;

    public PanelGraph() {

        panCard.add(pan2D, "2D");
        panCard.add(pan3D, "3D");

        this.setLayout(new BorderLayout());

        toggleButton = new JToggleButton(new AbstractAction("3D") {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (numCard == 0) {
                    cardLayout.show(panCard, nameCard[numCard + 1]);
                    toggleButton.setText(nameCard[numCard]);
                    numCard = 1;

                } else {
                    cardLayout.show(panCard, nameCard[numCard - 1]);
                    toggleButton.setText(nameCard[numCard]);
                    numCard = 0;

                }

            }
        });

        this.add(toggleButton, BorderLayout.NORTH);
        this.add(panCard, BorderLayout.CENTER);

    }

    public JPanel getPan3D() {
        return pan3D;
    }

    public JPanel getPan2D() {
        return pan2D;
    }

    public void createChart(final Variable variable) {
        Function3D function = new Function3D() {
            @Override
            public double getValue(double x, double z) {

                if (variable instanceof Map) {
                    try {
                        return Double.valueOf(((Map) variable).getzValue((int) z, (int) x));
                    } catch (NumberFormatException nbfEx) {
                        System.out.println(nbfEx);
                        return 0;
                    }

                }
                return 0;
            }
        };

        Chart3D chart = Chart3DFactory.createSurfaceChart(variable.getShortName(), variable.getSwFeatureRef(), function, "X", "Y", "Z");
        XYZPlot plot = (XYZPlot) chart.getPlot();
        plot.setDimensions(new Dimension3D(20, 10, 20));
        ValueAxis3D xAxis = plot.getXAxis();
        xAxis.setRange(0, ((Map) variable).getxValues().length - 1);
        ValueAxis3D zAxis = plot.getZAxis();
        zAxis.setRange(0, ((Map) variable).getyValues().length - 1);
        SurfaceRenderer renderer = (SurfaceRenderer) plot.getRenderer();
        renderer.setColorScale(new RainbowScale(new Range(((Map) variable).getMinZValue(), ((Map) variable).getMaxZValue())));
        renderer.setDrawFaceOutlines(false);
        renderer.setXSamples(((Map) variable).getxValues().length);
        renderer.setZSamples(((Map) variable).getyValues().length);
        chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER, Orientation.HORIZONTAL);

        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        pan3D.add(new DisplayPanel3D(chartPanel));
    }

    public void createXYChart(final Variable variable) {
        String chartTitle = "Objects Movement Chart";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries(variable.getShortName());

        series1.add(1.0, 2.0);
        series1.add(2.0, 3.0);
        series1.add(3.0, 2.5);
        series1.add(3.5, 2.8);
        series1.add(4.2, 6.0);

        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

        pan2D.add(new ChartPanel(chart));
    }

}
