package cdf;

public final class Axis extends Variable {

    private final Values zValues;

    public Axis(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.zValues = values;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("\n");

        for (short x = 0; x < this.zValues.getDimX(); x++) {
            sb.append(this.getzValues(x) + "\t");
        }
        sb.append("\n");

        return super.toString() + "Valeurs :" + sb.toString();
    }

    public final String getUnit() {
        return super.getSwUnitRef()[0];
    }

    public final String getzValues(int y) {
        return zValues.getValue(0, y);
    }

    @Override
    public Values getValues() {
        return zValues;
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append(getShortName() + " = ");
        // valeur
        sb.append("[");
        for (short x = 0; x < this.zValues.getDimX(); x++) {
            if (x > 0) {
                sb.append(" " + getzValues(x));
            } else {
                sb.append(getzValues(x));
            }

        }
        sb.append("];");
        sb.append("\t\t\t" + "%" + "(" + getUnit() + ")" + getLongName());
        return sb.toString();
    }

    @Override
    public double getChecksum() {

        double valCheck = 0;
        String value;

        for (short x = 0; x < this.zValues.getDimX(); x++) {
            value = getzValues(x);
            if (value != null) {
                valCheck += value.hashCode();
            }
        }
        return valCheck;
    }
}
