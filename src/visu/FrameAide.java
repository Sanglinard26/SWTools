package visu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


public final class FrameAide extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String FENETRE_ICON = "/aide_32.png";
	private static Logger logger = Logger.getLogger("MyLogger");
	private static final JEditorPane txtPane = new JEditorPane();
	
	public FrameAide() {
		super("Aide");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(FENETRE_ICON)));
		txtPane.setEditable(false);
		txtPane.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					try {
						txtPane.setPage(e.getURL());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}	
			}
		});
		URL helpURL = FrameAide.class.getResource("/aide.html");
		try {
			txtPane.setPage(helpURL);
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

		this.add(new JScrollPane(txtPane));
		this.setMinimumSize(new Dimension(700, 700));
		this.pack();
		this.setVisible(true);
	}

}
