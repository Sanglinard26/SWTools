package paco;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public final class ValueBlock extends Variable {
	
	private String[][] values;
    private JPanel panel;
    private String[] xValues;
    private String[] yValues;
    private String[][] zValues;

	public ValueBlock(String shortName, String category, String swFeatureRef, String[][] swCsHistory, String[][] values) {
		super(shortName, category, swFeatureRef, swCsHistory);
		this.values = values;
		
		
	}

	@Override
	public void initVariable() {
		panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Non implemente"),BorderLayout.LINE_START);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);

	}

	@Override
	public Component showView() {
		initVariable();
		return panel;
	}

	@Override
	public void exportToExcel() throws RowsExceededException, WriteException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void exportToPicture() {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyToClipboard() {
		// TODO Auto-generated method stub

	}

}
