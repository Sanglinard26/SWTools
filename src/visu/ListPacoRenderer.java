/*
 * Creation : 6 avr. 2017
 */
package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import paco.PaCo;

public class ListPacoRenderer extends JPanel implements ListCellRenderer<PaCo> {

    private static final long serialVersionUID = 1L;
    private final JLabel txtNamePaco = new JLabel();
    private final JLabel txtNbVariable = new JLabel();
    private final JProgressBar progressBarScore = new JProgressBar();

    public ListPacoRenderer() {
    	setLayout(new GridLayout(3, 1, 5, 5));
    	add(txtNamePaco);
    	add(txtNbVariable);
    	progressBarScore.setStringPainted(true);
    	add(progressBarScore);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PaCo> list, PaCo value, int index, boolean isSelected, boolean cellHasFocus) {

    	txtNamePaco.setText(value.getName());
    	txtNbVariable.setText("Nombre de label(s) : " + Integer.toString(value.getNbLabel()));
    	progressBarScore.setValue(Math.round(value.getAvgScore()));
    	progressBarScore.setString("Score moyen à " + value.getAvgScore() + "%");

        if (isSelected) {
            setBackground(Color.getHSBColor(100, 100, 100));
            setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }

}

//public class ListPacoRenderer extends JLabel implements ListCellRenderer<PaCo> {
//
//    private static final long serialVersionUID = 1L;
//
//    public ListPacoRenderer() {
//        setHorizontalAlignment(LEFT);
//        setVerticalAlignment(CENTER);
//        setOpaque(true);
//    }
//
//    @Override
//    public Component getListCellRendererComponent(JList<? extends PaCo> list, PaCo value, int index, boolean isSelected, boolean cellHasFocus) {
//
//        setText(value.toString());
//        setBorder(new EmptyBorder(2, 0, 2, 0));
//        setToolTipText("Nombre de label(s) : " + value.getNbLabel());
//
//        if (isSelected) {
//            setBackground(Color.getHSBColor(100, 100, 100));
//            setFont(new Font(null, Font.BOLD, 12));
//            setBorder(new LineBorder(Color.BLACK, 1));
//        } else {
//            setBackground(Color.WHITE);
//            setFont(new Font(null, Font.PLAIN, 12));
//        }
//
//        return this;
//    }
//
//}
