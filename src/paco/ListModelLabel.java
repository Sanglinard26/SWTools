/*
 * Creation : 20 janv. 2017
 */
package paco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class ListModelLabel extends AbstractListModel<Label> {

    private static final long serialVersionUID = 1L;
    private ArrayList<Label> listLabel;
    private ArrayList<Label> listLabelFiltre;

    public ListModelLabel() {
        this.listLabel = new ArrayList<Label>();
        this.listLabelFiltre = new ArrayList<Label>();
    }

    @Override
    public int getSize() {
        return listLabelFiltre.size();
    }

    public void setList(ArrayList<Label> list) {
        if (!listLabel.isEmpty()) {
            listLabel.clear();
        }
        listLabel.addAll(list);
        setFilter("");
        this.fireContentsChanged(this, 0, getSize());
    }

    public void setFilter(String filter) {
        listLabelFiltre.clear();
        for (Label label : listLabel) {
            if (label.toString().toLowerCase().indexOf(filter) > -1) {
                listLabelFiltre.add(label);
            }
        }
    }

    public void clearList() {
        this.listLabel.clear();
        setFilter("");
        this.fireContentsChanged(this, 0, getSize());
    }

    @Override
    public Label getElementAt(int index) {
        return listLabelFiltre.get(index);
    }

    public List<Label> getList() {
        return Collections.unmodifiableList(this.listLabelFiltre);
    }

}
