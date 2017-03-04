package visu;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import paco.Variable;

public final class PanelInfoVariable extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final JLabel labelShortName = new JLabel("Nom :");
	private final JLabel labelLongName = new JLabel("Description :");
	private final JLabel labelSwFeatureRef = new JLabel("Fonction :");
	private final JLabel labelValue = new JLabel("Valeur(s) :");
	
	public PanelInfoVariable(Variable variable) {
		
		this.setLayout(new GridLayout(4, 1));
		this.setBackground(Color.WHITE);
		
		labelShortName.setText("Nom : " + variable.getShortName());
		labelLongName.setText("Description : " + variable.getLongName());
		labelSwFeatureRef.setText("Fonction : " + variable.getSwFeatureRef());
		
		this.add(labelShortName);
		this.add(labelLongName);
		this.add(labelSwFeatureRef);
		this.add(labelValue);
	}

}
