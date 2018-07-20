package cdf;

public final class Curve extends Variable {

	private String[] sharedAxis;
    private final Values values;

    public Curve(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;
    }
    
    public Curve(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values, String[] sharedAxis) {
        this(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory, values);
        this.sharedAxis = sharedAxis;
    }

    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder("\n");
        for (byte y = 0; y < 2; y++) {
            for (byte x = 0; x < values.getDimX(); x++) {
                sb.append(this.getValue(y, x) + "\t");
            }
            sb.append("\n");
        }
        return super.toString() + "Valeurs :" + sb.toString();
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
    public final Values getValues() {
        return this.values;
    }

    public final String getValue(int col, int row) {
        return values.getValue(col, row);
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

    @Override
    public double getChecksum() {

        double valCheck = 0;
        String value;

        for (byte y = 0; y < 2; y++) {
            for (short x = 0; x < values.getDimX(); x++) {
                value = getValue(y, x);
                if (value != null) {
                    valCheck += value.hashCode();
                }
            }
        }

        return valCheck;
    }

}
