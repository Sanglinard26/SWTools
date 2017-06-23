/*
 * Creation : 20 juin 2017
 */
package cdf;

import java.io.File;
import java.util.ArrayList;

public interface Cdf {

    public String getName();

    public int getNbLabel();

    public ArrayList<Variable> getListLabel();

    public float getAvgScore();

    public int getMinScore();

    public int getMaxScore();

    public void exportToExcel(final File file);

    public void exportToTxt(File file);

}
