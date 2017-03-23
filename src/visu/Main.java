package visu;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    private static File logFile = null;
    private static Logger logger = Logger.getLogger("MyLogger");
    private static Handler fileHandler;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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

        final Ihm ihm = new Ihm();
        ihm.setVisible(true);

    }

    public static Logger getLogger() {
        return logger;
    }

    public static File getLogFile() {
        return logFile;
    }

}
