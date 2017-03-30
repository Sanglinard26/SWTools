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

public final class ValueBlock extends Variable {

    private final String[][] values;
    private JPanel panel;
    private final String[] xValues;
    private final String[] zValues;
    private final int dimX;

    public ValueBlock(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;

        this.xValues = values[0];
        this.zValues = values[1];
        this.dimX = values[0].length;

        // xValues = new String[values[0].length];
        // for (int i = 0; i < xValues.length; i++) {
        // // xValues[i] = Utilitaire.cutNumber(values[0][i]);
        // xValues[i] = values[0][i];
        // }
        //
        // zValues = new String[values[0].length];
        // for (int i = 0; i < zValues.length; i++) {
        // // zValues[i] = Utilitaire.cutNumber(values[1][i]);
        // zValues[i] = values[1][i];
        // }

    }

    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder("\n");
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < dimX; x++) {
                sb.append(this.getValue(y, x) + "\t");
            }
            sb.append("\n");
        }
        return super.toString() + "Valeurs :" + sb.toString();
    }

    public final String getUnitX() {
        return super.getSwUnitRef()[0];
    }

    public final String getUnitZ() {
        return super.getSwUnitRef()[1];
    }

    public final String[][] getValues() {
        return values;
    }

    public final String getValue(int col, int row) {
        // return Utilitaire.cutNumber(values[col][row]);
        return values[col][row];
    }

    public final int getDimX() {
        return dimX;
    }

    @Override
    public final void initVariable() {
        panel = new JPanel(new GridLayout(2, dimX, 1, 1));
        // panel.setLayout(new GridLayout(2, dimX, 1, 1));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);
        // JLabel[] valueViewX = new JLabel[getDimX()];
        // JLabel[] valueViewY = new JLabel[getDimX()];
        // for (int i = 0; i < valueViewX.length; i++) {
        // valueViewX[i] = new JLabel(getValue(0, i));
        // panel.add(valueViewX[i]);
        // valueViewX[i].setFont(new Font(null, Font.BOLD, valueViewX[i].getFont().getSize()));
        // valueViewX[i].setOpaque(true);
        // valueViewX[i].setBackground(Color.LIGHT_GRAY);
        // valueViewX[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // valueViewX[i].setHorizontalAlignment(SwingConstants.CENTER);
        // }
        // for (int i = 0; i < valueViewY.length; i++) {
        // valueViewY[i] = new JLabel(getValue(1, i));
        // panel.add(valueViewY[i]);
        // valueViewY[i].setOpaque(true);
        // valueViewY[i].setBackground(Color.LIGHT_GRAY);
        // valueViewY[i].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // valueViewY[i].setHorizontalAlignment(SwingConstants.CENTER);
        //
        // }

        JLabel valueViewX;
        JLabel valueViewY;

        for (int i = 0; i < dimX; i++) {
            valueViewX = new JLabel(getValue(0, i));
            panel.add(valueViewX);
            valueViewX.setFont(new Font(null, Font.BOLD, valueViewX.getFont().getSize()));
            valueViewX.setOpaque(true);
            valueViewX.setBackground(Color.LIGHT_GRAY);
            valueViewX.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewX.setHorizontalAlignment(SwingConstants.CENTER);
        }
        for (int i = 0; i < dimX; i++) {
            valueViewY = new JLabel(getValue(1, i));
            panel.add(valueViewY);
            valueViewY.setOpaque(true);
            valueViewY.setBackground(Color.LIGHT_GRAY);
            valueViewY.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            valueViewY.setHorizontalAlignment(SwingConstants.CENTER);

        }

    }

    @Override
    public final Component showView() {
        initVariable();
        return panel;
    }

    @Override
    public final void copyToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = img.createGraphics();
        panel.printAll(g);
        g.dispose();
        clipboard.setContents(new ImgTransfert(img), null);

    }

    final class ImgTransfert implements Transferable {
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
