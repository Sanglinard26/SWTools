/*
 * Creation : 9 févr. 2017
 */
package visu;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class BarreProgression extends JProgressBar {

    public BarreProgression() {
        setUI(new MyProgressUI());
    }

}

class MyProgressUI extends BasicProgressBarUI {

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        super.paintDeterminate(g, c);
        c.setForeground(Color.DARK_GRAY);
    }

}
