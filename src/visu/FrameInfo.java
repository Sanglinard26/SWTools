package visu;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class FrameInfo extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final JTextPane txtContact = new JTextPane();

	public FrameInfo() {
        this.setTitle("Info");
        this.setLayout(new GridLayout(2, 1));
        
        txtContact.setEditable(false);
        txtContact.setContentType("text/html");
        txtContact.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), "Contact"));
        txtContact.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	                sendMail(e.getURL().toString());
	                FrameInfo.this.dispose();
	            }
			}
		});
        txtContact.setText(
        		"Kevin RAILLON"
        		+ "<br> Service : DCTC/IVCT/PTCD/TCPR </br>"
        		+ "<br> Tel : 301479 </br>"
        		+ "<br> Bugs ou ameliorations ==> <a href='mailto:kevin.raillon1@mpsa.com'>Envoyer e-mail </br>");
        
        add(txtContact);
        
        this.pack();
        this.setVisible(true);

    }
	
	private void sendMail(String adresse)
	{
		if(Desktop.isDesktopSupported())
		{
			if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL))
			{
				adresse = adresse.replaceAll("mailto:", "");
				adresse += "?subject=[SW Tools] Bugs ou ameliorations";
				adresse += "&body=Bonjour,";
				try {
					Desktop.getDesktop().mail(new URI("mailto",adresse,null));
				} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
