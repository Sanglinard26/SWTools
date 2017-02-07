package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Scalaire extends Label {

    private Object value;
    private static final JPanel panel = new JPanel(new GridLayout(1, 1, 2, 2));
    private static final JLabel valueView = new JLabel();

    public Scalaire(String shortName, String category, String swFeatureRef, String[][] swCsHistory, Object value) {
        super(shortName, category, swFeatureRef, swCsHistory);
        this.value = value;
        initLabel();
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Component showView() {
        valueView.setText(value.toString());
        valueView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private static void initLabel() {
        panel.add(valueView);
        panel.setBackground(Color.WHITE);
        valueView.setOpaque(true);
        valueView.setBackground(Color.LIGHT_GRAY);
        valueView.setBorder(new LineBorder(Color.BLACK, 2));
        valueView.setHorizontalAlignment(SwingConstants.CENTER);
    }

}
