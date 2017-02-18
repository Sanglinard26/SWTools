package visu;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String APP_ICON = "/eeprom.png";
    private static final String ICON = "/Icon_tools.png";

    private JTabbedPane onglets;

    public Ihm() {

        setTitle("SW Tools");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(APP_ICON)));
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JToolBar toolBar = new JToolBar("Option");
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
        getContentPane().add(toolBar, BorderLayout.NORTH);

        onglets = new JTabbedPane(JTabbedPane.TOP);

        // Onglet lecteur PaCo
        JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
        ongletPaCo.add(new PanelPaCo());
        onglets.addTab("Lecteur PaCo", ongletPaCo);

        // Onglet comparaison de Lab
        JPanel onglet2 = new JPanel(new GridLayout(1, 1));
        onglet2.add(new PanelLab());
        onglets.addTab("Comparaison lab", onglet2);

        onglets.setOpaque(true);
        getContentPane().add(onglets, BorderLayout.CENTER);

        if (SystemTray.isSupported()) {

            PopupMenu menu = new PopupMenu("SystemTray");
            MenuItem itemQuit = new MenuItem("Quitter");
            itemQuit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            menu.add(itemQuit);

            if (getClass().getResource(ICON) != null) {
                ImageIcon imageIcon = new ImageIcon(getClass().getResource(ICON));
                TrayIcon trayIcon = new TrayIcon(imageIcon.getImage(), "SW Tools en cours...", menu);

                try {
                    SystemTray.getSystemTray().add(trayIcon);

                } catch (AWTException awtEx) {

                }
            }
        }

    }

}
