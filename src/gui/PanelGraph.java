package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cdf.Variable;
import chart.SurfaceChart;
import chart.XYChart;

public final class PanelGraph extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final CardLayout cardLayout = new CardLayout();
    private static final JPanel panCard = new JPanel(cardLayout);
    private static final ButtonGroup buttonGroup = new ButtonGroup();
    private static final JRadioButton radioBt2D = new JRadioButton("2D");
    private static final JRadioButton radioBt3D = new JRadioButton("3D", true);

    private static Variable selectedVar;

    public PanelGraph() {

        this.setLayout(new BorderLayout());

        radioBt2D.setOpaque(false);
        radioBt2D.addActionListener(new SwitchGraph());
        radioBt3D.setOpaque(false);
        radioBt3D.addActionListener(new SwitchGraph());

        buttonGroup.add(radioBt2D);
        buttonGroup.add(radioBt3D);

        final JPanel panBtRadio = new JPanel();
        panBtRadio.setBorder(BorderFactory.createLineBorder(panBtRadio.getBackground().darker()));
        panBtRadio.add(radioBt2D);
        panBtRadio.add(radioBt3D);

        this.add(panBtRadio, BorderLayout.NORTH);
        this.add(panCard, BorderLayout.CENTER);

    }

    private final class SwitchGraph implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            panCard.removeAll();

            switch (e.getActionCommand()) {
            case "2D":
                panCard.add(new XYChart(selectedVar), "2D");
                break;
            case "3D":
                panCard.add(new SurfaceChart(selectedVar), "3D");
                break;
            default:
            	break;
            }

            panCard.revalidate();
            panCard.repaint();

            cardLayout.show(panCard, e.getActionCommand());
        }
    }

    public final JPanel getPanCard() {
        return panCard;
    }

    public final void createChart(final Variable variable) {

        selectedVar = variable;

        if (radioBt2D.isSelected()) {
            panCard.add(new XYChart(variable), "2D");
        } else {
            panCard.add(new SurfaceChart(variable), "3D");
        }

    }

}
