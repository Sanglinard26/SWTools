package chart;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class XYChart extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;

    private final PanelXYPlot xyPlot;
    private Serie[] listSerieName;

    private ListLegend legendList;

    public XYChart(PanelXYPlot xyPlot, int position, Boolean legend) {
        this.xyPlot = xyPlot;

        setLayout(new BorderLayout());

        add(xyPlot, BorderLayout.CENTER);

        if (legend) {
            listSerieName = new Serie[xyPlot.getSeriesCollection().getSeriesCount()];

            for (int i = 0; i < xyPlot.getSeriesCollection().getSeriesCount(); i++) {
                listSerieName[i] = xyPlot.getSeriesCollection().getSerie(i);
            }

            legendList = new ListLegend(listSerieName);
            legendList.addListSelectionListener(new ListEvent());

            switch (position) {
            case LEFT_POSITION:
                add(new JScrollPane(legendList), BorderLayout.EAST);
                break;
            case RIGHT_POSITION:
                add(new JScrollPane(legendList), BorderLayout.WEST);
                break;
            }
        }

    }

    private final class ListEvent implements ListSelectionListener {

        private SeriesCollection serieCollection = new SeriesCollection();

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting() & !legendList.isSelectionEmpty()) {
                for (int i = 0; i < legendList.getSelectedValuesList().size(); i++) {
                    System.out.println(legendList.getSelectedValuesList().get(i));
                    serieCollection.addSerie(legendList.getSelectedValuesList().get(i));
                }
                xyPlot.selectSeries(serieCollection);
            }
        }
    }
}
