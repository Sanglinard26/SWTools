package visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import lab.Variable;

public final class ListVarRenderer extends JLabel implements ListCellRenderer<Variable> {
	
	public ListVarRenderer() {
		// TODO Auto-generated constructor stub
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
		setOpaque(true);
		
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Variable> list, Variable value, int index,
			boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		
		setText(value.getNom());
		setBorder(new EmptyBorder(2, 0, 2, 0));
		setToolTipText("Label de type : " + value.getType());
		
		switch(value.getType())
		{
		case "SCALAIRE":
			setIcon(new ImageIcon("images/variables/SCALAIRE.gif"));
			break;
		case "CURVE" :
			setIcon(new ImageIcon("images/variables/CURVE.gif"));
			break;
		case "MAP" :
			setIcon(new ImageIcon("images/variables/MAP.gif"));
			break;
		default :
			setIcon(new ImageIcon("images/variables/INCONNU.gif"));
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
