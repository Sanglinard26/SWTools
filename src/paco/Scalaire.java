package paco;

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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import tools.Utilitaire;

public final class Scalaire extends Variable {

    private String value;
    private static final JPanel panel = new JPanel(new GridLayout(1, 1, 2, 2));
    private static final JLabel valueView = new JLabel();

    public Scalaire(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String value) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.value = Utilitaire.cutNumber(value);
    }

    @Override
    public String toString() {
        return super.toString() + "Valeur : " + this.value;
    }

    public String getValue() {
        return Utilitaire.cutNumber(value);
    }

    public String getUnit() {
        return super.getSwUnitRef()[0];
    }

    @Override
    public Component showView() {
        initVariable();
        return panel;
    }

    @Override
    public void initVariable() {
        panel.setLayout(new GridLayout(1, 1, 2, 2));
        panel.add(valueView);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);
        valueView.setOpaque(true);
        valueView.setBackground(Color.LIGHT_GRAY);
        valueView.setBorder(new LineBorder(Color.BLACK, 2));
        valueView.setHorizontalAlignment(SwingConstants.CENTER);
        valueView.setText(getValue());
        valueView.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    }

    public void copyToClipboard() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = img.createGraphics();
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
