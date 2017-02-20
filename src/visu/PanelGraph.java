package visu;

import java.awt.BorderLayout;

import javax.swing.JPanel;

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
	
	public PanelGraph() {
		
		this.setLayout(new BorderLayout());
	}
	
	public void createChart(final Variable variable) {
        Function3D function = new Function3D() {
            @Override
            public double getValue(double x, double z) {

                if (variable instanceof Map) {
                    try {
                        return Double.valueOf(((Map) variable).getValue((int) z, (int) x));
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
        xAxis.setRange(1, ((Map) variable).getDimX() - 1);
        ValueAxis3D zAxis = plot.getZAxis();
        zAxis.setRange(1, ((Map) variable).getDimY() - 1);
        SurfaceRenderer renderer = (SurfaceRenderer) plot.getRenderer();
        renderer.setColorScale(new RainbowScale(new Range(-1.0, 1.0)));
        renderer.setDrawFaceOutlines(true);
        chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER, Orientation.HORIZONTAL);
        
        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        this.add(new DisplayPanel3D(chartPanel));
    }

}
