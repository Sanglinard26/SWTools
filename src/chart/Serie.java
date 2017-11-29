package chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class Serie {

    private final String serieName;
    private List<XYPoint> points;
    private Color color;

    public Serie(String serieName) {
        this.serieName = serieName;
        points = new ArrayList<>();
    }

    public void addPoint(double x, double y) {
        points.add(new XYPoint(x, y));
    }

    public int getPointsCount() {
        return points.size();
    }

    public List<XYPoint> getPoints() {
        return points;
    }

    public double getMaxYValue() {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < points.size(); i++) {
            maxValue = Math.max(maxValue, points.get(i).getY());
        }
        if (maxValue != Double.NEGATIVE_INFINITY) {
            return maxValue;
        }
        return Double.NaN;
    }

    public double getMaxXValue() {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < points.size(); i++) {
            maxValue = Math.max(maxValue, points.get(i).getX());
        }
        return maxValue;
    }

    public double getMinYValue() {
        double minValue = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.size(); i++) {
            minValue = Math.min(minValue, points.get(i).getY());
        }
        if (minValue != Double.POSITIVE_INFINITY) {
            return minValue;
        }
        return Double.NaN;
    }

    public double getMinXValue() {
        double minValue = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.size(); i++) {
            minValue = Math.min(minValue, points.get(i).getX());
        }
        return minValue;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
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
