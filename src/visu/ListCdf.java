/*
 * Creation : 6 avr. 2017
 */
package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.rmi.CORBA.Util;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cdf.Cdf;
import cdf.ListModelCdf;
import dcm.Dcm;
import paco.PaCo;
import tools.Preference;
import tools.Utilitaire;

public final class ListCdf extends JList<Cdf> implements KeyListener {

	private static final long serialVersionUID = 1L;
	private static final String ICON_EXCEL = "/excel_icon_16.png";
	private static final String ICON_TEXT = "/text_icon_16.png";
	private static final String ICON_MATLAB = "/matlab_icon_16.png";
	private static final String ICON_TRASH = "/corbeille_icon_16.png";
	private static final String ICON_COMPARAISON = "/comparaison_icon_16.png";
	private static final String ICON_UP = "/up_icon_16.png";
	private static final String ICON_DOWN = "/down_icon_16.png";

	public ListCdf(ListModelCdf dataModel) {
		super(dataModel);
		setCellRenderer(new ListCdfRenderer());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addKeyListener(this);
		addMouseListener(new ListMouseListener());

		// Activatation DnD
		setDropMode(DropMode.INSERT);
		setTransferHandler(new ListTransferHandler());
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		
		DropLocation loc= getDropLocation();
	    if (loc == null) {
	    	setBorder(null);
	        return;
	    }
	    
	    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.LIGHT_GRAY, Color.GRAY));
	}


	private final class ListTransferHandler extends TransferHandler
	{
		private static final long serialVersionUID = 1L;

		Boolean needDTD = false;
		
		@Override
		public boolean canImport(TransferSupport info) {

			if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}

			return true;
		}

		@Override
		public boolean importData(TransferSupport info) {

			if(!info.isDrop())
			{
				return false;
			}

			Transferable objetTransfer = info.getTransferable();

			List<File> dropFiles;
			try {
				dropFiles = (List<File>) objetTransfer.getTransferData(DataFlavor.javaFileListFlavor);

				for(int nFile = 0; nFile<dropFiles.size(); nFile++)
				{
					if (Utilitaire.getExtension(dropFiles.get(nFile)).equals("xml")) {
						needDTD = true;
						break;
					}
				}

				if (needDTD) {
					Utilitaire.createDtd(dropFiles.get(0).getParent());
				}

				for(int nFile = 0; nFile<dropFiles.size(); nFile++)
				{
					switch(Utilitaire.getExtension(dropFiles.get(nFile)))
					{
					case "xml":
						getModel().addCdf(new PaCo(dropFiles.get(nFile), null));
						break;
					case "dcm":
						getModel().addCdf(new Dcm(dropFiles.get(nFile), null));
						break;
					}

				}

			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		}
	}



	@Override
	public ListModelCdf getModel() {
		return (ListModelCdf) super.getModel();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 127 & this.getSelectedIndex() > -1) // touche suppr
		{
			for (int idx : this.getSelectedIndices()) {
				this.getModel().removeCdf(this.getSelectedIndex());
			}

			this.clearSelection();
			PanelCDF.razUI();
		}

		int moveMe = ListCdf.this.getSelectedIndex();

		if (e.isControlDown() & e.getKeyCode() == KeyEvent.VK_UP) {

			if (moveMe != 0) {
				swap(moveMe, moveMe - 1);
				ListCdf.this.setSelectedIndex(moveMe - 1);
				ListCdf.this.ensureIndexIsVisible(moveMe - 1);
			}

		}

		if (e.isControlDown() & e.getKeyCode() == KeyEvent.VK_DOWN) {

			if (moveMe != getModel().getSize() - 1) {
				swap(moveMe, moveMe + 1);
				ListCdf.this.setSelectedIndex(moveMe + 1);
				ListCdf.this.ensureIndexIsVisible(moveMe + 1);
			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private final class ListMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() & ListCdf.this.getModel().getSize() > 0 & ListCdf.this.getSelectedIndices().length <= 1) {
				final JPopupMenu menu = new JPopupMenu();
				final JMenu menuMove = new JMenu("Deplacer");
				final JMenu menuExport = new JMenu("Export");
				JMenuItem menuItem;
				if (ListCdf.this.locationToIndex(e.getPoint()) == ListCdf.this.getSelectedIndex()) {

					menuItem = new JMenuItem("Supprimer ce fichier", new ImageIcon(getClass().getResource(ICON_TRASH)));
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

					menuItem = new JMenuItem("Monter", new ImageIcon(getClass().getResource(ICON_UP)));
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));
					menuItem.addActionListener(new UpDownListener());
					menuMove.add(menuItem);

					menuMove.addSeparator();

					menuItem = new JMenuItem("Descendre", new ImageIcon(getClass().getResource(ICON_DOWN)));
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
					menuItem.addActionListener(new UpDownListener());
					menuMove.add(menuItem);

					menuItem = new JMenuItem("Exporter le fichier en txt", new ImageIcon(getClass().getResource(ICON_TEXT)));
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
							fileChooser.setDialogTitle("Enregistement du fichier");
							fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier texte (*.txt)", "txt"));
							fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".txt"));
							final int rep = fileChooser.showSaveDialog(null);

							if (rep == JFileChooser.APPROVE_OPTION) {
								if (ListCdf.this.getSelectedValue().exportToTxt(fileChooser.getSelectedFile())) {
									JOptionPane.showMessageDialog(null, "Export termine !");
								} else {
									JOptionPane.showMessageDialog(null, "Export abandonne !");
									fileChooser.getSelectedFile().delete();
								}
							}

						}
					});
					menuExport.add(menuItem);

					menuExport.addSeparator();
					menuItem = new JMenuItem("Exporter le fichier en xls", new ImageIcon(getClass().getResource(ICON_EXCEL)));
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							if (true) {
								final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
								fileChooser.setDialogTitle("Enregistement du fichier");
								fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier Excel (*.xls)", "xls"));
								fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".xls"));
								final int rep = fileChooser.showSaveDialog(null);

								if (rep == JFileChooser.APPROVE_OPTION) {
									if (ListCdf.this.getSelectedValue().exportToExcel(fileChooser.getSelectedFile())) {
										JOptionPane.showMessageDialog(null, "Export termine !");
									} else {
										JOptionPane.showMessageDialog(null, "Export abandonne !");
										fileChooser.getSelectedFile().delete();
									}
								}
							}

						}
					});
					menuExport.add(menuItem);

					menuExport.addSeparator();
					menuItem = new JMenuItem("Exporter le fichier en m", new ImageIcon(getClass().getResource(ICON_MATLAB)));
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							if (true) {
								final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
								fileChooser.setDialogTitle("Enregistement du fichier");
								fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier m (*.m)", ".m"));
								fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".m"));
								final int rep = fileChooser.showSaveDialog(null);

								if (rep == JFileChooser.APPROVE_OPTION) {
									if (ListCdf.this.getSelectedValue().exportToM(fileChooser.getSelectedFile())) {
										JOptionPane.showMessageDialog(null, "Export termine !");
									} else {
										JOptionPane.showMessageDialog(null, "Export abandonne !");
										fileChooser.getSelectedFile().delete();
									}
								}
							}

						}
					});
					menuExport.add(menuItem);

					menu.add(menuMove);
					menu.addSeparator();
					menu.add(menuExport);

				} else {
					menuItem = new JMenuItem("Supprimer tous les fichiers", new ImageIcon(getClass().getResource(ICON_TRASH)));
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
			} else if (e.isPopupTrigger() & ListCdf.this.getModel().getSize() > 0 & ListCdf.this.getSelectedIndices().length == 2) {
				final JPopupMenu menu = new JPopupMenu();
				JMenuItem menuItem;

				menuItem = new JMenuItem("Comparer les deux fichiers", new ImageIcon(getClass().getResource(ICON_COMPARAISON)));
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Cdf cdfCompar = ListCdf.this.getModel().getElementAt(ListCdf.this.getSelectedIndices()[0]).comparCdf(
								ListCdf.this.getModel().getElementAt(ListCdf.this.getSelectedIndices()[1]), PanelCDF.getRadiobtval().isSelected());
						if (cdfCompar != null)
							ListCdf.this.getModel().addCdf(cdfCompar);
					}
				});
				menu.add(menuItem);

				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class UpDownListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			int moveMe = ListCdf.this.getSelectedIndex();

			if (e.getActionCommand().equals("Monter")) {
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
		Cdf aObject = getModel().getElementAt(a);
		Cdf bObject = getModel().getElementAt(b);
		getModel().set(a, bObject);
		getModel().set(b, aObject);
	}

}
