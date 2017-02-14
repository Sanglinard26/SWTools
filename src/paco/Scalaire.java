package paco;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import tools.Utilitaire;

public class Scalaire extends Variable {

    private String value;
    private static final JPanel panel = new JPanel(new GridLayout(1, 1, 2, 2));
    private static final JLabel valueView = new JLabel();

    public Scalaire(String shortName, String category, String swFeatureRef, String[][] swCsHistory, String value) {
        super(shortName, category, swFeatureRef, swCsHistory);
        this.value = value;
    }

    public String getValue() {

        return Utilitaire.cutNumber(value);
    }

    @Override
    public Component showView() {
        initLabel();
        return panel;
    }

    private void initLabel() {
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

    @Override
    public void exportToExcel() throws RowsExceededException, WriteException, IOException {

        WritableWorkbook workbook = Workbook.createWorkbook(new File("C:/" + this.getShortName() + ".xls"));
        WritableSheet sheet = workbook.createSheet("Export", 0);
        WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

        sheet.addCell(new Label(0, 0, this.getShortName(), arial10format));
        sheet.addCell(new Label(0, 1, this.getValue()));

        workbook.write();
        workbook.close();

    }

    @Override
    public void exportToPicture() {
        // TODO Auto-generated method stub

    }
}
