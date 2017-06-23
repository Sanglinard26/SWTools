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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cdf.Cdf;
import cdf.ListModelCdf;
import tools.Preference;

public final class ListCdf extends JList<Cdf> implements KeyListener {

    private static final long serialVersionUID = 1L;
    private static final String ICON_EXCEL = "/excel_icon_16.png";
    private static final String ICON_TEXT = "/text_icon_16.png";
    private static final String ICON_TRASH = "/corbeille_icon_16.png";

    public ListCdf(ListModelCdf dataModel) {
        super(dataModel);
        setCellRenderer(new ListCdfRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addKeyListener(this);
        addMouseListener(new ListMouseListener());
    }

    @Override
    public ListModelCdf getModel() {
        return (ListModelCdf) super.getModel();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 127 & this.getSelectedIndex() > -1) // touche suppr
        {
            this.getModel().removeCdf(this.getSelectedIndex());
            this.clearSelection();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private final class ListMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() & ListCdf.this.getModel().getSize() > 0) {
                final JPopupMenu menu = new JPopupMenu();
                final JMenu menuExport = new JMenu("Export");
                JMenuItem menuItem;
                if (ListCdf.this.locationToIndex(e.getPoint()) == ListCdf.this.getSelectedIndex()) {

                    menuItem = new JMenuItem("Supprimer ce fichier", new ImageIcon(getClass().getResource(ICON_TRASH)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListCdf.this.getModel().removeCdf(ListCdf.this.getSelectedIndex());
                            ListCdf.this.clearSelection();
                            PanelCDF.razUI();
                        }
                    });
                    menu.add(menuItem);
                    menu.addSeparator();
                    menuItem = new JMenuItem("Exporter le fichier en txt", new ImageIcon(getClass().getResource(ICON_TEXT)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
                            fileChooser.setDialogTitle("Enregistement du fichier");
                            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier texte (*.txt)", "txt"));
                            fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".txt"));
                            final int rep = fileChooser.showSaveDialog(null);

                            if (rep == JFileChooser.APPROVE_OPTION) {
                                ListCdf.this.getSelectedValue().exportToTxt(fileChooser.getSelectedFile());
                                JOptionPane.showMessageDialog(null, "Enregistrement termine !");
                            }

                        }
                    });
                    menuExport.add(menuItem);
                    menuExport.addSeparator();
                    menuItem = new JMenuItem("Exporter le fichier en xls", new ImageIcon(getClass().getResource(ICON_EXCEL)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (true) {
                                final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
                                fileChooser.setDialogTitle("Enregistement du fichier");
                                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier Excel (*.xls)", "xls"));
                                fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".xls"));
                                final int rep = fileChooser.showSaveDialog(null);

                                if (rep == JFileChooser.APPROVE_OPTION) {
                                    ListCdf.this.getSelectedValue().exportToExcel(fileChooser.getSelectedFile());
                                    JOptionPane.showMessageDialog(null, "Enregistrement termine !");
                                }
                            }

                        }
                    });
                    menuExport.add(menuItem);
                    menu.add(menuExport);

                } else {
                    menuItem = new JMenuItem("Supprimer tous les fichiers", new ImageIcon(getClass().getResource(ICON_TRASH)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListCdf.this.getModel().clearList();
                            ListCdf.this.clearSelection();
                            PanelCDF.razUI();
                        }
                    });
                    menu.add(menuItem);
                }

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

}
