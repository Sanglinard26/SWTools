package cdf;

import utils.NumeralString;

public final class Value extends Variable {

    public Value(String shortName, String longName, TypeVariable type, String swFeatureRef, String[] swUnitRef, History[] swCsHistory,
            Values value) {
        super(shortName, longName, type, swFeatureRef, swUnitRef, swCsHistory);

        this.values = value;
    }

    @Override
    public String toMFormat(boolean transpose) {
        StringBuilder sb = new StringBuilder();
        sb.append(getShortName() + " = ");

        if (NumeralString.isNumber(getValue())) {
            sb.append(getValue() + ";");
        } else {
            if (Boolean.parseBoolean(getValue().toString())) {
                sb.append("1;");
            } else {
                sb.append("0;");
            }
        }

        sb.append("\t\t\t" + "%" + "(" + getUnit() + ")" + getLongName());
        return sb.toString();
    }

    public final String getValue() {
        return values.getValue(0, 0);
    }

    public final String getUnit() {
        return super.getSwUnitRef()[0];
    }

}
