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

public final class ValueBlock extends Variable {

    private String[][] values;
    private JPanel panel;
    private String[] xValues;
    private String[] zValues;

    public ValueBlock(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;

        xValues = new String[values[0].length];
        for (int i = 0; i < xValues.length; i++) {
            xValues[i] = Utilitaire.cutNumber(values[0][i]);
        }

        zValues = new String[values[0].length];
        for (int i = 0; i < zValues.length; i++) {
            zValues[i] = Utilitaire.cutNumber(values[1][i]);
        }

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("\n");
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < this.getDimX(); x++) {
                sb.append(this.getValue(y, x) + "\t");
            }
            sb.append("\n");
        }
        return super.toString() + "Valeurs :" + sb.toString();
    }

    public String getUnitX() {
        return super.getSwUnitRef()[0];
    }

    public String getUnitZ() {
        return super.getSwUnitRef()[1];
    }

    public String[][] getValues() {
        return values;
    }

    public String getValue(int col, int row) {
        return Utilitaire.cutNumber(values[col][row]);
    }

    public int getDimX() {
        return values[0].length;
    }

    @Override
    public void initVariable() {
        panel = new JPanel(new GridLayout(2, getDimX(), 1, 1));
        panel.setLayout(new GridLayout(2, getDimX(), 1, 1));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);
        JLabel[] valueViewX = new JLabel[getDimX()];
        JLabel[] valueViewY = new JLabel[getDimX()];
        for (int i = 0; i < valueViewX.length; i++) {
            valueViewX[i] = new JLabel(getValue(0, i));
            panel.add(valueViewX[i]);
            valueViewX[i].setFont(new Font(null, Font.BOLD, valueViewX[i].getFont().getSize()));
            valueViewX[i].setOpaque(true);
            valueViewX[i].setBackground(Color.LIGHT_GRAY);
            valueViewX[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewX[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
        for (int i = 0; i < valueViewY.length; i++) {
            valueViewY[i] = new JLabel(getValue(1, i));
            panel.add(valueViewY[i]);
            valueViewY[i].setOpaque(true);
            valueViewY[i].setBackground(Color.LIGHT_GRAY);
            valueViewY[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewY[i].setHorizontalAlignment(SwingConstants.CENTER);

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
