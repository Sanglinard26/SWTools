package visu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import graph.SurfaceChart;
import graph.XYChart;
import paco.Variable;

public class PanelGraph extends JPanel {

    private CardLayout cardLayout = new CardLayout();
    private JPanel panCard = new JPanel(cardLayout);
    private JPanel panBtRadio;
    private ButtonGroup buttonGroup;
    private JRadioButton radioBt2D;
    private JRadioButton radioBt3D;

    public PanelGraph() {
        
        this.setLayout(new BorderLayout());
        
        panBtRadio = new JPanel();
        
        radioBt2D = new JRadioButton("2D");
        radioBt3D = new JRadioButton("3D",true);
        
        radioBt2D.addActionListener(new SwitchGraph());
        radioBt3D.addActionListener(new SwitchGraph());
        
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioBt2D);
        buttonGroup.add(radioBt3D);
        
        panBtRadio.add(radioBt2D);
        panBtRadio.add(radioBt3D);

        this.add(panBtRadio, BorderLayout.NORTH);
        this.add(panCard, BorderLayout.CENTER);

    }
    
    private class SwitchGraph implements ActionListener
    {

		@Override
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(panCard, e.getActionCommand());
		}
    	
    }

    public JPanel getPanCard() {
		return panCard;
	}

    public void createChart(final Variable variable) {
    	if(radioBt2D.isSelected())
    	{
    		panCard.add(new XYChart(variable), "2D");
        	panCard.add(new SurfaceChart(variable), "3D");
    	}else{
    		panCard.add(new SurfaceChart(variable), "3D");
    		panCard.add(new XYChart(variable), "2D");
    	}
    	
    	
    }

}
