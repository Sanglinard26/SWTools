/*
 * Creation : 6 avr. 2017
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

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
        txtNamePaco.setFont(new Font(null, Font.BOLD, 12));
        add(txtNbVariable, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        txtNamePaco.setFont(new Font(null, Font.BOLD, 12));
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
            setBackground(Color.getHSBColor(100, 100, 100));
            setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }

}