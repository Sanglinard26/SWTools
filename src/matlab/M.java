/*
 * Creation : 27 nov. 2017
 */
package matlab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cdf.Axis;
import cdf.Cdf;
import cdf.Curve;
import cdf.History;
import cdf.Map;
import cdf.Scalaire;
import cdf.ValueBlock;
import cdf.Values;
import cdf.Variable;
import gui.SWToolsMain;

public final class M implements Cdf {

    private static BufferedReader buf = null;
    private static String line;

    private static final History[] EMPTY_COMMENT = new History[0];

    private double checkSum = 0;
    private static final String NO_FUNCTION = "Pas de fonction definie";

    private final String name;
    private boolean valid;
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();
    private final HashSet<String> listCategory = new HashSet<String>();
    private static final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(1);

    public M(final File file) {

        this.name = file.getName().substring(0, file.getName().length() - 2);

        this.parse(file);

    }

    private final void parse(File file) {

        try {

            buf = new BufferedReader(new FileReader(file));

            String shortName = null;
            String category = "";
            Values valeur;

            String subString;

            while (readLineM() != null) {

                if (!line.startsWith("%") && line.indexOf("=") > 0) {
                        shortName = line.substring(0, line.indexOf("=")).trim();

                        if (shortName.lastIndexOf("_") + 1 < line.indexOf("=")) {

                            // System.out.println(shortName);

                            switch (line.substring(shortName.lastIndexOf("_") + 1, line.indexOf("=")).trim()) {
                            case "C":

                                valeur = new Values(1, 1);
                                valeur.setValue(0, 0, line.substring(line.indexOf("=") + 1, line.indexOf(";")).trim());

                                listLabel.add(new Scalaire(shortName, "", VALUE, NO_FUNCTION, new String[] { "" }, EMPTY_COMMENT, valeur));

                                listCategory.add(Cdf.VALUE);

                                break;
                            case "T":

                                subString = line.substring(line.indexOf("["), line.indexOf("]") + 1).replace("[ ", "[").replace(" ]", "]");
                                subString = subString.replace("[", "");
                                subString = subString.replace("]", "");

                                // valeur = new String[2][subString.split("\\s+").length];
                                valeur = new Values(subString.split("\\s+").length, 2);

                                for (int x = 0; x < valeur.getDimX(); x++) {
                                    // valeur[0][x] = Integer.toString(x);
                                    // valeur[1][x] = subString.split("\\s+")[x].trim();
                                    valeur.setValue(0, x, Integer.toString(x));
                                    valeur.setValue(1, x, subString.split("\\s+")[x].trim());
                                }

                                listLabel
                                        .add(new Curve(shortName, "", CURVE_INDIVIDUAL, NO_FUNCTION, new String[] { "", "" }, EMPTY_COMMENT, valeur));

                                listCategory.add(Cdf.CURVE_INDIVIDUAL);

                                break;
                            case "CUR":

                                subString = line.substring(line.indexOf("["), line.indexOf("]") + 1).replace("[ ", "[").replace(" ]", "]");
                                subString = subString.replace("[", "");
                                subString = subString.replace("]", "");

                                // valeur = new String[2][subString.split("\\s+").length];
                                valeur = new Values(subString.split("\\s+").length, 2);

                                for (int x = 0; x < valeur.getDimX(); x++) {
                                    // valeur[0][x] = Integer.toString(x);
                                    // valeur[1][x] = subString.split("\\s+")[x].trim();
                                    valeur.setValue(0, x, Integer.toString(x));
                                    valeur.setValue(1, x, subString.split("\\s+")[x].trim());
                                }

                                listLabel
                                        .add(new Curve(shortName, "", CURVE_INDIVIDUAL, NO_FUNCTION, new String[] { "", "" }, EMPTY_COMMENT, valeur));

                                listCategory.add(Cdf.CURVE_INDIVIDUAL);

                                break;
                            case "M":

                                valeur = new Values(1, 1);
                                valeur.setValue(0, 0, "0");

                                listLabel
                                        .add(new Map(shortName, "", MAP_INDIVIDUAL, NO_FUNCTION, new String[] { "", "", "" }, EMPTY_COMMENT, valeur));

                                listCategory.add(Cdf.MAP_INDIVIDUAL);

                                break;
                            case "A":

                                // valeur = new String[1][line.substring(line.indexOf("[") + 1, line.indexOf("]")).split("\\s").length];
                                valeur = new Values(line.substring(line.indexOf("[") + 1, line.indexOf("]")).split("\\s").length, 1);

                                for (int x = 0; x < valeur.getDimX(); x++) {
                                    // valeur[0][x] = line.substring(line.indexOf("[") + 1, line.indexOf("]")).split("\\s")[x].trim();
                                    valeur.setValue(0, x, line.substring(line.indexOf("[") + 1, line.indexOf("]")).split("\\s")[x].trim());
                                }

                                listLabel.add(new Axis(shortName, "", AXIS_VALUES, NO_FUNCTION, new String[] { "" }, EMPTY_COMMENT, valeur));

                                listCategory.add(Cdf.AXIS_VALUES);

                                break;
                            case "CA":

                                valeur = new Values(1, 1);
                                valeur.setValue(0, 0, "0");

                                listLabel.add(new ValueBlock(shortName, "", VALUE_BLOCK, NO_FUNCTION, new String[] { "" }, EMPTY_COMMENT, valeur));

                                listCategory.add(Cdf.VALUE_BLOCK);

                                break;
                            default:

                                valeur = new Values(1, 1);
                                valeur.setValue(0, 0, "0");

                                listLabel.add(new Scalaire(shortName, "", category, NO_FUNCTION, new String[] { "" }, EMPTY_COMMENT, valeur));
                                break;
                            }

                            checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
                        }
                }

            }

            this.valid = true;

        } catch (Exception e) {

            e.printStackTrace();

            SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);

        } finally {

            try {
                buf.close();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }

    private final String readLineM() {
        try {
            return line = buf.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int getNbLabel() {
        return this.listLabel.size();
    }

    @Override
    public HashSet<String> getCategoryList() {
        return listCategory;
    }

    @Override
    public ArrayList<Variable> getListLabel() {
        return this.listLabel;
    }

    @Override
    public HashMap<Integer, Integer> getRepartitionScore() {
        return repartitionScore;
    }

    @Override
    public float getAvgScore() {
        return 0;
    }

    @Override
    public int getMinScore() {
        return 0;
    }

    @Override
    public int getMaxScore() {
        return 0;
    }

    @Override
    public double getCheckSum() {
        return checkSum;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

}
