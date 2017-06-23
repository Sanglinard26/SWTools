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
}
