/*
 * Creation : 27 janv. 2017
 */
package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.im.InputContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.LayerUI;
import javax.swing.text.JTextComponent;

import cdf.ComAxis;
import cdf.CdfUtils;
import cdf.ListModelLabel;
import cdf.TypeVariable;
import cdf.Variable;
import utils.Utilitaire;

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
        // Permet de ne pas calculer la taille de chaque item ==> Gain de temps au chargement de la liste
        setFixedCellHeight(38);
        setFixedCellWidth(200);
        // ...
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
        public void mouseReleased(final MouseEvent e) {
            if (e.isPopupTrigger() && ListLabel.this.getSelectedValue() != null
                    && ListLabel.this.locationToIndex(e.getPoint()) == ListLabel.this.getSelectedIndex()) {

                final Variable selectedVariable = ListLabel.this.getSelectedValue();

                final JPopupMenu menu = new JPopupMenu();
                final JMenu menuCopy = new JMenu("Copier dans le presse-papier");
                JMenuItem subMenu = new JMenuItem("Format image", new ImageIcon(getClass().getResource(ICON_IMAGE)));
                subMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedVariable.copyImgToClipboard();
                    }
                });
                menuCopy.add(subMenu);
                menuCopy.addSeparator();
                subMenu = new JMenuItem("Format texte", new ImageIcon(getClass().getResource(ICON_TEXT)));
                subMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedVariable.copyTxtToClipboard();
                    }
                });
                menuCopy.add(subMenu);
                menu.add(menuCopy);

                if (selectedVariable instanceof ComAxis) {
                    final JMenuItem menuShowDependency = new JMenuItem("Montrer les variables dependantes");
                    menuShowDependency.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent ae) {

                            final String res = CdfUtils.showAxisDependency(ListLabel.this.getModel().getList(), selectedVariable);

                            final AxisDialog dial = new AxisDialog(res);
                            dial.setLocationRelativeTo(e.getComponent());
                            dial.setLocation(e.getXOnScreen(), e.getYOnScreen());
                            dial.setVisible(true);
                        }
                    });
                    menu.add(menuShowDependency);
                }

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    protected final class FilterField extends JComponent implements DocumentListener, ActionListener {

        private static final long serialVersionUID = 1L;

        private final JComboBox<TypeVariable> typeFilter;
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

            typeFilter = new JComboBox<TypeVariable>();
            populateFilter(EnumSet.noneOf(TypeVariable.class));
            ((JLabel) typeFilter.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            typeFilter.addActionListener(this);

            txtFiltre = new JTextField(20);
            txtFiltre.setToolTipText("Clicker 'Entrer' pour enregistrer le filtre dans l'historique");
            txtFiltre.getDocument().addDocumentListener(this);
            txtFiltre.addActionListener(this);
            txtFiltre.setTransferHandler(new TransfertLab());

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

        public final void populateFilter(Set<TypeVariable> list) {

            final DefaultComboBoxModel<TypeVariable> cbModel = (DefaultComboBoxModel<TypeVariable>) typeFilter.getModel();

            if (typeFilter.getModel().getSize() > 0) {
                cbModel.removeAllElements();
            }

            cbModel.addElement(null);

            for (TypeVariable s : list) {
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
                getModel().setFilter((TypeVariable) typeFilter.getSelectedItem(), txtFiltre.getText().toLowerCase().split(","));
            }
        }

        private final void doFilter() {
            Variable selectedVar = null;
            if (getSelectedIndex() > -1) {
                selectedVar = getModel().getElementAt(getSelectedIndex());
            }
            if (selectedVar != null) {
                clearSelection();

                final String filter = txtFiltre.getText().toLowerCase();

                if (filter.indexOf(",") > -1) {
                    String[] filters = filter.trim().split(",");
                    getModel().setFilter((TypeVariable) typeFilter.getSelectedItem(), filters);
                } else {
                    getModel().setFilter((TypeVariable) typeFilter.getSelectedItem(), new String[] { filter });
                }

                setSelectedIndex(0);
                if (getModel().getSize() > 0) {
                    setSelectedValue(selectedVar, true);
                } else {
                    setSelectedIndex(-1);
                }

            } else {

                final String filter = txtFiltre.getText().toLowerCase();

                if (filter.indexOf(",") > -1) {
                    String[] filters = filter.trim().split(",");
                    getModel().setFilter((TypeVariable) typeFilter.getSelectedItem(), filters);
                } else {
                    getModel().setFilter((TypeVariable) typeFilter.getSelectedItem(), new String[] { filter });
                }

            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            doFilter();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            doFilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            doFilter();
        }

        private final class TransfertLab extends TransferHandler {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                JTextComponent c = (JTextComponent) comp;
                if (!(c.isEditable() && c.isEnabled())) {
                    return false;
                }
                return (getFlavor(transferFlavors) != null);
            }

            @Override
            public boolean importData(JComponent comp, Transferable t) {
                if (comp instanceof JTextComponent) {
                    DataFlavor flavor = getFlavor(t.getTransferDataFlavors());

                    if (flavor != null) {
                        InputContext ic = comp.getInputContext();
                        if (ic != null) {
                            ic.endComposition();
                        }

                        BufferedReader buf = null;

                        try {

                            if (flavor.equals(DataFlavor.stringFlavor)) {
                                String data = (String) t.getTransferData(flavor);

                                ((JTextComponent) comp).replaceSelection(data);

                            } else {

                                List<?> dropFiles = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
                                File fLab = (File) dropFiles.get(0);

                                if (fLab.exists() && Utilitaire.getExtension(fLab).equals(Utilitaire.LAB)) {

                                    buf = new BufferedReader(new FileReader(fLab));
                                    final StringBuilder sb = new StringBuilder();
                                    String line;

                                    while ((line = buf.readLine()) != null) {
                                        if (!"[Label]".equals(line) && !line.isEmpty()) {
                                            sb.append(line + ",");
                                        }
                                    }

                                    txtFiltre.setText(sb.toString());
                                }

                            }

                            return true;

                        } catch (UnsupportedFlavorException ufe) {

                        } catch (IOException ioe) {

                        } finally {
                            if (buf != null) {
                                try {
                                    buf.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                return false;
            }

            private DataFlavor getFlavor(DataFlavor[] flavors) {
                if (flavors != null) {
                    for (DataFlavor flavor : flavors) {
                        if (flavor.equals(DataFlavor.stringFlavor) || flavor.equals(DataFlavor.javaFileListFlavor)) {
                            return flavor;
                        }
                    }
                }
                return null;
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

    private final class AxisDialog extends JDialog {
        private static final long serialVersionUID = 1L;

        private final JTextPane txtPane = new JTextPane();

        public AxisDialog(String txt) {

            setTitle("VARIABLE(S) DEPENDANTE(S)");
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setModal(true);

            txtPane.setEditable(false);
            txtPane.setText(txt);
            add(new JScrollPane(txtPane));
            setMinimumSize(new Dimension(250, 10));
            this.pack();
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

            if (ListLabel.this.getFilterField().txtFiltre.getText().length() == 0 && !ListLabel.this.getFilterField().txtFiltre.isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = c.getHeight();
                int pad = 4;

                g2.setPaint(Color.GRAY);
                g2.drawString("Taper une partie du nom (ex : trb,air...) ou glisser un lab", pad, h / 2 + pad);

                g2.dispose();

                return;
            }

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

                return;
            }

            c.repaint();
        }
    }
}
