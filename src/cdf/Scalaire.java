package cdf;

public final class Scalaire extends Variable {

    private final String[][] value = new String[1][1];

    public Scalaire(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] value) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.value[0][0] = value[0][0];
    }

    @Override
    public final String toString() {
        return super.toString() + "Valeur : " + getValue();
    }

    @Override
    public String toMFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(getShortName() + " = ");
        sb.append(getValue() + ";");
        sb.append("\t\t\t" + "%" + "(" + getUnit() + ")" + getLongName());
        return sb.toString();
    }

    public final String getValue() {
        return value[0][0];
    }

    public final String getUnit() {
        return super.getSwUnitRef()[0];
    }

    @Override
    public String[][] getValues() {
        return value;
    }

}
