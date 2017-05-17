package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import visu.TableView;

public final class Curve extends Variable {

	private final String[][] values;
	private JPanel panel;
	private final int dimX;

	public Curve(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
			String[][] values) {
		super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
		this.values = values;

		this.dimX = values[0].length;

	}

	@Override
	public final String toString() {

		StringBuilder sb = new StringBuilder("\n");
		for (byte y = 0; y < 2; y++) {
			for (short x = 0; x < dimX; x++) {
				sb.append(this.getValue(y, x) + "\t");
			}
			sb.append("\n");
		}
		return super.toString() + "Valeurs :" + sb.toString();
	}

	public final String getUnitX() {
		return super.getSwUnitRef()[0];
	}

	public final String getUnitZ() {
		return super.getSwUnitRef()[1];
	}

	public final String[][] getValues() {
		return this.values;
	}

	public final String getValue(int col, int row) {
		return values[col][row];
	}

	public final int getDimX() {
		return dimX;
	}

	@Override
	public final Component showView(Boolean colored) {
		initVariable(colored);
		return panel;
	}

	@Override
	public final void initVariable(Boolean colored) {

		panel = new JPanel(new GridLayout(1, 1));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.addMouseListener(this);

		TableView tableView = new TableView(new TableModelView());
		tableView.getModel().setData(values);
		TableView.adjustCellsSize(tableView);

		panel.add(tableView);
	}

	@Override
	public final void copyToClipboard() {
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = img.createGraphics();
		panel.printAll(g);
		g.dispose();
		clipboard.setContents(new ImgTransfert(img), null);

	}

	final class ImgTransfert implements Transferable {
		private Image img;

		public ImgTransfert(Image img) {
			this.img = img;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return img;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

	}

}
