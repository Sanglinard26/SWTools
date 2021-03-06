/*
 * Creation : 18 mai 2017
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import cdf.History;

public final class PanelHistory extends JComponent {

    private static final long serialVersionUID = 1L;
    private static final JComponent header = new Header();
    private static final JPanel panData = new JPanel();

    private static HashMap<String, Integer> scores;

    public PanelHistory() {
    	
    	initScores();

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);

        panData.setLayout(new BoxLayout(panData, BoxLayout.Y_AXIS));

        add(panData, BorderLayout.CENTER);

    }
    
    private final static void initScores()
    {
    	scores = new HashMap<String, Integer>(6);
    	scores.put("---", 0);
    	scores.put("changed", 0);
    	scores.put("prelimcalibrated", 25);
    	scores.put("calibrated", 50);
    	scores.put("checked", 75);
    	scores.put("completed", 100);
    }

    private static final class Header extends JComponent {

        private static final long serialVersionUID = 1L;

        public Header() {

            this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

            add(createColumnHeader("DATE", 200));
            add(createColumnHeader("AUTEUR", 150));
            add(createColumnHeader("SCORE", 200));
            add(createColumnHeader("COMMENTAIRES", 820));
        }
        
        private static final JComponent createColumnHeader(String title, int width)
        {
        	final Color headerBackground = UIManager.getLookAndFeel().getDefaults().getColor("control").darker();
        	
        	final JLabel columHeader = new JLabel(title);
        	columHeader.setPreferredSize(new Dimension(width, 40));
        	columHeader.setHorizontalAlignment(SwingConstants.CENTER);
        	columHeader.setOpaque(true);
        	columHeader.setBackground(headerBackground);
        	columHeader.setBorder(BorderFactory.createEtchedBorder());
        	columHeader.setFont(new Font(null, Font.BOLD, 14));
        	
        	return columHeader;
        }
    }

    public final void setDatas(History[] datas) {

        removeDatas();

        for (History data : datas) {
            panData.add(new Data(data));
        }
    }

    public final void removeDatas() {
        panData.removeAll();
        panData.revalidate();
        panData.repaint();
    }

    private final class CommentDialog extends JDialog {
        private static final long serialVersionUID = 1L;

        private final JTextPane txtPane = new JTextPane();

        public CommentDialog(String txt) {

            setTitle("COMMENTAIRES");
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setModal(true);

            txtPane.setEditable(false);
            txtPane.setText(txt);
            add(new JScrollPane(txtPane));
            setMinimumSize(new Dimension(250, 10));
            this.pack();
        }
    }

    private final class Data extends JComponent {

        private static final long serialVersionUID = 1L;

        private final JLabel textDate = new JLabel();
        private final JLabel textAuteur = new JLabel();
        private final JProgressBar bar = new JProgressBar(0, 100);
        private final JTextPane textPane = new JTextPane();
        private final JScrollPane scrollPane = new JScrollPane(textPane);

        public Data(History data) {

            this.setLayout(null);
            this.setMinimumSize(new Dimension(header.getWidth(), 50));
            this.setPreferredSize(new Dimension(header.getWidth(), 50));
            this.setMaximumSize(new Dimension(header.getWidth(), 50));

            // Configuration des differents composants
            textDate.setOpaque(true);
            textDate.setBackground(Color.WHITE);
            textDate.setBorder(BorderFactory.createEtchedBorder());
            textDate.setHorizontalAlignment(SwingConstants.CENTER);
            textDate.setBounds(0, 0, 200, 50);

            textAuteur.setOpaque(true);
            textAuteur.setBackground(Color.WHITE);
            textAuteur.setBorder(BorderFactory.createEtchedBorder());
            textAuteur.setHorizontalAlignment(SwingConstants.CENTER);
            textAuteur.setBounds(200, 0, 150, 50);

            bar.setStringPainted(true);
            bar.setBorder(BorderFactory.createEtchedBorder());
            bar.setBounds(350, 0, 200, 50);

            textPane.setEditable(false);
            scrollPane.setBorder(BorderFactory.createEtchedBorder());
            scrollPane.setBounds(550, 0, 820, 50);
            //

            textPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent paramMouseEvent) {
                    if (paramMouseEvent.getClickCount() == 2 && scrollPane.getVerticalScrollBar().isShowing()) {

                        final CommentDialog dial = new CommentDialog(textPane.getText());

                        dial.setLocationRelativeTo(paramMouseEvent.getComponent());
                        dial.setLocation(paramMouseEvent.getXOnScreen() - dial.getWidth(), paramMouseEvent.getYOnScreen() - dial.getHeight());
                        dial.setVisible(true);
                    }
                }
            });

            textDate.setText(data.getDate());
            add(textDate);

            textAuteur.setText(data.getAuteur());
            add(textAuteur);

            bar.setValue(scores.get(data.getScore().toLowerCase()));
            bar.setString(String.valueOf(scores.get(data.getScore().toLowerCase())) + "%");
            add(bar);

            textPane.setText(data.getCommentaire());
            textPane.setCaretPosition(0);
            add(scrollPane);
        }
    }
}
