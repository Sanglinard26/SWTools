package visu;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

public final class DialogAide extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static final JEditorPane txtPane = new JEditorPane();
	private static final String htmlString = "<html>\n"
            + "<body>\n"
            + "<h1>Bienvenue dans l'aide !</h1>\n"
            + "<h2>This is an H2 header</h2>\n"
            + "<p>This is some sample text</p>\n"
            + "<p><a href=\"http://devdaily.com/blog/\">devdaily blog</a></p>\n"
            + "</body>\n";
	
	public DialogAide() {
		super(new JFrame(), "Aide");
		this.setMinimumSize(new Dimension(300, 300));
		HTMLEditorKit kit = new HTMLEditorKit();
		txtPane.setEditorKit(kit);
		txtPane.setEditable(false);
		Document doc = kit.createDefaultDocument();
		txtPane.setDocument(doc);
		txtPane.setText(htmlString);
		this.add(txtPane);
		this.setVisible(true);
	}

}
