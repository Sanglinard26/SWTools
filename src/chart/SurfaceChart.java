package chart;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import cdf.Map;
import cdf.Variable;
import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.surface.JSurface;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceVertex;

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

            MapSurfaceModel arraySurfaceModel = new MapSurfaceModel();
            arraySurfaceModel.setValues(map.getValues().getXAxis(), map.getValues().getYAxis(), map.getValues().getZvalues());

            this.add(new JSurface(arraySurfaceModel));
        } else {
            this.add(labelNoGraph, BorderLayout.CENTER);
        }

    }

    public class MapSurfaceModel extends AbstractSurfaceModel {
        SurfaceVertex[][] surfaceVertex;

        public MapSurfaceModel() {
            setPlotFunction2(false);
            setBoxed(true);
            setDisplayXY(true);
            setExpectDelay(false);
            setAutoScaleZ(true);
            setDisplayZ(true);
            setMesh(true);
            setPlotType(SurfaceModel.PlotType.SURFACE);
            setDisplayGrids(true);
            setPlotColor(SurfaceModel.PlotColor.SPECTRUM);
            setFirstFunctionOnly(true);
            setZMin(Float.MAX_VALUE);
            setZMax(Float.MIN_VALUE);
        }

        public void setValues(float[] xBreakPoint, float[] yBreakPoint, float[][] z1) {
            setDataAvailable(false);

            int xLength = xBreakPoint.length;
            int yLength = yBreakPoint.length;

            float xmin = xBreakPoint[0] - (xBreakPoint[1] - xBreakPoint[0]) / 2.0F;
            float xmax = xBreakPoint[(xLength - 1)] + (xBreakPoint[(xLength - 1)] - xBreakPoint[(xLength - 2)]) / 2.0F;
            float ymin = yBreakPoint[0] - (yBreakPoint[1] - yBreakPoint[0]) / 2.0F;
            float ymax = yBreakPoint[(yLength - 1)] + (yBreakPoint[(yLength - 1)] - yBreakPoint[(yLength - 2)]) / 2.0F;
            setXMin(xmin);
            setXMax(xmax);
            setYMin(ymin);
            setYMax(ymax);
            setCalcDivisions(Math.max(xLength - 1, yLength - 1));

            float xfactor = 20.0F / (xMax - xMin);
            float yfactor = 20.0F / (yMax - yMin);

            int total = (calcDivisions + 1) * (calcDivisions + 1);
            surfaceVertex = new SurfaceVertex[1][total];

            for (int i = 0; i <= xBreakPoint.length - 1; i++) {
                for (int j = 0; j <= yBreakPoint.length - 1; j++) {
                    int k = i * (calcDivisions + 1) + j;
                    float xv = xBreakPoint[i];
                    float yv = yBreakPoint[j];
                    float v1 = z1 != null ? z1[j][i] : Float.NaN;
                    if (Float.isInfinite(v1))
                        v1 = Float.NaN;
                    if (!Float.isNaN(v1)) {
                        if ((Float.isNaN(z1Max)) || (v1 > z1Max)) {
                            z1Max = v1;
                        } else if ((Float.isNaN(z1Min)) || (v1 < z1Min))
                            z1Min = v1;
                    }
                    surfaceVertex[0][k] = new SurfaceVertex((xv - xMin) * xfactor - 10.0F, (yv - yMin) * yfactor - 10.0F, v1);
                }
            }

            for (int s = 0; s < surfaceVertex[0].length; s++) {
                if (surfaceVertex[0][s] == null) {
                    surfaceVertex[0][s] = new SurfaceVertex(Float.NaN, Float.NaN, Float.NaN);
                }
            }

            autoScale();
            setDataAvailable(true);
            fireStateChanged();
        }

        @Override
        public SurfaceVertex[][] getSurfaceVertex() {
            return this.surfaceVertex;
        }
    }

}
