package chart;

import java.util.ArrayList;
import java.util.List;

public final class SeriesCollection {
	
	private List series;

    public SeriesCollection() {
        this(null);
    }

    public SeriesCollection(Serie serie) {
    	series = new ArrayList<>();
    	
        if (serie != null) {
        	series.add(serie);
        }
    }

    public void addSerie(Serie serie) {
    	if (!series.contains(serie))
    		series.add(serie);
    }

    public void removeAllSeries() {
    	if (!series.isEmpty())
    		series.clear();
    }

    public void removeSerie(Serie serie) {
    	if (series.contains(serie))
    		series.remove(serie);
    }
    
    public Serie getSerie(int index)
    {
    	return (Serie) series.get(index);
    }
    
    public int getSeriesCount()
    {
    	return series.size();
    }

}
