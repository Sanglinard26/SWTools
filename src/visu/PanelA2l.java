/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import soft.Soft;
import tools.Utilitaire;

public final class PanelA2l extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final GridBagConstraints gbc = new GridBagConstraints();

    // GUI
    private static final JButton btOpen = new JButton("Ouvrir A2l");

    public PanelA2l() {

        this.setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        btOpen.addActionListener(new OpenA2l());
        this.add(btOpen, gbc);
    }

    private final class OpenA2l implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return "A2l *.a2l";
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    String extension = Utilitaire.getExtension(f);
                    if (extension.equals(Utilitaire.a2l)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelA2l.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {
                new TaskCharging(jFileChooser.getSelectedFile()).execute();
            }

        }
    }

    private final class TaskCharging extends SwingWorker<Integer, Integer> {

        File fileA2l;

        public TaskCharging(File fileA2l) {
            this.fileA2l = fileA2l;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            Soft a2l = new Soft(fileA2l);
            return null;
        }

    }

}
