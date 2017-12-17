package chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.orsoncharts.Resources;

public final class PanelXYChart extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final String ICON_IMAGE = "/image_icon_24.png";
    private static final String ICON_COPY = "/copy_icon_24.png";

    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;

    private final PanelXYPlot xyPlot;
    private Serie[] listSerieName;

    private ListLegend legendList;

    public PanelXYChart(PanelXYPlot xyPlot, int position, Boolean legend) {
        this.xyPlot = xyPlot;

        setLayout(new BorderLayout());

        add(xyPlot, BorderLayout.CENTER);

        if (legend) {
            listSerieName = new Serie[xyPlot.getSeriesCollection().getSeriesCount()];

            for (short i = 0; i < xyPlot.getSeriesCollection().getSeriesCount(); i++) {
                listSerieName[i] = xyPlot.getSeriesCollection().getSerie(i);
            }

            legendList = new ListLegend(listSerieName);
            legendList.setBackground(Color.WHITE);
            legendList.addListSelectionListener(new ListEvent());

            switch (position) {
            case LEFT_POSITION:
                add(new JScrollPane(legendList), BorderLayout.EAST);
                break;
            case RIGHT_POSITION:
                add(new JScrollPane(legendList), BorderLayout.WEST);
                break;
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final JPopupMenu menu = new JPopupMenu();

                    final JMenuItem menuExpJpg = new JMenuItem("Export JPEG", new ImageIcon(getClass().getResource(ICON_IMAGE)));
                    menuExpJpg.addActionListener(new JPEGExport());
                    menu.add(menuExpJpg);

                    final JMenuItem menuCopyClipboard = new JMenuItem("Copier dans le presse-papier",
                            new ImageIcon(getClass().getResource(ICON_COPY)));
                    menuCopyClipboard.addActionListener(new ChartToClipboard());
                    menu.add(menuCopyClipboard);

                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

    }

    private final class ListEvent implements ListSelectionListener {

        private SeriesCollection serieCollection;

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting() & !legendList.isSelectionEmpty()) {

                if (serieCollection != null) {
                    serieCollection.removeAllSeries();
                } else {
                    serieCollection = new SeriesCollection();
                }

                for (short i = 0; i < legendList.getSelectedValuesList().size(); i++) {
                    serieCollection.addSerie(legendList.getSelectedValuesList().get(i));
                }
                xyPlot.setSeriesCollection(serieCollection);
            }
        }
    }

    private final class ChartToClipboard extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            final BufferedImage img = new BufferedImage(PanelXYChart.this.getWidth(), PanelXYChart.this.getHeight(), BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = img.createGraphics();
            PanelXYChart.this.printAll(g);
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

    private final class JPEGExport extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public JPEGExport() {
            super("Export JPEG");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter(Resources.localString("JPG_FILE_FILTER_DESCRIPTION"),
                    new String[] { "jpg" });
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);

            int option = fileChooser.showSaveDialog(PanelXYChart.this);
            if (option == 0) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (!filename.endsWith(".jpg")) {
                    filename = filename + ".jpg";
                }
                Dimension2D size = PanelXYChart.this.getSize();
                int w = (int) size.getWidth();
                int h = (int) size.getHeight();
                BufferedImage image = new BufferedImage(w, h, 1);

                Graphics2D g2 = image.createGraphics();
                PanelXYChart.this.printAll(g2);
                try {
                    ImageIO.write(image, "jpeg", new File(filename));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

}
