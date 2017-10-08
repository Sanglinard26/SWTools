/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import bdd.BddManager;

public final class PanelBdd extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private static final JButton btNew = new JButton("Créer une nouvelle BDD");
    
    public PanelBdd() {
    	
    	btNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BddManager.createBdd("toto.db");
				
			}
		});
    	add(btNew);
    	
	}

}
