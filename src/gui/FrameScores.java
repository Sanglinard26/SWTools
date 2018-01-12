/*
 * Creation : 11 janv. 2018
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cdf.Cdf;
import chart.PieChart;

public final class FrameScores extends JFrame {

    private static final long serialVersionUID = 1L;

    public FrameScores(Cdf cdf) {
        super("Repartition des scores");

        JPanel panFrm = new JPanel();
        panFrm.setBackground(Color.GRAY);

        add(panFrm);

        PieChart chart = new PieChart(new Dimension(300, 300), 5, cdf.getNbLabel());
        chart.setValue(cdf.getRepartitionScore());

        panFrm.add(chart);

        JPanel panLegend = new JPanel(new GridLayout(5, 1));
        panLegend.setOpaque(false);
        panLegend.add(new JLabel("<html><font size=+2 color=red>0%</font></html>"));
        panLegend.add(new JLabel("<html><font size=+2 color=orange>25%</font></html>"));
        panLegend.add(new JLabel("<html><font size=+2 color=yellow>50%</font></html>"));
        panLegend.add(new JLabel("<html><font size=+2 color=green>75%</font></html>"));
        panLegend.add(new JLabel("<html><font size=+2 color=blue>100%</font></html>"));

        panFrm.add(panLegend);

        pack();

        setVisible(true);
    }
}
