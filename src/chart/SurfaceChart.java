package chart;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

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
import net.ericaro.surfaceplotter.DefaultSurfaceModel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import utils.NumeralString;

public final class SurfaceChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final JLabel labelNoGraph = new JLabel("Graphique non disponible");

    static {
        labelNoGraph.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public SurfaceChart(final Variable variable) {

        setBorder(new LineBorder(Color.BLACK));

        this.setLayout(new BorderLayout());

        if (variable instanceof Map) {

            final Map map = (Map) variable;

            final Function3D function = new Function3D() {

                private static final long serialVersionUID = 1L;
                private String value;

                @Override
                public double getValue(double x, double y) {
                    if (x * y > 0) {

                        value = map.getValue((int) y, (int) x);

                        return NumeralString.isNumber(value) ? Double.parseDouble(value) : Double.NaN;
                    }
                    return 0;
                }
            };

            // Erreur sur les axes (nom)
            final Chart3D chart = Chart3DFactory.createSurfaceChart("", "", function, ("X [" + map.getUnitX() + "]").intern(),
                    ("Z [" + map.getUnitZ() + "]").intern(), ("Y [" + map.getUnitY() + "]").intern()); // Test String.intern()
            final XYZPlot plot = (XYZPlot) chart.getPlot();
            plot.setDimensions(new Dimension3D(20, 10, 20));

            final ValueAxis3D xAxis = plot.getXAxis();
            xAxis.setRange(1, map.getValues().getDimX() - 1);
            final ValueAxis3D zAxis = plot.getZAxis();
            zAxis.setRange(1, map.getValues().getDimY() - 1);

            final SurfaceRenderer renderer = (SurfaceRenderer) plot.getRenderer();
            if (map.getMaxZValue() - map.getMinZValue() != 0)
                renderer.setColorScale(new RainbowScale(new Range(map.getMinZValue(), map.getMaxZValue())));
            renderer.setDrawFaceOutlines(false);
            renderer.setXSamples(map.getValues().getDimX() - 1);
            renderer.setZSamples(map.getValues().getDimY() - 1);
            chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER, Orientation.HORIZONTAL);

            final Chart3DPanel chartPanel = new Chart3DPanel(chart);

            this.add(new DisplayPanel3D(chartPanel));

            // this.add(new JSurface(createSurfaceModel(map)));
        } else {
            this.add(labelNoGraph, BorderLayout.CENTER);
        }

    }

    @SuppressWarnings("unused")
    private static SurfaceModel createSurfaceModel(final Map map) {
        DefaultSurfaceModel sm = new DefaultSurfaceModel();

        sm.setPlotFunction2(false);
        sm.setCalcDivisions(Math.max(map.getValues().getDimX() - 1, map.getValues().getDimY() - 1));
        sm.setDispDivisions(sm.getCalcDivisions());

        sm.setBoxed(true);
        sm.setDisplayXY(true);
        sm.setExpectDelay(false);
        sm.setAutoScaleZ(true);
        sm.setDisplayZ(true);
        sm.setMesh(true);
        sm.setPlotType(SurfaceModel.PlotType.SURFACE);
        sm.setDisplayGrids(true);
        sm.setFirstFunctionOnly(true);

        sm.setXMin(1);
        sm.setXMax(map.getValues().getDimX() - 1);
        sm.setYMin(1);
        sm.setYMax(map.getValues().getDimY() - 1);

        sm.setPlotColor(SurfaceModel.PlotColor.SPECTRUM);

        sm.setMapper(new Mapper() {

            private String value;

            @Override
            public float f2(float paramFloat1, float paramFloat2) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public float f1(float x, float y) {
                if (x * y > 0) {

                    value = map.getValue((int) y, (int) x);

                    return (float) (NumeralString.isNumber(value) ? Double.parseDouble(value) : Double.NaN);
                }
                return 0;
            }
        });
        sm.plot().execute();

        return sm;
    }

}
