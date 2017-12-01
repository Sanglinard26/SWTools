package visu;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import tools.Preference;

public class SWToolsMain {

    private static File logFile = null;
    private static Logger logger = Logger.getLogger("MyLogger");
    private static Handler fileHandler;

    public static void main(String[] args) {

        try {

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (Preference.getPreference(Preference.KEY_NOM_LF).equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedLookAndFeelException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final Ihm ihm = new Ihm();
                ihm.setVisible(true);
            }
        });

        try {
            logFile = File.createTempFile("SwTools_", ".log");
            logFile.deleteOnExit();
            fileHandler = new FileHandler(logFile.getPath(), false);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Demarrage de l'application");

    }

    public static Logger getLogger() {
        return logger;
    }

    public static File getLogFile() {
        return logFile;
    }

}
