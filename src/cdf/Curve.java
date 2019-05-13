package cdf;

public final class Curve extends Variable {

	private String[] sharedAxis;

    public Curve(String shortName, String longName, TypeVariable type, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values) {
        super(shortName, longName, type, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;
    }
    
    public Curve(String shortName, String longName, TypeVariable type, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values, String[] sharedAxis) {
        this(shortName, longName, type, swFeatureRef, swUnitRef, swCsHistory, values);
        this.sharedAxis = sharedAxis;
    }

    public final String getUnitX() {
        return super.getSwUnitRef()[0];
    }

    public final String getUnitZ() {
        return super.getSwUnitRef()[1];
    }
    
    public String[] getSharedAxis() {
		return sharedAxis;
	}

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append("% " + getShortName());
        sb.append("\n");

        // valeur x
        sb.append(getShortName().substring(0, getShortName().length() - 2) + "X_A" + " = ");
        sb.append("[");
        for (short x = 0; x < values.getDimX(); x++) {
            if (x > 0) {
                sb.append(" " + getValue(0, x));
            } else {
                sb.append(getValue(0, x));
            }
        }
        sb.append("];");
        // unite x
        sb.append("\t\t\t" + "%" + "(" + getUnitX() + ")");

        // valeur z
        sb.append("\n");
        sb.append(getShortName() + " = ");
        sb.append("[");
        for (short x = 0; x < values.getDimX(); x++) {
            if (x > 0) {
                sb.append(" " + getValue(1, x));
            } else {
                sb.append(getValue(1, x));
            }
        }
        sb.append("];");

        // unite z
        sb.append("\t\t\t" + "%" + "(" + getUnitZ() + ")" + getLongName());

        return sb.toString();
    }

}
