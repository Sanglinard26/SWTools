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

public final class Axis extends Variable {

    private JPanel panel;
    private final String[] zValues;
    private final int dim;

    public Axis(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.zValues = values;
        this.dim = zValues.length;

    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("\n");

        for (short x = 0; x < dim; x++) {
            sb.append(this.getzValues(x) + "\t");
        }
        sb.append("\n");

        return super.toString() + "Valeurs :" + sb.toString();
    }

    public final int getDim() {
        return dim;
    }

    public final String getUnit() {
        return super.getSwUnitRef()[0];
    }

    public final String getzValues(int x) {
        return zValues[x];
    }

    @Override
    public final void initVariable(Boolean colored) {
        panel = new JPanel(new GridLayout(1, dim, 1, 1));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);

        JLabel valueViewZ;

        for (short i = 0; i < dim; i++) {
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
    public final Component showView(Boolean colored) {
        initVariable(colored);
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
