package visu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import cdf.Cdf;
import cdf.ListModelCdf;
import cdf.ListModelLabel;
import dcm.Dcm;
import paco.PaCo;
import tools.Preference;
import tools.Utilitaire;

public final class PanelCDF extends JPanel implements Observer {

    private static final long serialVersionUID = 1L;

    // Constante
    private static final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";
    private static final String ICON_HISTORY = "/historique_32.png";
    private static final String ICON_CHART = "/graph_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    // GUI
    private static final JButton btOpen = new JButton("Ajouter fichier(s) de donnees de calibration");
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

    private File dtd;

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

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        listCDF.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listCDF.isSelectionEmpty()) {
                    razUI();
                    listLabel.getModel().setList(listCDF.getSelectedValue().getListLabel());
                    listLabel.ensureIndexIsVisible(0);
                    listLabel.getFilterField().populateFilter(listCDF.getSelectedValue().getCategoryList());
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

                    panelHistory.setDatas(listLabel.getSelectedValue().getSwCsHistory());

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
                    panVisu.add(new PanelInfoVariable(listLabel.getSelectedValue()), gbc);

                    gbc.fill = GridBagConstraints.NONE;
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.gridheight = 1;
                    gbc.weightx = 1;
                    gbc.weighty = 1;
                    gbc.insets = new Insets(0, 10, 0, 0);
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START;

                    panVisu.add(listLabel.getSelectedValue().showValues(), gbc);
                    panVisu.revalidate();
                    panVisu.repaint();

                    panGraph.getPanCard().removeAll();
                    panGraph.createChart(listLabel.getSelectedValue());
                    panGraph.getPanCard().revalidate();
                    panGraph.getPanCard().repaint();
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
                    return "Fichier d'echange de donnees (*.xml), (*.dcm)";
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    String extension = Utilitaire.getExtension(f);
                    if (extension.equals(Utilitaire.xml) | extension.equals(Utilitaire.dcm)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelCDF.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {

                Boolean needDTD = false;

                for (File f : jFileChooser.getSelectedFiles()) {
                    if (Utilitaire.getExtension(f).equals("xml")) {
                        needDTD = true;
                        break;
                    }
                }

                if (needDTD) {
                    dtd = new File(jFileChooser.getSelectedFile().getParent() + "/" + DTD);
                    dtd.deleteOnExit();
                    if (!dtd.exists()) {
                        final InputStream myDtd = getClass().getResourceAsStream("/" + DTD);

                        try {
                            final OutputStream out = new FileOutputStream(jFileChooser.getSelectedFile().getParent() + "/" + DTD);
                            final byte[] buffer = new byte[1024];
                            int len = myDtd.read(buffer);
                            while (len != -1) {
                                out.write(buffer, 0, len);
                                len = myDtd.read(buffer);
                            }
                            myDtd.close();
                            out.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                razUI();

                pm = new ProgressMonitor(PanelCDF.this, "Fichier :", "...", 0, 0);
                pm.setMillisToDecideToPopup(0);
                pm.setMillisToPopup(0);

                new TaskCharging(jFileChooser.getSelectedFiles()).execute();
            }
        }

    }

    private final class TaskCharging extends SwingWorker<Integer, Integer> {

        private final File[] filesCDF;
        private int cnt = 0;
        private Cdf cdf;

        public TaskCharging(File[] filesPaco) {
            this.filesCDF = filesPaco;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            pm.setMaximum(filesCDF.length);
            pm.setProgress(cnt);

            for (File file : filesCDF) {
                if (!(listCDF.getModel().getList().contains(file.getName().substring(0, file.getName().length() - 4)))) {
                    if (!pm.isCanceled()) {

                        switch (Utilitaire.getExtension(file)) {
                        case "xml":
                            cdf = new PaCo(file, PanelCDF.this);

                            if (((PaCo) cdf).isValid()) {
                                listCDF.getModel().addCdf(cdf);
                            }
                            break;
                        case "dcm":
                            cdf = new Dcm(file, PanelCDF.this);

                            listCDF.getModel().addCdf(cdf);
                            break;
                        }

                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Fichier deja present dans la liste !" + "\nNom : " + file.getName().substring(0, file.getName().length() - 4), "INFO",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                cnt += 1;
                pm.setProgress(cnt);
            }

            if (listCDF.getSelectedIndices().length > 0)
                listCDF.clearSelection();

            return cnt;
        }
    }

    public final static void razUI() {

        if (listLabel.getModel().getSize() > 0) {
            listLabel.clearFilter();
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

    @Override
    public void update(String cdf, String variable, String rate) {
        pm.setNote(cdf + " : " + rate);
    }

}
