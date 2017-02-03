/*
 * Creation : 2 févr. 2017
 */
package visu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import tools.Preference;

public final class FramePreferences extends JFrame {

    private static final long serialVersionUID = 1L;

    public FramePreferences() {
        this.setTitle("Préférences utilisateur");
        this.setMinimumSize(new Dimension(300, 300));

        getContentPane().add(createPanPrefLab());
        this.pack();
        this.setVisible(true);

    }

    private JPanel createPanPrefLab() {
        final JPanel panLab = new JPanel();
        panLab.setLayout(new BoxLayout(panLab, BoxLayout.Y_AXIS));

        panLab.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Comparaison de Lab"));

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

    private String getFolder(String title, String defautPah) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        final int reponse = fileChooser.showDialog(null, "Select");
        if (reponse == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        }
        return defautPah;
    }

}
