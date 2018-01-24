package cdf;

public final class ValueBlock extends Variable {

    private final String[][] values;
    private final int dimX;
    private final int dimY;

    public ValueBlock(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;

        this.dimX = values[0].length;
        this.dimY = values.length;
    }

    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder("\n");
        for (short y = 0; y < dimY; y++) {
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
        return values;
    }

    public final String getValue(int col, int row) {
        return values[col][row];
    }

    public final int getDimX() {
        return dimX;
    }

    public final int getDimY() {
        return dimY;
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();

        // valeur z
        sb.append(getShortName() + " = ");
        sb.append("[");

        if (dimY > 2) {
            for (short x = 1; x < values[0].length; x++) {
                for (short y = 1; y < values.length; y++) {
                    if (y > 1) {
                        sb.append(" " + getValue(y, x));
                    } else {
                        sb.append(getValue(y, x));
                    }

                }
                if (x != values[0].length - 1) {
                    sb.append(";");
                    sb.append("\n");
                } else {
                    sb.append("];");
                }
            }
        } else {
            for (short x = 1; x < values[0].length; x++) {
                if (x > 1) {
                    sb.append(" " + getValue(1, x));
                } else {
                    sb.append(getValue(1, x));
                }
            }
            sb.append("];");
        }

        // unite z
        sb.append("\t\t\t" + "%" + "(" + ")" + getLongName());

        return sb.toString();
    }

    @Override
    public double getChecksum() {

        double valCheck = 0;

        for (short y = 0; y < dimY; y++) {
            for (short x = 0; x < dimX; x++) {
                valCheck += getValue(y, x).hashCode();
            }
        }

        return valCheck;
    }

}
