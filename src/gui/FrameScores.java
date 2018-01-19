/*
 * Creation : 11 janv. 2018
 */
package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import cdf.Cdf;
import chart.PieChart;

public final class FrameScores extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final GridBagConstraints gbc = new GridBagConstraints();

    public FrameScores(Cdf cdf) {
        super("Repartition des scores");
        setResizable(false);

        Container container = getContentPane();
        container.setLayout(new GridBagLayout());

        PieChart chart = new PieChart(new Dimension(300, 300), 5, cdf.getNbLabel());
        chart.setValue(cdf.getRepartitionScore());

        JLabel nomCdf = new JLabel(cdf.getName(), SwingConstants.CENTER);
        nomCdf.setFont(new Font(null, Font.PLAIN, 14));
        nomCdf.setBorder(new LineBorder(Color.BLACK));
        nomCdf.setOpaque(true);
        nomCdf.setBackground(Color.WHITE);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        container.add(nomCdf, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        container.add(chart, gbc);

        JPanel panLegend = new JPanel(new GridLayout(5, 1));
        panLegend.setBorder(new LineBorder(Color.BLACK, 1));

        String[] score = new String[] { "0%", "25%", "50%", "75%", "100%" };
        Color[] color = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE };
        JLabel label;

        for (int i = 0; i < 5; i++) {
            label = new JLabel(score[i], SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(color[i]);
            label.setFont(new Font(null, Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(50, 30));
            panLegend.add(label);
        }

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        container.add(panLegend, gbc);

        pack();

        setVisible(true);
    }
}
