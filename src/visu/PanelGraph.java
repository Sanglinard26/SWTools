package visu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import cdf.Variable;
import graph.SurfaceChart;
import graph.XYChart;

public final class PanelGraph extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final CardLayout cardLayout = new CardLayout();
    private static final JPanel panCard = new JPanel(cardLayout);
    private static final JPanel panBtRadio = new JPanel();
    private static final ButtonGroup buttonGroup = new ButtonGroup();
    private static final JRadioButton radioBt2D = new JRadioButton("2D");
    private static final JRadioButton radioBt3D = new JRadioButton("3D", true);

    public PanelGraph() {

        this.setLayout(new BorderLayout());

        radioBt2D.addActionListener(new SwitchGraph());
        radioBt3D.addActionListener(new SwitchGraph());

        buttonGroup.add(radioBt2D);
        buttonGroup.add(radioBt3D);

        panBtRadio.setBorder(LineBorder.createBlackLineBorder());
        panCard.setBorder(LineBorder.createBlackLineBorder());

        panBtRadio.add(radioBt2D);
        panBtRadio.add(radioBt3D);

        this.add(panBtRadio, BorderLayout.NORTH);
        this.add(panCard, BorderLayout.CENTER);

    }

    private final class SwitchGraph implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            cardLayout.show(panCard, e.getActionCommand());
        }

    }

    public final JPanel getPanCard() {
        return panCard;
    }

    public final void createChart(final Variable variable) {
        if (radioBt2D.isSelected()) {
            panCard.add(new XYChart(variable), "2D");
            panCard.add(new SurfaceChart(variable), "3D");
        } else {
            panCard.add(new SurfaceChart(variable), "3D");
            panCard.add(new XYChart(variable), "2D");
        }

    }

}
