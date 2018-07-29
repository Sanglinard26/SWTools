/*
 * Creation : 20 janv. 2017
 */
package cdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    public final void setList(List<Variable> list) {
        if (!listLabel.isEmpty()) {
            listLabel.clear();
        }
        listLabel.addAll(list);
        setFilter("", new String[]{""});
    }

    public final void setFilter(String type, String[] filters) {
    	
    	final Set<Variable> tmpList = new LinkedHashSet<Variable>();
    	
        listLabelFiltre.clear();

        final int nbLabel = listLabel.size();
        Variable var;

        for (int i = 0; i < nbLabel; i++) {
            var = listLabel.get(i);

            for (String filter : filters) {
                if (var.getShortName().toLowerCase().indexOf(filter) > -1) {
                    if ("ALL".equals(type) || type.isEmpty()) {
                        tmpList.add(var);
                    }
                    if (var.getCategory().equals(type)) {
                        tmpList.add(var);
                    }
                }
            }
        }
        
        listLabelFiltre.addAll(tmpList);

        this.fireContentsChanged(this, 0, getSize());
    }

    public final void clearList() {
        listLabel.clear();
        setFilter("", new String[]{""});
    }

    @Override
    public Variable getElementAt(int index) {
        return listLabelFiltre.get(index);
    }

    public final List<Variable> getFilteredList() {
        return Collections.unmodifiableList(listLabelFiltre);
    }

    public final List<Variable> getList() {
        return Collections.unmodifiableList(listLabel);
    }

}
