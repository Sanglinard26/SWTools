package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.orsoncharts.Range;
import com.orsoncharts.renderer.RainbowScale;

import visu.TableView;

public final class Map extends Variable {

	private final String[][] values;
	private JPanel panel;
	private final String[] xValues;
	private final String[] yValues;
	private final String[][] zValues;
	private final int dimX;
	private final int dimY;

	private Double minZValue = Double.POSITIVE_INFINITY;
	private Double maxZValue = Double.NEGATIVE_INFINITY;

	private final RainbowScale rainbowScale;

	public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
			String[][] values) {
		super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

		this.values = values;
		this.dimX = values[0].length;
		this.dimY = values.length;

		xValues = new String[dimX - 1];
		for (short i = 0; i < xValues.length; i++) {
			xValues[i] = values[0][i + 1];
		}

		yValues = new String[dimY - 1];
		for (short i = 0; i < yValues.length; i++) {
			yValues[i] = values[i + 1][0];
		}

		zValues = new String[yValues.length][xValues.length];
		for (short x = 0; x < xValues.length; x++) {
			for (short y = 0; y < yValues.length; y++) {
				zValues[y][x] = values[y + 1][x + 1];

				try {
					if (Double.parseDouble(values[y + 1][x + 1]) < minZValue)
						minZValue = Double.parseDouble(values[y + 1][x + 1]);

					if (Double.parseDouble(values[y + 1][x + 1]) > maxZValue)
						maxZValue = Double.parseDouble(values[y + 1][x + 1]);
				} catch (NumberFormatException e) {
					minZValue = Double.NaN;
					maxZValue = Double.NaN;
				}

			}
		}
		rainbowScale = new RainbowScale(new Range(this.getMinZValue(), this.getMaxZValue()), (dimX - 1) * (dimY - 1), RainbowScale.BLUE_TO_RED_RANGE);
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder("\n");

		for (short y = 0; y < dimY; y++) {
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

	public final String getUnitY() {
		return super.getSwUnitRef()[1];
	}

	public final String getUnitZ() {
		return super.getSwUnitRef()[2];
	}

	public final String[] getxValues() {
		return xValues;
	}

	public final String[] getyValues() {
		return yValues;
	}

	public final String getzValue(int col, int row) {
		return zValues[col][row];
	}

	public final Double getMaxZValue() {
		return maxZValue;
	}

	public final Double getMinZValue() {
		return minZValue;
	}

	public final String[][] getValues() {
		return values;
	}

	public final String getValue(int col, int row) {
		return values[col][row];
	}

	public final int getDimX() {
		return dimX;
	}

	public final int getDimY() {
		return dimY;
	}

	@Override
	public final Component showView(Boolean colored) {
		initVariable(colored);
		return panel;
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

	@Override
	public final void initVariable(Boolean colored) {

		
		if (false)
		{
			panel = new JPanel(new GridLayout(dimY, dimX, 1, 1));
			panel.setBackground(Color.BLACK);
			panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			panel.addMouseListener(this);

			JLabel valueView;

			for (short y = 0; y < dimY; y++) {
				for (short x = 0; x < dimX; x++) {
					valueView = new JLabel(getValue(y, x));
					panel.add(valueView);

					if (y == 0 | x == 0 | (maxZValue - minZValue == 0) | colored == false) {
						valueView.setFont(new Font(null, Font.BOLD, valueView.getFont().getSize()));
						valueView.setBackground(Color.LIGHT_GRAY);
					} else {
						try {
							valueView.setBackground(rainbowScale.valueToColor(Double.parseDouble(getValue(y, x))));
						} catch (Exception e) {
							valueView.setBackground(Color.LIGHT_GRAY);
						}
					}
					valueView.setOpaque(true);
					valueView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					valueView.setHorizontalAlignment(SwingConstants.CENTER);
				}
			}

		}else{
			panel = new JPanel();
			panel.addMouseListener(this);
			panel.add(new TableView(new TableModelView(values)));

		}
		
		
	}

}
