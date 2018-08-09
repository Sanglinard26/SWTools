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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public final class FrameLog extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String FENETRE_ICON = "/log_icon_16.png";
    private static final String ADRESSE_MAIL = "kevin.raillon1@mpsa.com";

    private final JTextPane txtLog = new JTextPane();
    private final JButton btSendMail = new JButton("Envoyer le log");

    private Thread logThread;

    public FrameLog() {

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

        watchLog();

        this.setSize(400, 300);
        this.setVisible(true);

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

    private final void watchLog() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                final Path path = SWToolsMain.getLogFile().getParentFile().toPath();
                try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
                    while (true) {

                        final WatchKey wk = watchService.take();
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            // we only register "ENTRY_MODIFY" so the context is always a Path.

                            final Path changed = (Path) event.context();

                            System.out.println(changed.toFile().exists());

                            if (changed.endsWith(SWToolsMain.getLogFile().getName())) {
                                loadLog();
                            }
                        }
                        // reset the key
                        boolean valid = wk.reset();
                    }
                } catch (IOException | InterruptedException e1) {
                    System.out.println(e1);
                    e1.printStackTrace();
                }

                return null;
            }
        };
        worker.execute();
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
