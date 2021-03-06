/*
 * Creation : 6 avr. 2017
 */
package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cdf.Cdf;
import cdf.CdfUtils;
import cdf.ListModelCdf;
import utils.Preference;

public final class ListCdf extends JList<Cdf> {

	private static final long serialVersionUID = 1L;

	private static final String ICON_EXCEL = "/excel_icon_24.png";
	private static final String ICON_TEXT = "/text_icon_24.png";
	private static final String ICON_MATLAB = "/matlab_icon_24.png";
	private static final String ICON_HTML = "/html_icon_24.png";
	private static final String ICON_TRASH = "/corbeille_icon_24.png";
	private static final String ICON_COMPARAISON = "/comparaison_icon_24.png";
	private static final String ICON_UP = "/up_icon_24.png";
	private static final String ICON_DOWN = "/down_icon_24.png";

	private static final Locale lang = Preference.getPreference(Preference.KEY_LANGUAGE).equals("en") ? Locale.ENGLISH : Locale.FRENCH;
	private static final ResourceBundle bundle = ResourceBundle.getBundle("properties.langue", lang);

	private enum FileFormat {
		TXT("txt", "Fichier texte (*.txt)"), XLS("xls", "Fichier Excel (*.xls)"), M("m", "Fichier Matlab (*.m)"), HTML("html",
				"Fichier html (*.html)");

		private String extension;
		private String extensionDesc;

		private FileFormat(String extension, String extensionDesc) {
			this.extension = extension;
			this.extensionDesc = extensionDesc;
		}

		public String getExtension() {
			return extension;
		}

		public String getExtensionDesc() {
			return extensionDesc;
		}
	}

	public ListCdf(ListModelCdf dataModel) {
		super(dataModel);
		setCellRenderer(new ListCdfRenderer());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addKeyListener(new MyKeyListener());
		addMouseListener(new ListMouseListener());

		// Activatation DnD
		setDropMode(DropMode.INSERT);
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();

		super.paintComponent(g);

		final DropLocation loc = getDropLocation();
		if (loc == null) {
			setBackground(Color.WHITE);
			setBorder(null);
			return;
		}

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.LIGHT_GRAY, Color.GRAY));

		Point2D center = new Point2D.Float(loc.getDropPoint().x, loc.getDropPoint().y);
		float radius = 72f;
		float[] dist = { 0.0f, 1f };
		Color[] colors = { Color.WHITE, UIManager.getLookAndFeel().getDefaults().getColor("textHighlight") };
		RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
		g2.setPaint(p);

		if (getModel().getSize() > 0) {
			g2.fillRect(0, (int) getCellBounds(0, getModel().getSize() - 1).getHeight(), getWidth(),
					(int) (getHeight() - getCellBounds(0, getModel().getSize() - 1).getHeight()));
		} else {
			g2.fillRect(0, 0, getWidth(), getHeight());
		}

		g2.dispose();
	}

	@Override
	public ListModelCdf getModel() {
		return (ListModelCdf) super.getModel();
	}

	private final class MyKeyListener extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent paramKeyEvent) {
			if (paramKeyEvent.getKeyCode() == 127 && ListCdf.this.getSelectedIndex() > -1) // touche suppr
			{
				for (@SuppressWarnings("unused")
				int idx : ListCdf.this.getSelectedIndices()) {
					ListCdf.this.getModel().removeCdf(ListCdf.this.getSelectedIndex());
				}

				ListCdf.this.clearSelection();
				PanelCDF.razUI();
			}

			final int moveMe = ListCdf.this.getSelectedIndex();

			if (paramKeyEvent.isControlDown() && paramKeyEvent.getKeyCode() == KeyEvent.VK_UP && moveMe != 0) {
				swap(moveMe, moveMe - 1);
				ListCdf.this.setSelectedIndex(moveMe - 1);
				ListCdf.this.ensureIndexIsVisible(moveMe - 1);
			}

			if (paramKeyEvent.isControlDown() && paramKeyEvent.getKeyCode() == KeyEvent.VK_DOWN && moveMe != getModel().getSize() - 1) {
				swap(moveMe, moveMe + 1);
				ListCdf.this.setSelectedIndex(moveMe + 1);
				ListCdf.this.ensureIndexIsVisible(moveMe + 1);
			}
		}
	}

	private final class ListMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && ListCdf.this.getModel().getSize() > 0 && ListCdf.this.getSelectedIndices().length <= 1) {
				final JPopupMenu menu = new JPopupMenu();
				final JMenu menuMove = new JMenu(bundle.getString("ListCdf.move"));
				final JMenu menuExport = new JMenu(bundle.getString("ListCdf.export"));
				JMenuItem menuItem;
				if (ListCdf.this.locationToIndex(e.getPoint()) == ListCdf.this.getSelectedIndex()) {

					menuItem = new JMenuItem(bundle.getString("ListCdf.deleteFile"), new ImageIcon(getClass().getResource(ICON_TRASH)));
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							ListCdf.this.getModel().removeCdf(ListCdf.this.getSelectedIndex());
							ListCdf.this.clearSelection();
							PanelCDF.razUI();
						}
					});
					menu.add(menuItem);
					menu.addSeparator();

					menuItem = new JMenuItem(bundle.getString("ListCdf.up"), new ImageIcon(getClass().getResource(ICON_UP)));
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));
					menuItem.addActionListener(new UpDownListener());
					menuMove.add(menuItem);

					menuMove.addSeparator();

					menuItem = new JMenuItem(bundle.getString("ListCdf.down"), new ImageIcon(getClass().getResource(ICON_DOWN)));
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
					menuItem.addActionListener(new UpDownListener());
					menuMove.add(menuItem);

					menuItem = new JMenuItem(bundle.getString("ListCdf.exportToTxt"), new ImageIcon(getClass().getResource(ICON_TEXT)));
					menuItem.addActionListener(new ExportListener(FileFormat.TXT));
					menuExport.add(menuItem);

					menuExport.addSeparator();
					menuItem = new JMenuItem(bundle.getString("ListCdf.exportToXls"), new ImageIcon(getClass().getResource(ICON_EXCEL)));
					menuItem.addActionListener(new ExportListener(FileFormat.XLS));
					menuExport.add(menuItem);

					menuExport.addSeparator();
					menuItem = new JMenuItem(bundle.getString("ListCdf.exportToM"), new ImageIcon(getClass().getResource(ICON_MATLAB)));
					menuItem.addActionListener(new ExportListener(FileFormat.M));
					menuExport.add(menuItem);

					menuExport.addSeparator();
					menuItem = new JMenuItem(bundle.getString("ListCdf.exportToHtml"), new ImageIcon(getClass().getResource(ICON_HTML)));
					menuItem.addActionListener(new ExportListener(FileFormat.HTML));
					menuExport.add(menuItem);

					menu.add(menuMove);
					menu.addSeparator();
					menu.add(menuExport);
					menu.addSeparator();

					menuItem = new JMenuItem(bundle.getString("ListCdf.score"));
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							new FrameScores(ListCdf.this.getSelectedValue());
						}
					});
					menu.add(menuItem);

				} else {
					menuItem = new JMenuItem(bundle.getString("ListCdf.deleteAll"), new ImageIcon(getClass().getResource(ICON_TRASH)));
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							ListCdf.this.getModel().clearList();
							ListCdf.this.clearSelection();
							PanelCDF.razUI();
						}
					});
					menu.add(menuItem);
				}

				menu.show(e.getComponent(), e.getX(), e.getY());
			} else if (e.isPopupTrigger() && ListCdf.this.getModel().getSize() > 0 && ListCdf.this.getSelectedIndices().length == 2) {
				final JPopupMenu menu = new JPopupMenu();
				JMenuItem menuItem;

				menuItem = new JMenuItem(bundle.getString("ListCdf.comparFile"), new ImageIcon(getClass().getResource(ICON_COMPARAISON)));
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						final Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {

								Ihm.getProgressPanel().setText("Comparaison en cours...");
								Ihm.getProgressPanel().setVisible(true);

								Cdf cdfCompar = CdfUtils.comparCdf(ListCdf.this.getModel().getElementAt(ListCdf.this.getSelectedIndices()[0]),
										ListCdf.this.getModel().getElementAt(ListCdf.this.getSelectedIndices()[1]),
										PanelCDF.getRadiobtval().isSelected());

								Ihm.getProgressPanel().setText(null);
								Ihm.getProgressPanel().setVisible(false);

								if (cdfCompar != null) {
									ListCdf.this.getModel().addCdf(cdfCompar);
									JOptionPane.showMessageDialog(null, "Comparaison terminee !", null, JOptionPane.INFORMATION_MESSAGE);
								} else {
									JOptionPane.showMessageDialog(null, "Pas de difference de valeur entre les deux fichiers", null,
											JOptionPane.INFORMATION_MESSAGE);
								}
							}
						});
						thread.start();
					}
				});
				menu.add(menuItem);

				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class UpDownListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			final int moveMe = ListCdf.this.getSelectedIndex();

			if (bundle.getString("ListCdf.up").equals(e.getActionCommand())) {
				if (moveMe != 0) {
					swap(moveMe, moveMe - 1);
					ListCdf.this.setSelectedIndex(moveMe - 1);
					ListCdf.this.ensureIndexIsVisible(moveMe - 1);
				}
			} else {
				if (moveMe != ListCdf.this.getModel().getSize() - 1) {
					swap(moveMe, moveMe + 1);
					ListCdf.this.setSelectedIndex(moveMe + 1);
					ListCdf.this.ensureIndexIsVisible(moveMe + 1);
				}
			}
		}
	}

	private final void swap(int a, int b) {
		final Cdf aObject = getModel().getElementAt(a);
		final Cdf bObject = getModel().getElementAt(b);
		getModel().set(a, bObject);
		getModel().set(b, aObject);
	}

	private final class ExportListener implements ActionListener {

		private final FileFormat fileFormat;

		public ExportListener(FileFormat fileFormat) {
			this.fileFormat = fileFormat;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
			fileChooser.setDialogTitle("Enregistement du fichier");
			fileChooser.setFileFilter(new FileNameExtensionFilter(fileFormat.getExtensionDesc(), fileFormat.getExtension()));
			fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + "." + fileFormat.getExtension()));
			final int rep = fileChooser.showSaveDialog(null);

			if (rep == JFileChooser.APPROVE_OPTION) {

				boolean result = false;
				boolean transpose = false;

				if (!fileChooser.getSelectedFile().exists()) {

					switch (fileFormat) {
					case TXT:
						result = CdfUtils.toText(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
						break;
					case XLS:
						result = CdfUtils.toExcel(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
						break;
					case M:

						if (JOptionPane.showConfirmDialog(null, "Transposer pour Matlab?", null,
								JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
							transpose = true;
						}
						result = CdfUtils.toM(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile(), transpose);
						break;
					case HTML:
						result = CdfUtils.toHtml(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
						break;
					default:
						break;
					}

				} else {

					switch (JOptionPane.showConfirmDialog(null, "Le fichier existe deja, ecraser?", null, JOptionPane.INFORMATION_MESSAGE)) {
					case JOptionPane.OK_OPTION:

						switch (fileFormat) {
						case TXT:
							result = CdfUtils.toText(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
							break;
						case XLS:
							result = CdfUtils.toExcel(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
							break;
						case M:
							result = CdfUtils.toM(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile(), transpose);
							break;
						case HTML:
							result = CdfUtils.toHtml(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
							break;
						default:
							break;
						}
						break;
					case JOptionPane.NO_OPTION:
						this.actionPerformed(e);
						return;
					default:
						break;
					}
				}

				if (result) {

					final int reponse = JOptionPane.showConfirmDialog(null,
							"Export termine !\n" + fileChooser.getSelectedFile() + "\nVoulez-vous ouvrir le fichier?", null,
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					switch (reponse) {
					case JOptionPane.OK_OPTION:
						try {
							if (Desktop.isDesktopSupported()) {
								Desktop.getDesktop().open(fileChooser.getSelectedFile());
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					case JOptionPane.NO_OPTION:
						break;
					default:
						break;
					}
				} else {
					JOptionPane.showMessageDialog(null, "Export abandonne !");
				}
			}
		}
	}
}
