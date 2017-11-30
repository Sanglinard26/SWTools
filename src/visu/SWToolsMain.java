package visu;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SWToolsMain {

    private static File logFile = null;
    private static Logger logger = Logger.getLogger("MyLogger");
    private static Handler fileHandler;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // MetalLookAndFeel.setCurrentTheme(new DarkTheme());
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
