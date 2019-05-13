package gui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import cdf.Curve;
import cdf.Map;
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

		final String[] axe = new String[]{"X", "Y"};
		String[] sharedAxis = null;

		if(variable instanceof Curve)
		{	
			sharedAxis = ((Curve) variable).getSharedAxis();
		}else if(variable instanceof Map)
		{
			sharedAxis = ((Map) variable).getSharedAxis();
		}

		if(sharedAxis != null)
		{
			for(int i = 0; i < sharedAxis.length; i++)
			{
				infoVariable.append("Axe " + axe[i] + " : " + sharedAxis[i] + "\n");
			}
		}

		infoVariable.append("Valeur(s) :");

		this.add(infoVariable);
	}

}
