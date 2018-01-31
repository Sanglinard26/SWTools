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
import java.util.List;

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
import javax.swing.ProgressMonitor;
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
import cdf.Variable;
import dcm.Dcm;
import matlab.M;
import paco.Paco;
import paco.StAXPaco;
import tools.Preference;
import tools.Utilitaire;

public final class PanelCDF extends JComponent implements Observer {

    private static final long serialVersionUID = 1L;

    // Constante
    private static final String ICON_HISTORY = "/historique_32.png";
    private static final String ICON_CHART = "/graph_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    private static Variable selVariable;

    // GUI
    private static final JButton btOpen = new JButton("Ajouter fichier(s) de donnees de calibration");
    private static final JPanel panComparaison = new JPanel();
    private static final ButtonGroup btGroup = new ButtonGroup();
    private static final JRadioButton radioBtVal = new JRadioButton("Affichage des valeurs", true);
    private static final JRadioButton radioBtDiff = new JRadioButton("Affichage des differences (Work-Ref)");
    private static final ListCdf listCDF = new ListCdf(new ListModelCdf());
    private static final ListLabel listLabel = new ListLabel(new ListModelLabel());
    private static final JPanel panVisu = new JPanel(new GridBagLayout());
    private static final PanelGraph panGraph = new PanelGraph();
    private static final JTabbedPane tabPan = new JTabbedPane();
    private static final JPanel panCDF = new JPanel(new GridBagLayout());
    private static final JPanel panLabel = new JPanel(new GridBagLayout());
    private static final JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panCDF, panLabel);
    private static final JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(panVisu), tabPan);
    private static final JSplitPane splitPaneGlobal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitPaneLeft, splitPaneRight);
    private static final PanelHistory panelHistory = new PanelHistory();

    private ProgressMonitor pm;

    public PanelCDF() {

        setLayout(new BorderLayout());

        panCDF.setMinimumSize(new Dimension(500, 300));
        panLabel.setMinimumSize(new Dimension(500, 300));

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        btOpen.addActionListener(new OpenCDF());
        panCDF.add(btOpen, gbc);

        btGroup.add(radioBtVal);
        btGroup.add(radioBtDiff);
        panComparaison.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1), "Comparaison :", TitledBorder.LEADING, TitledBorder.BELOW_TOP));
        panComparaison.add(radioBtVal);
        panComparaison.add(radioBtDiff);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
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
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        listCDF.setTransferHandler(new ListTransferHandler());
        listCDF.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listCDF.isSelectionEmpty()) {

                    final Variable oldVar = listLabel.getSelectedValue();

                    razUI();

                    listLabel.getModel().setList(listCDF.getSelectedValue().getListLabel());
                    listLabel.ensureIndexIsVisible(0);
                    listLabel.getFilterField().populateFilter(listCDF.getSelectedValue().getCategoryList());

                    if (oldVar != null & listLabel.getModel().getList().contains(oldVar)) {
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
                if (!e.getValueIsAdjusting() & !listLabel.isSelectionEmpty()) {

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

                    if (tabPan.getSelectedIndex() == 0) {
                        panelHistory.setDatas(selVariable.getSwCsHistory());
                    } else {
                        panGraph.getPanCard().removeAll();
                        panGraph.createChart(selVariable);
                        panGraph.getPanCard().revalidate();
                        panGraph.getPanCard().repaint();
                    }

                }
            }
        });
        panLabel.add(new JScrollPane(listLabel), gbc);

        splitPaneLeft.setOneTouchExpandable(true);
        splitPaneLeft.setDividerLocation(200);

        splitPaneRight.setOneTouchExpandable(true);
        splitPaneRight.setDividerLocation(400);

        panVisu.setBackground(Color.WHITE);

        tabPan.addTab("Historique", new ImageIcon(getClass().getResource(ICON_HISTORY)), new JScrollPane(panelHistory));

        tabPan.addTab("Graphique", new ImageIcon(getClass().getResource(ICON_CHART)), panGraph);

        tabPan.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (selVariable != null) {
                    if (((JTabbedPane) e.getSource()).getSelectedIndex() == 0) {
                        panelHistory.setDatas(selVariable.getSwCsHistory());
                    } else {
                        panGraph.getPanCard().removeAll();
                        panGraph.createChart(selVariable);
                        panGraph.getPanCard().revalidate();
                        panGraph.getPanCard().repaint();
                    }
                }
            }
        });

        splitPaneGlobal.setDividerSize(10);
        splitPaneGlobal.setDividerLocation(500);
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
                    if (extension.equals(Utilitaire.XML) | extension.equals(Utilitaire.DCM) | extension.equals(Utilitaire.M)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelCDF.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {

                for (File f : jFileChooser.getSelectedFiles()) {
                    if (Utilitaire.getExtension(f).equals("xml")) {
                        Utilitaire.createDtd(jFileChooser.getSelectedFile().getParent());
                        break;
                    }
                }

                pm = new ProgressMonitor(PanelCDF.this, "Fichier :", "...", 0, 0);
                pm.setMillisToDecideToPopup(0);
                pm.setMillisToPopup(0);

                new TaskCharging(jFileChooser.getSelectedFiles()).execute();
            }
        }

    }

    private final class TaskCharging extends SwingWorker<Integer, Integer> {

        private File[] filesCDF;
        private int cnt = 0;
        private Cdf cdf;
        private final StringBuilder cdfName = new StringBuilder();

        private boolean stax = false;

        public TaskCharging(File[] filesPaco) {
            this.filesCDF = filesPaco;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            pm.setMaximum(filesCDF.length);
            pm.setProgress(cnt);

            if (Ihm.testMode) {
                int reponse = JOptionPane.showConfirmDialog(null, "Utiliser StAX?");
                if (reponse == JOptionPane.OK_OPTION) {
                    stax = true;
                }
            }

            for (File file : filesCDF) {

                cdfName.setLength(0);
                cdfName.append(file.getName().substring(0, file.getName().length() - 4));

                if (!(ListModelCdf.getListcdfname().contains(cdfName.toString()))) {
                    if (!pm.isCanceled()) {

                        switch (Utilitaire.getExtension(file)) {
                        case "xml":

                            if (stax) {
                                cdf = new StAXPaco(file);
                                listCDF.getModel().addCdf(cdf);
                            } else {
                                cdf = new Paco(file, PanelCDF.this);

                                if (((Paco) cdf).isValid()) {
                                    listCDF.getModel().addCdf(cdf);
                                }
                            }

                            break;
                        case "dcm":
                            cdf = new Dcm(file, PanelCDF.this);

                            listCDF.getModel().addCdf(cdf);
                            break;
                        case "m":
                            cdf = new M(file, PanelCDF.this);

                            listCDF.getModel().addCdf(cdf);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Fichier deja present dans la liste !" + "\nNom : " + cdfName, "INFO",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                cnt += 1;
                pm.setProgress(cnt);
            }

            filesCDF = null;
            cdf = null;

            return cnt;
        }

    }

    private final class ListTransferHandler extends TransferHandler {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean canImport(TransferSupport info) {

            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            return true;
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

                for (int nFile = 0; nFile < dropFiles.size(); nFile++) {
                    if (Utilitaire.getExtension(dropFiles.get(nFile)).equals("xml")) {
                        Utilitaire.createDtd(dropFiles.get(0).getParent());
                        break;
                    }
                }

                pm = new ProgressMonitor(PanelCDF.this, "Fichier :", "...", 0, 0);
                pm.setMillisToDecideToPopup(0);
                pm.setMillisToPopup(0);

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
            listLabel.getFilterField().populateFilter(null);

            panelHistory.removeDatas();

            panVisu.removeAll();
            panVisu.revalidate();
            panVisu.repaint();

            panGraph.getPanCard().removeAll();
            panGraph.getPanCard().revalidate();
            panGraph.getPanCard().repaint();

        }

    }

    public final static JRadioButton getRadiobtval() {
        return radioBtVal;
    }

    @Override
    public final void update(String cdf, String rate) {
        pm.setNote(cdf + " : " + rate);
    }

}
