/*
 * Creation : 16 janv. 2017
 */
package lab;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import observer.Observable;
import observer.Observateur;

public class ListModelLab extends AbstractListModel<Lab> implements Observable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        ADD, DEL, CLEAR
    }

    private ArrayList<Lab> listLab;
    private ArrayList<Observateur> listObs = new ArrayList<Observateur>();

    public ListModelLab() {
        this.listLab = new ArrayList<Lab>();
    }

    public void addLab(Lab lab) {
        if (!(listLab.contains(lab))) {
            listLab.add(lab);
            this.fireContentsChanged(this, 0, getSize());
            this.updateObservateur(this.listLab, Type.ADD.toString());
        }
    }

    public void removeLab(int index) {
        if (listLab.remove(index) != null) {
            this.fireContentsChanged(this, 0, getSize());
            this.updateObservateur(this.listLab, Type.DEL.toString());
        }
    }

    public void clearList() {
        this.listLab.clear();
        this.fireContentsChanged(this, 0, getSize());
        this.updateObservateur(this.listLab, Type.CLEAR.toString());
    }

    @Override
    public int getSize() {
        return this.listLab.size();
    }

    @Override
    public Lab getElementAt(int index) {
        return this.listLab.get(index);
    }

    public ArrayList<Lab> getList() {
        return this.listLab;
    }

    @Override
    public void addObservateur(Observateur obs) {
        this.listObs.add(obs);

    }

    @Override
    public void delObservateur() {
        this.listObs = new ArrayList<Observateur>();

    }

    @Override
    public void updateObservateur(ArrayList<Lab> listLab, String typeAction) {
        for (Observateur obs : this.listObs) {
            obs.update(listLab, typeAction);
        }

    }

}
