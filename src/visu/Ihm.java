package visu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String APP_ICON = "/eeprom.png";

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

}
