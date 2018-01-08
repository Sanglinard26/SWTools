/*
 * Creation : 12 déc. 2017
 */
package cdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import gui.SWToolsMain;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import paco.Paco;
import tools.Utilitaire;

public final class CdfUtils {

    public static final Cdf comparCdf(Cdf cdfRef, Cdf cdfWork, boolean modeValeur) {
        ArrayList<Variable> listCompa;

        if (cdfRef.getCheckSum() != cdfWork.getCheckSum()) {
            listCompa = new ArrayList<Variable>();
            Variable varCompar;
            Variable varBase = null;
            String[][] copyVal = null;
            String[][] nullHistory = new String[1][4];
            int checkDim = 0;

            for (int i = 0; i < 4; i++) {
                nullHistory[0][i] = "0";
            }

            for (Variable var : cdfRef.getListLabel()) {
                if (cdfWork.getListLabel().contains(var)) {
                    varCompar = cdfWork.getListLabel().get(cdfWork.getListLabel().indexOf(var));
                    if (var.getChecksum() != varCompar.getChecksum()) {

                        checkDim = 0;

                        if (var instanceof Axis) {
                            copyVal = new String[1][((Axis) var).getDim()];
                            for (short x = 0; x < var.getValues()[0].length; x++) {
                                if (modeValeur) {
                                    copyVal[0][x] = var.getValues()[0][x];
                                } else {
                                    copyVal[0][x] = "0";
                                }

                            }
                            varBase = new Axis(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(), var.getSwUnitRef(),
                                    new String[0][0], copyVal);
                        } else if (var instanceof Curve) {
                            copyVal = new String[2][((Curve) var).getDimX()];
                            for (byte y = 0; y < 2; y++) {
                                for (short x = 0; x < var.getValues()[0].length; x++) {
                                    if (modeValeur) {
                                        copyVal[y][x] = var.getValues()[y][x];
                                    } else {
                                        copyVal[y][x] = "0";
                                    }

                                }
                            }
                            varBase = new Curve(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(), var.getSwUnitRef(),
                                    new String[0][0], copyVal);
                        } else if (var instanceof Map) {
                            copyVal = new String[((Map) var).getDimY()][((Map) var).getDimX()];
                            for (short x = 0; x < var.getValues()[0].length; x++) {
                                for (short y = 0; y < var.getValues().length; y++) {
                                    if (modeValeur) {
                                        copyVal[y][x] = var.getValues()[y][x];
                                    } else {
                                        copyVal[y][x] = "0";
                                    }

                                }
                            }
                            varBase = new Map(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(), var.getSwUnitRef(),
                                    new String[0][0], copyVal);
                        } else if (var instanceof Scalaire) {
                            if (modeValeur) {
                                copyVal = new String[1][1];
                            } else {
                                copyVal = new String[][] { { "" } };
                            }

                            varBase = new Scalaire(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(),
                                    var.getSwUnitRef(), new String[0][0], copyVal);
                        } else if (var instanceof ValueBlock) {
                            copyVal = new String[((ValueBlock) var).getDimY()][((ValueBlock) var).getDimX()];
                            for (short x = 0; x < var.getValues()[0].length; x++) {
                                for (short y = 0; y < var.getValues().length; y++) {
                                    if (modeValeur) {
                                        copyVal[y][x] = var.getValues()[y][x];
                                    } else {
                                        copyVal[y][x] = "0";
                                    }

                                }
                            }
                            copyVal = new String[((ValueBlock) var).getDimY()][((ValueBlock) var).getDimX()];
                            varBase = new ValueBlock(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(),
                                    var.getSwUnitRef(), new String[0][0], copyVal);
                        }
                        for (short x = 0; x < var.getValues()[0].length; x++) {
                            for (short y = 0; y < var.getValues().length; y++) {
                                try {
                                    if (!var.getValues()[y][x].equals(varCompar.getValues()[y][x])) // Exception possible sur dimmension
                                    {
                                        if (modeValeur) {
                                            varBase.getValues()[y][x] = var.getValues()[y][x] + " => " + varCompar.getValues()[y][x];
                                        } else {
                                            if (Utilitaire.isNumber(var.getValues()[y][x]) & Utilitaire.isNumber(varCompar.getValues()[y][x])) {
                                                varBase.getValues()[y][x] = Float.toString(
                                                        Float.parseFloat(varCompar.getValues()[y][x]) - Float.parseFloat(var.getValues()[y][x]));
                                            } else {
                                                varBase.getValues()[y][x] = var.getValues()[y][x] + " => " + varCompar.getValues()[y][x];
                                            }

                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    if (checkDim != 1) {
                                        SWToolsMain.getLogger()
                                                .info(var.getShortName() + " / " + varCompar.getShortName() + " => Dimension differente");
                                        checkDim = 1;
                                    }
                                }
                            }
                        }

                        listCompa.add(varBase);
                    }
                }
            }
            return new Paco(cdfRef.getName() + "_vs_" + cdfWork.getName(), listCompa);
        }
        return null;
    }

    public static final boolean toExcel(Cdf cdf, final File file) {

        WritableWorkbook workbook = null;

        final int COL_SCALAIRE = 0;
        final int COL_COURBE = 1;
        final int COL_MAP = 2;
        final int COL_VALUEBLOCK = 3;
        final int COL_AXE = 4;

        try {
            workbook = Workbook.createWorkbook(file);

            final WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            final WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

            final WritableCellFormat borderFormat = new WritableCellFormat();
            borderFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            final WritableCellFormat axisFormat = new WritableCellFormat(arial10Bold);
            axisFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            axisFormat.setBackground(Colour.VERY_LIGHT_YELLOW);

            final WritableSheet sheetInfo = workbook.createSheet("Infos", 0);
            sheetInfo.getSettings().setShowGridLines(false);
            sheetInfo.getSettings().setDefaultColumnWidth(50);
            sheetInfo.mergeCells(0, 0, 4, 0);
            sheetInfo.setRowView(0, 1000);

            // Entete de la feuille
            WritableFont arial20Bold = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD);
            WritableCellFormat infoFormat = new WritableCellFormat(arial20Bold);
            infoFormat.setBackground(Colour.GRAY_25);
            infoFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
            infoFormat.setAlignment(Alignment.CENTRE);
            infoFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            writeCell(sheetInfo, 0, 0, "INFOS", infoFormat);

            writeCell(sheetInfo, 0, 1, "Nom du fichier : " + cdf.getName(), borderFormat);
            sheetInfo.mergeCells(0, 1, 4, 1);
            writeCell(sheetInfo, 0, 2, "Nombre de variables : " + String.valueOf(cdf.getNbLabel()), borderFormat);
            sheetInfo.mergeCells(0, 2, 4, 2);
            writeCell(sheetInfo, COL_SCALAIRE, 3, "Scalaire", borderFormat);
            writeCell(sheetInfo, COL_COURBE, 3, "Courbe", borderFormat);
            writeCell(sheetInfo, COL_MAP, 3, "Map", borderFormat);
            writeCell(sheetInfo, COL_VALUEBLOCK, 3, "Bloc de valeurs", borderFormat);
            writeCell(sheetInfo, COL_AXE, 3, "Axe", borderFormat);
            //

            final WritableSheet shtScore = workbook.createSheet("Scores", 2);
            shtScore.getSettings().setDefaultColumnWidth(50);

            writeCell(shtScore, 0, 0, "Score moyen du fichier : " + cdf.getAvgScore(), arial10format);
            writeCell(shtScore, 0, 2, "0% " + "(" + cdf.getRepartitionScore().get(0) + ")", arial10format);
            writeCell(shtScore, 1, 2, "25% " + "(" + cdf.getRepartitionScore().get(25) + ")", arial10format);
            writeCell(shtScore, 2, 2, "50% " + "(" + cdf.getRepartitionScore().get(50) + ")", arial10format);
            writeCell(shtScore, 3, 2, "75% " + "(" + cdf.getRepartitionScore().get(75) + ")", arial10format);
            writeCell(shtScore, 4, 2, "100% " + "(" + cdf.getRepartitionScore().get(100) + ")", arial10format);

            final WritableSheet sheetValues = workbook.createSheet("Valeurs", 1);
            sheetValues.getSettings().setShowGridLines(false);

            int row = 0;
            int cnt0 = 0;
            int cnt25 = 0;
            int cnt50 = 0;
            int cnt75 = 0;
            int cnt100 = 0;

            int cntScalaire = 0;
            int cntCourbe = 0;
            int cntMap = 0;
            int cntValueBlock = 0;
            int cntAxe = 0;

            final int offsetEntete = 4;

            WritableCellFormat centerAlign = new WritableCellFormat();
            centerAlign.setAlignment(Alignment.CENTRE);
            centerAlign.setVerticalAlignment(VerticalAlignment.CENTRE);

            for (Variable var : cdf.getListLabel()) {

                switch (var.getLastScore()) {
                case 0:
                    writeCell(shtScore, 0, cnt0 + offsetEntete, var.getShortName(), new WritableCellFormat());
                    cnt0++;
                    break;
                case 25:
                    writeCell(shtScore, 1, cnt25 + offsetEntete, var.getShortName(), new WritableCellFormat());
                    cnt25++;
                    break;
                case 50:
                    writeCell(shtScore, 2, cnt50 + offsetEntete, var.getShortName(), new WritableCellFormat());
                    cnt50++;
                    break;
                case 75:
                    writeCell(shtScore, 3, cnt75 + offsetEntete, var.getShortName(), new WritableCellFormat());
                    cnt75++;
                    break;
                case 100:
                    writeCell(shtScore, 4, cnt100 + offsetEntete, var.getShortName(), new WritableCellFormat());
                    cnt100++;
                    break;
                default:
                    break;
                }

                if (var instanceof Scalaire) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_SCALAIRE, offsetEntete + cntScalaire, var.getShortName(), sheetValues, 0, row));
                    sheetInfo.getWritableCell(COL_SCALAIRE, offsetEntete + cntScalaire).setCellFormat(centerAlign);
                    cntScalaire++;

                    Scalaire variableType = (Scalaire) var;
                    writeCell(sheetValues, 0, row, variableType.getShortName(), arial10format);
                    row += 1;
                    writeCell(sheetValues, 0, row, variableType.getValue(), borderFormat);
                    row += 2;
                }
                if (var instanceof Axis) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_AXE, offsetEntete + cntAxe, var.getShortName(), sheetValues, 0, row));
                    sheetInfo.getWritableCell(COL_AXE, offsetEntete + cntAxe).setCellFormat(centerAlign);
                    cntAxe++;

                    Axis variableType = (Axis) var;
                    writeCell(sheetValues, 0, row, variableType.getShortName(), arial10format);

                    int col = 0;
                    row += 1;
                    for (short x = 0; x < variableType.getDim(); x++) {
                        writeCell(sheetValues, col, row, variableType.getzValues(x), borderFormat);
                        col += 1;
                    }
                    row += 2;
                }
                if (var instanceof Curve) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_COURBE, offsetEntete + cntCourbe, var.getShortName(), sheetValues, 0, row));
                    sheetInfo.getWritableCell(COL_COURBE, offsetEntete + cntCourbe).setCellFormat(centerAlign);
                    cntCourbe++;

                    Curve variableType = (Curve) var;
                    writeCell(sheetValues, 0, row, variableType.getShortName(), arial10format);

                    for (byte y = 0; y < 2; y++) {
                        int col = 0;
                        row += 1;
                        for (short x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0) {
                                writeCell(sheetValues, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheetValues, col, row, variableType.getValue(y, x), borderFormat);
                            }

                            col += 1;
                        }

                    }
                    row += 2;
                }

                if (var instanceof ValueBlock) {

                    sheetInfo.addHyperlink(
                            new WritableHyperlink(COL_VALUEBLOCK, offsetEntete + cntValueBlock, var.getShortName(), sheetValues, 0, row));
                    sheetInfo.getWritableCell(COL_VALUEBLOCK, offsetEntete + cntValueBlock).setCellFormat(centerAlign);
                    cntValueBlock++;

                    ValueBlock variableType = (ValueBlock) var;
                    writeCell(sheetValues, 0, row, variableType.getShortName(), arial10format);

                    for (byte y = 0; y < 2; y++) {
                        int col = 0;
                        row += 1;
                        for (short x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0) {
                                writeCell(sheetValues, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheetValues, col, row, variableType.getValue(y, x), borderFormat);
                            }
                            col += 1;
                        }

                    }
                    row += 2;
                }

                if (var instanceof Map) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_MAP, offsetEntete + cntMap, var.getShortName(), sheetValues, 0, row));
                    sheetInfo.getWritableCell(COL_MAP, offsetEntete + cntMap).setCellFormat(centerAlign);
                    cntMap++;

                    Map variableType = (Map) var;
                    writeCell(sheetValues, 0, row, variableType.getShortName(), arial10format);

                    for (short y = 0; y < variableType.getDimY(); y++) {
                        int col = 0;
                        row += 1;
                        for (short x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0 | x == 0) {
                                writeCell(sheetValues, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheetValues, col, row, variableType.getValue(y, x), borderFormat);
                            }
                            col += 1;
                        }
                    }
                    row += 2;
                }
            }

            workbook.write();

        } catch (IOException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
        } catch (WriteException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
            if (e instanceof RowsExceededException) {
                JOptionPane.showMessageDialog(null, "Trop de variables à exporter !", "ERREUR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (WriteException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }

    static final void writeCell(WritableSheet sht, int col, int row, String txtValue, WritableCellFormat format)
            throws RowsExceededException, WriteException {

        if (Utilitaire.isNumber(txtValue)) {
            sht.addCell(new Number(col, row, Double.parseDouble(txtValue), format));
        } else {
            sht.addCell(new Label(col, row, txtValue, format));
        }
    }

    public static final boolean toText(Cdf cdf, final File file) {

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
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
        return true;

    }

    public static final boolean toM(Cdf cdf, final File file) {

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
        } finally {
            printWriter.close();
        }
        return true;
    }

}
