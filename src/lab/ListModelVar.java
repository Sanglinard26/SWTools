/*
 * Creation : 20 janv. 2017
 */
package lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public final class ListModelVar extends AbstractListModel<Variable> {

    private static final long serialVersionUID = 1L;
    private final List<Variable> listVariable;
    private final List<Variable> listVariableFiltre;

    public ListModelVar() {
        this.listVariable = new ArrayList<Variable>();
        this.listVariableFiltre = new ArrayList<Variable>();
    }

    @Override
    public int getSize() {
        return listVariableFiltre.size();
    }

    public void setList(Lab lab) {
        if (!listVariable.isEmpty()) {
            listVariable.clear();
        }
        listVariable.addAll(lab.getListVariable());
        Collections.sort(listVariable);
        setFilter("");
    }

    public void setFilter(String filter) {
        listVariableFiltre.clear();
        for (Variable var : listVariable) {
            if (var.toString().toLowerCase().indexOf(filter) > -1) {
                listVariableFiltre.add(var);
            }
        }
        this.fireContentsChanged(this, 0, getSize());
    }

    public void clearList() {
        this.listVariable.clear();
        setFilter("");
    }

    @Override
    public Variable getElementAt(int index) {
        return listVariableFiltre.get(index);
    }

    public List<Variable> getList() {
        return Collections.unmodifiableList(this.listVariableFiltre);
    }

}
