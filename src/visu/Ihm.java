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

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String APP_ICON = "/eeprom.png";

    private static final JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
    private static final JToolBar toolBar = new JToolBar("Option");
    private static final JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletLab = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletA2l = new JPanel(new GridLayout(1, 1));

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
                new FramePreferences();

            }
        });
        toolBar.add(new AbstractAction("Info") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new FrameInfo();
            }
        });

        toolBar.add(new AbstractAction("Log") {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new FrameLog();
            }
        });

        getContentPane().add(toolBar, BorderLayout.NORTH);

        // onglets = new JTabbedPane(SwingConstants.TOP);

        // Onglet lecteur PaCo
        ongletPaCo.add(new PanelPaCo());
        onglets.addTab("Lecteur PaCo", ongletPaCo);

        // Onglet comparaison de Lab
        ongletLab.add(new PanelLab());
        onglets.addTab("Comparaison lab", ongletLab);

        // Onglet lecteur A2l
        ongletA2l.add(new PanelA2l());
        onglets.addTab("Lecture A2l", ongletA2l);

        onglets.setOpaque(true);
        getContentPane().add(onglets, BorderLayout.CENTER);

    }

}
