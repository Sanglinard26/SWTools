package cdf;

public final class ValueBlock extends Variable {

    public ValueBlock(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, History[] swCsHistory,
            Values values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);
        this.values = values;

    }

    public final String getUnitX() {
        return super.getSwUnitRef()[0];
    }

    public final String getUnitZ() {
        return super.getSwUnitRef()[1];
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();

        // valeur z
        sb.append(getShortName() + " = ");
        sb.append("[");

        if (values.getDimY() > 2) {
            for (short x = 1; x < this.values.getDimX(); x++) {
                for (short y = 1; y < this.values.getDimY(); y++) {
                    if (y > 1) {
                        sb.append(" " + getValue(y, x));
                    } else {
                        sb.append(getValue(y, x));
                    }

                }
                if (x != this.values.getDimX() - 1) {
                    sb.append(";");
                    sb.append("\n");
                } else {
                    sb.append("];");
                }
            }
        } else {
            for (short x = 1; x < this.values.getDimX(); x++) {
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

}
