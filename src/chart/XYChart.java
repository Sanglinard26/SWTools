package chart;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class XYChart extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;

    private final PanelXYPlot xyPlot;
    private String[] listSerieName;

    private ListLegend legendList;

    public XYChart(PanelXYPlot xyPlot, int position, Boolean legend) {
        this.xyPlot = xyPlot;

        setLayout(new BorderLayout());

        add(xyPlot, BorderLayout.CENTER);

        if (legend) {
            listSerieName = new String[xyPlot.getSeriesCollection().getSeriesCount()];

            for (int i = 0; i < xyPlot.getSeriesCollection().getSeriesCount(); i++) {
                listSerieName[i] = xyPlot.getSeriesCollection().getSerie(i).toString();
            }

            legendList = new ListLegend(listSerieName);

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
}
