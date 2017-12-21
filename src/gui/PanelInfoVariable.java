package gui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import cdf.Variable;

public final class PanelInfoVariable extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final JTextArea infoVariable = new JTextArea();

    static {
        infoVariable.setEditable(false);
        infoVariable.setFont(new Font(null, Font.PLAIN, 12));
    }

    public PanelInfoVariable(final Variable variable) {

        this.setLayout(new GridLayout(1, 1));

        infoVariable.setText(null);

        infoVariable.append("Nom : " + variable.getShortName() + "\n");
        infoVariable.append("Description : " + variable.getLongName() + "\n");
        infoVariable.append("Fonction : " + variable.getSwFeatureRef() + "\n");

        final StringBuilder unite = new StringBuilder();
        for (String s : variable.getSwUnitRef()) {
            unite.append("[" + s + "] ");
        }
        infoVariable.append("Unite(s) : " + unite + "\n");
        infoVariable.append("Valeur(s) :");

        this.add(infoVariable);
    }

}
