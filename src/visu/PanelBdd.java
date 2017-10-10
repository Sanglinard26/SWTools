/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import bdd.BddConnexion;

public final class PanelBdd extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final JButton btNewBdd = new JButton("Creer une nouvelle BDD");
    private static final JButton btNewTable = new JButton("Creer une nouvelle table dans la BDD");
    private static final JButton btAddPaco = new JButton("Ajouter un PaCo");
    private static final JButton btDelPaco = new JButton("Supprimer un PaCo");
    private static final JButton btVisu = new JButton("Visualiser la BDD");
    private static final JButton btCloseBdd = new JButton("Fermer la BDD");

    private static final String[] columnNames = { "ID", "NOM", "NB LABELS", "LISTE LABELS", "SCORE MINI", "SCORE MAXI" };
    private String[][] data = new String[0][0];
    private static JTable tabVisu;

    private BddConnexion bdConnection = null;

    public PanelBdd() {

        btNewBdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection = new BddConnexion("myDbTest.db");
                bdConnection.connectBdd();

            }
        });

        add(btNewBdd);

        btNewTable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection.createTable();

            }
        });
        add(btNewTable);

        btAddPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection.addPaCo();

            }
        });
        add(btAddPaco);

        add(btDelPaco);

        btVisu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ResultSet rs = bdConnection.query("SELECT * FROM paco");

                try {
                    while (rs.next()) {

                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        int nblabel = rs.getInt("nblabel");
                        String listlabel = rs.getString("listlabel");
                        float minscore = rs.getFloat("minscore");
                        float maxscore = rs.getFloat("maxscore");

                        System.out.println("ID = " + id);
                        System.out.println("Nom PaCo = " + name);
                        System.out.println("Nombre de label = " + nblabel);
                        System.out.println("Liste des labels = " + listlabel);
                        System.out.println("Score mini = " + minscore);
                        System.out.println("Score maxi = " + maxscore);
                        System.out.println();

                    }
                    rs.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        });
        add(btVisu);

        btCloseBdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection.closeBdd();

            }
        });
        add(btCloseBdd);

        tabVisu = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(tabVisu);
        add(scrollPane);

    }

}
