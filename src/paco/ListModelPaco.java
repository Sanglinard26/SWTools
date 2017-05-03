/*
 * Creation : 6 avr. 2017
 */
package paco;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

public final class ListModelPaco extends AbstractListModel<PaCo> {

    private static final long serialVersionUID = 1L;
    // private static final ArrayList<PaCo> listPaco = new ArrayList<PaCo>();
    private static final Vector<PaCo> listPaco = new Vector<PaCo>(); // Remplacement de l'arraylist par un vector car thread safe

    @Override
    public int getSize() {
        return listPaco.size();
    }

    @Override
    public PaCo getElementAt(int index) {
        return listPaco.get(index);
    }

    public final void addPaco(PaCo paco) {
        if (!(listPaco.contains(paco))) {
            listPaco.add(paco);
            this.fireIntervalAdded(this, getSize() - 1, getSize() - 1);
        }
    }

    public final void removePaco(int index) {
        if (listPaco.remove(index) != null) {
            this.fireIntervalRemoved(this, index, index);
        }
    }

    public final void clearList() {
        listPaco.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public final List<String> getList() {
        final List<String> listNomPaco = new ArrayList<String>(listPaco.size());
        for (PaCo p : listPaco) {
            listNomPaco.add(p.getName());
        }
        return listNomPaco;
    }
}
