package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import lab.Variable;
import paco.Label;
import paco.PaCo;

public final class ListLabelRenderer extends JLabel implements ListCellRenderer<Label> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String SCALAIRE = "/variable/SCALAIRE.gif";
	private static final String CURVE = "/variable/CURVE.gif";
	private static final String MAP = "/variable/MAP.gif";
	private static final String INCONNU = "/variable/INCONNU.gif";
	
	public ListLabelRenderer() {
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Label> list, Label value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		setText(value.getShortName());
		setBorder(new EmptyBorder(2, 0, 2, 0));
		setToolTipText("Fonction : " + value.getSwFeatureRef());
		
		switch(value.getCategory())
		{
		case PaCo._C:
			setIcon(new ImageIcon(getClass().getResource(SCALAIRE)));
			break;
		case PaCo._T :
			setIcon(new ImageIcon(getClass().getResource(CURVE)));
			break;
		case PaCo._T_GROUPED :
			setIcon(new ImageIcon(getClass().getResource(CURVE)));
			break;
		case PaCo._M :
			setIcon(new ImageIcon(getClass().getResource(MAP)));
			break;
		case PaCo._M_GROUPED :
			setIcon(new ImageIcon(getClass().getResource(MAP)));
			break;
		default :
			setIcon(new ImageIcon(getClass().getResource(INCONNU)));
			break;
		}
		
		if(isSelected)
		{
			setBackground(Color.WHITE);
			setFont(new Font(null, Font.BOLD, 12));
			setBorder(new LineBorder(Color.BLACK, 1));
		}else{
			setBackground(Color.WHITE);
			setFont(new Font(null, Font.PLAIN, 12));
		}
		
		return this;
	}








}
