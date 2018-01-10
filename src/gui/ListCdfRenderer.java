/*
 * Creation : 6 avr. 2017
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

import cdf.Cdf;

public final class ListCdfRenderer extends JPanel implements ListCellRenderer<Cdf> {

    private static final long serialVersionUID = 1L;

    private final JLabel txtNamePaco = new JLabel();
    private final JLabel txtNbVariable = new JLabel();
    private final JProgressBar progressBarScore = new JProgressBar();
    private static final DecimalFormat df = new DecimalFormat("###.##");

    private static final GridBagConstraints gbc = new GridBagConstraints();

    public ListCdfRenderer() {
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        txtNamePaco.setFont(new Font(null, Font.BOLD, 12));
        add(txtNamePaco, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(txtNbVariable, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.CENTER;
        progressBarScore.setStringPainted(true);
        add(progressBarScore, gbc);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Cdf> list, Cdf value, int index, boolean isSelected, boolean cellHasFocus) {

        if (!value.getName().contains("_vs_")) {
            txtNamePaco.setText(value.getName());
            progressBarScore.setValue(Math.round(value.getAvgScore()));
            progressBarScore.setString("Score moyen = " + df.format(value.getAvgScore()) + "%" + " (Min = " + value.getMinScore() + "%" + " ; Max = "
                    + value.getMaxScore() + "%)");
        } else {
            txtNamePaco.setText("<html>Comparaison :<br>" + "Ref : " + value.getName().split("_vs_")[0] + "<br>" + "Work : "
                    + value.getName().split("_vs_")[1] + "</html>");
            progressBarScore.setValue(0);
            progressBarScore.setString("...");
        }

        txtNbVariable.setText("Nombre de label(s) : " + Integer.toString(value.getNbLabel()));

        if (isSelected) {
            setBorder(new LineBorder(Color.BLACK, 2));
        } else {
            setBackground(Color.WHITE);
            setBorder(null);
        }

        return this;
    }

    @SuppressWarnings("unused")
    private class BarUI extends BasicProgressBarUI {

        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int width = progressBar.getWidth();
            final int height = progressBar.getHeight();

            double dProgress = progressBar.getPercentComplete();
            if (dProgress < 0) {
                dProgress = 0;
            } else if (dProgress > 1) {
                dProgress = 1;
            }

            int iInnerWidth = (int) Math.round(width * dProgress);

            int x = 0;
            int y = height / 2;

            Point2D start = new Point2D.Double(x, y);
            Point2D end = new Point2D.Double(x + iInnerWidth + 1, y);

            float[] dist = { 0.0f, 0.5f, 1.0f };
            Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
            LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);

            g2d.setPaint(p);

            Rectangle2D fill = new Rectangle2D.Double(0, 0, iInnerWidth, height);

            g2d.fill(fill);

            g2d.setColor(Color.BLACK);
            g2d.drawString(progressBar.getString(), width / 4, height - 6);

            g2d.dispose();

        }

    }

}