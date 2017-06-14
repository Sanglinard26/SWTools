package chart;

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
    
    public double getMaxYValue()
    {
    	double maxValue = Double.NEGATIVE_INFINITY;
    	for (int i = 0; i<points.size(); i++)
    	{
    		maxValue = Math.max(maxValue, ((XYPoint)points.get(i)).getY());
    	}
    	return maxValue;
    }
    
    public double getMaxXValue()
    {
    	double maxValue = Double.NEGATIVE_INFINITY;
    	for (int i = 0; i<points.size(); i++)
    	{
    		maxValue = Math.max(maxValue, ((XYPoint)points.get(i)).getX());
    	}
    	return maxValue;
    }
    
    public double getMinYValue()
    {
    	double MinValue = Double.POSITIVE_INFINITY;
    	for (int i = 0; i<points.size(); i++)
    	{
    		MinValue = Math.min(MinValue, ((XYPoint)points.get(i)).getY());
    	}
    	return MinValue;
    }
    
    public double getMinXValue()
    {
    	double MinValue = Double.POSITIVE_INFINITY;
    	for (int i = 0; i<points.size(); i++)
    	{
    		MinValue = Math.min(MinValue, ((XYPoint)points.get(i)).getX());
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
