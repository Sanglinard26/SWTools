/*
 * Creation : 12 déc. 2017
 */
package cdf;

import java.util.ArrayList;

import paco.PaCo;
import tools.Utilitaire;
import visu.SWToolsMain;

public final class CdfUtils {

    public static final Cdf comparCdf(Cdf cdfRef, Cdf cdfWork, Boolean modeValeur) {
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
            return new PaCo(cdfRef.getName() + "_vs_" + cdfWork.getName(), listCompa);
        }
        return null;
    }

}
