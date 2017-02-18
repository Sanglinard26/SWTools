package paco;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public abstract class Variable extends MouseAdapter {

    private String shortName;
    private String category;
    private String swFeatureRef;
    private String[][] swCsHistory;

    public Variable(String shortName, String category, String swFeatureRef, String[][] swCsHistory) {
        this.shortName = shortName;
        this.category = category;
        this.swFeatureRef = swFeatureRef;
        this.swCsHistory = swCsHistory;
    }

    public String getShortName() {
        return shortName;
    }

    public String getCategory() {
        return category;
    }

    public String getSwFeatureRef() {
        return swFeatureRef;
    }

    public String[][] getSwCsHistory() {
        return swCsHistory;
    }
    
    public abstract void initVariable();

    public abstract Component showView();

    public abstract void exportToExcel() throws RowsExceededException, WriteException, IOException;

    public abstract void exportToPicture();
    
    public abstract void copyToClipboard();

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem menuItem;
            menuItem = new JMenuItem("Exporter vers Excel");
            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        exportToExcel();
                    } catch (WriteException | IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            menu.add(menuItem);
            menuItem = new JMenuItem("Exporter en image");
            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    exportToPicture();
                }
            });
            menu.add(menuItem);
            menuItem = new JMenuItem("Copier dans le presse-papier");
            menuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    copyToClipboard();
                }
            });
            menu.add(menuItem);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

}
