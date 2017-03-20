/*
 * Creation : 27 janv. 2017
 */
package visu;

import javax.swing.JList;

import lab.ListModelVar;
import lab.Variable;

public final class ListVar extends JList<Variable> {

    private static final long serialVersionUID = 1L;

    public ListVar(ListModelVar dataModel) {
        super(dataModel);
        setCellRenderer(new ListVarRenderer());
    }

    @Override
    public ListModelVar getModel() {
        return (ListModelVar) super.getModel();
    }

}
