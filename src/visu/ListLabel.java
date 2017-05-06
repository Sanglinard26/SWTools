/*
 * Creation : 27 janv. 2017
 */
package visu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import paco.ListModelLabel;
import paco.PaCo;
import paco.Variable;

public final class ListLabel extends JList<Variable> {

	private static final long serialVersionUID = 1L;

	private static final String ICON_OLD_SEARCH = "/oldsearch_24.png";

	private final FilterField filterField;

	public ListLabel(ListModelLabel dataModel) {
		super(dataModel);
		setCellRenderer(new ListLabelRenderer());
		filterField = new FilterField();
	}

	@Override
	public ListModelLabel getModel() {
		return (ListModelLabel) super.getModel();
	}


	private class FilterField extends JComponent implements DocumentListener, ActionListener
	{

		private static final long serialVersionUID = 1L;

		private final String[] nameFilter = {
				"TOUT TYPE",
				PaCo._A,
				PaCo._C,
				PaCo._M,
				PaCo._M_FIXED,
				PaCo._M_GROUPED,
				PaCo._T,
				PaCo._T_CA,
				PaCo._T_GROUPED,
				PaCo.ASCII
		};

		private final JComboBox<String> typeFilter;
		private final JTextField txtFiltre;
		private final JButton oldSearchBt;
		private JPopupMenu oldSearchMenu;
		private LinkedList<String> oldSearchItem;

		public FilterField() {
			super();
			setLayout(new BorderLayout());

			typeFilter = new JComboBox<>(nameFilter);
			((JLabel)typeFilter.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
			typeFilter.addActionListener(this);

			txtFiltre = new JTextField(20);
			txtFiltre.getDocument().addDocumentListener(this);
			txtFiltre.addActionListener(this);

			oldSearchBt = new JButton(new ImageIcon(getClass().getResource(ICON_OLD_SEARCH)));
			oldSearchBt.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			oldSearchBt.setToolTipText("Historique de filtre");
			oldSearchBt.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					popMenu(e.getX(), e.getY());
				}
			});

			add(typeFilter, BorderLayout.WEST);
			add(txtFiltre, BorderLayout.CENTER);
			add(oldSearchBt, BorderLayout.EAST);

			oldSearchItem = new LinkedList<String>();
		}

		public void popMenu(int x, int y)
		{
			oldSearchMenu = new JPopupMenu();
			Iterator<String> it = oldSearchItem.iterator();
			while (it.hasNext()) {
				oldSearchMenu.add(new OldSearchAct(it.next().toString()));
			}
			oldSearchMenu.show(oldSearchBt, x, y);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == txtFiltre)
			{
				oldSearchItem.addFirst(txtFiltre.getText());
				if (oldSearchItem.size() > 10)
				{
					oldSearchItem.removeLast();
				}
			}
			if (e.getSource() == typeFilter)
			{

				getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
		}

	}

	private class OldSearchAct extends AbstractAction
	{

		private static final long serialVersionUID = 1L;

		private String terme;

		public OldSearchAct(String terme) {
			this.terme = terme;
			putValue(javax.swing.Action.NAME, terme);
		}

		@Override
		public String toString() {
			return terme;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			getFilterField().txtFiltre.setText(terme);

		}

	}

	public final FilterField getFilterField() {
		return filterField;
	}

	public final void clearFilter()
	{
		getFilterField().txtFiltre.setText("");
		getFilterField().typeFilter.getModel().setSelectedItem("TOUT TYPE");
	}
}
