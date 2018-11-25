/*
 * Creation : 26 janv. 2018
 */
package cdf;

import utils.NumeralString;

public final class Values {

    private final int dimX;
    private final int dimY;
    private final String[] values;
    private static int idx;

    public Values(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.values = new String[dimX * dimY];
    }

    public final int getDimX() {
        return dimX;
    }

    public final int getDimY() {
        return dimY;
    }

    public final String getValue(int axeX, int axeY) {
        idx = axeY + dimX * axeX;
        return this.values[idx];
    }

    public final void setValue(int axeX, int axeY, String value) {
        idx = axeY + dimX * axeX;
        this.values[idx] = NumeralString.cutNumber(value);
    }

    public final double[][] toDouble2D() {

        double[][] doubleValues = new double[dimY][dimX];

        for (short y = 0; y < dimY; y++) {
            for (short x = 0; x < dimX; x++) {

                if (NumeralString.isNumber(getValue(y, x))) {
                    doubleValues[y][x] = Double.parseDouble(getValue(y, x));
                } else {
                    if (x * y != 0) {
                        doubleValues = new double[0][0];
                        break;
                    }

                }
            }
        }

        return doubleValues;

    }

}
