/*
 * Creation : 27 juin 2017
 */
package cdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import visu.SWToolsMain;

public final class ExportUtils {

    public static final Boolean toExcel(Cdf cdf, final File file) {

        WritableWorkbook workbook = null;

        try {
            workbook = Workbook.createWorkbook(file);

            final WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            final WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

            final WritableCellFormat borderFormat = new WritableCellFormat();
            borderFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            final WritableCellFormat axisFormat = new WritableCellFormat(arial10Bold);
            axisFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            axisFormat.setBackground(Colour.VERY_LIGHT_YELLOW);

            final WritableSheet shtInfo = workbook.createSheet("Infos", 0);

            writeCell(shtInfo, 0, 0, "Nom du fichier : " + cdf.getName(), arial10format);
            writeCell(shtInfo, 0, 1, "Nombre de variables : " + String.valueOf(cdf.getNbLabel()), arial10format);
            writeCell(shtInfo, 0, 2, "Liste des variables : ", arial10format);

            final WritableSheet shtScore = workbook.createSheet("Scores", 2);

            writeCell(shtScore, 0, 0, "Score moyen du fichier : " + cdf.getAvgScore(), arial10format);
            writeCell(shtScore, 0, 2, "0% " + "(" + cdf.getRepartitionScore().get(0) + ")", arial10format);
            writeCell(shtScore, 1, 2, "25% " + "(" + cdf.getRepartitionScore().get(25) + ")", arial10format);
            writeCell(shtScore, 2, 2, "50% " + "(" + cdf.getRepartitionScore().get(50) + ")", arial10format);
            writeCell(shtScore, 3, 2, "75% " + "(" + cdf.getRepartitionScore().get(75) + ")", arial10format);
            writeCell(shtScore, 4, 2, "100% " + "(" + cdf.getRepartitionScore().get(100) + ")", arial10format);

            final WritableSheet sheet = workbook.createSheet("Export", 1);

            int row = 0;
            int cnt = 0;
            int cnt0 = 0;
            int cnt25 = 0;
            int cnt50 = 0;
            int cnt75 = 0;
            int cnt100 = 0;

            for (Variable var : cdf.getListLabel()) {

                shtInfo.addHyperlink(new WritableHyperlink(0, 3 + cnt, var.getShortName(), sheet, 0, row));

                switch (var.getLastScore()) {
                case 0:
                    writeCell(shtScore, 0, cnt0 + 3, var.getShortName(), new WritableCellFormat());
                    cnt0++;
                    break;
                case 25:
                    writeCell(shtScore, 1, cnt25 + 3, var.getShortName(), new WritableCellFormat());
                    cnt25++;
                    break;
                case 50:
                    writeCell(shtScore, 2, cnt50 + 3, var.getShortName(), new WritableCellFormat());
                    cnt50++;
                    break;
                case 75:
                    writeCell(shtScore, 3, cnt75 + 3, var.getShortName(), new WritableCellFormat());
                    cnt75++;
                    break;
                case 100:
                    writeCell(shtScore, 4, cnt100 + 3, var.getShortName(), new WritableCellFormat());
                    cnt100++;
                    break;
                default:
                    break;
                }

                if (var instanceof Scalaire) {
                    Scalaire variableType = (Scalaire) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);
                    row += 1;
                    writeCell(sheet, 0, row, variableType.getValue(), borderFormat);
                    row += 2;
                }
                if (var instanceof Axis) {
                    Axis variableType = (Axis) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    int col = 0;
                    row += 1;
                    for (short x = 0; x < variableType.getDim(); x++) {
                        writeCell(sheet, col, row, variableType.getzValues(x), borderFormat);
                        col += 1;
                    }
                    row += 2;
                }
                if (var instanceof Curve) {
                    Curve variableType = (Curve) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    for (byte y = 0; y < 2; y++) {
                        int col = 0;
                        row += 1;
                        for (short x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0) {
                                writeCell(sheet, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
                            }

                            col += 1;
                        }

                    }
                    row += 2;
                }

                if (var instanceof ValueBlock) {
                    ValueBlock variableType = (ValueBlock) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    for (byte y = 0; y < 2; y++) {
                        int col = 0;
                        row += 1;
                        for (short x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0) {
                                writeCell(sheet, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
                            }
                            col += 1;
                        }

                    }
                    row += 2;
                }

                if (var instanceof Map) {
                    Map variableType = (Map) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    for (short y = 0; y < variableType.getDimY(); y++) {
                        int col = 0;
                        row += 1;
                        for (short x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0 | x == 0) {
                                writeCell(sheet, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
                            }
                            col += 1;
                        }
                    }
                    row += 2;
                }
                cnt += 1;
            }

            workbook.write();
            workbook.close();

        } catch (IOException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
        } catch (WriteException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
            if (e instanceof RowsExceededException) {
                JOptionPane.showMessageDialog(null, "Trop de variables à exporter !", "ERREUR", JOptionPane.ERROR_MESSAGE);
                if (workbook != null)
                    try {
                        workbook.close();
                    } catch (WriteException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                return false;
            }
        }
        return true;
    }

    private static final void writeCell(WritableSheet sht, int col, int row, String txtValue, WritableCellFormat format)
            throws RowsExceededException, WriteException {
        try {
            final Double value = Double.parseDouble(txtValue);
            sht.addCell(new Number(col, row, value, format));
        } catch (NumberFormatException e) {
            sht.addCell(new Label(col, row, txtValue, format));
        }

    }

    public static final Boolean toText(Cdf cdf, final File file) {
    	
    	PrintWriter printWriter = null;
    	
        try {
        	printWriter = new PrintWriter(file);

            printWriter.println(" -----------------------");
            printWriter.println("| EXPORT TO TEXT FORMAT |");
            printWriter.println(" -----------------------");
            printWriter.println();
            printWriter.println("Nom du fichier : " + cdf.getName());
            printWriter.println("Nombre de label(s) : " + cdf.getNbLabel());
            printWriter.println();

            for (Variable var : cdf.getListLabel()) {
                printWriter.println();

                printWriter.println(var.toString());
            }

            printWriter.println();
            printWriter.println();
            printWriter.println(" -----");
            printWriter.println("| FIN |");
            printWriter.println(" -----");
            printWriter.println();
            printWriter.println("Fichier cree par SWTools, " + new Date().toString());

        } catch (FileNotFoundException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
            return false;
        }
        finally {
        	if (printWriter != null) printWriter.close();
		}
        return true;

    }

    public static final Boolean toM(Cdf cdf, final File file) {
    	
    	PrintWriter printWriter = null;
    	
        try {
        	printWriter = new PrintWriter(file);

            printWriter.println("% --------------------");
            printWriter.println("%| EXPORT TO M FORMAT |");
            printWriter.println("% --------------------");
            printWriter.println();
            printWriter.println("% Nom du fichier : " + cdf.getName());
            printWriter.println("% Nombre de label(s) : " + cdf.getNbLabel());
            printWriter.println();

            for (Variable var : cdf.getListLabel()) {
                printWriter.println();

                printWriter.println(var.toMFormat());
            }
        } catch (FileNotFoundException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
            return false;
        }
        finally {
        	printWriter.close();
		}
        return true;
    }

}
