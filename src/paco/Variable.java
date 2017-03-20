package paco;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public abstract class Variable extends MouseAdapter {

    private String longName;
    private String shortName;
    private String category;
    private String swFeatureRef;
    private String[] swUnitRef;
    private String[][] swCsHistory;

    private static final String ICON_TEXT = "/text_icon_16.png";
    private static final String ICON_IMAGE = "/image_icon_16.png";

    public Variable(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory) {
        this.shortName = shortName;
        this.longName = longName;
        this.category = category;
        this.swFeatureRef = swFeatureRef;
        this.swUnitRef = swUnitRef;
        this.swCsHistory = swCsHistory;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getCategory() {
        return category;
    }

    public String getSwFeatureRef() {
        return swFeatureRef;
    }

    public String[] getSwUnitRef() {
        return swUnitRef;
    }

    public String[][] getSwCsHistory() {
        return swCsHistory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nom : " + this.shortName + "\n");
        sb.append("Description : " + this.longName + "\n");
        sb.append("Fonction : " + this.swFeatureRef + "\n");
        return sb.toString();
    }

    public abstract void initVariable();

    public abstract Component showView();

    public abstract void copyToClipboard();

    public void copyTxtToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new TxtTransfert(this.toString()), null);
    }

    class TxtTransfert implements Transferable {
        private String s;

        public TxtTransfert(String s) {
            this.s = s;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return s;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.stringFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.stringFlavor.equals(flavor);
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu menu = new JPopupMenu();
            JMenu menuCopy = new JMenu("Copier dans le presse-papier");
            JMenuItem subMenu = new JMenuItem("Format image", new ImageIcon(getClass().getResource(ICON_IMAGE)));
            subMenu.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    copyToClipboard();
                }
            });
            menuCopy.add(subMenu);
            menuCopy.addSeparator();
            subMenu = new JMenuItem("Format texte", new ImageIcon(getClass().getResource(ICON_TEXT)));
            subMenu.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    copyTxtToClipboard();
                }
            });
            menuCopy.add(subMenu);
            menu.add(menuCopy);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

}
