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
import jxl.write.Blank;
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
            Values copyVal = null;

            int checkDim = 0;

            for (Variable var : cdfRef.getListLabel()) {
                if (cdfWork.getListLabel().contains(var)) {
                    varCompar = cdfWork.getListLabel().get(cdfWork.getListLabel().indexOf(var));
                    if (var.getChecksum() != varCompar.getChecksum()) {

                        checkDim = 0;

                        if (var instanceof Axis) {
                            copyVal = new Values(var.getValues().getDimX(), 1); // new String[1][((Axis) var).getDim()];
                            for (short x = 0; x < var.getValues().getDimX(); x++) {
                                if (modeValeur) {
                                    copyVal.setValue(0, x, var.getValues().getValue(0, x));
                                } else {
                                    copyVal.setValue(0, x, "0");
                                }

                            }
                            varBase = new Axis(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(), var.getSwUnitRef(),
                                    new History[0], copyVal);
                        } else if (var instanceof Curve) {
                            copyVal = new Values(var.getValues().getDimX(), 2); // new String[2][((Curve) var).getDimX()];
                            for (byte y = 0; y < 2; y++) {
                                for (short x = 0; x < var.getValues().getDimX(); x++) {
                                    if (modeValeur) {
                                        copyVal.setValue(y, x, var.getValues().getValue(y, x));
                                    } else {
                                        copyVal.setValue(y, x, "0");
                                    }

                                }
                            }
                            varBase = new Curve(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(), var.getSwUnitRef(),
                                    new History[0], copyVal);
                        } else if (var instanceof Map) {
                            copyVal = new Values(var.getValues().getDimX(), var.getValues().getDimY()); // new String[((Map) var).getDimY()][((Map)
                                                                                                        // var).getDimX()];
                            for (short x = 0; x < var.getValues().getDimX(); x++) {
                                for (short y = 0; y < var.getValues().getDimY(); y++) {
                                    if (modeValeur) {
                                        copyVal.setValue(y, x, var.getValues().getValue(y, x));
                                    } else {
                                        copyVal.setValue(y, x, "0");
                                    }

                                }
                            }
                            varBase = new Map(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(), var.getSwUnitRef(),
                                    new History[0], copyVal);
                        } else if (var instanceof Scalaire) {
                            if (modeValeur) {
                                copyVal = new Values(1, 1);
                            } else {
                                copyVal = new Values(1, 1);
                                copyVal.setValue(0, 0, "");
                            }

                            varBase = new Scalaire(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(),
                                    var.getSwUnitRef(), new History[0], copyVal);
                        } else if (var instanceof ValueBlock) {
                            copyVal = new Values(var.getValues().getDimX(), var.getValues().getDimY()); // new String[((ValueBlock)
                                                                                                        // var).getDimY()][((ValueBlock)
                                                                                                        // var).getDimX()];
                            for (short x = 0; x < var.getValues().getDimX(); x++) {
                                for (short y = 0; y < var.getValues().getDimY(); y++) {
                                    if (modeValeur) {
                                        copyVal.setValue(y, x, var.getValues().getValue(y, x));
                                    } else {
                                        copyVal.setValue(y, x, "0");
                                    }

                                }
                            }
                            // copyVal = new String[((ValueBlock) var).getDimY()][((ValueBlock) var).getDimX()];
                            varBase = new ValueBlock(var.getShortName(), var.getLongName(), var.getCategory(), var.getSwFeatureRef(),
                                    var.getSwUnitRef(), new History[0], copyVal);
                        }
                        for (short x = 0; x < var.getValues().getDimX(); x++) {
                            for (short y = 0; y < var.getValues().getDimY(); y++) {
                                try {
                                    if (!var.getValues().getValue(y, x).equals(varCompar.getValues().getValue(y, x))) // Exception possible sur
                                                                                                                      // dimmension
                                    {
                                        if (modeValeur) {
                                            varBase.getValues().setValue(y, x,
                                                    var.getValues().getValue(y, x) + " => " + varCompar.getValues().getValue(y, x));
                                        } else {
                                            if (Utilitaire.isNumber(var.getValues().getValue(y, x))
                                                    & Utilitaire.isNumber(varCompar.getValues().getValue(y, x))) {
                                                varBase.getValues().setValue(y, x,
                                                        Float.toString(Float.parseFloat(varCompar.getValues().getValue(y, x))
                                                                - Float.parseFloat(var.getValues().getValue(y, x))));
                                            } else {
                                                varBase.getValues().setValue(y, x,
                                                        var.getValues().getValue(y, x) + " => " + varCompar.getValues().getValue(y, x));
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

        final int offsetEntete = 4;

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
            borderFormat.setAlignment(Alignment.CENTRE);
            borderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

            final WritableCellFormat borderBoldFormat = new WritableCellFormat(arial10Bold);
            borderBoldFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            borderBoldFormat.setAlignment(Alignment.CENTRE);
            borderBoldFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

            final WritableCellFormat axisFormat = new WritableCellFormat(arial10Bold);
            axisFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            axisFormat.setBackground(Colour.VERY_LIGHT_YELLOW);
            axisFormat.setAlignment(Alignment.CENTRE);
            axisFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

            final WritableSheet sheetInfo = workbook.createSheet("Infos", 0);
            sheetInfo.getSettings().setShowGridLines(false);
            sheetInfo.getSettings().setDefaultColumnWidth(50);
            sheetInfo.mergeCells(0, 0, 4, 0);
            sheetInfo.setRowView(0, 800);

            // Entete de la feuille Infos
            WritableFont arial20Bold = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD);
            WritableCellFormat titreFormat = new WritableCellFormat(arial20Bold);
            titreFormat.setBackground(Colour.GRAY_25);
            titreFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
            titreFormat.setAlignment(Alignment.CENTRE);
            titreFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            writeCell(sheetInfo, 0, 0, "INFOS", titreFormat);
            writeCell(sheetInfo, 0, 1, "Nom du fichier : " + cdf.getName(), borderBoldFormat);
            sheetInfo.mergeCells(0, 1, 4, 1);
            writeCell(sheetInfo, 0, 2, "Nombre de variables : " + String.valueOf(cdf.getNbLabel()), borderBoldFormat);
            sheetInfo.mergeCells(0, 2, 4, 2);
            writeCell(sheetInfo, COL_SCALAIRE, 3, "Scalaire", borderBoldFormat);
            writeCell(sheetInfo, COL_COURBE, 3, "Courbe", borderBoldFormat);
            writeCell(sheetInfo, COL_MAP, 3, "Map", borderBoldFormat);
            writeCell(sheetInfo, COL_VALUEBLOCK, 3, "Bloc de valeurs", borderBoldFormat);
            writeCell(sheetInfo, COL_AXE, 3, "Axe", borderBoldFormat);

            WritableCellFormat cellInfoFormat = new WritableCellFormat();
            cellInfoFormat.setBackground(Colour.VERY_LIGHT_YELLOW);

            int nbVar = cdf.getNbLabel();
            for (int r = offsetEntete; r < nbVar + offsetEntete; r++) {
                for (int c = 0; c < 5; c++) {
                    sheetInfo.addCell(new Blank(c, r, cellInfoFormat));
                }
            }
            //

            // Entete de la feuille Scores
            final WritableSheet shtScore = workbook.createSheet("Scores", 2);
            shtScore.getSettings().setDefaultColumnWidth(50);
            shtScore.getSettings().setShowGridLines(false);
            writeCell(shtScore, 0, 0, "SCORES", titreFormat);
            shtScore.mergeCells(0, 0, 4, 0);
            shtScore.setRowView(0, 800);
            writeCell(shtScore, 0, 1, "Score moyen du fichier : " + cdf.getAvgScore() + "%", borderBoldFormat);
            shtScore.mergeCells(0, 1, 4, 1);
            writeCell(shtScore, 0, 2, "0% " + "(" + cdf.getRepartitionScore().get(0) + ")", borderBoldFormat);
            writeCell(shtScore, 1, 2, "25% " + "(" + cdf.getRepartitionScore().get(25) + ")", borderBoldFormat);
            writeCell(shtScore, 2, 2, "50% " + "(" + cdf.getRepartitionScore().get(50) + ")", borderBoldFormat);
            writeCell(shtScore, 3, 2, "75% " + "(" + cdf.getRepartitionScore().get(75) + ")", borderBoldFormat);
            writeCell(shtScore, 4, 2, "100% " + "(" + cdf.getRepartitionScore().get(100) + ")", borderBoldFormat);

            for (int r = offsetEntete - 1; r < nbVar + offsetEntete - 1; r++) {
                for (int c = 0; c < 5; c++) {
                    shtScore.addCell(new Blank(c, r, cellInfoFormat));
                }
            }
            //

            // Entete de la feuille Valeurs
            final WritableSheet sheetValues = workbook.createSheet("Valeurs", 1);
            writeCell(sheetValues, 0, 0, "VALEURS", titreFormat);
            sheetValues.mergeCells(0, 0, 27, 0);
            sheetValues.setRowView(0, 800);
            sheetValues.getSettings().setShowGridLines(false);
            //

            int row = 2; // Point de depart, une ligne d'espace par rapport au titre
            int cnt0 = -1; // Offset de -1 pour rattraper offsetEntete
            int cnt25 = -1;
            int cnt50 = -1;
            int cnt75 = -1;
            int cnt100 = -1;

            int cntScalaire = 0;
            int cntCourbe = 0;
            int cntMap = 0;
            int cntValueBlock = 0;
            int cntAxe = 0;

            WritableCellFormat centerAlignYellow = new WritableCellFormat();
            centerAlignYellow.setAlignment(Alignment.CENTRE);
            centerAlignYellow.setVerticalAlignment(VerticalAlignment.CENTRE);
            centerAlignYellow.setBackground(Colour.VERY_LIGHT_YELLOW);

            String variableName, variableDesc;

            for (Variable var : cdf.getListLabel()) {

                variableName = var.getShortName();

                if (var.getLongName().length() > 0) {
                    variableDesc = var.getLongName();
                } else {
                    variableDesc = "Pas de description";
                }

                switch (var.getLastScore()) {
                case 0:
                    writeCell(shtScore, 0, cnt0 + offsetEntete, variableName, centerAlignYellow);
                    cnt0++;
                    break;
                case 25:
                    writeCell(shtScore, 1, cnt25 + offsetEntete, variableName, centerAlignYellow);
                    cnt25++;
                    break;
                case 50:
                    writeCell(shtScore, 2, cnt50 + offsetEntete, variableName, centerAlignYellow);
                    cnt50++;
                    break;
                case 75:
                    writeCell(shtScore, 3, cnt75 + offsetEntete, variableName, centerAlignYellow);
                    cnt75++;
                    break;
                case 100:
                    writeCell(shtScore, 4, cnt100 + offsetEntete, variableName, centerAlignYellow);
                    cnt100++;
                    break;
                }

                writeCell(sheetValues, 0, row, variableName, arial10format);
                writeCell(sheetValues, 0, ++row, variableDesc, new WritableCellFormat());

                if (var instanceof Scalaire) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_SCALAIRE, offsetEntete + cntScalaire, variableName, sheetValues, 0, row - 1));
                    sheetInfo.getWritableCell(COL_SCALAIRE, offsetEntete + cntScalaire).setCellFormat(centerAlignYellow);
                    cntScalaire++;

                    Scalaire variableType = (Scalaire) var;

                    row += 1;
                    writeCell(sheetValues, 0, row, variableType.getValue(), borderFormat);
                    row += 2;
                }
                if (var instanceof Axis) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_AXE, offsetEntete + cntAxe, variableName, sheetValues, 0, row - 1));
                    sheetInfo.getWritableCell(COL_AXE, offsetEntete + cntAxe).setCellFormat(centerAlignYellow);
                    cntAxe++;

                    Axis variableType = (Axis) var;

                    int col = 0;
                    row += 1;
                    for (short x = 0; x < variableType.getDim(); x++) {
                        writeCell(sheetValues, col, row, variableType.getzValues(x), borderFormat);
                        col += 1;
                    }
                    row += 2;
                }
                if (var instanceof Curve) {

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_COURBE, offsetEntete + cntCourbe, variableName, sheetValues, 0, row - 1));
                    sheetInfo.getWritableCell(COL_COURBE, offsetEntete + cntCourbe).setCellFormat(centerAlignYellow);
                    cntCourbe++;

                    Curve variableType = (Curve) var;

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

                    sheetInfo
                            .addHyperlink(new WritableHyperlink(COL_VALUEBLOCK, offsetEntete + cntValueBlock, variableName, sheetValues, 0, row - 1));
                    sheetInfo.getWritableCell(COL_VALUEBLOCK, offsetEntete + cntValueBlock).setCellFormat(centerAlignYellow);
                    cntValueBlock++;

                    ValueBlock variableType = (ValueBlock) var;

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

                    sheetInfo.addHyperlink(new WritableHyperlink(COL_MAP, offsetEntete + cntMap, variableName, sheetValues, 0, row - 1));
                    sheetInfo.getWritableCell(COL_MAP, offsetEntete + cntMap).setCellFormat(centerAlignYellow);
                    cntMap++;

                    Map variableType = (Map) var;

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

    public static final boolean toM(Cdf cdf, final File file, boolean transpose) {

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

                printWriter.println(var.toMFormat(transpose));
            }

            printWriter.println();
            printWriter.println();
            printWriter.println("% -----");
            printWriter.println("%| FIN |");
            printWriter.println("% -----");
            printWriter.println();
            printWriter.println("%Fichier cree par SWTools, " + new Date().toString());

        } catch (FileNotFoundException e) {
            SWToolsMain.getLogger().severe(e.getMessage());
            return false;
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
        return true;
    }

}
