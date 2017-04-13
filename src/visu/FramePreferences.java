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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import tools.Preference;

public final class FramePreferences extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);
    private static final JPanel ongletPaCo = new JPanel(new GridLayout(1, 1));
    private static final JPanel ongletLab = new JPanel(new GridLayout(1, 1));

    private static final GridBagConstraints gbc = new GridBagConstraints();

    public FramePreferences() {
        this.setTitle("Preferences utilisateur");
        this.setMinimumSize(new Dimension(300, 300));

        ongletPaCo.add(createPanPrefPaco());
        onglets.addTab("Lecteur PaCo", ongletPaCo);

        ongletLab.add(createPanPrefLab());
        onglets.addTab("Comparaison lab", ongletLab);

        onglets.setOpaque(true);
        getContentPane().add(onglets, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);

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
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panPaco.add(txtPath, gbc);

        final JLabel path = new JLabel(Preference.getPreference(Preference.KEY_OPEN_PACO));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panPaco.add(path, gbc);

        final JButton btPathOpenPaco = new JButton("...");
        btPathOpenPaco.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_OPEN_PACO));
                Preference.setPreference(Preference.KEY_OPEN_PACO, path);
            }
        });
        btPathOpenPaco.setToolTipText("Clicker pour choisir le chemin");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panPaco.add(btPathOpenPaco, gbc);

        return panPaco;
    }

    private final JPanel createPanPrefLab() {
        final JPanel panLab = new JPanel();
        panLab.setLayout(new BoxLayout(panLab, BoxLayout.Y_AXIS));

        final JButton btPathAddLab = new JButton(Preference.getPreference(Preference.KEY_ADD_LAB));
        btPathAddLab.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = getFolder("Choix du chemin d'import", Preference.getPreference(Preference.KEY_ADD_LAB));
                Preference.setPreference(Preference.KEY_ADD_LAB, path);
                btPathAddLab.setText(path);
            }
        });
        btPathAddLab.setToolTipText("Clicker pour choisir le chemin");
        panLab.add(btPathAddLab);

        final JButton btPathResLab = new JButton(Preference.getPreference(Preference.KEY_RESULT_LAB));
        btPathResLab.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String path = getFolder("Choix du chemin d'export", Preference.getPreference(Preference.KEY_RESULT_LAB));
                Preference.setPreference(Preference.KEY_RESULT_LAB, path);
                btPathResLab.setText(path);
            }
        });
        btPathResLab.setToolTipText("Clicker pour choisir le chemin");
        panLab.add(btPathResLab);

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
