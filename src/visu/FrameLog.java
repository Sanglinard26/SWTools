/*
 * Creation : 9 mars 2017
 */
package visu;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public final class FrameLog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String ADRESSE_MAIL = "kevin.raillon1@mpsa.com";

    private final JTextPane txtLog = new JTextPane();
    private final JButton btSendMail = new JButton("Envoyer le log");
    private BufferedReader brLog;

    public FrameLog() {
        this.setTitle("Log");
        this.setLayout(new BorderLayout());

        try {
            String line;
            StringBuilder sLog = new StringBuilder("");
            brLog = new BufferedReader(new FileReader(Main.getLogFile()));
            while ((line = brLog.readLine()) != null) {
                sLog.append("\n" + line);
            }
            txtLog.setText(sLog.toString());
            brLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(new JScrollPane(txtLog), BorderLayout.CENTER);
        add(btSendMail, BorderLayout.SOUTH);
        btSendMail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                sendMail(txtLog.getText());
                FrameLog.this.dispose();
            }
        });

        this.setSize(400, 300);
        this.setVisible(true);
    }

    // Nombre de caractere limite avec l'URI, à voir pour remplacer l'envoi de mail par l'API JavaMail.
    private static void sendMail(String texteLog) {
        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {

                String adresse = ADRESSE_MAIL + "?subject=[SW Tools] Bugs ou ameliorations" + "&body=Bonjour,\n\nVoici mon log :\n" + texteLog;

                try {
                    Desktop.getDesktop().mail(new URI("mailto", adresse, null));
                } catch (IOException | URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
