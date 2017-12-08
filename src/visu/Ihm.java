package visu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.Preference;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String APP_ICON = "/eeprom.png";

    // Test
    private static final JMenuBar menuBar = new JMenuBar();

    //

    private static final JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
    private static final JToolBar toolBar = new JToolBar("Option");
    private static final JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletLab = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletBdd = new JPanel(new GridLayout(1, 1));

    private static PanelLab panelLab = null;
    private static PanelBdd panelBdd = null;

    private static FramePreferences fp = null;
    private static FrameInfo fi = null;
    private static FrameLog fl = null;
    private static FrameAide fa = null;

    private static final Boolean debugBDD = true;

    public Ihm() {

        setTitle("SW Tools");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(APP_ICON)));
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Test JMenuBar
        JMenu menu, subMenu;
        JMenuItem menuItem;
        JCheckBoxMenuItem cbMenuItem;

        menu = new JMenu("Preference");
        cbMenuItem = new JCheckBoxMenuItem("Coloration des cartographies",
                Boolean.parseBoolean(Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP)));

        subMenu = new JMenu("Chemin d'acces");
        menuItem = new JMenuItem(new AbstractAction("Import fichier d'echange de donnees") {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String pathFolder = getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_OPEN_CDF));
                if (!Preference.KEY_OPEN_CDF.equals(pathFolder)) {
                    Preference.setPreference(Preference.KEY_OPEN_CDF, pathFolder);
                }

            }
        });
        subMenu.add(menuItem);
        menuItem = new JMenuItem("Import fichier de variable");
        subMenu.add(menuItem);
        menuItem = new JMenuItem("Export comparaison fichier de variable");
        subMenu.add(menuItem);

        menu.add(cbMenuItem);
        menu.add(subMenu);

        menuBar.add(menu);

        menu = new JMenu(new AbstractAction("Info") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fi == null) {
                    fi = new FrameInfo();
                } else {
                    fi.setVisible(true);
                }

            }
        });
        menuBar.add(menu);

        menu = new JMenu(new AbstractAction("Log") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fl == null) {
                    fl = new FrameLog();
                } else {
                    fl.setVisible(true);
                }

            }
        });
        menuBar.add(menu);

        JMenu menuAide = new JMenu(new AbstractAction("Aide") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fa == null) {
                    fa = new FrameAide();
                } else {
                    fa.setVisible(true);
                }

            }
        });
        menuBar.add(menuAide);

        setJMenuBar(menuBar);
        //

        toolBar.add(new AbstractAction("Preferences") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fp == null) {
                    fp = new FramePreferences(Ihm.this);
                } else {
                    fp.setVisible(true);
                }
            }
        });
        toolBar.add(new AbstractAction("Info") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (fi == null) {
                    fi = new FrameInfo();
                } else {
                    fi.setVisible(true);
                }
            }
        });

        toolBar.add(new AbstractAction("Log") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (fl == null) {
                    fl = new FrameLog();
                } else {
                    fl.setVisible(true);
                }
            }
        });

        toolBar.add(new AbstractAction("Aide") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (fa == null) {
                    fa = new FrameAide();
                } else {
                    fa.setVisible(true);
                }
            }
        });

        getContentPane().add(toolBar, BorderLayout.NORTH);

        // Onglet lecteur PaCo
        ongletPaCo.add(new PanelCDF());
        onglets.addTab("Lecteur PaCo", ongletPaCo);

        // Onglet comparaison de Lab
        onglets.addTab("Comparaison lab", ongletLab);

        // Onglet BDD
        onglets.addTab("Gestion BDD", ongletBdd);
        onglets.setEnabledAt(2, debugBDD);

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

    private final String getFolder(String title, String defautPath) {
        final JFileChooser fileChooser = new JFileChooser("C:/");
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        final int reponse = fileChooser.showDialog(null, "Select");
        if (reponse == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        }
        return defautPath;
    }

}
