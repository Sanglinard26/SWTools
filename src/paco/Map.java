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

public final class Map extends Variable {

    private String[][] values;
    private JPanel panel;
    private String[] xValues;
    private String[] yValues;
    private String[][] zValues;

    private Double minZValue = Double.POSITIVE_INFINITY;
    private Double maxZValue = Double.NEGATIVE_INFINITY;

    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;

        xValues = new String[values[0].length - 1];
        for (int i = 0; i < xValues.length; i++) {
            xValues[i] = Utilitaire.cutNumber(values[0][i + 1]);
        }

        yValues = new String[values.length - 1];
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = Utilitaire.cutNumber(values[i + 1][0]);
        }

        zValues = new String[yValues.length][xValues.length];
        for (int x = 0; x < xValues.length; x++) {
            for (int y = 0; y < yValues.length; y++) {
                zValues[y][x] = Utilitaire.cutNumber(values[y + 1][x + 1]);

                try {

                    if (Double.parseDouble(values[y + 1][x + 1]) < minZValue) {
                        minZValue = Double.parseDouble(values[y + 1][x + 1]);
                    }

                    if (Double.parseDouble(values[y + 1][x + 1]) > maxZValue) {
                        maxZValue = Double.parseDouble(values[y + 1][x + 1]);
                    }
                } catch (NumberFormatException e) {
                    minZValue = Double.NaN;
                    maxZValue = Double.NaN;
                }

            }
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");

        for (int y = 0; y < this.getDimY(); y++) {
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

    public String getUnitY() {
        return super.getSwUnitRef()[1];
    }

    public String getUnitZ() {
        return super.getSwUnitRef()[2];
    }

    public String[] getxValues() {
        return xValues;
    }

    public String[] getyValues() {
        return yValues;
    }

    public String getzValue(int col, int row) {
        return Utilitaire.cutNumber(zValues[col][row]);
    }

    public Double getMaxZValue() {
        return maxZValue;
    }

    public Double getMinZValue() {
        return minZValue;
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

    public int getDimY() {
        return values.length;
    }

    @Override
    public Component showView() {
        initVariable();
        return panel;
    }

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

    @Override
    public void initVariable() {
        panel = new JPanel(new GridLayout(getDimY(), getDimX(), 1, 1));
        panel.setLayout(new GridLayout(getDimY(), getDimX(), 1, 1));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel.addMouseListener(this);
        JLabel[] valueView = new JLabel[getDimX() * (getDimY() - 1)];

        for (int y = 0; y < getDimY(); y++) {
            for (int x = 0; x < getDimX(); x++) {
                valueView[y * x] = new JLabel(getValue(y, x));
                panel.add(valueView[y * x]);

                if (y == 0 | x == 0) {
                    valueView[y * x].setFont(new Font(null, Font.BOLD, valueView[y * x].getFont().getSize()));
                }
                valueView[y * x].setOpaque(true);
                valueView[y * x].setBackground(Color.LIGHT_GRAY);
                valueView[y * x].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                valueView[y * x].setHorizontalAlignment(SwingConstants.CENTER);

            }
        }

    }

}
