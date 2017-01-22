package visu;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String ICON = "/Icon_tools.png";

    private JTabbedPane onglets;

    public Ihm() {
        setTitle("SW Tools");
        // setExtendedState(MAXIMIZED_BOTH);
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        onglets = new JTabbedPane(JTabbedPane.TOP);

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
