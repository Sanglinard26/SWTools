/*
 * Creation : 4 mai 2017
 */
package visu;

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
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
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

    private BddConnexion bdConnection = null;

    private File dtd;

    private static final Boolean AUTO_IMPORT = true;

    public PanelBdd() {

        btNewBdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection = new BddConnexion("jdbc:h2:C:/" + "DV5RC_D-MAP-REGULS");
                if (bdConnection.connectBdd()) {
                    voyant.setBackground(Color.GREEN);

                }

                modelCombo.removeAllElements();
                for (String tab : bdConnection.getListTable()) {
                    modelCombo.addElement(tab);
                }

            }
        });
        add(btNewBdd);

        voyant.setPreferredSize(new Dimension(20, 20));
        voyant.setBorder(new LineBorder(Color.BLACK));
        voyant.setBackground(Color.RED);
        add(voyant);

        btEmptyTable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection.emptyTable(comboTable.getSelectedItem().toString());
                new VisuTable().actionPerformed(null);
            }
        });
        add(btEmptyTable);

        btNewTable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection.createTable("PACO_" + Long.toString(Math.round(Math.random() * 100)));

            }
        });
        add(btNewTable);

        btAddPaco.addActionListener(new OpenCDF());
        add(btAddPaco);

        btModPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection.modifPaCo(comboTable.getSelectedItem().toString(),
                        Integer.parseInt(tabVisu.getValueAt(tabVisu.getSelectedRow(), 0).toString()));
                new VisuTable().actionPerformed(null);

            }
        });
        add(btModPaco);

        btDelPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row = 0; row < tabVisu.getSelectedRows().length; row++) {
                    System.out.println("id = " + tabVisu.getValueAt(tabVisu.getSelectedRows()[row], 0));
                    bdConnection.deletePaCo(comboTable.getSelectedItem().toString(),
                            Integer.parseInt(tabVisu.getValueAt(tabVisu.getSelectedRows()[row], 0).toString()));
                }

                new VisuTable().actionPerformed(null);

            }
        });
        add(btDelPaco);

        btVisu.addActionListener(new VisuTable());
        add(btVisu);

        btCloseBdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (bdConnection.closeBdd()) {
                    voyant.setBackground(Color.RED);
                }

            }
        });
        add(btCloseBdd);

        comboTable.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                new VisuTable().actionPerformed(null);

            }
        });
        add(comboTable);

        tabVisu = new JTable(new DefaultTableModel(data, columnNames));
        model = (DefaultTableModel) tabVisu.getModel();
        JScrollPane scrollPane = new JScrollPane(tabVisu);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        add(scrollPane);

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
                bdConnection.addPaCo(cdf, AUTO_IMPORT);
            }

            new VisuTable().actionPerformed(null);
            cdf = null;

            return 0;
        }
    }

    private final class VisuTable implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (modelCombo.getSize() > 0) {
                ResultSet rs = bdConnection.query("SELECT * FROM " + comboTable.getSelectedItem().toString());

                model.setRowCount(0);

                String[] rowData = new String[5];

                try {
                    while (rs.next()) {

                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        int nblabel = rs.getInt("nblabel");
                        String listlabel = rs.getString("listlabel");
                        float minscore = rs.getFloat("minscore");
                        float maxscore = rs.getFloat("maxscore");

                        rowData[0] = Integer.toString(id);
                        rowData[1] = name;
                        rowData[2] = Integer.toString(nblabel);
                        rowData[3] = Float.toString(minscore);
                        rowData[4] = Float.toString(maxscore);

                        model.addRow(rowData);

                    }
                    rs.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

}
