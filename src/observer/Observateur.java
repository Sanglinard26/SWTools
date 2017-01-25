/*
 * Creation : 24 janv. 2017
 */
package observer;

import java.util.ArrayList;

import lab.Lab;

public interface Observateur {

    public void update(ArrayList<Lab> listLab, String typeAction);

}
