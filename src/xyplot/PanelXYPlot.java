/*
 * Creation : 6 juin 2017
 */
package xyplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public final class PanelXYPlot extends JPanel {

	private static final long serialVersionUID = 1L;

	private int padding = 25;
	private int labelPadding = 25;
	private Color lineColor = new Color(44, 102, 230, 180);
	private Color pointColor = new Color(100, 100, 100, 180);
	private Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4;
	private int numberYDivisions = 10;

	List<Point> graphPoints;

	private final String title;

	private SeriesCollection seriesCollection;

	private Rectangle clip;

	public PanelXYPlot(String title, SeriesCollection seriesCollection) {
		this.title = title;
		this.seriesCollection = seriesCollection;
		setPreferredSize(new Dimension(500, 500));
		//        this.addMouseListener(new MouseAdapter() {
		//            @Override
		//            public void mouseClicked(MouseEvent e) {
		//                for (int i = 0; i < graphPoints.size(); i++) {
		//                    if (Math.abs(graphPoints.get(i).x - e.getX()) < 4 & Math.abs(graphPoints.get(i).y - e.getY()) < 4) {
		//                        System.out.println(scores.get(i));
		//
		//                        markPoint(getGraphics(), graphPoints.get(i));
		//                        clip = getGraphics().getClipBounds(new Rectangle(graphPoints.get(i).x - 10 / 2, graphPoints.get(i).y - 10 / 2, 10, 10));
		//                    }
		//                }
		//            }
		//        });
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (getNbXPoints() - 1);
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue());

		graphPoints = new ArrayList<>();
		for (int nSerie = 0; nSerie<seriesCollection.getSeriesCount(); nSerie++)
		{
			for (int nPoint = 0; nPoint<seriesCollection.getSerie(nSerie).getPointsCount(); nPoint++)
			{
				int x1 = (int) (nPoint * xScale + padding + labelPadding);
				int y1 = (int) ((getMaxValue() - seriesCollection.getSerie(nSerie).getPoints().get(nPoint).getY()) * yScale + padding);
				graphPoints.add(new Point(x1, y1));
			}
		}
		for (int i = 0; i < getNbXPoints(); i++) {

		}

		// draw title
		if (title != null) {
			g2.setColor(Color.WHITE);
			g2.fillRect(getWidth() / 2 - 50, 0, 100, 20);
			g2.setColor(Color.BLACK);
			g2.drawRect(getWidth() / 2 - 50, 0, 100, 20);
			FontMetrics metrics = g2.getFontMetrics();
			int labelWidth = metrics.stringWidth(title);
			g2.drawString(title, getWidth() / 2 - (labelWidth / 2), metrics.getHeight());
		}

		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);

		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (getNbXPoints() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((getMinValue() + (getMaxValue() - getMinValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < getNbXPoints(); i++) {
			if (getNbXPoints() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (getNbXPoints() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if ((i % ((int) ((getNbXPoints() / 20.0)) + 1)) == 0) {
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					String xLabel = i + "";
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}

		// create x and y axes
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

		Stroke oldStroke = g2.getStroke();
		g2.setColor(lineColor);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);
		}

		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int i = 0; i < graphPoints.size(); i++) {
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			g2.fillOval(x, y, ovalW, ovalH);
		}
	}

	public void markPoint(Graphics g, Point p) {
		Graphics2D g2 = (Graphics2D) g;

		if (clip != null)
			repaint(clip);

		g2.setColor(Color.RED);
		int x = p.x - 10 / 2;
		int y = p.y - 10 / 2;
		int ovalW = 10;
		int ovalH = 10;
		g2.fillOval(x, y, ovalW, ovalH);
	}

	public int getNbXPoints()
	{
		int nbMaxXpoints = 0;
		for (int i = 0; i < seriesCollection.getSeriesCount(); i++)
		{
			nbMaxXpoints = Math.max(nbMaxXpoints, seriesCollection.getSerie(i).getPointsCount());
		}
		return nbMaxXpoints;
	}

	public double getMaxValue()
	{
		double maxValue = Double.MIN_VALUE;
		for (int i = 0; i < seriesCollection.getSeriesCount(); i++)
		{
			maxValue = Math.max(maxValue, seriesCollection.getSerie(i).getMaxValue());
		}
		return maxValue;
	}

	public double getMinValue()
	{
		double minValue = Double.MAX_VALUE;
		for (int i = 0; i < seriesCollection.getSeriesCount(); i++)
		{
			minValue = Math.min(minValue, seriesCollection.getSerie(i).getMinValue());
		}
		return minValue;
	}

	public void setSeriesCollection(SeriesCollection seriesCollection) {
		this.seriesCollection = seriesCollection;
		invalidate();
		this.repaint();
	}

}
