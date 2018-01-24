package cdf;

import tools.Utilitaire;

public final class Map extends Variable {

    private final String[][] values;
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

        float value;

        for (short x = 1; x < dimX; x++) {
            for (short y = 1; y < dimY; y++) {

                if (Utilitaire.isNumber(values[y][x])) {
                    value = Float.parseFloat(values[y][x]);

                    if (value < minZValue)
                        minZValue = value;

                    if (value > maxZValue)
                        maxZValue = value;
                } else {
                    minZValue = Float.NaN;
                    maxZValue = Float.NaN;
                    break;
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

    /*
     * public final double[] getXvalues() { double[] xValues = new double[this.dimX - 1]; for (int i = 1; i < this.dimX - 1; i++) { if
     * (Utilitaire.isNumber(this.getValue(0, i))) { xValues[i - 1] = Double.parseDouble(this.getValue(0, i)); } else { xValues[i - 1] = i; } } return
     * xValues; }
     * 
     * public final double[] getYvalues() { double[] yValues = new double[this.dimY - 1]; for (int i = 1; i < this.dimY - 1; i++) { if
     * (Utilitaire.isNumber(this.getValue(i, 0))) { yValues[i - 1] = Double.parseDouble(this.getValue(i, 0)); } else { yValues[i - 1] = i; } } return
     * yValues; }
     */

    public final int getDimX() {
        return dimX;
    }

    public final int getDimY() {
        return dimY;
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append("% " + getShortName());
        sb.append("\n");

        // valeur x
        sb.append(getShortName().substring(0, getShortName().length() - 2) + "X_A" + " = ");
        sb.append("[");
        for (short x = 1; x < dimX; x++) {
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
        for (short x = 1; x < dimY; x++) {
            if (x > 1) {
                sb.append(" " + getValue(x, 0));
            } else {
                sb.append(getValue(x, 0));
            }
        }
        sb.append("];");

        // unite y
        sb.append("\t\t\t" + "%" + "(" + getUnitY() + ")");

        if (transpose) {

            // valeur z
            sb.append("\n");
            sb.append(getShortName() + " = ");
            sb.append("[");
            for (short x = 1; x < dimX; x++) {
                for (short y = 1; y < dimY; y++) {
                    if (y > 1) {
                        sb.append(" " + getValue(y, x));
                    } else {
                        sb.append(getValue(y, x));
                    }
                }
                if (x != dimX - 1) {
                    sb.append(";");
                    sb.append("\n");
                } else {
                    sb.append("];");
                }
            }

        } else {

            // valeur z
            sb.append("\n");
            sb.append(getShortName() + " = ");
            sb.append("[");
            for (short y = 1; y < dimY; y++) {
                for (short x = 1; x < dimX; x++) {
                    if (x > 1) {
                        sb.append(" " + getValue(y, x));
                    } else {
                        sb.append(getValue(y, x));
                    }
                }

                if (y != dimY - 1) {
                    sb.append(";");
                    sb.append("\n");
                } else {
                    sb.append("];");
                }
            }
        }

        // unite z
        sb.append("\t\t\t" + "%" + "(" + getUnitZ() + ")" + getLongName());

        return sb.toString();
    }

    @Override
    public double getChecksum() {

        double valCheck = 0;

        for (short x = 0; x < dimX; x++) {
            for (short y = 0; y < dimY; y++) {
                valCheck += values[y][x].hashCode();
            }
        }

        return valCheck;
    }

}
