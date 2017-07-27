/*
 * Creation : 6 avr. 2017
 */
package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
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

    public ListCdfRenderer() {
        setLayout(new GridLayout(3, 1, 5, 5));
        txtNamePaco.setFont(new Font(null, Font.BOLD, 12));
        add(txtNamePaco);
        add(txtNbVariable);
        progressBarScore.setStringPainted(true);
        add(progressBarScore);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Cdf> list, Cdf value, int index, boolean isSelected, boolean cellHasFocus) {

        txtNamePaco.setText(value.getName());
        txtNbVariable.setText("Nombre de label(s) : " + Integer.toString(value.getNbLabel()));
        progressBarScore.setValue(Math.round(value.getAvgScore()));
        progressBarScore.setString("Score moyen = " + df.format(value.getAvgScore()) + "%" + " (Min = " + value.getMinScore() + "%" + " ; Max = "
                + value.getMaxScore() + "%)");

        if (isSelected) {
            setBackground(Color.getHSBColor(100, 100, 100));
            setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }

}