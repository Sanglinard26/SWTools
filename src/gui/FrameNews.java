/*
 * Creation : 9 mars 2017
 */
package gui;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public final class FrameNews extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String FENETRE_ICON = "/new_icon_16.png";
    private static final String CHANGELOG = "/news.html";

    private final JEditorPane txtNews = new JEditorPane();

    public FrameNews() {
        this.setTitle("Nouveautes");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(FENETRE_ICON)));

        URL urlNews = FrameNews.class.getResource(CHANGELOG);
        
        txtNews.setEditable(false);
        
        try {
			txtNews.setPage(urlNews);
		} catch (IOException e) {
			e.printStackTrace();
		}
               
        add(new JScrollPane(txtNews));

        this.setSize(600, 600);
        this.setVisible(true);
    }
}
