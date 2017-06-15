package visu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.JFrame;
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

import chart.PanelXYPlot;
import chart.Serie;
import chart.SeriesCollection;
import chart.XYChart;
import paco.Curve;
import paco.ListModelLabel;
import paco.ListModelPaco;
import paco.Map;
import paco.PaCo;
import tools.Preference;
import tools.Utilitaire;

public final class PanelPaCo extends JPanel {

    private static final long serialVersionUID = 1L;

    // Constante
    private static final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";
    private static final String ICON_HISTORY = "/historique_32.png";
    private static final String ICON_CHART = "/graph_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    // GUI
    private static final JButton btOpen = new JButton("Ajouter PaCo(s)");
    private static final ListPaco listPaco = new ListPaco(new ListModelPaco());
    private static final ListLabel listLabel = new ListLabel(new ListModelLabel());
    private static final JPanel panVisu = new JPanel(new GridBagLayout());
    private static final PanelGraph panGraph = new PanelGraph();
    private static final JTabbedPane tabPan = new JTabbedPane();
    private static final JPanel panPaco = new JPanel(new GridBagLayout());
    private static final JPanel panLabel = new JPanel(new GridBagLayout());
    private static final JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panPaco, panLabel);
    private static final JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(panVisu), tabPan);
    private static final JSplitPane splitPaneGlobal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPaneLeft, splitPaneRight);
    private static final PanelHistory panelHistory = new PanelHistory();

    private ProgressMonitor pm;

    private File dtd;

    public PanelPaCo() {

        setLayout(new BorderLayout());

        panPaco.setMinimumSize(new Dimension(500, 300));
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
        btOpen.addActionListener(new OpenPaco());
        panPaco.add(btOpen, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        listPaco.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listPaco.isSelectionEmpty()) {
                    razUI();
                    listLabel.getModel().setList(listPaco.getSelectedValue().getListLabel());
                    listLabel.ensureIndexIsVisible(0);
                }
            }
        });
        panPaco.add(new JScrollPane(listPaco), gbc);

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

                    if (true) {
                        SeriesCollection seriesCollection = new SeriesCollection();
                        Serie serie;

                        if (listLabel.getSelectedValue() instanceof Curve) {
                            Curve curve = (Curve) listLabel.getSelectedValue();

                            serie = new Serie("Serie");
                            for (short x = 0; x < curve.getDimX(); x++) {
                                try {
                                    serie.addPoint(Double.parseDouble(curve.getValue(0, x)), Double.parseDouble(curve.getValue(1, x)));
                                } catch (NumberFormatException nfe) {
                                    serie.addPoint(x, Double.parseDouble(curve.getValue(1, x)));
                                }

                            }

                            seriesCollection.addSerie(serie);

                            JFrame frame = new JFrame("XY Plot");
                            frame.setLayout(new GridLayout(1, 1));

                            frame.add(new XYChart(new PanelXYPlot(curve.getShortName(), "X [" + curve.getUnitX() + "]",
                                    "Y [" + curve.getUnitZ() + "]", seriesCollection, true), XYChart.RIGHT_POSITION, false));

                            frame.pack();
                            frame.setVisible(true);
                        }

                        if (listLabel.getSelectedValue() instanceof Map) {
                            Map map = (Map) listLabel.getSelectedValue();

                            for (short x = 0; x < map.getDimX() - 1; x++) {

                                serie = new Serie(map.getxValues()[x]);

                                for (short y = 0; y < map.getDimY() - 1; y++) {

                                    try {
                                        serie.addPoint(Double.parseDouble(map.getyValues()[y]), Double.parseDouble(map.getzValue(y, x)));
                                    } catch (NumberFormatException nfe) {
                                        if (Utilitaire.isNumber(map.getzValue(y, x))) {
                                            serie.addPoint(y, Double.parseDouble(map.getzValue(y, x)));
                                        } else {
                                            serie.addPoint(y, Float.NaN);
                                        }
                                    }
                                }
                                seriesCollection.addSerie(serie);
                            }

                            JFrame frame = new JFrame("XY Plot");
                            frame.setLayout(new GridLayout(1, 1));

                            frame.add(new XYChart(new PanelXYPlot(map.getShortName(), "Y [" + map.getUnitY() + "]", "Z [" + map.getUnitZ() + "]",
                                    seriesCollection, true), XYChart.RIGHT_POSITION, true));

                            frame.pack();
                            frame.setVisible(true);
                        }
                    }

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

    private final class OpenPaco implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
            jFileChooser.setMultiSelectionEnabled(true);
            jFileChooser.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return "PaCo *.xml";
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    String extension = Utilitaire.getExtension(f);
                    if (extension.equals(Utilitaire.xml)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelPaCo.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {

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

                razUI();

                pm = new ProgressMonitor(PanelPaCo.this, "Fichier :", "...", 0, 0);
                pm.setMillisToDecideToPopup(0);
                pm.setMillisToPopup(0);

                new TaskCharging(jFileChooser.getSelectedFiles()).execute();
            }
        }

    }

    private final class TaskCharging extends SwingWorker<Integer, Integer> {

        private final File[] filesPaco;
        private int cnt = 0;
        private PaCo paco;

        public TaskCharging(File[] filesPaco) {
            this.filesPaco = filesPaco;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            pm.setMaximum(filesPaco.length);

            for (File file : filesPaco) {
                if (!(listPaco.getModel().getList().contains(file.getName().substring(0, file.getName().length() - 4)))) {
                    if (!pm.isCanceled()) {
                        paco = new PaCo(file);

                        if (paco.isValid()) {
                            listPaco.getModel().addPaco(paco);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "PaCo deja present dans la liste !" + "\nNom : " + file.getName().substring(0, file.getName().length() - 4), "INFO",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                cnt += 1;
                pm.setProgress(cnt);
                pm.setNote(file.getName().substring(0, file.getName().length() - 4));
            }

            if (listPaco.getSelectedIndices().length > 0)
                listPaco.clearSelection();

            return cnt;
        }
    }

    public final static void razUI() {

        if (listLabel.getModel().getSize() > 0) {
            listLabel.clearFilter();
            listLabel.clearSelection();
            listLabel.getModel().clearList();

            panelHistory.removeDatas();

            panVisu.removeAll();
            panVisu.revalidate();
            panVisu.repaint();

            panGraph.getPanCard().removeAll();
            panGraph.getPanCard().revalidate();
            panGraph.getPanCard().repaint();
        }

    }

}
