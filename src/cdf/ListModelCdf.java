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
    private static final List<String> listCdfName = new ArrayList<String>();

    @Override
    public final int getSize() {
        return listCdf.size();
    }

    @Override
    public final Cdf getElementAt(int index) {
        return listCdf.get(index);
    }

    public final void addCdf(Cdf paco) {
        if (!(listCdf.contains(paco))) {
            listCdf.add(paco);
            listCdfName.add(paco.getName());
            this.fireIntervalAdded(this, getSize() - 1, getSize() - 1);
        }
    }

    public final Cdf set(int index, Cdf element) {
        final Cdf cdf = listCdf.get(index);
        listCdf.set(index, element);
        listCdfName.set(index, element.getName());
        fireContentsChanged(this, index, index);
        return cdf;
    }

    public final void removeCdf(int index) {
        if (listCdf.remove(index) != null) {
            listCdfName.remove(index);
            this.fireIntervalRemoved(this, index, index);
        }
    }

    public final void clearList() {
        listCdf.clear();
        listCdfName.clear();
        fireContentsChanged(this, 0, getSize());
    }

    public static List<String> getListcdfname() {
        return listCdfName;
    }

}
