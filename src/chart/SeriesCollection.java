package chart;

import java.util.ArrayList;
import java.util.List;

public final class SeriesCollection {

    private List<Serie> series;

    public SeriesCollection() {
        this(null);
    }

    public SeriesCollection(Serie serie) {
        series = new ArrayList<>();

        if (serie != null) {
            series.add(serie);
        }
    }

    public final void addSerie(Serie serie) {
        if (!series.contains(serie))
            series.add(serie);
    }

    public final void removeAllSeries() {
        if (!series.isEmpty())
            series.clear();
    }

    public final Serie getSerie(int index) {
        return series.get(index);
    }

    public final int getSeriesCount() {
        return series.size();
    }

}
