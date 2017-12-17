/*
 * Creation : 20 juin 2017
 */
package cdf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public interface Cdf {

    public static final String ASCII = "ASCII";
    public static final String VALUE = "VALUE";
    public static final String CURVE_INDIVIDUAL = "CURVE_INDIVIDUAL";
    public static final String MAP_INDIVIDUAL = "MAP_INDIVIDUAL";
    public static final String AXIS_VALUES = "AXIS_VALUES";
    public static final String VALUE_BLOCK = "VALUE_BLOCK";
    public static final String CURVE_GROUPED = "CURVE_GROUPED";
    public static final String MAP_GROUPED = "MAP_GROUPED";
    public static final String MAP_FIXED = "MAP_FIXED";
    public static final String CURVE_FIXED = "CURVE_FIXED";

    public String getName();

    public int getNbLabel();

    public ArrayList<String> getCategoryList();

    public ArrayList<Variable> getListLabel();

    public HashMap<Integer, Integer> getRepartitionScore();

    public float getAvgScore();

    public int getMinScore();

    public int getMaxScore();

    public double getCheckSum();

    public boolean exportToExcel(final File file);

    public boolean exportToTxt(File file);

    public boolean exportToM(File file);
}
