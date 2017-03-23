package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tools.Utilitaire;

public final class Axis extends Variable {

    private JPanel panel;
    private String[] zValues;

    public Axis(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.zValues = values;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");

        for (int x = 0; x < this.getDim(); x++) {
            sb.append(this.getzValues(x) + "\t");
        }
        sb.append("\n");

        return super.toString() + "Valeurs :" + sb.toString();
    }

    public int getDim() {
        return zValues.length;
    }

    public String getUnit() {
        return super.getSwUnitRef()[0];
    }

    public String getzValues(int x) {
        return Utilitaire.cutNumber(zValues[x]);
    }

    @Override
    public void initVariable() {
        panel = new JPanel(new GridLayout(1, getDim(), 1, 1));
        panel.setLayout(new GridLayout(1, getDim(), 1, 1));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);
//        JLabel[] valueViewZ = new JLabel[getDim()];
//        for (int i = 0; i < valueViewZ.length; i++) {
//            valueViewZ[i] = new JLabel(getzValues(i));
//            panel.add(valueViewZ[i]);
//            valueViewZ[i].setFont(new Font(null, Font.BOLD, valueViewZ[i].getFont().getSize()));
//            valueViewZ[i].setOpaque(true);
//            valueViewZ[i].setBackground(Color.LIGHT_GRAY);
//            valueViewZ[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//            valueViewZ[i].setHorizontalAlignment(SwingConstants.CENTER);
//        }
        
        JLabel valueViewZ;
        for (int i = 0; i < getDim(); i++) {
            valueViewZ = new JLabel(getzValues(i));
            panel.add(valueViewZ);
            valueViewZ.setFont(new Font(null, Font.BOLD, valueViewZ.getFont().getSize()));
            valueViewZ.setOpaque(true);
            valueViewZ.setBackground(Color.LIGHT_GRAY);
            valueViewZ.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewZ.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    @Override
    public Component showView() {
        initVariable();
        return panel;
    }

    @Override
    public void copyToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        panel.printAll(g);
        g.dispose();
        clipboard.setContents(new ImgTransfert(img), null);
    }

    class ImgTransfert implements Transferable {
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
