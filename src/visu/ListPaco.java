/*
 * Creation : 6 avr. 2017
 */
package visu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import paco.ListModelPaco;
import paco.PaCo;
import tools.Preference;

public class ListPaco extends JList<PaCo> implements KeyListener {

    private static final long serialVersionUID = 1L;
    private static final String ICON_EXCEL = "/excel_icon_16.png";
    private static final String ICON_TEXT = "/text_icon_16.png";

    public ListPaco(ListModelPaco dataModel) {
        super(dataModel);
        setCellRenderer(new ListPacoRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addKeyListener(this);
        addMouseListener(new ListMouseListener());
    }

    @Override
    public ListModelPaco getModel() {
        return (ListModelPaco) super.getModel();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 127 & this.getSelectedIndex() > -1) // touche suppr
        {
            this.getModel().removePaco(this.getSelectedIndex());
            this.clearSelection();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    private class ListMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() & ListPaco.this.getModel().getSize() > 0) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem menuItem;
                if (ListPaco.this.locationToIndex(e.getPoint()) == ListPaco.this.getSelectedIndex()) {

                    menuItem = new JMenuItem("Supprimer");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListPaco.this.getModel().removePaco(ListPaco.this.getSelectedIndex());
                            ListPaco.this.clearSelection();
                            PanelPaCo.razUI();
                        }
                    });
                    menu.add(menuItem);
                    menuItem = new JMenuItem("Exporter le PaCo en txt", new ImageIcon(getClass().getResource(ICON_TEXT)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
                            fileChooser.setDialogTitle("Enregistement du PaCo");
                            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier texte (*.txt)", "txt"));
                            fileChooser.setSelectedFile(new File(ListPaco.this.getSelectedValue() + ".txt"));
                            final int rep = fileChooser.showSaveDialog(null);

                            if (rep == JFileChooser.APPROVE_OPTION) {
                                ListPaco.this.getSelectedValue().exportToTxt(fileChooser.getSelectedFile());
                                JOptionPane.showMessageDialog(null, "Enregistrement termine !");
                            }

                        }
                    });
                    menu.add(menuItem);
                    menu.addSeparator();
                    menuItem = new JMenuItem("Exporter le PaCo en xls", new ImageIcon(getClass().getResource(ICON_EXCEL)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (true) {
                                final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
                                fileChooser.setDialogTitle("Enregistement du PaCo");
                                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier Excel (*.xls)", "xls"));
                                fileChooser.setSelectedFile(new File(ListPaco.this.getSelectedValue() + ".xls"));
                                final int rep = fileChooser.showSaveDialog(null);

                                if (rep == JFileChooser.APPROVE_OPTION) {
                                    ListPaco.this.getSelectedValue().exportToExcel(fileChooser.getSelectedFile());
                                    JOptionPane.showMessageDialog(null, "Enregistrement termine !");
                                }
                            }

                        }
                    });
                    menu.add(menuItem);

                } else {
                    menuItem = new JMenuItem("Tout supprimer");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListPaco.this.getModel().clearList();
                            ListPaco.this.clearSelection();
                            PanelPaCo.razUI();
                        }
                    });
                    menu.add(menuItem);
                }

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

}
