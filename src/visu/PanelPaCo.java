package visu;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import paco.PaCo;
import tools.Utilitaire;

public final class PanelPaCo extends JPanel {
	
	public PanelPaCo() {
		
		
		JButton btOpen = new JButton(new AbstractAction("Ouvrir") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						return "PaCo *.xml";
					}
					
					@Override
					public boolean accept(File f) {
						
						if (f.isDirectory()) return true;
						
						String extension = Utilitaire.getExtension(f);
						if (extension.equals(Utilitaire.xml))
						{
							return true;
						}
						return false;
					}
				});
				
				int reponse = jFileChooser.showOpenDialog(PanelPaCo.this);
				if (reponse == JFileChooser.APPROVE_OPTION)
				{
					new PaCo(jFileChooser.getSelectedFile().getPath());
				}
			}
		});
		add(btOpen);
	}

}
