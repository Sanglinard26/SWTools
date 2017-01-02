package visu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.TableCellRenderer;

import lab.Lab;

public class CellTabRenderer extends JLabel implements TableCellRenderer {
	
	@Override
	public JToolTip createToolTip() {
		// TODO Auto-generated method stub
		return super.createToolTip();
	}

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4,
			int arg5) {
		
		if (arg1 instanceof Lab) {
			Lab lab = (Lab) arg1;
			this.setToolTipText("Nombre de label(s) : " + lab.getListVariable().size());
		}
		
		
		this.setText(arg1.toString());
		
		
		return this;
	}

}
