package visu;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public final class DialogAide extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("MyLogger");
	private static final JEditorPane txtPane = new JEditorPane();
	
	public DialogAide() {
		super(new JFrame(), "Aide");
		txtPane.setEditable(false);
		URL helpURL = DialogAide.class.getResource("/aide.html");
		try {
			txtPane.setPage(helpURL);
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

		this.add(new JScrollPane(txtPane));
		this.pack();
		this.setVisible(true);
	}

}
