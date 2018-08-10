/*
 * Creation : 9 mars 2017
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public final class FrameLog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String FENETRE_ICON = "/log_icon_16.png";
    private static final String ADRESSE_MAIL = "kevin.raillon1@mpsa.com";

    private final JTextPane txtLog = new JTextPane();
    private final JButton btSendMail = new JButton("Envoyer le log");

    private WatchLog watchLog;
    private Timer timer;

    /** L'instance statique */
    private static FrameLog instance;

    /**
     * Récupère l'instance unique de la class Singleton.
     * <p>
     * Remarque : le constructeur est rendu inaccessible
     */
    public static FrameLog getInstance() {
        if (null == instance || !instance.isVisible()) { // Premier appel
            instance = new FrameLog();
        }
        return instance;
    }

    /**
     * Constructeur redéfini comme étant privé pour interdire son appel et forcer à passer par la méthode <link
     */
    private FrameLog() {

        this.setTitle("Log");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(FENETRE_ICON)));

        add(new JScrollPane(txtLog), BorderLayout.CENTER);
        add(btSendMail, BorderLayout.SOUTH);
        btSendMail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMail(txtLog.getText());
                FrameLog.this.dispose();
            }
        });

        loadLog();

        this.watchLog = new WatchLog();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(watchLog, 1000, 1000);

        this.setSize(400, 300);
        this.setVisible(true);
    }

    private final class WatchLog extends TimerTask {

        @Override
        public void run() {
            if (FrameLog.this.isVisible()) {
                loadLog();
            } else {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                    watchLog = null;
                }
            }

        }
    }

    public final void loadLog() {

        final BufferedReader brLog;

        try {
            String line;
            final StringBuilder sLog = new StringBuilder("");
            brLog = new BufferedReader(new FileReader(SWToolsMain.getLogFile()));
            while ((line = brLog.readLine()) != null) {
                sLog.append("\n" + line);
            }
            txtLog.setText(sLog.toString());
            brLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Nombre de caractere limite avec l'URI, à voir pour remplacer l'envoi de mail par l'API JavaMail.
    private static void sendMail(String texteLog) {
        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {

                final String adresse = ADRESSE_MAIL + "?subject=[SW Tools] Envoi du log" + "&body=Bonjour,\n\nVoici mon log :\n" + texteLog;

                try {
                    Desktop.getDesktop().mail(new URI("mailto", adresse, null));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
