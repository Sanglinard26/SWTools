package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tools.Utilitaire;

public class Map extends Variable {

    private String[][] values;
    private JPanel panel;

    public Map(String shortName, String category, String swFeatureRef, String[][] swCsHistory, String[][] values) {
        super(shortName, category, swFeatureRef, swCsHistory);
        this.values = values;

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
        initLabel();
        return panel;
    }

    private void initLabel() {
        panel = new JPanel(new GridLayout(getDimY(), getDimX(), 1, 1));
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

    @Override
    public void exportToExcel() throws RowsExceededException, WriteException, IOException {
        WritableWorkbook workbook = Workbook.createWorkbook(new File("C:/" + this.getShortName() + ".xls"));
        WritableSheet sheet = workbook.createSheet("Export", 0);
        WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

        sheet.addCell(new Label(0, 0, this.getShortName(), arial10format));
        for (int x = 0; x < getDimX(); x++) {
            for (int y = 0; y < getDimY(); y++) {
                sheet.addCell(new Label(x, y + 1, this.getValue(y, x)));
            }
        }

        workbook.write();
        workbook.close();

    }

    @Override
    public void exportToPicture() {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        panel.printAll(g);
        g.dispose();
        try {
            ImageIO.write(image, "jpg", new File("C:/" + this.getShortName() + ".jpg"));
        } catch (IOException exp) {
            exp.printStackTrace();
        }

    }

}
