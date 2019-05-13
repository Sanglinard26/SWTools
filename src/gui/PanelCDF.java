package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import cdf.Cdf;
import cdf.ListModelCdf;
import cdf.ListModelLabel;
import cdf.TypeVariable;
import cdf.Variable;
import cdfx.Cdfx;
import dcm.Dcm;
import matlab.M;
import paco.Paco;
import paco.StAXPaco;
import utils.Preference;
import utils.Utilitaire;

public final class PanelCDF extends JComponent {

	private static final long serialVersionUID = 1L;

	// Constante
	private static final String ICON_ADD = "/add_icon_32.png";
	private static final String ICON_HISTORY = "/historique_32.png";
	private static final String ICON_CHART = "/graph_32.png";
	private static final String ICON_MATH = "/math_icon_32.png";

	private static final GridBagConstraints gbc = new GridBagConstraints();

	private static Variable selVariable = null;

	private final static Locale lang = Preference.getPreference(Preference.KEY_LANGUAGE).equals("en") ? Locale.ENGLISH : Locale.FRENCH;
	private final static ResourceBundle bundle = ResourceBundle.getBundle("properties.langue", lang);

	// GUI
	private final JButton btOpen = new JButton(new ImageIcon(getClass().getResource(ICON_ADD)));
	private final JPanel panComparaison = new JPanel();
	private final ButtonGroup btGroup = new ButtonGroup();
	private static final JRadioButton radioBtVal = new JRadioButton(bundle.getString("showValues"), true);
	private final JRadioButton radioBtDiff = new JRadioButton(bundle.getString("showDiff"));
	private final ListCdf listCDF = new ListCdf(new ListModelCdf());
	private static final ListLabel listLabel = new ListLabel(new ListModelLabel());
	private static final JPanel panVisu = new JPanel(new GridBagLayout());
	private static final PanelGraph panGraph = new PanelGraph();
	private final JTabbedPane tabPan = new JTabbedPane();
	private final JPanel panCDF = new JPanel(new GridBagLayout());
	private final JPanel panLabel = new JPanel(new GridBagLayout());
	private final JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panCDF, panLabel);
	private final JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(panVisu), tabPan);
	private final JSplitPane splitPaneGlobal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitPaneLeft, splitPaneRight);
	private static final PanelHistory panelHistory = new PanelHistory();
	private static final PanelInterpolation panelInterpolation = new PanelInterpolation();

	public PanelCDF() {

		setLayout(new BorderLayout());

		panCDF.setMinimumSize(new Dimension(500, 300));
		panCDF.setBorder(BorderFactory.createEmptyBorder());
		panLabel.setMinimumSize(new Dimension(500, 300));
		panLabel.setBorder(BorderFactory.createEmptyBorder());

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		btOpen.setToolTipText(bundle.getString("addFiles"));
		btOpen.addActionListener(new OpenCDF());
		panCDF.add(btOpen, gbc);

		btGroup.add(radioBtVal);
		btGroup.add(radioBtDiff);
		panComparaison
		.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 0), bundle.getString("compar"), TitledBorder.LEADING, TitledBorder.BELOW_TOP));
		panComparaison.add(radioBtVal);
		panComparaison.add(radioBtDiff);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		panCDF.add(panComparaison, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		listCDF.setTransferHandler(new ListTransferHandler());
		listCDF.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && !listCDF.isSelectionEmpty()) {

					final Variable oldVar = listLabel.getSelectedValue();

					razUI();

					listLabel.getModel().setList(listCDF.getSelectedValue().getListLabel());
					listLabel.ensureIndexIsVisible(0);
					listLabel.getFilterField().populateFilter(listCDF.getSelectedValue().getCategoryList());

					if (oldVar != null && listLabel.getModel().getFilteredList().contains(oldVar)) {
						listLabel.setSelectedIndex(0);
						listLabel.setSelectedValue(oldVar, true);
					}
				}
			}
		});
		panCDF.add(new JScrollPane(listCDF), gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		panLabel.add(listLabel.getFilterField(), gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		listLabel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && !listLabel.isSelectionEmpty()) {

					selVariable = listLabel.getSelectedValue();

					panVisu.removeAll();

					gbc.fill = GridBagConstraints.HORIZONTAL;
					gbc.gridx = 0;
					gbc.gridy = 0;
					gbc.gridwidth = GridBagConstraints.REMAINDER;
					gbc.gridheight = 1;
					gbc.weightx = 1;
					gbc.weighty = 0;
					gbc.insets = new Insets(10, 10, 10, 0);
					gbc.anchor = GridBagConstraints.FIRST_LINE_START;
					panVisu.add(new PanelInfoVariable(selVariable), gbc);

					gbc.fill = GridBagConstraints.NONE;
					gbc.gridx = 0;
					gbc.gridy = 1;
					gbc.gridwidth = GridBagConstraints.REMAINDER;
					gbc.gridheight = 1;
					gbc.weightx = 1;
					gbc.weighty = 1;
					gbc.insets = new Insets(0, 10, 0, 0);
					gbc.anchor = GridBagConstraints.FIRST_LINE_START;

					panVisu.add(selVariable.showValues(), gbc);
					panVisu.revalidate();
					panVisu.repaint();

					switch (tabPan.getSelectedIndex()) {
					case 0:
						panelHistory.setDatas(selVariable.getSwCsHistory());
						break;
					case 1:
						panGraph.getPanCard().removeAll();
						panGraph.createChart(selVariable);
						panGraph.getPanCard().revalidate();
						panGraph.getPanCard().repaint();
						break;
					case 2:

						panelInterpolation.setVariable(selVariable);

						if (!panelInterpolation.isModeMap()) {
							panelInterpolation.calcZvalue();
						}

						break;
					default:
						break;
					}

				}
			}
		});
		panLabel.add(new JScrollPane(listLabel), gbc);

		splitPaneLeft.setOneTouchExpandable(true);
		splitPaneLeft.setDividerLocation(200);
		splitPaneLeft.setBorder(BorderFactory.createEmptyBorder());

		splitPaneRight.setOneTouchExpandable(true);
		splitPaneRight.setDividerLocation(400);
		splitPaneRight.setBorder(BorderFactory.createEmptyBorder());

		panVisu.setBackground(Color.WHITE);
		panVisu.setBorder(BorderFactory.createEmptyBorder());

		panGraph.setBorder(BorderFactory.createEmptyBorder());

		panelHistory.setBorder(BorderFactory.createEmptyBorder());

		tabPan.addTab(bundle.getString("remark"), new ImageIcon(getClass().getResource(ICON_HISTORY)), new JScrollPane(panelHistory));
		tabPan.addTab(bundle.getString("chart"), new ImageIcon(getClass().getResource(ICON_CHART)), panGraph);
		tabPan.addTab(bundle.getString("interpolation"), new ImageIcon(getClass().getResource(ICON_MATH)), panelInterpolation);

		tabPan.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (selVariable != null) {

					switch (((JTabbedPane) e.getSource()).getSelectedIndex()) {
					case 0:
						panelHistory.setDatas(selVariable.getSwCsHistory());
						break;
					case 1:
						panGraph.getPanCard().removeAll();
						panGraph.createChart(selVariable);
						panGraph.getPanCard().revalidate();
						panGraph.getPanCard().repaint();
						break;
					case 2:
						panelInterpolation.setVariable(selVariable);

						if (!panelInterpolation.isModeMap()) {
							panelInterpolation.calcZvalue();
						}

						break;
					default:
						break;
					}

				}
			}
		});

		splitPaneGlobal.setDividerSize(5);
		splitPaneGlobal.setDividerLocation(500);
		splitPaneGlobal.setBorder(BorderFactory.createEmptyBorder());
		add(splitPaneGlobal, BorderLayout.CENTER);
	}

	private final class OpenCDF implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
			jFileChooser.setMultiSelectionEnabled(true);
			jFileChooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Fichier d'echange de donnees (*.xml), (*.dcm), (*.m)";
				}

				@Override
				public boolean accept(File f) {

					if (f.isDirectory())
						return true;

					final String extension = Utilitaire.getExtension(f);
					if (extension.equals(Utilitaire.XML) || extension.equals(Utilitaire.DCM) || extension.equals(Utilitaire.M) || extension.equals(Utilitaire.CDFX)) {
						return true;
					}
					return false;
				}
			});

			final int reponse = jFileChooser.showOpenDialog(PanelCDF.this);
			if (reponse == JFileChooser.APPROVE_OPTION) {

				new TaskCharging(jFileChooser.getSelectedFiles()).execute();

			}
		}

	}

	private final class TaskCharging extends SwingWorker<Integer, Integer> {

		private static final int MAXSIZE = 10000;
		private File[] filesCDF;
		private Cdf cdf;
		private final StringBuilder cdfName = new StringBuilder();

		public TaskCharging(File[] filesPaco) {
			this.filesCDF = filesPaco;

		}

		@Override
		protected Integer doInBackground() throws Exception {

			Ihm.getProgressPanel().setVisible(true);

			for (File file : filesCDF) {

				cdfName.setLength(0);
				cdfName.append(file.getName().substring(0, file.getName().length() - 4));

				Ihm.getProgressPanel().setText(cdfName.toString());

				if (!(ListModelCdf.getListcdfname().contains(cdfName.toString()))) {

					switch (Utilitaire.getExtension(file)) {
					case "xml":

						if (Preference.getPreference(Preference.KEY_XML_PARSEUR).equals("DOM") && (file.length() / 1024) + 1 < MAXSIZE) {
							cdf = new Paco(file);
						} else {
							cdf = new StAXPaco(file);
						}
						break;
					case "cdfx":
						cdf = new Cdfx(file);
						break;
					case "dcm":
						cdf = new Dcm(file);
						break;
					case "m":
						cdf = new M(file);
						break;
					default:
						break;
					}

					if (cdf != null && cdf.isValid()) {
						listCDF.getModel().addCdf(cdf);
					}

				} else {
					JOptionPane.showMessageDialog(null, bundle.getString("cdfAlreadyExist") + cdfName, "INFO", JOptionPane.INFORMATION_MESSAGE);
				}
			}

			Ihm.getProgressPanel().setText(null);
			Ihm.getProgressPanel().setVisible(false);

			filesCDF = null;
			cdf = null;

			return 0;
		}

	}

	private final class ListTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean canImport(TransferSupport info) {
			return info.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(TransferSupport info) {

			if (!info.isDrop()) {
				return false;
			}

			final Transferable objetTransfer = info.getTransferable();

			List<File> dropFiles;
			try {
				dropFiles = (List<File>) objetTransfer.getTransferData(DataFlavor.javaFileListFlavor);

				new TaskCharging(dropFiles.toArray(new File[dropFiles.size()])).execute();

			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		}
	}

	public final static void razUI() {

		if (listLabel.getModel().getSize() > 0) {

			selVariable = null;

			listLabel.clearSelection();
			listLabel.getModel().clearList();
			listLabel.getFilterField().populateFilter(EnumSet.noneOf(TypeVariable.class));

			panelHistory.removeDatas();

			panVisu.removeAll();
			panVisu.revalidate();
			panVisu.repaint();

			panGraph.getPanCard().removeAll();
			panGraph.getPanCard().revalidate();
			panGraph.getPanCard().repaint();

		}

	}

	public static final Variable getSelVariable() {
		return selVariable;
	}

	public static final JRadioButton getRadiobtval() {
		return radioBtVal;
	}

}
