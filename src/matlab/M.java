/*
 * Creation : 27 nov. 2017
 */
package matlab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cdf.Axis;
import cdf.Cdf;
import cdf.Curve;
import cdf.ExportUtils;
import cdf.Map;
import cdf.Observable;
import cdf.Scalaire;
import cdf.ValueBlock;
import cdf.Variable;
import gui.Observer;
import gui.PanelCDF;

public final class M implements Cdf, Observable {

    private static BufferedReader buf = null;
    private static String line;
    private static int numLine;
    private static final NumberFormat nbf = NumberFormat.getInstance();

    private double checkSum = 0;
    private static final String NO_FUNCTION = "Pas de fonction definie";

    private final String name;
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();
    private final ArrayList<String> listCategory = new ArrayList<String>();
    private static final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(1);

    private final ArrayList<Observer> listObserver = new ArrayList<Observer>();

    public M(final File file, PanelCDF panCdf) {

        numLine = 0;

        if (panCdf != null)
            addObserver(panCdf);

        nbf.setMaximumFractionDigits(1);

        this.name = file.getName().substring(0, file.getName().length() - 2);

        this.parse(file);
        
        listObserver.clear(); // Plus besoin d'observer

    }

    private final void parse(File file) {

        try {

            buf = new BufferedReader(new FileReader(file));
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            int nbLines = 0;
            while (reader.readLine() != null)
                nbLines++;
            reader.close();

            String shortName = null;
            String category = "";
            String[][] valeur;

            String subString;

            while (readLineM() != null) {

                notifyObserver(this.name, nbf.format(((double) numLine / (double) (nbLines)) * 100) + "%");

                if (!line.startsWith("%")) {
                    if (line.indexOf("=") > 0) {

                        shortName = line.substring(0, line.indexOf("=")).trim();

                        if (shortName.lastIndexOf("_") + 1 < line.indexOf("=")) {

                            System.out.println(shortName);

                            switch (line.substring(shortName.lastIndexOf("_") + 1, line.indexOf("=")).trim()) {
                            case "C":

                                listLabel.add(new Scalaire(shortName, "", VALUE, NO_FUNCTION, new String[] { "" }, new String[0][0],
                                        new String[][] { { line.substring(line.indexOf("=") + 1, line.indexOf(";")).trim() } }));

                                if (!listCategory.contains(Cdf.VALUE))
                                    listCategory.add(Cdf.VALUE);

                                break;
                            case "T":

                                subString = line.substring(line.indexOf("["), line.indexOf("]") + 1).replace("[ ", "[").replace(" ]", "]");
                                subString = subString.replace("[", "");
                                subString = subString.replace("]", "");

                                valeur = new String[2][subString.split("\\s+").length];

                                for (int x = 0; x < valeur[0].length; x++) {
                                    valeur[0][x] = Integer.toString(x);
                                    valeur[1][x] = subString.split("\\s+")[x].trim();

                                }

                                listLabel.add(
                                        new Curve(shortName, "", CURVE_INDIVIDUAL, NO_FUNCTION, new String[] { "", "" }, new String[0][0], valeur));

                                if (!listCategory.contains(Cdf.CURVE_INDIVIDUAL))
                                    listCategory.add(Cdf.CURVE_INDIVIDUAL);

                                break;
                            case "CUR":

                                subString = line.substring(line.indexOf("["), line.indexOf("]") + 1).replace("[ ", "[").replace(" ]", "]");
                                subString = subString.replace("[", "");
                                subString = subString.replace("]", "");

                                valeur = new String[2][subString.split("\\s+").length];

                                for (int x = 0; x < valeur[0].length; x++) {
                                    valeur[0][x] = Integer.toString(x);
                                    valeur[1][x] = subString.split("\\s+")[x].trim();

                                }

                                listLabel.add(
                                        new Curve(shortName, "", CURVE_INDIVIDUAL, NO_FUNCTION, new String[] { "", "" }, new String[0][0], valeur));

                                if (!listCategory.contains(Cdf.CURVE_INDIVIDUAL))
                                    listCategory.add(Cdf.CURVE_INDIVIDUAL);

                                break;
                            case "M":
                                listLabel.add(new Map(shortName, "", MAP_INDIVIDUAL, NO_FUNCTION, new String[] { "", "", "" }, new String[0][0],
                                        new String[][] { { "0" } }));

                                if (!listCategory.contains(Cdf.MAP_INDIVIDUAL))
                                    listCategory.add(Cdf.MAP_INDIVIDUAL);

                                break;
                            case "A":

                                valeur = new String[1][line.substring(line.indexOf("[") + 1, line.indexOf("]")).split("\\s").length];

                                for (int x = 0; x < valeur[0].length; x++) {
                                    valeur[0][x] = line.substring(line.indexOf("[") + 1, line.indexOf("]")).split("\\s")[x].trim();

                                }

                                listLabel.add(new Axis(shortName, "", AXIS_VALUES, NO_FUNCTION, new String[] { "" }, new String[0][0], valeur));

                                if (!listCategory.contains(Cdf.AXIS_VALUES))
                                    listCategory.add(Cdf.AXIS_VALUES);

                                break;
                            case "CA":
                                listLabel.add(new ValueBlock(shortName, "", VALUE_BLOCK, NO_FUNCTION, new String[] { "" }, new String[0][0],
                                        new String[][] { { "0" } }));

                                if (!listCategory.contains(Cdf.VALUE_BLOCK))
                                    listCategory.add(Cdf.VALUE_BLOCK);

                                break;
                            default:
                                listLabel.add(new Scalaire(shortName, "", category, NO_FUNCTION, new String[] { "" }, new String[0][0],
                                        new String[][] { { "0" } }));
                                break;
                            }

                            checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
                        }

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                numLine = 0;
                buf.close();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }

    private final String readLineM() {
        try {
            numLine++;
            return line = buf.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addObserver(Observer obs) {
        listObserver.add(obs);
    }

    @Override
    public void notifyObserver(String cdf, String rate) {
        for (Observer obs : listObserver) {
            obs.update(cdf, rate);
        }
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
    public ArrayList<String> getCategoryList() {
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
    public boolean exportToExcel(File file) {
        return ExportUtils.toExcel(this, file);
    }

    @Override
    public boolean exportToTxt(File file) {
        return ExportUtils.toText(this, file);
    }

    @Override
    public boolean exportToM(File file) {
        return ExportUtils.toM(this, file);
    }

}
