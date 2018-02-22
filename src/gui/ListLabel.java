/*
 * Creation : 27 janv. 2017
 */
package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.LayerUI;

import cdf.ListModelLabel;
import cdf.Variable;

public final class ListLabel extends JList<Variable> {

    private static final long serialVersionUID = 1L;

    private static final String ICON_TEXT = "/text_icon_24.png";
    private static final String ICON_IMAGE = "/image_icon_24.png";
    private static final String ICON_OLD_SEARCH = "/oldsearch_24.png";
    private static final String ICON_CLEAR = "/clear_icon_24.png";

    private final FilterField filterField;

    public ListLabel(ListModelLabel dataModel) {
        super(dataModel);
        setCellRenderer(new ListLabelRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
            if (e.isPopupTrigger() && ListLabel.this.getSelectedValue() != null
                    && ListLabel.this.locationToIndex(e.getPoint()) == ListLabel.this.getSelectedIndex()) {
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

    protected class FilterField extends JComponent implements DocumentListener, ActionListener {

        private static final long serialVersionUID = 1L;

        private final JComboBox<String> typeFilter;
        private final JTextField txtFiltre;
        private final JPanel panelBt;
        private final JButton oldSearchBt;
        private final JButton delSearchBt;
        private JPopupMenu oldSearchMenu;
        private LinkedList<String> oldSearchItem;

        public FilterField() {
            super();
            setLayout(new BorderLayout());

            panelBt = new JPanel(new GridLayout(1, 2));

            typeFilter = new JComboBox<String>();
            populateFilter(null);
            ((JLabel) typeFilter.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            typeFilter.addActionListener(this);

            txtFiltre = new JTextField(20);
            txtFiltre.setToolTipText("Clicker 'Entrer' pour enregistrer le filtre dans l'historique");
            txtFiltre.getDocument().addDocumentListener(this);
            txtFiltre.addActionListener(this);

            delSearchBt = new JButton(new ImageIcon(getClass().getResource(ICON_CLEAR)));
            delSearchBt.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            delSearchBt.setToolTipText("Suppression du filtre");
            delSearchBt.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    txtFiltre.setText("");
                }
            });
            panelBt.add(delSearchBt);

            oldSearchBt = new JButton(new ImageIcon(getClass().getResource(ICON_OLD_SEARCH)));
            oldSearchBt.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            oldSearchBt.setToolTipText("Historique de filtre");
            oldSearchBt.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    popMenu(e.getX(), e.getY());
                }
            });
            panelBt.add(oldSearchBt);

            add(typeFilter, BorderLayout.WEST);
            add(new JLayer<JTextField>(txtFiltre, new ValidationLayerUI()), BorderLayout.CENTER);
            add(panelBt, BorderLayout.EAST);

            oldSearchItem = new LinkedList<String>();
        }

        public final void populateFilter(Set<String> list) {

            final DefaultComboBoxModel<String> cbModel = (DefaultComboBoxModel<String>) typeFilter.getModel();

            if (typeFilter.getModel().getSize() > 0)
                cbModel.removeAllElements();

            cbModel.addElement("ALL");

            if (list != null)
                for (String s : list) {
                    cbModel.addElement(s);
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
            Variable selectedVar = null;
            if (getSelectedIndex() > -1) {
                selectedVar = getModel().getElementAt(getSelectedIndex());
            }
            if (selectedVar != null) {
                clearSelection();
                getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
                setSelectedIndex(0);
                if (getModel().getSize() > 0) {
                    setSelectedValue(selectedVar, true);
                } else {
                    setSelectedIndex(-1);
                }

            } else {
                getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            Variable selectedVar = null;
            if (getSelectedIndex() > -1) {
                selectedVar = getModel().getElementAt(getSelectedIndex());
            }
            if (selectedVar != null) {
                clearSelection();
                getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
                setSelectedIndex(0);
                if (getModel().getSize() > 0) {
                    setSelectedValue(selectedVar, true);
                } else {
                    setSelectedIndex(-1);
                }
            } else {
                getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            Variable selectedVar = null;
            if (getSelectedIndex() > -1) {
                selectedVar = getModel().getElementAt(getSelectedIndex());
            }
            if (selectedVar != null) {
                clearSelection();
                getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
                setSelectedIndex(0);
                if (getModel().getSize() > 0) {
                    setSelectedValue(selectedVar, true);
                } else {
                    setSelectedIndex(-1);
                }
            } else {
                getModel().setFilter(typeFilter.getSelectedItem().toString(), txtFiltre.getText().toLowerCase());
            }
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

    final class ValidationLayerUI extends LayerUI<JTextField> {

        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);

            if (ListLabel.this.getFilterField().txtFiltre.getText().length() > 0 && ListLabel.this.getModel().getSize() < 1) {
                Graphics2D g2 = (Graphics2D) g.create();

                // Paint the red X.
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth();
                int h = c.getHeight();
                int s = 16;
                int pad = 4;
                int x = w - pad - s;
                int y = (h - s) / 2;
                g2.setPaint(Color.RED);
                g2.fillRect(x, y, s + 1, s + 1);
                g2.setPaint(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(x, y, x + s, y + s);
                g2.drawLine(x, y + s, x + s, y);

                g2.dispose();
            }
        }
    }
}
