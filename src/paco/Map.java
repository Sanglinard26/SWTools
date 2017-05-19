package paco;

public final class Map extends Variable {

    private final String[][] values;
    private final String[] xValues;
    private final String[] yValues;
    private final String[][] zValues;
    private final int dimX;
    private final int dimY;

    private Double minZValue = Double.POSITIVE_INFINITY;
    private Double maxZValue = Double.NEGATIVE_INFINITY;

    // private final RainbowScale rainbowScale;

    public Map(String shortName, String longName, String category, String swFeatureRef, String[] swUnitRef, String[][] swCsHistory,
            String[][] values) {
        super(shortName, longName, category, swFeatureRef, swUnitRef, swCsHistory);

        this.values = values;
        this.dimX = values[0].length;
        this.dimY = values.length;

        xValues = new String[dimX - 1];
        for (short i = 0; i < xValues.length; i++) {
            xValues[i] = values[0][i + 1];
        }

        yValues = new String[dimY - 1];
        for (short i = 0; i < yValues.length; i++) {
            yValues[i] = values[i + 1][0];
        }

        zValues = new String[yValues.length][xValues.length];
        for (short x = 0; x < xValues.length; x++) {
            for (short y = 0; y < yValues.length; y++) {
                zValues[y][x] = values[y + 1][x + 1];

                try {
                    if (Double.parseDouble(values[y + 1][x + 1]) < minZValue)
                        minZValue = Double.parseDouble(values[y + 1][x + 1]);

                    if (Double.parseDouble(values[y + 1][x + 1]) > maxZValue)
                        maxZValue = Double.parseDouble(values[y + 1][x + 1]);
                } catch (NumberFormatException e) {
                    minZValue = Double.NaN;
                    maxZValue = Double.NaN;
                }

            }
        }
        // rainbowScale = new RainbowScale(new Range(this.getMinZValue(), this.getMaxZValue()), (dimX - 1) * (dimY - 1),
        // RainbowScale.BLUE_TO_RED_RANGE);
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

    public final String[] getxValues() {
        return xValues;
    }

    public final String[] getyValues() {
        return yValues;
    }

    public final String getzValue(int col, int row) {
        return zValues[col][row];
    }

    public final Double getMaxZValue() {
        return maxZValue;
    }

    public final Double getMinZValue() {
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

}
