package visu;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cdf.Variable;

public final class PanelInfoVariable extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel labelShortName = new JLabel("Nom :");
    private final JLabel labelLongName = new JLabel("Description :");
    private final JLabel labelSwFeatureRef = new JLabel("Fonction :");
    private final JLabel labelUnit = new JLabel("Unite(s) :");
    private final JLabel labelValue = new JLabel("Valeur(s) :");

    public PanelInfoVariable(final Variable variable) {

        this.setLayout(new GridLayout(5, 1));
        this.setBackground(Color.WHITE);

        labelShortName.setText("Nom : " + variable.getShortName());
        labelLongName.setText("Description : " + variable.getLongName());
        labelSwFeatureRef.setText("Fonction : " + variable.getSwFeatureRef());

        final StringBuilder unite = new StringBuilder();
        for (String s : variable.getSwUnitRef()) {
            unite.append("[" + s + "] ");
        }

        labelUnit.setText("Unite(s) : " + unite);

        this.add(labelShortName);
        this.add(labelLongName);
        this.add(labelSwFeatureRef);
        this.add(labelUnit);
        this.add(labelValue);
    }

}
