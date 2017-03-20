/*
 * Creation : 16 janv. 2017
 */
package lab;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

public final class ListModelLab extends AbstractListModel<Lab> {

    private static final long serialVersionUID = 1L;

    private final ArrayList<Lab> listLab;

    public ListModelLab() {
        this.listLab = new ArrayList<Lab>();
    }

    public void addLab(Lab lab) {
        if (!(listLab.contains(lab))) {
            listLab.add(lab);
            this.fireIntervalAdded(this, getSize() - 1, getSize() - 1);
        }
    }

    public void removeLab(int index) {
        if (listLab.remove(index) != null) {
            this.fireIntervalRemoved(this, index, index);
        }
    }

    public void clearList() {
        this.listLab.clear();
        this.fireContentsChanged(this, 0, getSize());
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

}
