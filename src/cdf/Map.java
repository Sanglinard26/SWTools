package cdf;

public final class Map extends Variable {

    private final String[][] values;
    // private final String[] xValues;
    // private final String[] yValues;
    // private final String[][] zValues;
    private final int dimX;
    private final int dimY;

    private float minZValue = Float.POSITIVE_INFINITY;
    private float maxZValue = Float.NEGATIVE_INFINITY;

    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.values = values;
        this.dimX = values[0].length;
        this.dimY = values.length;

        // xValues = new String[dimX - 1];
        // for (short i = 0; i < xValues.length; i++) {
        // xValues[i] = values[0][i + 1];
        // }
        //
        // yValues = new String[dimY - 1];
        // for (short i = 0; i < yValues.length; i++) {
        // yValues[i] = values[i + 1][0];
        // }

        // zValues = new String[yValues.length][xValues.length];
        for (short x = 1; x < values[0].length; x++) {
            for (short y = 1; y < values.length; y++) {
                // zValues[y][x] = values[y + 1][x + 1];

                try {
                    if (Float.parseFloat(values[y][x]) < minZValue)
                        minZValue = Float.parseFloat(values[y][x]);

                    if (Float.parseFloat(values[y][x]) > maxZValue)
                        maxZValue = Float.parseFloat(values[y][x]);
                } catch (NumberFormatException e) {
                    minZValue = Float.NaN;
                    maxZValue = Float.NaN;
                }

            }
        }
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

    public final String getUnitY() {
        return super.getSwUnitRef()[1];
    }

    public final String getUnitZ() {
        return super.getSwUnitRef()[2];
    }

    // public final String[] getxValues() {
    // return xValues;
    // }
    //
    // public final String[] getyValues() {
    // return yValues;
    // }
    //
    // public final String getzValue(int col, int row) {
    // return zValues[col][row];
    // }

    public final float getMaxZValue() {
        return maxZValue;
    }

    public final float getMinZValue() {
        return minZValue;
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
    public String toMFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("% " + getShortName());
        sb.append("\n");

        // valeur x
        sb.append(getShortName().substring(0, getShortName().length() - 2) + "X_A" + " = ");
        sb.append("[");
        for (int x = 1; x < values[0].length; x++) {
            if (x > 1) {
                sb.append(" " + getValue(0, x));
            } else {
                sb.append(getValue(0, x));
            }
        }
        sb.append("];");
        // unite x
        sb.append("\t\t\t" + "%" + "(" + getUnitX() + ")");

        // valeur y
        sb.append("\n");
        sb.append(getShortName().substring(0, getShortName().length() - 2) + "Y_A" + " = ");
        sb.append("[");
        for (int x = 1; x < values.length; x++) {
            if (x > 1) {
                sb.append(" " + getValue(x, 0));
            } else {
                sb.append(getValue(x, 0));
            }
        }
        sb.append("];");

        // unite y
        sb.append("\t\t\t" + "%" + "(" + getUnitY() + ")");

        // valeur z
        sb.append("\n");
        sb.append(getShortName() + " = ");
        sb.append("[");
        for (int x = 1; x < values[0].length; x++) {
            for (int y = 1; y < values.length; y++) {
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

        // unite z
        sb.append("\t\t\t" + "%" + "(" + getUnitZ() + ")" + getLongName());

        return sb.toString();
    }

}
