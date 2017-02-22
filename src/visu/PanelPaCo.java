package visu;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import paco.ListModelLabel;
import paco.PaCo;
import paco.TableModelHistory;
import tools.Preference;
import tools.Utilitaire;

public final class PanelPaCo extends JPanel implements Observer {

    private static final long serialVersionUID = 1L;

    // Constante
    private static final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";
    private static final String WARNING = "/warning_32.png";
    private static final String ICON_HISTORY = "/historique_32.png";
    private static final String ICON_CHART = "/graph_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    // GUI
    private final JButton btOpen;
    private final JLabel labelNomPaCo;
    private final JTextField txtFiltre;
    private final ListLabel listLabel;
    private JPanel panVisu;
    private static final PanelGraph panGraph = new PanelGraph();
    private final TableHistory tableHistory;
    private final JProgressBar barChargement = new BarreProgression();

    // PaCo
    private File dtd;
    private PaCo paco;

    public PanelPaCo() {

        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        btOpen = new JButton(new AbstractAction("Ouvrir") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
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

                int reponse = jFileChooser.showOpenDialog(PanelPaCo.this);
                if (reponse == JFileChooser.APPROVE_OPTION) {
                    dtd = new File(jFileChooser.getSelectedFile().getParent() + "/" + DTD);
                    if (!dtd.exists()) {
                        InputStream myDtd = getClass().getResourceAsStream("/" + DTD);

                        try {
                            OutputStream out = new FileOutputStream(jFileChooser.getSelectedFile().getParent() + "/" + DTD);
                            byte[] buffer = new byte[1024];
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

                    panVisu.removeAll();
                    barChargement.setString("...");
                    barChargement.setValue(0);

                    new ReaderPaCo(jFileChooser.getSelectedFile()).start();
                }
            }
        });
        add(btOpen, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        labelNomPaCo = new JLabel("Nom : ");
        labelNomPaCo.setFont(new Font(null, Font.BOLD, 14));
        add(labelNomPaCo, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        txtFiltre = new JTextField(20);
        txtFiltre.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                listLabel.getModel().setFilter(txtFiltre.getText().toLowerCase());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                listLabel.getModel().setFilter(txtFiltre.getText().toLowerCase());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Non utilise

            }
        });
        add(txtFiltre, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        listLabel = new ListLabel(new ListModelLabel());
        listLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() & listLabel.getModel().getSize() > 0) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem menuItem;
                    menuItem = new JMenuItem("Exporter le PaCo en txt");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
                            fileChooser.setDialogTitle("Enregistement du PaCo");
                            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier texte (*.txt)", "txt"));
                            fileChooser.setSelectedFile(new File(".txt"));
                            int rep = fileChooser.showSaveDialog(null);

                            if (rep == JFileChooser.APPROVE_OPTION) {
                                paco.exportToTxt(fileChooser.getSelectedFile());
                            }

                        }
                    });
                    menu.add(menuItem);
                    menuItem = new JMenuItem("Exporter le PaCo en xls");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (true) {
                                JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
                                fileChooser.setDialogTitle("Enregistement du PaCo");
                                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier Excel (*.xls)", "xls"));
                                fileChooser.setSelectedFile(new File(paco.getName() + ".xls"));
                                int rep = fileChooser.showSaveDialog(null);

                                if (rep == JFileChooser.APPROVE_OPTION) {
                                    paco.exportToExcel(fileChooser.getSelectedFile());
                                }
                            }

                        }
                    });
                    menu.add(menuItem);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        listLabel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() & !listLabel.isSelectionEmpty()) {
                    tableHistory.getModel().setData(listLabel.getSelectedValue().getSwCsHistory());
                    panVisu.removeAll();
                    panVisu.add(listLabel.getSelectedValue().showView());
                    panVisu.revalidate();
                    panVisu.repaint();

                    panGraph.getPan3D().removeAll();
                    panGraph.createChart(listLabel.getSelectedValue());
                    panGraph.getPan3D().revalidate();
                    panGraph.getPan3D().repaint();

                    panGraph.getPan2D().removeAll();
                    panGraph.createXYChart(listLabel.getSelectedValue());
                    panGraph.getPan2D().revalidate();
                    panGraph.getPan2D().repaint();
                }
            }
        });
        add(new JScrollPane(listLabel), gbc);

        panVisu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panVisu.setBackground(Color.WHITE);

        tableHistory = new TableHistory(new TableModelHistory());
        tableHistory.setFillsViewportHeight(false);

        JTabbedPane tabPan = new JTabbedPane();
        tabPan.addTab("Historique", new ImageIcon(getClass().getResource(ICON_HISTORY)), new JScrollPane(tableHistory));
        tabPan.addTab("Graphique", new ImageIcon(getClass().getResource(ICON_CHART)), panGraph);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 4;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        // JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(panVisu), new JScrollPane(tableHistory));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(panVisu), tabPan);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(400);
        add(splitPane, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 5, 0, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        barChargement.setStringPainted(true);
        barChargement.setString("Aucun label");
        barChargement.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(barChargement, gbc);
    }

    @Override
    public void update(Observable o, Object arg) {
        barChargement.setString(arg.toString() + " label(s)");
        barChargement.setMaximum((((PaCo) o).getNbLabel()));
        barChargement.setValue((int) arg);
    }

    private class ReaderPaCo extends Thread {

        private File file;

        public ReaderPaCo(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            paco = new PaCo(file, PanelPaCo.this);
            labelNomPaCo.setText("Nom : " + paco.getName());
            if (paco.checkName()) {
                labelNomPaCo.setToolTipText(null);
                labelNomPaCo.setIcon(null);
            } else {
                labelNomPaCo.setToolTipText("Incoherence de nom");
                labelNomPaCo.setIcon(new ImageIcon(getClass().getResource(WARNING)));
            }

            listLabel.getModel().setList(paco.getListLabel());
            listLabel.clearSelection();
            tableHistory.getModel().setData(new String[0][0]);

            dtd.delete();
        }
    }

}
