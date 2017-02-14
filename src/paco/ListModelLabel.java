/*
 * Creation : 20 janv. 2017
 */
package paco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class ListModelLabel extends AbstractListModel<Variable> {

    private static final long serialVersionUID = 1L;
    private ArrayList<Variable> listLabel;
    private ArrayList<Variable> listLabelFiltre;

    public ListModelLabel() {
        this.listLabel = new ArrayList<Variable>();
        this.listLabelFiltre = new ArrayList<Variable>();
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
        setFilter("");
        this.fireContentsChanged(this, 0, getSize());
    }

    public void setFilter(String filter) {
        listLabelFiltre.clear();
        for (Variable label : listLabel) {
            if (label.getShortName().toLowerCase().indexOf(filter) > -1) {
                listLabelFiltre.add(label);
            }
        }
        this.fireContentsChanged(this, 0, getSize());
    }

    public void clearList() {
        this.listLabel.clear();
        setFilter("");
        this.fireContentsChanged(this, 0, getSize());
    }

    @Override
    public Variable getElementAt(int index) {
        return listLabelFiltre.get(index);
    }

    public List<Variable> getList() {
        return Collections.unmodifiableList(this.listLabelFiltre);
    }

}
