/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import bdd.BddConnexion;
import cdf.Cdf;
import dcm.Dcm;
import paco.PaCo;
import tools.Preference;
import tools.Utilitaire;

public final class PanelBdd extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";

    // Panel configuration
    private static final JPanel panelConfig = new JPanel();
    private static final JButton btNewFolder = new JButton("Creer un nouveau répertoire de stockage");
    private static final JTextField fieldDbName = new JTextField("DV5RC_D-MAP-REGULS", 20);

    // Panel table
    private static final JPanel panelTable = new JPanel();
    private static final JButton btNewBdd = new JButton("Creer/Connecter la BDD");
    private static final JButton btNewTable = new JButton("Creer une nouvelle table dans la BDD");
    private static final JPanel voyant = new JPanel();
    private static final JButton btEmptyTable = new JButton("Vider la table");
    private static final JButton btAddPaco = new JButton("Ajouter un PaCo");
    private static final JButton btModPaco = new JButton("Modifier un PaCo");
    private static final JButton btDelPaco = new JButton("Supprimer un PaCo");
    private static final JButton btVisu = new JButton("Visualiser la BDD");
    private static final JButton btCloseBdd = new JButton("Fermer la BDD");
    private static final DefaultComboBoxModel<String> modelCombo = new DefaultComboBoxModel<String>();
    private static final JComboBox<String> comboTable = new JComboBox<String>(modelCombo);

    private static final String[] columnNames = { "ID", "NOM", "NB LABELS", "SCORE MINI", "SCORE MAXI" };
    private String[][] data = new String[0][0];
    private static JTable tabVisu;
    private static DefaultTableModel model;

    private Connection bdConnection = null;

    private File dtd;

    private static final Boolean AUTO_IMPORT = true;

    public PanelBdd() {

        setLayout(new BorderLayout());

        panelConfig.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Configuration"));
        add(panelConfig, BorderLayout.NORTH);

        btNewFolder.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int reponse = fc.showOpenDialog(PanelBdd.this);

                if (reponse == JFileChooser.APPROVE_OPTION)
                    try {
                        Preference.setPreference(Preference.KEY_PATH_FOLDER_DB,
                                Files.createDirectories(Paths.get(fc.getSelectedFile().getAbsolutePath() + "/SWTools/Database")).toString());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

            }
        });
        panelConfig.add(btNewFolder);

        panelConfig.add(fieldDbName);

        panelTable.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Base de données"));
        add(panelTable, BorderLayout.CENTER);

        btNewBdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BddConnexion.setDbPath(Preference.getPreference(Preference.KEY_PATH_FOLDER_DB) + "/" + fieldDbName.getText());
                bdConnection = BddConnexion.getInstance();
                if (bdConnection != null) {
                    voyant.setBackground(Color.GREEN);

                }

            }
        });
        panelTable.add(btNewBdd);

        voyant.setPreferredSize(new Dimension(20, 20));
        voyant.setBorder(new LineBorder(Color.BLACK));
        voyant.setBackground(Color.RED);
        panelTable.add(voyant);

        btEmptyTable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // bdConnection.emptyTable(comboTable.getSelectedItem().toString());
                new VisuTable().actionPerformed(null);
            }
        });
        panelTable.add(btEmptyTable);

        btNewTable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // bdConnection.createTable("PACO_" + Long.toString(Math.round(Math.random() * 100)));

            }
        });
        panelTable.add(btNewTable);

        btAddPaco.addActionListener(new OpenCDF());
        panelTable.add(btAddPaco);

        btModPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // bdConnection.modifPaCo(comboTable.getSelectedItem().toString(),
                // Integer.parseInt(tabVisu.getValueAt(tabVisu.getSelectedRow(), 0).toString()));
                new VisuTable().actionPerformed(null);

            }
        });
        panelTable.add(btModPaco);

        btDelPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row = 0; row < tabVisu.getSelectedRows().length; row++) {
                    System.out.println("id = " + tabVisu.getValueAt(tabVisu.getSelectedRows()[row], 0));
                    // bdConnection.deletePaCo(comboTable.getSelectedItem().toString(),Integer.parseInt(tabVisu.getValueAt(tabVisu.getSelectedRows()[row],
                    // 0).toString()));
                }

                new VisuTable().actionPerformed(null);

            }
        });
        panelTable.add(btDelPaco);

        btVisu.addActionListener(new VisuTable());
        panelTable.add(btVisu);

        btCloseBdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (bdConnection != null)
                    voyant.setBackground(Color.RED);

            }
        });
        panelTable.add(btCloseBdd);

        comboTable.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                new VisuTable().actionPerformed(null);

            }
        });
        panelTable.add(comboTable);

        tabVisu = new JTable(new DefaultTableModel(data, columnNames));
        model = (DefaultTableModel) tabVisu.getModel();
        JScrollPane scrollPane = new JScrollPane(tabVisu);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        panelTable.add(scrollPane);

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

            final int reponse = jFileChooser.showOpenDialog(PanelBdd.this);
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

                new TaskCharging(jFileChooser.getSelectedFiles()).execute();
            }
        }

    }

    private final class TaskCharging extends SwingWorker<Integer, Integer> {

        private final File[] filesCDF;
        private Cdf cdf;

        public TaskCharging(File[] filesPaco) {
            this.filesCDF = filesPaco;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            for (File file : filesCDF) {

                switch (Utilitaire.getExtension(file)) {
                case "xml":
                    cdf = new PaCo(file, null);
                    break;
                case "dcm":
                    cdf = new Dcm(file, null);
                    break;
                }
                // bdConnection.addPaCo(cdf, AUTO_IMPORT);
            }

            new VisuTable().actionPerformed(null);
            cdf = null;

            return 0;
        }
    }

    private final class VisuTable implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            /*
             * if (modelCombo.getSize() > 0) { ResultSet rs = bdConnection.query("SELECT * FROM " + comboTable.getSelectedItem().toString());
             * 
             * model.setRowCount(0);
             * 
             * String[] rowData = new String[5];
             * 
             * try { while (rs.next()) {
             * 
             * int id = rs.getInt("id"); String name = rs.getString("name"); int nblabel = rs.getInt("nblabel"); float minscore =
             * rs.getFloat("minscore"); float maxscore = rs.getFloat("maxscore");
             * 
             * rowData[0] = Integer.toString(id); rowData[1] = name; rowData[2] = Integer.toString(nblabel); rowData[3] = Float.toString(minscore);
             * rowData[4] = Float.toString(maxscore);
             * 
             * model.addRow(rowData);
             * 
             * } rs.close(); } catch (SQLException e1) { e1.printStackTrace(); } }
             */

        }

    }

}
