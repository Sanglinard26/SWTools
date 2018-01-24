package cdf;

public final class Axis extends Variable {

    private final String[][] zValues;
    private final int dim;

    public Axis(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.zValues = values;
        this.dim = zValues[0].length;

    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("\n");

        for (short x = 0; x < dim; x++) {
            sb.append(this.getzValues(x) + "\t");
        }
        sb.append("\n");

        return super.toString() + "Valeurs :" + sb.toString();
    }

    public final int getDim() {
        return dim;
    }

    public final String getUnit() {
        return super.getSwUnitRef()[0];
    }

    public final String getzValues(int x) {
        return zValues[0][x];
    }

    @Override
    public String[][] getValues() {
        return zValues;
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append(getShortName() + " = ");
        // valeur
        sb.append("[");
        for (short x = 0; x < this.zValues[0].length; x++) {
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

        for (short x = 0; x < dim; x++) {
            valCheck += getzValues(x).hashCode();
        }
        return valCheck;
    }
}
