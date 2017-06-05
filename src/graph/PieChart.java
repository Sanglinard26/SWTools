package graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

public final class PieChart extends JComponent {

	private static final long serialVersionUID = 1L;

	private final Dimension dim;
	private final double value;

	private Slice[] slices = new Slice[2];

	public PieChart(Dimension dim, double value) {
		this.dim = dim;
		this.value = value;

		slices[0] = new Slice(100-value, Color.WHITE);
		slices[1] = new Slice(value, Color.BLACK);
	}

	@Override
	public Dimension getPreferredSize() {
		return dim;
	}


	@Override
	public void paint(Graphics g) {
		drawPie((Graphics2D) g, new Rectangle(dim.width, dim.height), slices);
	}


	private void drawPie(Graphics2D g, Rectangle area, Slice[] slices) {

		double curValue = 25.0D;
		int startAngle = 0;
		
//		BasicStroke bs = new BasicStroke(2);
//		
//		g.setStroke(bs);
//		g.setColor(Color.RED);
//		g.drawOval(area.x, area.y, area.width, area.height);
		
		for (int i = 0; i < 2; i++) {
			startAngle = (int) (curValue * 360 / 100);
			int arcAngle = (int) (slices[i].value * 360 / 100);

			g.setColor(slices[i].color);
			g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
			curValue += slices[i].value;
		}
	}

	private final class Slice {
		double value;
		Color color;

		public Slice(double value, Color color) {
			this.value = value;
			this.color = color;
		}
	}
}
