package visu;

import java.awt.Color;
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
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.nimbus.NimbusStyle;
import javax.swing.plaf.synth.SynthLookAndFeel;

import tools.Preference;

public class SWToolsMain {

	private static File logFile = null;
	private static Logger logger = Logger.getLogger("MyLogger");
	private static Handler fileHandler;

	public static void main(String[] args) {

		try {
			
			//SynthLookAndFeel synth = new SynthLookAndFeel();
			//synth.load(SWToolsMain.class.getResourceAsStream("/synthDemo.xml"), SWToolsMain.class);
			//UIManager.setLookAndFeel(synth);

			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (Preference.getPreference(Preference.KEY_NOM_LF).equals(info.getName())) {
					try {
						paramUI(info.getName());
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

	public static void paramUI(String lf)
	{
		switch (lf) {
		case "Windows":

			break;
		case "Metal":

			break;
		case "Nimbus":
			//UIManager.put("nimbusBase", Color.GRAY);
			//UIManager.put("nimbusBlueGrey", Color.GRAY);
			//UIManager.put("nimbusLightBackground", Color.LIGHT_GRAY);
			//UIManager.put("control", Color.GRAY);
			//UIManager.put("text", Color.WHITE);
			//UIManager.put("nimbusOrange", Color.GRAY);
			//UIManager.put("nimbusFocus", Color.WHITE);
			
			//UIManager.put("info", Color.BLACK);
			
			//UIManager.put("Label.foreground", new Color(100,100,100));
			//UIManager.put("TextField.foreground", Color.BLACK);
			break;

		}

	}

}
