/*
 * Creation : 24 janv. 2017
 */
package observer;

import java.util.ArrayList;

import lab.Lab;

public interface Observable {

    public void addObservateur(Observateur obs);

    public void delObservateur();

    public void updateObservateur(ArrayList<Lab> listLab, String typeAction);

}
