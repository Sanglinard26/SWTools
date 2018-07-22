/*
 * Creation : 20 janv. 2017
 */
package cdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public final class ListModelLabel extends AbstractListModel<Variable> {

    private static final long serialVersionUID = 1L;

    private static final List<Variable> listLabel = new ArrayList<Variable>();
    private static final List<Variable> listLabelFiltre = new ArrayList<Variable>();

    public ListModelLabel() {
    }

    @Override
    public int getSize() {
        return listLabelFiltre.size();
    }

    public void setList(List<Variable> list) {
        if (!listLabel.isEmpty()) {
            listLabel.clear();
        }
        listLabel.addAll(list);
        setFilter("", "");
    }

    public void setFilter(String type, String filter) {
        listLabelFiltre.clear();

        final int nbLabel = listLabel.size();
        Variable var;

        for (int i = 0; i < nbLabel; i++) {
            var = listLabel.get(i);

            if (var.getShortName().toLowerCase().indexOf(filter) > -1) {
                if (type.equals("ALL") | type.isEmpty()) {
                    listLabelFiltre.add(var);
                }
                if (var.getCategory().equals(type)) {
                    listLabelFiltre.add(var);
                }
            }
        }

        this.fireContentsChanged(this, 0, getSize());
    }

    public void clearList() {
        listLabel.clear();
        setFilter("", "");
    }

    @Override
    public Variable getElementAt(int index) {
        return listLabelFiltre.get(index);
    }

    public List<Variable> getFilteredList() {
        return Collections.unmodifiableList(listLabelFiltre);
    }
    
    public List<Variable> getList() {
        return Collections.unmodifiableList(listLabel);
    }

}
