/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import soft.A2LParser;
import tools.Utilitaire;

public final class PanelA2l extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final GridBagConstraints gbc = new GridBagConstraints();

    // GUI
    private final MyButton btOpen = new MyButton("Ouvrir A2l");

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Rectangle2D tr = new Rectangle2D.Double(0, 0, 100, 100);
        GradientPaint gp = new GradientPaint(0, 0, Color.LIGHT_GRAY, 1, 1, Color.BLACK, true);
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D big2d = bi.createGraphics();
        big2d.setPaint(gp);
        big2d.fill(tr);
        g2d.setPaint(new TexturePaint(bi, tr));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
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
                A2LParser.parse(jFileChooser.getSelectedFile());
            }

        }
    }

    private final class MyButton extends JButton {

        private static final long serialVersionUID = 1L;

        public MyButton(String s) {
            super(s);
            setOpaque(false);
        }

    }

}
