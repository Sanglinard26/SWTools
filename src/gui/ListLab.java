/*
 * Creation : 27 janv. 2017
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import lab.Lab;
import lab.ListModelLab;

public final class ListLab extends JList<Lab> implements KeyListener {

    private static final long serialVersionUID = 1L;

    public ListLab(ListModelLab dataModel) {
        super(dataModel);
        setCellRenderer(new ListLabRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addKeyListener(this);
        addMouseListener(new ListMouseListener());
    }

    @Override
    public ListModelLab getModel() {
        return (ListModelLab) super.getModel();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 127 && this.getSelectedIndex() > -1) // touche suppr
        {
            this.getModel().removeLab(this.getSelectedIndex());
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
            if (e.isPopupTrigger() && ListLab.this.getModel().getSize() > 0) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem menuItem;
                if (ListLab.this.locationToIndex(e.getPoint()) == ListLab.this.getSelectedIndex()) {

                    menuItem = new JMenuItem("Supprimer");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListLab.this.getModel().removeLab(ListLab.this.getSelectedIndex());
                            ListLab.this.clearSelection();
                        }
                    });

                } else {
                    menuItem = new JMenuItem("Tout supprimer");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListLab.this.getModel().clearList();
                            ListLab.this.clearSelection();
                        }
                    });

                }
                menu.add(menuItem);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
}
