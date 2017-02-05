package paco;

import java.awt.Component;

import javax.swing.JLabel;

public class Curve extends Label {

	public Curve(String shortName, String category, String swFeatureRef, String[][] swCsHistory) {
		super(shortName, category, swFeatureRef, swCsHistory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component showView() {
		// TODO Auto-generated method stub
		return new JLabel("Non implémenté");
	}

}
