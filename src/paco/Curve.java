package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

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

public class Curve extends Variable {

    private String[][] values;
    private JPanel panel;

    public Curve(String shortName, String category, String swFeatureRef, String[][] swCsHistory, String[][] values) {
        super(shortName, category, swFeatureRef, swCsHistory);
        this.values = values;

    }

    public String[][] getValues() {
        return this.values;
    }

    public String getValue(int x, int y) {
        return Utilitaire.cutNumber(values[x][y]);
    }

    public int getDimX() {
        return values[0].length;
    }

    @Override
    public Component showView() {
        initLabel();
        return panel;
    }

    private void initLabel() {
        panel = new JPanel(new GridLayout(2, getDimX(), 1, 1));
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
    public void exportToExcel() throws RowsExceededException, WriteException, IOException {
        WritableWorkbook workbook = Workbook.createWorkbook(new File("C:/" + this.getShortName() + ".xls"));
        WritableSheet sheet = workbook.createSheet("Export", 0);
        WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

        sheet.addCell(new Label(0, 0, this.getShortName(), arial10format));
        for (int x = 0; x < getDimX(); x++) {
            for (int y = 0; y < 2; y++) {
                sheet.addCell(new Label(x, y + 1, this.getValue(y, x)));
            }
        }

        workbook.write();
        workbook.close();

    }

    @Override
    public void exportToPicture() {
        // TODO Auto-generated method stub

    }

}
