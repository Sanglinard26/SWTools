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

import tools.Preference;
import tools.Utilitaire;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String APP_ICON = "/eeprom.png";
    private static final String ICON_FDONNEE = "/fdonnee_icon_32.png";
    private static final String ICON_FVARIABLE = "/fvariable_icon_32.png";
    private static final String ICON_BDD = "/bdd_icon_32.png";
    private static final String ICON_LOG = "/log_icon_16.png";
    private static final String ICON_CONTACT = "/contact_icon_16.png";
    private static final String ICON_AIDE = "/manuel_icon_16.png";
    private static final String ICON_NEWS = "/new_icon_16.png";

    private final JMenuBar menuBar = new JMenuBar();
    private JMenu menu, subMenu;
    private JMenuItem menuItem;
    private final JCheckBoxMenuItem cbMenuItem;
    private JRadioButtonMenuItem radioMenuItem;

    private static final JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
    private static final JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletLab = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletBdd = new JPanel(new GridLayout(1, 1));

    private static PanelLab panelLab = null;
    private static PanelBdd panelBdd = null;

    private static FrameContact fi = null;
    private static FrameLog fl = null;
    private static FrameAide fa = null;
    private static FrameNews fn = null;

    public static final boolean testMode = true;

    public Ihm() {

        setTitle("SW Tools");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(APP_ICON)));
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        menu = new JMenu("Preferences");
        cbMenuItem = new JCheckBoxMenuItem("Coloration des cartographies",
                Boolean.parseBoolean(Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP)));
        cbMenuItem.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Preference.setPreference(Preference.KEY_ETAT_COLOR_MAP, Boolean.toString(cbMenuItem.isSelected()));

            }
        });
        menu.add(cbMenuItem);
        menu.addSeparator();

        subMenu = new JMenu("Theme d'apparence");
        ButtonGroup group = new ButtonGroup();
        radioMenuItem = new JRadioButtonMenuItem("Windows");
        radioMenuItem.addActionListener(new ClickRadio());
        group.add(radioMenuItem);
        subMenu.add(radioMenuItem);
        radioMenuItem = new JRadioButtonMenuItem("Metal");
        radioMenuItem.addActionListener(new ClickRadio());
        group.add(radioMenuItem);
        subMenu.add(radioMenuItem);
        radioMenuItem = new JRadioButtonMenuItem("Nimbus");
        radioMenuItem.addActionListener(new ClickRadio());
        group.add(radioMenuItem);
        subMenu.add(radioMenuItem);

        final Enumeration<AbstractButton> enumAb = group.getElements();
        AbstractButton ab;
        while (enumAb.hasMoreElements()) {
            ab = enumAb.nextElement();
            if (ab.getActionCommand().equals(Preference.getPreference(Preference.KEY_NOM_LF))) {
                ab.setSelected(true);
                break;
            }
        }

        menu.add(subMenu);
        menu.addSeparator();

        subMenu = new JMenu("Chemin d'acces");
        menuItem = new JMenuItem(new AbstractAction("Import fichier d'echange de donnees") {

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

        subMenu = new JMenu("Parseur XML");
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
        while (enumAb.hasMoreElements()) {
            abParseur = enumAbParseur.nextElement();
            if (abParseur.getActionCommand().equals(Preference.getPreference(Preference.KEY_XML_PARSEUR))) {
                abParseur.setSelected(true);
                break;
            }
        }

        menu = new JMenu("Infos");
        menuItem = new JMenuItem(new AbstractAction("Log", new ImageIcon(getClass().getResource(ICON_LOG))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent paramActionEvent) {
                if (fl == null) {
                    fl = new FrameLog();
                } else {
                    fl.loadLog();
                    fl.setVisible(true);
                }
            }
        });
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem(new AbstractAction("Contact", new ImageIcon(getClass().getResource(ICON_CONTACT))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent paramActionEvent) {
                if (fi == null) {
                    fi = new FrameContact();
                } else {
                    fi.setVisible(true);
                }
            }
        });
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem(new AbstractAction("Aide", new ImageIcon(getClass().getResource(ICON_AIDE))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent paramActionEvent) {
                if (fa == null) {
                    fa = new FrameAide();
                } else {
                    fa.setVisible(true);
                }
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(new AbstractAction("Nouveautes", new ImageIcon(getClass().getResource(ICON_NEWS))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent paramActionEvent) {
                if (fn == null) {
                    fn = new FrameNews();
                } else {
                    fn.setVisible(true);
                }
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
        //

        // test glasspane
        InfiniteProgressPanel progressPanel = new InfiniteProgressPanel();
        setGlassPane(progressPanel);

        // Onglet lecteur PaCo
        ongletPaCo.add(new PanelCDF(progressPanel));
        onglets.addTab("Fichier de calibration", new ImageIcon(getClass().getResource(ICON_FDONNEE)), ongletPaCo);

        // Onglet comparaison de Lab
        onglets.addTab("Fichier de variables", new ImageIcon(getClass().getResource(ICON_FVARIABLE)), ongletLab);

        // Onglet BDD
        onglets.addTab("Gestion BDD", new ImageIcon(getClass().getResource(ICON_BDD)), ongletBdd);
        onglets.setEnabledAt(2, testMode);

        onglets.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                switch (onglets.getSelectedIndex()) {
                case 1:
                    if (panelLab == null)
                        panelLab = new PanelLab();
                    ongletLab.add(panelLab);
                    break;
                case 2:
                    if (panelBdd == null)
                        panelBdd = new PanelBdd();
                    ongletBdd.add(panelBdd);
                    break;

                }

            }
        });

        onglets.setOpaque(true);
        getContentPane().add(onglets, BorderLayout.CENTER);

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

}
