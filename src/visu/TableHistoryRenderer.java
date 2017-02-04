package visu;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class TableHistoryRenderer implements TableCellRenderer {
	
	private Component component;
	private JLabel label = new JLabel();
	private JProgressBar progressBar = new JProgressBar(0, 100);
	
	public enum Maturite
	{
		Changed(0),
		PrelimCalibrated(25),
		Calibrated(50),
		Checked(75),
		Completed(100);
		
		private final int score;
		Maturite(int score)
		{
			this.score = score;
		}
		
		private int getScore()
		{
			return this.score;
		}
	}
	

	public TableHistoryRenderer() {
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		switch (column) {
		case 0:
			label.setText(value.toString());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			component = label;
			break;
		case 1:
			label.setText(value.toString());
			label.setHorizontalAlignment(SwingConstants.CENTER);
			component = label;
			break;
		case 2:
			progressBar.setValue(50);
			component = progressBar;
			break;
		case 3:
			label.setText(value.toString());
			label.setHorizontalAlignment(SwingConstants.LEFT);
			component = label;
			break;
		}

		return component;
	}

}
