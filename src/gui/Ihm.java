package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.Preference;
import utils.Utilitaire;

public final class Ihm extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String APP_ICON = "/eeprom.png";
	private static final String ICON_FDONNEE = "/fdonnee_icon_32.png";
	private static final String ICON_FVARIABLE = "/fvariable_icon_32.png";
	private static final String ICON_LOG = "/log_icon_16.png";
	private static final String ICON_CONTACT = "/contact_icon_16.png";
	private static final String ICON_AIDE = "/manuel_icon_16.png";
	private static final String ICON_NEWS = "/new_icon_16.png";

	private final JMenuBar menuBar = new JMenuBar();
	
	private static final SingleComponentInfiniteProgress progressPanel = new SingleComponentInfiniteProgress(false);

	private static final JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
	private static final JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
	private static final JPanel ongletLab = new JPanel(new GridLayout(1, 1));

	private static PanelLab panelLab = null;

	public Ihm() {

		setTitle("SW Tools");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(APP_ICON)));
		setExtendedState(MAXIMIZED_BOTH);
		setMinimumSize(new Dimension(1200, 700));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// test glasspane
		setGlassPane(progressPanel);

		final Locale lang = Preference.getPreference(Preference.KEY_LANGUAGE).equals("en") ? Locale.ENGLISH : Locale.FRENCH;

		final ResourceBundle bundle = ResourceBundle.getBundle("properties.langue", lang);

		JMenu menu = new JMenu(bundle.getString("settings"));

		JMenu subMenu = new JMenu(bundle.getString("lang"));
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem radioMenuItem = new JRadioButtonMenuItem(bundle.getString("en"));
		radioMenuItem.addActionListener(new ClickLanguage());
		group.add(radioMenuItem);
		subMenu.add(radioMenuItem);
		radioMenuItem = new JRadioButtonMenuItem(bundle.getString("fr"));
		radioMenuItem.addActionListener(new ClickLanguage());
		group.add(radioMenuItem);
		subMenu.add(radioMenuItem);

		final Enumeration<AbstractButton> enumAb = group.getElements();
		AbstractButton ab;
		while (enumAb.hasMoreElements()) {
			ab = enumAb.nextElement();
			if (ab.getActionCommand().equals(bundle.getString(Preference.getPreference(Preference.KEY_LANGUAGE)))) {
				ab.setSelected(true);
				break;
			}
		}

		menu.add(subMenu);
		menu.addSeparator();

		final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(bundle.getString("colorMap"),
				Boolean.parseBoolean(Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP)));
		cbMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Preference.setPreference(Preference.KEY_ETAT_COLOR_MAP, Boolean.toString(cbMenuItem.isSelected()));
				if (PanelCDF.getSelVariable() != null) {
					PanelCDF.getSelVariable().showValues(); // Permet de prendre en compte la coloration directement, a voir pour trouver une solution
					// plus propre
				}

			}
		});
		menu.add(cbMenuItem);
		menu.addSeparator();

		subMenu = new JMenu(bundle.getString("theme"));
		ButtonGroup groupBis = new ButtonGroup();
		radioMenuItem = new JRadioButtonMenuItem("Windows");
		radioMenuItem.addActionListener(new ClickRadio());
		groupBis.add(radioMenuItem);
		subMenu.add(radioMenuItem);
		radioMenuItem = new JRadioButtonMenuItem("Metal");
		radioMenuItem.addActionListener(new ClickRadio());
		groupBis.add(radioMenuItem);
		subMenu.add(radioMenuItem);
		radioMenuItem = new JRadioButtonMenuItem("Nimbus");
		radioMenuItem.addActionListener(new ClickRadio());
		groupBis.add(radioMenuItem);
		subMenu.add(radioMenuItem);

		final Enumeration<AbstractButton> enumAbBis = groupBis.getElements();
		AbstractButton abBis;
		while (enumAbBis.hasMoreElements()) {
			abBis = enumAbBis.nextElement();
			if (abBis.getActionCommand().equals(Preference.getPreference(Preference.KEY_NOM_LF))) {
				abBis.setSelected(true);
				break;
			}
		}

		menu.add(subMenu);
		menu.addSeparator();

		subMenu = new JMenu(bundle.getString("filePath"));
		JMenuItem menuItem = new JMenuItem(new AbstractAction("Import fichier d'echange de donnees") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				final String pathFolder = Utilitaire.getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_OPEN_CDF));
				if (!Preference.KEY_OPEN_CDF.equals(pathFolder)) {
					Preference.setPreference(Preference.KEY_OPEN_CDF, pathFolder);
					((JMenuItem) e.getSource()).setToolTipText(pathFolder);
				}
			}
		});
		menuItem.setToolTipText(Preference.getPreference(Preference.KEY_OPEN_CDF));
		subMenu.add(menuItem);
		subMenu.addSeparator();

		menuItem = new JMenuItem(new AbstractAction("Import fichier de variable") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				final String pathFolder = Utilitaire.getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_ADD_LAB));
				if (!Preference.KEY_ADD_LAB.equals(pathFolder)) {
					Preference.setPreference(Preference.KEY_ADD_LAB, pathFolder);
					((JMenuItem) e.getSource()).setToolTipText(pathFolder);
				}
			}
		});
		menuItem.setToolTipText(Preference.getPreference(Preference.KEY_ADD_LAB));
		subMenu.add(menuItem);
		subMenu.addSeparator();

		menuItem = new JMenuItem(new AbstractAction("Export comparaison fichier de variable") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				final String pathFolder = Utilitaire.getFolder("Choix du chemin d'export des resultats",
						Preference.getPreference(Preference.KEY_RESULT_LAB));
				if (!Preference.KEY_RESULT_LAB.equals(pathFolder)) {
					Preference.setPreference(Preference.KEY_RESULT_LAB, pathFolder);
					((JMenuItem) e.getSource()).setToolTipText(pathFolder);
				}
			}
		});
		menuItem.setToolTipText(Preference.getPreference(Preference.KEY_RESULT_LAB));
		subMenu.add(menuItem);
		menu.add(subMenu);

		menu.addSeparator();

		subMenu = new JMenu(bundle.getString("xmlParser"));
		ButtonGroup btGroup = new ButtonGroup();
		radioMenuItem = new JRadioButtonMenuItem("DOM");
		radioMenuItem.setToolTipText("A utiliser pour les XML de moins de 20000 variables car gourmand en memoire");
		radioMenuItem.addActionListener(new ClickParseur());
		btGroup.add(radioMenuItem);
		subMenu.add(radioMenuItem);
		radioMenuItem = new JRadioButtonMenuItem("StAX");
		radioMenuItem.setToolTipText("Plus rapide et utilise moins de memoire mais moin robuste que DOM");
		radioMenuItem.addActionListener(new ClickParseur());
		btGroup.add(radioMenuItem);
		subMenu.add(radioMenuItem);
		menu.add(subMenu);

		menuBar.add(menu);

		final Enumeration<AbstractButton> enumAbParseur = btGroup.getElements();
		AbstractButton abParseur;
		while (enumAbBis.hasMoreElements()) {
			abParseur = enumAbParseur.nextElement();
			if (abParseur.getActionCommand().equals(Preference.getPreference(Preference.KEY_XML_PARSEUR))) {
				abParseur.setSelected(true);
				break;
			}
		}

		menu = new JMenu(bundle.getString("info"));
		menuItem = new JMenuItem(new AbstractAction(bundle.getString("log"), new ImageIcon(getClass().getResource(ICON_LOG))) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {

				FrameLog.getInstance();
			}
		});
		menu.add(menuItem);
		menu.addSeparator();

		menuItem = new JMenuItem(new AbstractAction(bundle.getString("contact"), new ImageIcon(getClass().getResource(ICON_CONTACT))) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				new DialContact(Ihm.this);

			}
		});
		menu.add(menuItem);
		menu.addSeparator();

		menuItem = new JMenuItem(new AbstractAction(bundle.getString("help"), new ImageIcon(getClass().getResource(ICON_AIDE))) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				new FrameAide();

			}
		});
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(new AbstractAction(bundle.getString("news"), new ImageIcon(getClass().getResource(ICON_NEWS))) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				new DialNews(Ihm.this);

			}
		});
		menu.add(menuItem);

		menuBar.add(menu);

		setJMenuBar(menuBar);
		//

		// Onglet lecteur PaCo
		ongletPaCo.add(new PanelCDF());
		onglets.addTab("Fichier de calibration", new ImageIcon(getClass().getResource(ICON_FDONNEE)), ongletPaCo);

		// Onglet comparaison de Lab
		onglets.addTab("Fichier de variables", new ImageIcon(getClass().getResource(ICON_FVARIABLE)), ongletLab);

		onglets.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				switch (onglets.getSelectedIndex()) {
				case 1:
					if (panelLab == null)
						panelLab = new PanelLab();
					ongletLab.add(panelLab);
					break;
				default:
					break;
				}

			}
		});

		onglets.setOpaque(true);
		getContentPane().add(onglets, BorderLayout.CENTER);

	}

	public static final SingleComponentInfiniteProgress getProgressPanel() {
		return progressPanel;
	}

	private final class ClickRadio implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent action) {

			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (action.getActionCommand().equals(info.getName())) {
					try {
						UIManager.setLookAndFeel(info.getClassName());
						SwingUtilities.updateComponentTreeUI(Ihm.this);
						Preference.setPreference(Preference.KEY_NOM_LF, action.getActionCommand());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

	private final class ClickParseur implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent action) {
			Preference.setPreference(Preference.KEY_XML_PARSEUR, action.getActionCommand());
		}
	}

	private final class ClickLanguage implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent action) {

			String res;

			switch (action.getActionCommand()) {
			case "English":
				res = "en";
				break;
			case "French":
				res = "fr";
				break;
			default:
				res = "en";
				break;
			}
			Preference.setPreference(Preference.KEY_LANGUAGE, res);
		}
	}

}
