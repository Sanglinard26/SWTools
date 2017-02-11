/*
 * Creation : 27 janv. 2017
 */
package visu;

import javax.swing.JList;
import paco.Label;
import paco.ListModelLabel;

public class ListLabel extends JList<Label> {

	private static final long serialVersionUID = 1L;

	public ListLabel(ListModelLabel dataModel) {
        super(dataModel);
        setCellRenderer(new ListLabelRenderer());
    }

    @Override
    public ListModelLabel getModel() {
        return (ListModelLabel) super.getModel();
    }

}
