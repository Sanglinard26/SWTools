package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Scalaire extends Label {
	
	private Object value;
	private static final JPanel panel = new JPanel(new GridLayout(30, 30));
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
			valueView.setPreferredSize(new Dimension(50, 30));
			valueView.setText(value.toString());
		return panel;
	}
	
	private static void initLabel()
	{
		panel.add(valueView);
		panel.setBackground(Color.WHITE);
		valueView.setOpaque(true);
		valueView.setBackground(Color.LIGHT_GRAY);
		valueView.setBorder(new LineBorder(Color.BLACK, 2));
		valueView.setHorizontalAlignment(SwingConstants.CENTER);
	}

}
