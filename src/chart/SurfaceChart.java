package chart;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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

import cdf.Map;
import cdf.Variable;

public final class SurfaceChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final JLabel labelNoGraph = new JLabel("Graphique non disponible");

    public SurfaceChart(final Variable variable) {

        if (variable instanceof Map) {
        	
        	final Map map = (Map) variable;

            this.setLayout(new BorderLayout());

            final Function3D function = new Function3D() {

                private static final long serialVersionUID = 1L;

                @Override
                public double getValue(double x, double y) {
                    if (x != 0 & y != 0) {
                        try {
                            return Double.valueOf(map.getValue((int) y, (int) x));
                        } catch (NumberFormatException nbfEx) {
                            return Double.NaN;
                        }
                    }
                    return 0;
                }
            };

            // Erreur sur les axes (nom)
            final Chart3D chart = Chart3DFactory.createSurfaceChart("", "", function, "X [" + map.getUnitX() + "]",
                    "Z [" + map.getUnitZ() + "]", "Y [" + map.getUnitY() + "]");
            final XYZPlot plot = (XYZPlot) chart.getPlot();
            plot.setDimensions(new Dimension3D(20, 10, 20));

            final ValueAxis3D xAxis = plot.getXAxis();
            xAxis.setRange(1, map.getValues()[0].length - 1);

            final ValueAxis3D zAxis = plot.getZAxis();
            zAxis.setRange(1, map.getValues().length - 1);
            final SurfaceRenderer renderer = (SurfaceRenderer) plot.getRenderer();
            if (map.getMaxZValue() - map.getMinZValue() != 0)
                renderer.setColorScale(new RainbowScale(new Range(map.getMinZValue(), map.getMaxZValue())));
            renderer.setDrawFaceOutlines(false);
            renderer.setXSamples(map.getDimX() - 1);
            renderer.setZSamples(map.getDimY() - 1);
            chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER, Orientation.HORIZONTAL);

            final Chart3DPanel chartPanel = new Chart3DPanel(chart);

            this.add(new DisplayPanel3D(chartPanel));
        } else {
            this.setLayout(new BorderLayout());
            labelNoGraph.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(labelNoGraph, BorderLayout.CENTER);
        }

    }

}
