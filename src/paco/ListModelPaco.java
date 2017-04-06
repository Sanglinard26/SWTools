/*
 * Creation : 6 avr. 2017
 */
package paco;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

public class ListModelPaco extends AbstractListModel<PaCo> {

    private static final long serialVersionUID = 1L;
    private static final ArrayList<PaCo> listPaco = new ArrayList<PaCo>();

    @Override
    public int getSize() {
        return listPaco.size();
    }

    @Override
    public PaCo getElementAt(int index) {
        return listPaco.get(index);
    }

    public void addPaco(PaCo Paco) {
        if (!(listPaco.contains(Paco))) {
            listPaco.add(Paco);
            this.fireIntervalAdded(this, getSize() - 1, getSize() - 1);
        }
    }

    public void removePaco(int index) {
        if (listPaco.remove(index) != null) {
            this.fireIntervalRemoved(this, index, index);
        }
    }

    public void clearList() {
        listPaco.clear();
        fireContentsChanged(this, 0, getSize());
    }

}
