/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import bdd.BddConnexion;

public final class PanelBdd extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final JButton btNew = new JButton("Creer une nouvelle BDD");

    private BddConnexion bdConnection = null;

    public PanelBdd() {

        btNew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bdConnection = new BddConnexion("myDbTest.db");
                bdConnection.connectBdd();

                bdConnection.closeBdd();
            }
        });
        add(btNew);

    }

}
