/*
 * Creation : 6 avr. 2017
 */
package cdf;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public final class ListModelCdf extends AbstractListModel<Cdf> {

    private static final long serialVersionUID = 1L;

    private static final List<Cdf> listCdf = new ArrayList<Cdf>();

    @Override
    public int getSize() {
        return listCdf.size();
    }

    @Override
    public Cdf getElementAt(int index) {
        return listCdf.get(index);
    }

    public final void addCdf(Cdf paco) {
        if (!(listCdf.contains(paco))) {
            listCdf.add(paco);
            this.fireIntervalAdded(this, getSize() - 1, getSize() - 1);
        }
    }

    public final void removeCdf(int index) {
        if (listCdf.remove(index) != null)
            this.fireIntervalRemoved(this, index, index);
    }

    public final void clearList() {
        listCdf.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public final List<String> getList() {
        final List<String> listNomPaco = new ArrayList<String>(listCdf.size());
        for (Cdf p : listCdf) {
            listNomPaco.add(p.getName());
        }
        return listNomPaco;
    }
}
