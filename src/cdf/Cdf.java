/*
 * Creation : 20 juin 2017
 */
package cdf;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface Cdf {

    public String getName();

    public int getNbLabel();

    public Set<TypeVariable> getCategoryList();

    public List<Variable> getListLabel();

    public HashMap<Integer, Integer> getRepartitionScore();

    public float getAvgScore();

    public int getMinScore();

    public int getMaxScore();

    public double getCheckSum();

    public boolean isValid();
}
