package cdf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import paco.TableModelView;
import visu.TableView;

public abstract class Variable {

    private final String longName;
    private final String shortName;
    private final String category;
    private final String swFeatureRef;
    private final String[] swUnitRef;
    private final String[][] swCsHistory;

    private JPanel panel;

    private static final HashMap<String, Integer> maturite = new HashMap<String, Integer>(6);

    public Variable(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory) {
        this.shortName = shortName;
        this.longName = longName;
        this.category = category;
        this.swFeatureRef = swFeatureRef;
        this.swUnitRef = swUnitRef;
        this.swCsHistory = swCsHistory;

        maturite.put("---", 0);
        maturite.put("changed", 0);
        maturite.put("prelimcalibrated", 25);
        maturite.put("calibrated", 50);
        maturite.put("checked", 75);
        maturite.put("completed", 100);
    }

    public final String getShortName() {
        return shortName;
    }

    public final String getLongName() {
        return longName;
    }

    public final String getCategory() {
        return category;
    }

    public final String getSwFeatureRef() {
        return swFeatureRef;
    }

    public final String[] getSwUnitRef() {
        return swUnitRef;
    }

    public final String[][] getSwCsHistory() {
        return swCsHistory;
    }

    public final int getLastScore() {
        if (!(swCsHistory.length < 1))
            return maturite.get(swCsHistory[swCsHistory.length - 1][2].toLowerCase());
        return 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Nom : " + this.shortName + "\n");
        sb.append("Description : " + this.longName + "\n");
        sb.append("Fonction : " + this.swFeatureRef + "\n");
        return sb.toString();
    }

    public abstract String[][] getValues();

    public final Component showValues() {
        panel = new JPanel(new GridLayout(1, 1));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        final TableView tableView = new TableView(new TableModelView(), this);
        tableView.getModel().setData(getValues());
        TableView.adjustCells(tableView);

        panel.add(tableView);

        return panel;
    }

    public final void copyTxtToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new TxtTransfert(this.toString()), null);
    }

    private final class TxtTransfert implements Transferable {
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

    public final void copyImgToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = img.createGraphics();
        panel.printAll(g);
        g.dispose();
        clipboard.setContents(new ImgTransfert(img), null);

    }

    private final class ImgTransfert implements Transferable {
        private Image img;

        public ImgTransfert(Image img) {
            this.img = img;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return img;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

    }
}
