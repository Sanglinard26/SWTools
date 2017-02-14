package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import tools.Utilitaire;

public class Scalaire extends Label {

    private String value;
    private static final JPanel panel = new JPanel(new GridLayout(1, 1, 2, 2));
    private static final JLabel valueView = new JLabel();

    public Scalaire(String shortName, String category, String swFeatureRef, String[][] swCsHistory, String value) {
        super(shortName, category, swFeatureRef, swCsHistory);
        this.value = value;
    }

    public String getValue() {

        return Utilitaire.cutNumber(value);
    }

    @Override
    public Component showView() {
        initLabel();
        return panel;
    }

    private void initLabel() {
        panel.add(valueView);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        valueView.setOpaque(true);
        valueView.setBackground(Color.LIGHT_GRAY);
        valueView.setBorder(new LineBorder(Color.BLACK, 2));
        valueView.setHorizontalAlignment(SwingConstants.CENTER);
        valueView.setText(getValue());
        valueView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

}
