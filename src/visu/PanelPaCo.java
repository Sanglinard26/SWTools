package visu;

import java.awt.Color;
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
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import paco.ListModelLabel;
import paco.ListModelPaco;
import paco.PaCo;
import paco.TableModelHistory;
import tools.Preference;
import tools.Utilitaire;

public final class PanelPaCo extends JPanel implements Observer {

    private static final long serialVersionUID = 1L;

    // Constante
    private static final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";
    private static final String ICON_HISTORY = "/historique_32.png";
    private static final String ICON_CHART = "/graph_32.png";

    private static final GridBagConstraints gbc = new GridBagConstraints();

    // GUI
    private static final JButton btOpen = new JButton("Ajouter PaCo(s)");
    private static final ListPaco listPaco = new ListPaco(new ListModelPaco());
    private static final JTextField txtFiltre = new JTextField(20);
    private static final ListLabel listLabel = new ListLabel(new ListModelLabel());
    private static final JPanel panVisu = new JPanel(new GridBagLayout());;
    private static final PanelGraph panGraph = new PanelGraph();
    private static final TableHistory tableHistory = new TableHistory(new TableModelHistory());
    private static final JProgressBar barChargement = new BarreProgression();
    private static final JTabbedPane tabPan = new JTabbedPane();
    private static final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(panVisu), tabPan);

    private File dtd;

    public PanelPaCo() {

        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        btOpen.addActionListener(new OpenPaco());
        add(btOpen, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        listPaco.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listPaco.isSelectionEmpty()) {
                    razUI();
                    listLabel.getModel().setList(listPaco.getSelectedValue().getListLabel());
                }

            }
        });
        add(new JScrollPane(listPaco), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
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
        listLabel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() & !listLabel.isSelectionEmpty()) {

                    Main.getLogger().info("Selection de la variable < " + listLabel.getSelectedValue().getShortName() + " >");

                    tableHistory.getModel().setData(listLabel.getSelectedValue().getSwCsHistory());
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
                    panVisu.add(listLabel.getSelectedValue().showView(), gbc);
                    panVisu.revalidate();
                    panVisu.repaint();

                    panGraph.getPanCard().removeAll();
                    panGraph.createChart(listLabel.getSelectedValue());
                    panGraph.getPanCard().revalidate();
                    panGraph.getPanCard().repaint();
                }
            }
        });
        add(new JScrollPane(listLabel), gbc);

        panVisu.setBackground(Color.WHITE);

        tableHistory.setFillsViewportHeight(false);

        tabPan.addTab("Historique", new ImageIcon(getClass().getResource(ICON_HISTORY)), new JScrollPane(tableHistory));
        tabPan.addTab("Graphique", new ImageIcon(getClass().getResource(ICON_CHART)), panGraph);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 6;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.anchor = GridBagConstraints.CENTER;
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

    private class OpenPaco implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
            jFileChooser.setMultiSelectionEnabled(true); // Test
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

                for (File file : jFileChooser.getSelectedFiles()) {
                    new ReaderPaCo(file).start();
                }
            }
        }

    }

    @Override
    public void update(final Observable o, final Object arg) {

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                barChargement.setString(arg.toString() + " label(s)");
                barChargement.setMaximum((((PaCo) o).getNbLabel()));
                barChargement.setValue((int) arg);

            }
        });

        SwingUtilities.invokeLater(t);

    }

    private final class ReaderPaCo extends Thread {

        private final File file;

        public ReaderPaCo(File file) {
            this.file = file;
            Main.getLogger().info("Ouverture de < " + file + " >");
        }

        @Override
        public void run() {
            listPaco.getModel().addPaco(new PaCo(file, PanelPaCo.this));
        }
    }

    private static void razUI() {

        if (listLabel.getModel().getSize() > 0) {
            txtFiltre.setText("");
            listLabel.clearSelection();
            listLabel.getModel().clearList();

            tableHistory.getModel().setData(new String[0][0]);

            panVisu.removeAll();
            panVisu.revalidate();
            panVisu.repaint();

            panGraph.getPanCard().removeAll();
            panGraph.getPanCard().revalidate();
            panGraph.getPanCard().repaint();

            barChargement.setString("...");
            barChargement.setValue(0);
        }

    }

}
