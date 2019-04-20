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
import javax.swing.border.LineBorder;

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

        private static final JLabel date = new JLabel("DATE");
        private static final JLabel auteur = new JLabel("AUTEUR");
        private static final JLabel score = new JLabel("SCORE");
        private static final JLabel comment = new JLabel("COMMENTAIRES");

        public Header() {

            this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

            date.setPreferredSize(new Dimension(200, 40));
            date.setHorizontalAlignment(SwingConstants.CENTER);
            date.setOpaque(true);
            date.setBackground(Color.LIGHT_GRAY);
            date.setBorder(new LineBorder(Color.BLACK, 1));
            date.setFont(new Font(null, Font.BOLD, 14));

            auteur.setPreferredSize(new Dimension(150, 40));
            auteur.setHorizontalAlignment(SwingConstants.CENTER);
            auteur.setOpaque(true);
            auteur.setBackground(Color.LIGHT_GRAY);
            auteur.setBorder(new LineBorder(Color.BLACK, 1));
            auteur.setFont(new Font(null, Font.BOLD, 14));

            score.setPreferredSize(new Dimension(200, 40));
            score.setHorizontalAlignment(SwingConstants.CENTER);
            score.setOpaque(true);
            score.setBackground(Color.LIGHT_GRAY);
            score.setBorder(new LineBorder(Color.BLACK, 1));
            score.setFont(new Font(null, Font.BOLD, 14));

            comment.setPreferredSize(new Dimension(820, 40));
            comment.setHorizontalAlignment(SwingConstants.CENTER);
            comment.setOpaque(true);
            comment.setBackground(Color.LIGHT_GRAY);
            comment.setBorder(new LineBorder(Color.BLACK, 1));
            comment.setFont(new Font(null, Font.BOLD, 14));

            add(date);
            add(auteur);
            add(score);
            add(comment);

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
            textDate.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            textDate.setHorizontalAlignment(SwingConstants.CENTER);
            textDate.setBounds(0, 0, 200, 50);

            textAuteur.setOpaque(true);
            textAuteur.setBackground(Color.WHITE);
            textAuteur.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            textAuteur.setHorizontalAlignment(SwingConstants.CENTER);
            textAuteur.setBounds(200, 0, 150, 50);

            bar.setStringPainted(true);
            bar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            bar.setBounds(350, 0, 200, 50);

            textPane.setEditable(false);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
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
