/*
 * Creation : 20 janv. 2017
 */
package lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import lab.ListModelLab.Type;
import observer.Observateur;

public class ListModelVar extends AbstractListModel<Variable> implements Observateur {

    private static final long serialVersionUID = 1L;
    private ArrayList<Variable> listVariable;
    private ArrayList<Variable> listVariableFiltre;

    public ListModelVar() {
        this.listVariable = new ArrayList<Variable>();
        this.listVariableFiltre = new ArrayList<Variable>();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "ListModelVar";
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
        setFilter("");
    }

    public void addVar(Variable var) {
        if (!listVariable.contains(var)) {
            listVariable.add(var);
            setFilter("");
        }
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

    public void removeVar(int index) {
        if (listVariable.remove(index) != null) {
            setFilter("");
        }
    }

    public void removeVar(Variable var) {
        if (listVariable.remove(var)) {
            setFilter("");
        }
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

    @Override
    public void update(ArrayList<Lab> listLab, String typeAction) {
        if (typeAction.equals(Type.DEL.toString()) | typeAction.equals(Type.CLEAR.toString())) {
            this.clearList();
        }

    }

}
