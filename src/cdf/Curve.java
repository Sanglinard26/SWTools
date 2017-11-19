package cdf;

public final class Curve extends Variable {

    private final String[][] values;
    private final int dimX;

    public Curve(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;

        this.dimX = values[0].length;

    }

    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder("\n");
        for (byte y = 0; y < 2; y++) {
            for (short x = 0; x < dimX; x++) {
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

    @Override
    public final String[][] getValues() {
        return this.values;
    }

    public final String getValue(int col, int row) {
        return values[col][row];
    }

    public final int getDimX() {
        return dimX;
    }

    @Override
    public String toMFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("% " + getShortName());
        sb.append("\n");

        // valeur x
        sb.append(getShortName().substring(0, getShortName().length() - 2) + "X_A" + " = ");
        sb.append("[");
        for (int x = 0; x < values[0].length; x++) {
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
        for (int x = 0; x < values[0].length; x++) {
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
		
		for (byte y = 0; y < 2; y++) {
            for (short x = 0; x < dimX; x++) {
                valCheck += getValue(y, x).hashCode();
            }
        }
		
		return valCheck;
	}

}
