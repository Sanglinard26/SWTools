package xyplot;

import java.util.ArrayList;
import java.util.List;


public final class Serie {
	
	private final String serieName;
    private List<XYPoint> points;

    public Serie(String serieName) {
        this.serieName = serieName;
        points = new ArrayList<>();
    }
    
    public void addPoint(double x, double y)
    {
    	points.add(new XYPoint(x, y));
    }
    
    public int getPointsCount()
    {
    	return points.size();
    }
    
    public List<XYPoint> getPoints() {
		return points;
	}
    
    public double getMaxValue()
    {
    	double maxValue = Double.MIN_VALUE;
    	for (int i = 0; i<points.size(); i++)
    	{
    		maxValue = Math.max(maxValue, ((XYPoint)points.get(i)).getY());
    	}
    	return maxValue;
    }
    
    public double getMinValue()
    {
    	double MinValue = Double.MAX_VALUE;
    	for (int i = 0; i<points.size(); i++)
    	{
    		MinValue = Math.min(MinValue, ((XYPoint)points.get(i)).getY());
    	}
    	return MinValue;
    }
    
    @Override
    public String toString() {
    	return this.serieName;
    }
    
    final class XYPoint
    {
    	private final double x;
    	private final double y;
    	
    	public XYPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
    	
    	public double getX() {
			return x;
		}
    	
    	public double getY() {
			return y;
		}
    	
    	@Override
    	public String toString() {
    		return "x = " + this.x + " ; " + "y = " + this.y;
    	}
    }

}
