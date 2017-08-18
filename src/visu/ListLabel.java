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
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cdf.ListModelLabel;
import cdf.Variable;

public final class ListLabel extends JList<Variable> {

    private static final long serialVersionUID = 1L;

    private static final String ICON_TEXT = "/text_icon_16.png";
    private static final String ICON_IMAGE = "/image_icon_16.png";
    private static final String ICON_OLD_SEARCH = "/oldsearch_24.png";

    private final FilterField filterField;

    public ListLabel(ListModelLabel dataModel) {
        super(dataModel);
        setCellRenderer(new ListLabelRenderer());
        filterField = new FilterField();
        addMouseListener(new ListMouseListener());
    }

    @Override
    public ListModelLabel getModel() {
        return (ListModelLabel) super.getModel();
    }

    private final class ListMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() & ListLabel.this.getSelectedValue() != null
                    & ListLabel.this.locationToIndex(e.getPoint()) == ListLabel.this.getSelectedIndex()) {
                final JPopupMenu menu = new JPopupMenu();
                final JMenu menuCopy = new JMenu("Copier dans le presse-papier");
                JMenuItem subMenu = new JMenuItem("Format image", new ImageIcon(getClass().getResource(ICON_IMAGE)));
                subMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ListLabel.this.getSelectedValue().copyImgToClipboard();
                    }
                });
                menuCopy.add(subMenu);
                menuCopy.addSeparator();
                subMenu = new JMenuItem("Format texte", new ImageIcon(getClass().getResource(ICON_TEXT)));
                subMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ListLabel.this.getSelectedValue().copyTxtToClipboard();
                    }
                });
                menuCopy.add(subMenu);
                menu.add(menuCopy);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    class FilterField extends JComponent implements DocumentListener, ActionListener {

        private static final long serialVersionUID = 1L;

        private final JComboBox<String> typeFilter;
        private final JTextField txtFiltre;
        private final JButton oldSearchBt;
        private JPopupMenu oldSearchMenu;
        private LinkedList<String> oldSearchItem;

        public FilterField() {
            super();
            setLayout(new BorderLayout());

            typeFilter = new JComboBox<String>(new ModelCombo());
            populateFilter(null);
            ((JLabel) typeFilter.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
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

        private class ModelCombo extends DefaultComboBoxModel<String> {

            private static final long serialVersionUID = 1L;

            public ModelCombo() {
                super();
            }
        }

        public final void populateFilter(Vector<String> list) {

            if (typeFilter.getModel().getSize() > 0)
                ((ModelCombo) typeFilter.getModel()).removeAllElements();
            ((ModelCombo) typeFilter.getModel()).addElement("ALL");

            if (list != null)
                for (int i = 0; i < list.size(); i++) {
                    ((ModelCombo) typeFilter.getModel()).addElement(list.get(i));
                }

        }

        public final void popMenu(int x, int y) {
            oldSearchMenu = new JPopupMenu();
            Iterator<String> it = oldSearchItem.iterator();
            while (it.hasNext()) {
                oldSearchMenu.add(new OldSearchAct(it.next().toString()));
            }
            oldSearchMenu.show(oldSearchBt, x, y);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == txtFiltre) {
                oldSearchItem.addFirst(txtFiltre.getText());
                if (oldSearchItem.size() > 10) {
                    oldSearchItem.removeLast();
                }
            }
            if (e.getSource() == typeFilter) {
                clearSelection();
                if (typeFilter.getSelectedItem() != null)
                    getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            clearSelection();
            getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            clearSelection();
            getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            clearSelection();
            getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
        }

    }

    private final class OldSearchAct extends AbstractAction {

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

    public final void clearFilter() {
        getFilterField().txtFiltre.setText("");
        getFilterField().typeFilter.getModel().setSelectedItem("ALL");
    }
}
