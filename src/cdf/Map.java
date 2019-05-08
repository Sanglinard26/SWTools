package cdf;

import utils.NumeralString;

public final class Map extends Variable {

    private String[] sharedAxis;

    private float minZValue = Float.POSITIVE_INFINITY;
    private float maxZValue = Float.NEGATIVE_INFINITY;

    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values) {

        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.values = values;
        float value;

        for (short y = 1; y < values.getDimY(); y++) {
            for (short x = 1; x < values.getDimX(); x++) {

                if (NumeralString.isNumber(values.getValue(y, x))) {
                    value = Float.parseFloat(values.getValue(y, x));

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

    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values,
            String[] sharedAxis) {

        this(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory, values);
        this.sharedAxis = sharedAxis;
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

    public String[] getSharedAxis() {
        return sharedAxis;
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append("% " + getShortName());
        sb.append("\n");

        // valeur x
        sb.append(getShortName().substring(0, getShortName().length() - 2) + "X_A" + " = ");
        sb.append("[");
        for (short x = 1; x < values.getDimX(); x++) {
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
        for (short x = 1; x < values.getDimY(); x++) {
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
            for (short x = 1; x < values.getDimX(); x++) {
                for (short y = 1; y < values.getDimY(); y++) {
                    if (y > 1) {
                        sb.append(" " + getValue(y, x));
                    } else {
                        sb.append(getValue(y, x));
                    }
                }
                if (x != values.getDimX() - 1) {
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
            for (short y = 1; y < values.getDimY(); y++) {
                for (short x = 1; x < values.getDimX(); x++) {
                    if (x > 1) {
                        sb.append(" " + getValue(y, x));
                    } else {
                        sb.append(getValue(y, x));
                    }
                }

                if (y != values.getDimY() - 1) {
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

}
