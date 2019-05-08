package cdf;

public final class Axis extends Variable {

    public Axis(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.values = values;
    }

    public final String getUnit() {
        return super.getSwUnitRef()[0];
    }

    public final String getzValues(int y) {
        return values.getValue(0, y);
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append(getShortName() + " = ");
        // valeur
        sb.append("[");
        for (short x = 0; x < this.values.getDimX(); x++) {
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
}
