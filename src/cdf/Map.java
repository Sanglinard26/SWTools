package cdf;

import utils.Utilitaire;

public final class Map extends Variable {

	private String[] sharedAxis;
    private final Values values;

    private float minZValue = Float.POSITIVE_INFINITY;
    private float maxZValue = Float.NEGATIVE_INFINITY;

    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values) {

        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.values = values;
        float value;

        for (short y = 1; y < values.getDimY(); y++) {
            for (short x = 1; x < values.getDimX(); x++) {

                if (Utilitaire.isNumber(values.getValue(y, x))) {
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
    
    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory, Values values, String[] sharedAxis) {
    	
    	this(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory, values);
    	this.sharedAxis = sharedAxis;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("\n");

        for (short y = 0; y < values.getDimY(); y++) {
            for (short x = 0; x < values.getDimX(); x++) {
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
    
    public String[] getSharedAxis() {
		return sharedAxis;
	}

    @Override
    public final Values getValues() {
        return values;
    }

    public final String getValue(int col, int row) {
        return values.getValue(col, row);
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

    @Override
    public double getChecksum() {

        double valCheck = 0;
        String value;

        for (short y = 0; y < values.getDimY(); y++) {
            for (short x = 0; x < values.getDimX(); x++) {
                value = getValue(y, x);
                if (value != null) {
                    valCheck += value.hashCode();
                }
            }
        }

        return valCheck;
    }

}
