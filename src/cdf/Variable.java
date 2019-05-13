package cdf;

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

import javax.swing.JComponent;
import javax.swing.JPanel;

import gui.TableView;

public abstract class Variable {

    private final String longName;
    private final String shortName;
    private final TypeVariable type;
    private final String swFeatureRef;
    private final String[] swUnitRef;
    private final History[] swCsHistory;
    
    protected Values values;

    private static TableView tableView;
    private static JPanel panel;

    private static HashMap<String, Integer> scores;

    public Variable(String shortName, String longName, TypeVariable type, String swFeatureRef, String[] swUnitRef, History[] swCsHistory) {
    	
    	initScores();
    	
        this.shortName = shortName;
        this.longName = longName;
        this.type = type;
        this.swFeatureRef = swFeatureRef;
        this.swUnitRef = swUnitRef;
        this.swCsHistory = swCsHistory;
    }
    
    private final static void initScores()
    {
    	scores = new HashMap<String, Integer>(6);
    	scores.put("---", 0);
    	scores.put("changed", 0);
    	scores.put("prelimcalibrated", 25);
    	scores.put("calibrated", 50);
    	scores.put("checked", 75);
    	scores.put("completed", 100);
    }

    public final String getShortName() {
        return shortName;
    }

    public final String getLongName() {
        return longName;
    }

    public final TypeVariable getCategory() {
        return type;
    }

    public final String getSwFeatureRef() {
        return swFeatureRef;
    }

    public final String[] getSwUnitRef() {
        return swUnitRef;
    }

    public final History[] getSwCsHistory() {
        return swCsHistory;
    }

    public final int getLastScore() {
        return swCsHistory.length > 0 ? scores.get(swCsHistory[swCsHistory.length - 1].getScore().toLowerCase()) : 0;
    }

    @Override
    public boolean equals(Object paramObject) {
        return this.getShortName().equals(((Variable) paramObject).getShortName());
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Nom : " + this.shortName + "\n");
        sb.append("Description : " + this.longName + "\n");
        sb.append("Fonction : " + this.swFeatureRef + "\n");
        sb.append("Unite : ");
        for (String unit : this.swUnitRef) {
            sb.append("[" + unit + "]");
        }
        sb.append("\nValeurs:\n");
        
        for (short y = 0; y < values.getDimY(); y++) {
            for (short x = 0; x < values.getDimX(); x++) {
                sb.append(this.getValue(y, x) + "\t");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

    public final double getChecksum()
    {
    	double valCheck = 0;
        String value;

        for (short y = 0; y < values.getDimY(); y++) {
            for (short x = 0; x < values.getDimX(); x++) {
                value = getValue(y, x);
                if (value != null) {
                    valCheck += value.hashCode();
                }
            }
        }

        return valCheck;
    }

    public abstract String toMFormat(boolean transpose);

    public final Values getValues()
    {
    	return this.values;
    }
    
    public final String getValue(int col, int row) {
        return values.getValue(col, row);
    }

    public final JComponent showValues() {

        if (tableView == null) {
            tableView = new TableView(new TableModelView());
        }

        tableView.getModel().setData(getValues());
        tableView.getDefaultRenderer(Object.class).colorMap(this);
        TableView.adjustCells(tableView);

        if (panel == null) {
            panel = new JPanel(new GridLayout(1, 1));
            panel.add(tableView);
        }

        return panel;
    }

    public final void copyTxtToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new TxtTransfert(this.toString()), null);
    }

    private final class TxtTransfert implements Transferable {
        private final String s;

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
        private final Image img;

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
