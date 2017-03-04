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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tools.Preference;
import tools.Utilitaire;

public final class Scalaire extends Variable {

	private String value;
	private static final JPanel panel = new JPanel(new GridLayout(1, 1, 2, 2));
	private static final JLabel valueView = new JLabel();

	public Scalaire(String shortName,String longName, String category, String swFeatureRef, String[][] swCsHistory, String value) {
		super(shortName, longName, category, swFeatureRef, swCsHistory);
		this.value = Utilitaire.cutNumber(value);
	}

	public String getValue() {
		return Utilitaire.cutNumber(value);
	}

	@Override
	public Component showView() {
		initVariable();
		return panel;
	}

	@Override
	public void exportToExcel() throws RowsExceededException, WriteException, IOException {

		WritableWorkbook workbook = Workbook.createWorkbook(new File("D:/" + this.getShortName() + ".xls"));
		WritableSheet sheet = workbook.createSheet("Export", 0);
		WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

		sheet.addCell(new Label(0, 0, this.getShortName(), arial10format));
		sheet.addCell(new Label(0, 1, this.getValue()));

		workbook.write();
		workbook.close();

	}


	@Override
	public void initVariable() {
		panel.setLayout(new GridLayout(1, 1, 2, 2));
		panel.add(valueView);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		panel.addMouseListener(this);
		valueView.setOpaque(true);
		valueView.setBackground(Color.LIGHT_GRAY);
		valueView.setBorder(new LineBorder(Color.BLACK, 2));
		valueView.setHorizontalAlignment(SwingConstants.CENTER);
		valueView.setText(getValue());
		valueView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	}

	@Override
	public void exportToPicture() {

		JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_RESULT_LAB));
		fileChooser.setDialogTitle("Enregistement de l'image");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image (*.jpg)", "jpg"));
		fileChooser.setSelectedFile(new File(".jpg"));
		int rep = fileChooser.showSaveDialog(null);

		if (rep == JFileChooser.APPROVE_OPTION) {
			File img = fileChooser.getSelectedFile();
			BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			panel.printAll(g);
			g.dispose();
			try {
				String pathImg = img.getPath();
				String extension = "";
				if(Utilitaire.getExtension(img)==null)
				{
					extension = ".jpg";
				}else{
					if(!Utilitaire.getExtension(img).equals(Utilitaire.jpg))
					{
						pathImg = img.getPath().substring(0, img.getPath().lastIndexOf("."));
						extension = ".jpg";
					}
				}
				ImageIO.write(image, "jpg", new File(pathImg + extension));
			} catch (IOException exp) {
				System.out.println(exp);
			}
		}


	}

	public void copyToClipboard()
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		panel.printAll(g);
		g.dispose();
		clipboard.setContents(new ImgTransfert(img), null);
		
	}
	
	class ImgTransfert implements Transferable
	{
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
			return new DataFlavor[]{DataFlavor.imageFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}
		
	}

}
