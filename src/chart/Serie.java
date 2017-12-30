package chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class Serie {

    private final String serieName;
    private final List<XYPoint> points;
    private Color color;

    public Serie(String serieName) {
        this.serieName = serieName;
        points = new ArrayList<>();
    }

    public final void addPoint(double x, double y) {
        points.add(new XYPoint(x, y));
    }

    public final int getPointsCount() {
        return points.size();
    }

    public final List<XYPoint> getPoints() {
        return points;
    }
    
    public final double[][] getRangeXY()
    {
    	final double[][] range = new double[2][2];
    	
    	double minXValue = Double.POSITIVE_INFINITY;
		double maxXValue = Double.NEGATIVE_INFINITY;
		double minYValue = Double.POSITIVE_INFINITY;
		double maxYValue = Double.NEGATIVE_INFINITY;
    	
		final int nbPoint = points.size();
		
		for (int i = 0; i < nbPoint; i++) {
			minXValue = Math.min(minXValue, points.get(i).getX());
			maxXValue = Math.max(maxXValue, points.get(i).getX());
			minYValue = Math.min(minYValue, points.get(i).getY());
			maxYValue = Math.max(maxYValue, points.get(i).getY());
        }
		
		range[0][0] = minXValue;
		range[0][1] = maxXValue;
		
		if (minYValue != Double.POSITIVE_INFINITY) {
			range[1][0] = minYValue;
        }else{
        	range[1][0] = Double.NaN;
        }
		
		if (maxYValue != Double.NEGATIVE_INFINITY) {
			range[1][1] = maxYValue;
        }else{
        	range[1][1] = Double.NaN;
        }
		
		return range;
    	
    }

    public final Color getColor() {
        return color;
    }

    public final void setColor(Color color) {
        this.color = color;
    }

    public final String getName() {
        return this.serieName;
    }

    @Override
    public String toString() {
        return this.serieName;
    }

    final class XYPoint {
        private final double x;
        private final double y;

        public XYPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public final double getX() {
            return x;
        }

        public final double getY() {
            return y;
        }

        @Override
        public String toString() {
            return "x = " + this.x + " ; " + "y = " + this.y;
        }
    }

}
