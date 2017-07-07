/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class PanelA2l extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private Rectangle2D r2d = new Rectangle2D.Float(20, 500, 50, 50);

    private Timer timer;
    private Boolean backX = false;

    private static final int nbSample = 500;
    private double sinusX[] = new double[nbSample];
    private double sinusY[] = new double[nbSample];
    int cnt = 0;

    // GUI
    private final JButton btOpen = new JButton("Start");
    private final JButton btStop = new JButton("Stop");

    public PanelA2l() {

        btOpen.addActionListener(new OpenA2l());
        this.add(btOpen);

        btStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();

            }
        });

        this.add(btStop);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.fill(r2d);
    }

    // @Override
    // protected void paintComponent(Graphics g) {
    // super.paintComponent(g);
    //
    // Graphics2D g2d = (Graphics2D) g;
    //
    // Rectangle2D tr = new Rectangle2D.Double(0, 0, 100, 100);
    // GradientPaint gp = new GradientPaint(0, 0, Color.LIGHT_GRAY, 1, 1, Color.BLACK, true);
    // BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    // Graphics2D big2d = bi.createGraphics();
    // big2d.setPaint(gp);
    // big2d.fill(tr);
    // g2d.setPaint(new TexturePaint(bi, tr));
    // g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
    // }

    private final class OpenA2l implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            for (int i = 0; i < sinusX.length; i++) {
                sinusX[i] = i * (PanelA2l.this.getWidth() / (sinusX.length - 1));
                sinusY[i] = 500 + Math.sin((i % 2) * (-1)) * (nbSample - i);
            }

            timer = new Timer(100, PanelA2l.this);
            timer.start();

            // final JFileChooser jFileChooser = new JFileChooser();
            // jFileChooser.setMultiSelectionEnabled(false);
            // jFileChooser.setFileFilter(new FileFilter() {
            //
            // @Override
            // public String getDescription() {
            // return "A2l *.a2l";
            // }
            //
            // @Override
            // public boolean accept(File f) {
            //
            // if (f.isDirectory())
            // return true;
            //
            // String extension = Utilitaire.getExtension(f);
            // if (extension.equals(Utilitaire.a2l)) {
            // return true;
            // }
            // return false;
            // }
            // });
            //
            // final int reponse = jFileChooser.showOpenDialog(PanelA2l.this);
            // if (reponse == JFileChooser.APPROVE_OPTION) {
            // A2LParser.parse(jFileChooser.getSelectedFile());
            // }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (r2d.getFrame().getX() - r2d.getFrame().getWidth() > this.getWidth())
            backX = true;
        if (r2d.getFrame().getX() < 0)
            backX = false;

        r2d.setFrame(sinusX[cnt % nbSample], sinusY[cnt % nbSample], r2d.getFrame().getWidth(), r2d.getFrame().getHeight());
        repaint();

        cnt++;

    }

}
