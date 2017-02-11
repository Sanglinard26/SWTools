package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Map extends Label {

	private Object[][] values;
	private JPanel panel;

	public Map(String shortName, String category, String swFeatureRef, String[][] swCsHistory,Object[][] values) {
		super(shortName, category, swFeatureRef, swCsHistory);
		this.values = values;

	}

	public String getValue(int col, int row) {
		return values[col][row].toString();
	}

	public int getDimX() {
		return values[0].length;
	}

	public int getDimY() {
		return values.length;
	}

	@Override
	public Component showView() {
		initLabel();
		return panel;
	}

	private void initLabel() {
		panel = new JPanel(new GridLayout(getDimY(), getDimX(), 1, 1));
		panel.setBackground(Color.BLACK);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		JLabel[] valueView = new JLabel[getDimX()*(getDimY()-1)];

		for(int y = 0; y<getDimY(); y++)
		{
			for(int x = 0; x<getDimX(); x++)
			{
				valueView[y*x] = new JLabel(getValue(y, x));
				panel.add(valueView[y*x]);
				
				if(y == 0 | x == 0)
				{
					valueView[y*x].setFont(new Font(null, Font.BOLD, valueView[y*x].getFont().getSize()));
				}
				valueView[y*x].setOpaque(true);
				valueView[y*x].setBackground(Color.LIGHT_GRAY);
				valueView[y*x].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				valueView[y*x].setHorizontalAlignment(SwingConstants.CENTER);

			}
		}

	}

}
