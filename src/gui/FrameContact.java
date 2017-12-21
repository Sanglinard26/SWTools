package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public final class FrameContact extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String FENETRE_ICON = "/contact_icon_16.png";
    private static final String ADRESSE_MAIL = "kevin.raillon1@mpsa.com";

    private final JTextPane txtContact = new JTextPane();

    public FrameContact() {
        this.setTitle("Contact");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(FENETRE_ICON)));
        this.setLayout(new BorderLayout());

        txtContact.setEditable(false);
        txtContact.setContentType("text/html");
        txtContact.setBorder(new EmptyBorder(10, 10, 10, 10));
        txtContact.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    sendMail();
                    FrameContact.this.dispose();
                }
            }
        });
        txtContact.setText("Kevin RAILLON" + "<br> Service : DCTC/IVCT/PTCD/TCPR </br>" + "<br> Tel : 301479 </br>"
                + "<br> Bugs ou ameliorations ==> <a href='mailto:kevin.raillon1@mpsa.com'>Envoyer e-mail </br>");

        add(txtContact);

        this.pack();
        this.setVisible(true);
    }

    private static void sendMail() {
        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {

                final String adresse = ADRESSE_MAIL + "?subject=[SW Tools] Bugs ou ameliorations" + "&body=Bonjour,";

                try {
                    Desktop.getDesktop().mail(new URI("mailto", adresse, null));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
