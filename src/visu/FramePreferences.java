/*
 * Creation : 2 févr. 2017
 */
package visu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import tools.Preference;

public final class FramePreferences extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String FENETRE_ICON = "/preferences_32.png";

    private final JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
    private final JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
    private final JPanel ongletLab = new JPanel(new GridLayout(1, 1));

    private static final GridBagConstraints gbc = new GridBagConstraints();

    public FramePreferences() {
        this.setTitle("Preferences utilisateur");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(FENETRE_ICON)));
        this.setMinimumSize(new Dimension(300, 300));

        ongletPaCo.add(createPanPrefPaco());
        onglets.addTab("Lecteur PaCo", ongletPaCo);

        ongletLab.add(createPanPrefLab());
        onglets.addTab("Comparaison lab", ongletLab);

        onglets.setOpaque(true);
        getContentPane().add(onglets, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private final JPanel createPanPrefPaco() {
        final JPanel panPaco = new JPanel();
        panPaco.setLayout(new GridBagLayout());

        final JLabel txtPath = new JLabel("Chemin d'import : ");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 5, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panPaco.add(txtPath, gbc);

        final JLabel path = new JLabel(Preference.getPreference(Preference.KEY_OPEN_PACO));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panPaco.add(path, gbc);

        final JButton btPathOpenPaco = new JButton("...");
        btPathOpenPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String pathFolder = getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_OPEN_PACO));
                if (!Preference.KEY_OPEN_PACO.equals(pathFolder)) {
                    Preference.setPreference(Preference.KEY_OPEN_PACO, pathFolder);
                    path.setText(pathFolder);

                }

            }
        });
        btPathOpenPaco.setToolTipText("Clicker pour choisir le chemin");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panPaco.add(btPathOpenPaco, gbc);

        final JCheckBox chkColorMap = new JCheckBox("Coloration des cartographies",
                Boolean.parseBoolean(Preference.getPreference(Preference.KEY_ETAT_COLOR_MAP)));
        chkColorMap.setHorizontalTextPosition(SwingConstants.LEFT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        chkColorMap.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Preference.setPreference(Preference.KEY_ETAT_COLOR_MAP, Boolean.toString(chkColorMap.isSelected()));
            }
        });
        panPaco.add(chkColorMap, gbc);

        return panPaco;
    }

    private final JPanel createPanPrefLab() {
        final JPanel panLab = new JPanel();
        panLab.setLayout(new GridBagLayout());

        final JLabel txtPathAdd = new JLabel("Chemin d'import : ");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 5, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panLab.add(txtPathAdd, gbc);

        final JLabel pathAdd = new JLabel(Preference.getPreference(Preference.KEY_ADD_LAB));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panLab.add(pathAdd, gbc);

        final JButton btPathAddLab = new JButton("...");
        btPathAddLab.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String pathFolder = getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_ADD_LAB));
                if (!Preference.KEY_ADD_LAB.equals(pathFolder)) {
                    Preference.setPreference(Preference.KEY_ADD_LAB, pathFolder);
                    pathAdd.setText(pathFolder);
                }
            }
        });
        btPathAddLab.setToolTipText("Clicker pour choisir le chemin");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panLab.add(btPathAddLab, gbc);

        final JLabel txtPathRes = new JLabel("Choix du chemin d'export des resultats : ");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 5, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panLab.add(txtPathRes, gbc);

        final JLabel pathRes = new JLabel(Preference.getPreference(Preference.KEY_RESULT_LAB));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panLab.add(pathRes, gbc);

        final JButton btPathResLab = new JButton("...");
        btPathResLab.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String pathFolder = getFolder("Choix du chemin d'export des r�sultats", Preference.getPreference(Preference.KEY_RESULT_LAB));
                if (!Preference.KEY_RESULT_LAB.equals(pathFolder)) {
                    Preference.setPreference(Preference.KEY_RESULT_LAB, pathFolder);
                    pathRes.setText(pathFolder);
                }
            }
        });
        btPathResLab.setToolTipText("Clicker pour choisir le chemin");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panLab.add(btPathResLab, gbc);

        return panLab;
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
