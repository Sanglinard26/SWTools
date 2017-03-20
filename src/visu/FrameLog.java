/*
 * Creation : 9 mars 2017
 */
package visu;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public final class FrameLog extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextPane txtLog = new JTextPane();
    private BufferedReader brLog;

    public FrameLog() {
        this.setTitle("Log");
        this.setLayout(new BorderLayout());

        try {
            String line;
            StringBuilder sLog = new StringBuilder("");
            brLog = new BufferedReader(new FileReader(new File("C:/SwTools.log")));
            while ((line = brLog.readLine()) != null) {
                sLog.append("\n" + line);
            }
            txtLog.setText(sLog.toString());
            brLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(new JScrollPane(txtLog));

        this.setSize(400, 300);
        this.setVisible(true);
    }

}
