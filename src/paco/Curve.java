package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Curve extends Label {

    private Object[][] value;
    private JPanel panel;

    public Curve(String shortName, String category, String swFeatureRef, String[][] swCsHistory, Object[][] value) {
        super(shortName, category, swFeatureRef, swCsHistory);
        this.value = value;

    }

    public String getValue(int x, int y) {
        return value[x][y].toString();
    }

    public int getDimX() {
        return value[0].length;
    }

    @Override
    public Component showView() {
        initLabel();
        return panel;
    }

    private void initLabel() {
        panel = new JPanel(new GridLayout(2, getDimX(), 2, 2));
        JLabel[] valueViewX = new JLabel[getDimX()];
        JLabel[] valueViewY = new JLabel[getDimX()];
        for (int i = 0; i < valueViewX.length; i++) {
            valueViewX[i] = new JLabel(getValue(0, i));
            panel.add(valueViewX[i]);
            panel.setBackground(Color.WHITE);
            valueViewX[i].setOpaque(true);
            valueViewX[i].setBackground(Color.LIGHT_GRAY);
            valueViewX[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewX[i].setHorizontalAlignment(SwingConstants.CENTER);

        }
        for (int i = 0; i < valueViewY.length; i++) {
            valueViewY[i] = new JLabel(getValue(1, i));
            panel.add(valueViewY[i]);
            panel.setBackground(Color.WHITE);
            valueViewY[i].setOpaque(true);
            valueViewY[i].setBackground(Color.LIGHT_GRAY);
            valueViewY[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewY[i].setHorizontalAlignment(SwingConstants.CENTER);

        }

    }

}
