/*
 * Creation : 20 juin 2017
 */
package cdf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public interface Cdf {

    public String getName();

    public int getNbLabel();

    public ArrayList<Variable> getListLabel();

    public HashMap<Integer, Integer> getRepartitionScore();

    public float getAvgScore();

    public int getMinScore();

    public int getMaxScore();

    public Boolean exportToExcel(final File file);

    public Boolean exportToTxt(File file);

    public Boolean exportToM(File file);

}
