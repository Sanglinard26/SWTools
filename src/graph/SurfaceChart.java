package graph;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
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

import paco.Map;
import paco.Variable;

public class SurfaceChart extends JPanel{
	
	private static final JLabel labelNoGraph = new JLabel("Graphique non disponible");

	public SurfaceChart(final Variable variable) {

		if (variable instanceof Map) {

			this.setLayout(new BorderLayout());

			Function3D function = new Function3D() {
				@Override
				public double getValue(double x, double z) {


					try {
						return Double.valueOf(((Map) variable).getzValue((int) z, (int) x));
					} catch (NumberFormatException nbfEx) {
						System.out.println(nbfEx);
						return 0;
					}
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
			this.add(new DisplayPanel3D(chartPanel));
		}else{
			this.setLayout(new BorderLayout());
			labelNoGraph.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(labelNoGraph,BorderLayout.CENTER);
		}

	}


}
