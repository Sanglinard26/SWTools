package visu;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import paco.Variable;

public final class PanelInfoVariable extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String ICON_TEXT = "/text_icon_16.png";
    private static final String ICON_IMAGE = "/image_icon_16.png";

    private final JLabel labelShortName = new JLabel("Nom :");
    private final JLabel labelLongName = new JLabel("Description :");
    private final JLabel labelSwFeatureRef = new JLabel("Fonction :");
    private final JLabel labelUnit = new JLabel("Unite(s) :");
    private final JLabel labelValue = new JLabel("Valeur(s) :");

    public PanelInfoVariable(final Variable variable) {

        this.setLayout(new GridLayout(5, 1));
        this.setBackground(Color.WHITE);

        labelShortName.setText("Nom : " + variable.getShortName());
        labelLongName.setText("Description : " + variable.getLongName());
        labelSwFeatureRef.setText("Fonction : " + variable.getSwFeatureRef());

        final StringBuilder unite = new StringBuilder();
        for (String s : variable.getSwUnitRef()) {
            unite.append("[" + s + "] ");
        }

        labelUnit.setText("Unite(s) : " + unite);

        this.add(labelShortName);
        this.add(labelLongName);
        this.add(labelSwFeatureRef);
        this.add(labelUnit);
        this.add(labelValue);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final JPopupMenu menu = new JPopupMenu();
                    final JMenu menuCopy = new JMenu("Copier dans le presse-papier");
                    JMenuItem subMenu = new JMenuItem("Format image", new ImageIcon(getClass().getResource(ICON_IMAGE)));
                    subMenu.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            variable.copyToClipboard();
                        }
                    });
                    menuCopy.add(subMenu);
                    menuCopy.addSeparator();
                    subMenu = new JMenuItem("Format texte", new ImageIcon(getClass().getResource(ICON_TEXT)));
                    subMenu.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            variable.copyTxtToClipboard(variable.toString());
                        }
                    });
                    menuCopy.add(subMenu);
                    menu.add(menuCopy);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

}
