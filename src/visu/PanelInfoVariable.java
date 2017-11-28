package visu;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import cdf.Variable;

public final class PanelInfoVariable extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextArea infoVariable = new JTextArea();

    public PanelInfoVariable(final Variable variable) {

        this.setLayout(new GridLayout(1, 1));
        this.setBackground(Color.WHITE);

        infoVariable.append("Nom : " + variable.getShortName() + "\n");
        infoVariable.append("Description : " + variable.getLongName() + "\n");
        infoVariable.append("Fonction : " + variable.getSwFeatureRef() + "\n");

        final StringBuilder unite = new StringBuilder();
        for (String s : variable.getSwUnitRef()) {
            unite.append("[" + s + "] ");
        }
        infoVariable.append("Unite(s) : " + unite + "\n");
        infoVariable.append("Valeur(s) :");

        infoVariable.setEditable(false);
        infoVariable.setFont(new Font(null, Font.PLAIN, 12));
        this.add(infoVariable);
    }

}
