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
    
    private static final ArrayList<Variable> listLabel = new ArrayList<Variable>();
    private static final ArrayList<Variable> listLabelFiltre = new ArrayList<Variable>();

    public ListModelLabel() {
    }

    @Override
    public int getSize() {
        return listLabelFiltre.size();
    }

    public void setList(ArrayList<Variable> list) {
        if (!listLabel.isEmpty()) {
            listLabel.clear();
        }
        listLabel.addAll(list);
        setFilter("","");
    }

    public void setFilter(String type, String filter) {
        listLabelFiltre.clear();
        for (Variable label : listLabel) {
            if (label.getShortName().toLowerCase().indexOf(filter) > -1) {
            	if (type.equals("TOUT TYPE") | type.equals(""))
            	{
            		listLabelFiltre.add(label);
            	}
				if (label.getCategory().equals(type))
				{
					listLabelFiltre.add(label);
				}
            }
        }
        this.fireContentsChanged(this, 0, getSize());
    }

    public void clearList() {
        listLabel.clear();
        setFilter("","");
    }

    @Override
    public Variable getElementAt(int index) {
        return listLabelFiltre.get(index);
    }

    public List<Variable> getList() {
        return Collections.unmodifiableList(listLabelFiltre);
    }

}