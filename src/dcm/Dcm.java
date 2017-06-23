/*
 * Creation : 19 juin 2017
 */
package dcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cdf.Axis;
import cdf.Cdf;
import cdf.Curve;
import cdf.Map;
import cdf.Scalaire;
import cdf.ValueBlock;
import cdf.Variable;
import paco.PaCo;
import tools.Utilitaire;

public final class Dcm implements Cdf {

    // Mot cle global
    public static final String COMMENTAIRE = "*";
    public static final String END = "END";

    // Mot cle pour les variables
    public static final String PARAMETER = "FESTWERT";
    public static final String ARRAY = "FESTWERTEBLOCK";
    public static final String MATRIX = "FESTWERTEBLOCK";
    public static final String LINE = "KENNLINIE";
    public static final String MAP = "KENNFELD";
    public static final String FIXED_LINE = "FESTKENNLINIE";
    public static final String FIXED_MAP = "FESTKENNFELD";
    public static final String GROUP_LINE = "GRUPPENKENNLINIE";
    public static final String GROUP_MAP = "GRUPPENKENNFELD";
    public static final String DISTRIBUTION = "STUETZSTELLENVERTEILUNG";
    public static final String TEXTSTRING = "TEXTSTRING";

    // Mot cle dans le bloc d'une variable
    public static final String DESCRIPTION = "LANGNAME";
    public static final String FONCTION = "FUNKTION";
    public static final String UNITE_X = "EINHEIT_X";
    public static final String UNITE_Y = "EINHEIT_Y";
    public static final String UNITE_W = "EINHEIT_W";
    public static final String AXE_X = "ST/X";
    public static final String AXE_Y = "ST/Y";
    public static final String AXE_X_TXT = "ST_TX/X";
    public static final String AXE_Y_TXT = "ST_TX/Y";
    public static final String VALEUR_NOMBRE = "WERT";
    public static final String VALEUR_TEXT = "TEXT";
    public static final String AXE_PARTAGE_X = "*SSTX";
    public static final String AXE_PARTAGE_Y = "*SSTY";

    private final String name;
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();

    public Dcm(final File file) {

        this.name = file.getName().substring(0, file.getName().length() - 4);

        this.parse(file);

    }

    private final void parse(File file) {

        BufferedReader buf = null;
        final StringBuilder description = new StringBuilder();
        final StringBuilder fonction = new StringBuilder();
        String[] unite;
        String[][] valeur;

        try {

            buf = new BufferedReader(new FileReader(file));
            String line;
            String[] spaceSplitLine;
            String[] spaceSplitLine2;
            String[] quotesSplitLine;
            String[] threeSpaceSplitLine;

            // Pour les LINE
            ArrayList<String> axeX = new ArrayList<String>();
            ArrayList<String> axeY = new ArrayList<String>();

            // Pour les MAP
            int cnt;
            ArrayList<String> axeTmp = new ArrayList<String>();

            while ((line = buf.readLine()) != null) {

                spaceSplitLine = line.split(" ");

                if (spaceSplitLine.length > 0) {

                    switch (spaceSplitLine[0]) {

                    case PARAMETER:

                        unite = new String[1];
                        valeur = new String[1][1];

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }
                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }
                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[0] = " ";
                                }

                            }
                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {
                                valeur[0][0] = Utilitaire.cutNumber(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Scalaire(spaceSplitLine[1], description.toString(), PaCo._C, fonction.toString(), unite, new String[0][0],
                                valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        break;

                    case TEXTSTRING:

                        unite = new String[] { " " };
                        valeur = new String[1][1];

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }
                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(VALEUR_TEXT)) {
                                valeur[0][0] = quotesSplitLine[quotesSplitLine.length - 1];
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Scalaire(spaceSplitLine[1], description.toString(), PaCo._C, fonction.toString(), unite, new String[0][0],
                                valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        break;

                    case LINE:

                        unite = new String[2];
                        valeur = new String[2][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[0] = " ";
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[1] = " ";
                                }
                            }

                            if (line.trim().startsWith(AXE_X)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_X)) {
                                        axeX.add(Utilitaire.cutNumber(s));
                                    }
                                }
                            }

                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(VALEUR_NOMBRE) & !s.equals(VALEUR_TEXT)) {
                                        axeY.add(Utilitaire.cutNumber(s));
                                    }
                                }
                            }

                        }

                        valeur[0] = axeX.toArray(new String[axeX.size()]);
                        valeur[1] = axeY.toArray(new String[axeY.size()]);

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(
                                new Curve(spaceSplitLine[1], description.toString(), PaCo._T, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeX.clear();
                        axeY.clear();

                        break;

                    case GROUP_LINE:

                        unite = new String[2];
                        valeur = new String[2][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[0] = " ";
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[1] = " ";
                                }
                            }

                            if (line.trim().startsWith(AXE_X)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_X)) {
                                        axeX.add(Utilitaire.cutNumber(s));
                                    }
                                }
                            }

                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(VALEUR_NOMBRE) & !s.equals(VALEUR_TEXT)) {
                                        axeY.add(Utilitaire.cutNumber(s));
                                    }
                                }
                            }

                        }

                        valeur[0] = axeX.toArray(new String[axeX.size()]);
                        valeur[1] = axeY.toArray(new String[axeY.size()]);

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(
                                new Curve(spaceSplitLine[1], description.toString(), PaCo._T, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeX.clear();
                        axeY.clear();

                        break;

                    case MAP:

                        cnt = 1;

                        unite = new String[3];
                        valeur = new String[Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])
                                + 1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1];

                        axeTmp.add("Y \\ X");

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[0] = " ";
                                }
                            }

                            if (line.trim().startsWith(UNITE_Y)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[1] = " ";
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[2] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[2] = " ";
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_X) & !s.equals(AXE_X_TXT)) {
                                        axeTmp.add(Utilitaire.cutNumber(s));
                                    }
                                }

                                if (axeTmp.size() == valeur[0].length) {
                                    valeur[0] = axeTmp.toArray(new String[axeTmp.size()]);

                                    axeTmp.clear();
                                }
                            }

                            if (line.trim().startsWith(AXE_Y) | line.trim().startsWith(AXE_Y_TXT) | line.trim().startsWith(VALEUR_NOMBRE)
                                    | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_Y) & !s.equals(AXE_Y_TXT) & !s.equals(VALEUR_NOMBRE)
                                            & !s.equals(VALEUR_TEXT)) {
                                        axeTmp.add(Utilitaire.cutNumber(s));
                                    }
                                }

                                if (axeTmp.size() == valeur[0].length) {
                                    valeur[cnt] = axeTmp.toArray(new String[axeTmp.size()]);

                                    cnt++;

                                    axeTmp.clear();
                                }

                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(
                                new Map(spaceSplitLine[1], description.toString(), PaCo._M, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        break;

                    case GROUP_MAP:

                        cnt = 1;

                        unite = new String[3];
                        valeur = new String[Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])
                                + 1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1];

                        axeTmp.add("Y \\ X");

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[0] = " ";
                                }
                            }

                            if (line.trim().startsWith(UNITE_Y)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[1] = " ";
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[2] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[2] = " ";
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_X) & !s.equals(AXE_X_TXT)) {
                                        axeTmp.add(Utilitaire.cutNumber(s));
                                    }
                                }

                                if (axeTmp.size() == valeur[0].length) {
                                    valeur[0] = axeTmp.toArray(new String[axeTmp.size()]);

                                    axeTmp.clear();
                                }
                            }

                            if (line.trim().startsWith(AXE_Y) | line.trim().startsWith(AXE_Y_TXT) | line.trim().startsWith(VALEUR_NOMBRE)
                                    | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_Y) & !s.equals(AXE_Y_TXT) & !s.equals(VALEUR_NOMBRE)
                                            & !s.equals(VALEUR_TEXT)) {
                                        axeTmp.add(Utilitaire.cutNumber(s));
                                    }
                                }

                                if (axeTmp.size() == valeur[0].length) {
                                    valeur[cnt] = axeTmp.toArray(new String[axeTmp.size()]);

                                    cnt++;

                                    axeTmp.clear();
                                }

                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(
                                new Map(spaceSplitLine[1], description.toString(), PaCo._M, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        break;

                    case DISTRIBUTION:

                        unite = new String[1];

                        valeur = new String[1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        ArrayList<String> distribution = new ArrayList<String>();

                        while (!(line = buf.readLine()).equals(END)) {

                            spaceSplitLine2 = line.split(" ");
                            quotesSplitLine = line.split("\"");

                            if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                } else {
                                    unite[0] = " ";
                                }
                            }

                            if (line.trim().startsWith(AXE_X)) {

                                threeSpaceSplitLine = line.split("   ");

                                for (String s : threeSpaceSplitLine) {
                                    if (s.length() != 0 & !s.equals(AXE_X)) {
                                        distribution.add(Utilitaire.cutNumber(s));
                                    }
                                }
                            }

                        }

                        valeur[0] = distribution.toArray(new String[distribution.size()]);

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(
                                new Axis(spaceSplitLine[1], description.toString(), PaCo._A, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        break;

                    case MATRIX:

                        cnt = 1;

                        if (spaceSplitLine[spaceSplitLine.length - 2].equals("@")) {
                            unite = new String[1];
                            valeur = new String[Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])
                                    + 1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 3]) + 1];

                            axeTmp.add("Y \\ X");

                            for (int x = 1; x < valeur[0].length; x++) {
                                axeTmp.add(Integer.toString(x));
                            }
                            valeur[0] = axeTmp.toArray(new String[axeTmp.size()]);

                            axeTmp.clear();

                            while (!(line = buf.readLine()).equals(END)) {

                                spaceSplitLine2 = line.split(" ");
                                quotesSplitLine = line.split("\"");

                                if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                    description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                                }

                                if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                    fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                                }

                                if (line.trim().startsWith(UNITE_W)) {

                                    if (quotesSplitLine.length > 1) {
                                        unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                    } else {
                                        unite[0] = " ";
                                    }
                                }

                                if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                    threeSpaceSplitLine = line.split("   ");

                                    axeTmp.add(Integer.toString(cnt));

                                    for (String s : threeSpaceSplitLine) {
                                        if (s.length() != 0 & !s.equals(VALEUR_NOMBRE) & !s.equals(VALEUR_TEXT)) {
                                            if (Utilitaire.isNumber(s)) {
                                                axeTmp.add(Utilitaire.cutNumber(s));
                                            } else {
                                                axeTmp.add(s);
                                            }
                                        }
                                    }

                                    if (axeTmp.size() == valeur[0].length) {
                                        valeur[cnt] = axeTmp.toArray(new String[axeTmp.size()]);

                                        cnt++;

                                        axeTmp.clear();
                                    }

                                }

                            }

                        } else {

                            unite = new String[1];
                            valeur = new String[2][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]) + 1];

                            axeTmp.add("X");

                            for (int x = 1; x < valeur[0].length; x++) {
                                axeTmp.add(Integer.toString(x));
                            }
                            valeur[0] = axeTmp.toArray(new String[axeTmp.size()]);

                            axeTmp.clear();

                            axeTmp.add("Z");

                            while (!(line = buf.readLine()).equals(END)) {

                                spaceSplitLine2 = line.split(" ");
                                quotesSplitLine = line.split("\"");

                                if (line.trim().startsWith(DESCRIPTION) & quotesSplitLine.length > 1) {
                                    description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                                }

                                if (line.trim().startsWith(FONCTION) & spaceSplitLine2.length > 1) {
                                    fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                                }

                                if (line.trim().startsWith(UNITE_W)) {

                                    if (quotesSplitLine.length > 1) {
                                        unite[0] = quotesSplitLine[quotesSplitLine.length - 1];
                                    } else {
                                        unite[0] = " ";
                                    }
                                }

                                if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                    threeSpaceSplitLine = line.split("   ");

                                    for (String s : threeSpaceSplitLine) {
                                        if (s.length() != 0 & !s.equals(VALEUR_NOMBRE) & !s.equals(VALEUR_TEXT)) {
                                            if (Utilitaire.isNumber(s)) {
                                                axeTmp.add(Utilitaire.cutNumber(s));
                                            } else {
                                                axeTmp.add(s);
                                            }

                                        }
                                    }

                                    if (axeTmp.size() == valeur[0].length) {
                                        valeur[1] = axeTmp.toArray(new String[axeTmp.size()]);

                                        axeTmp.clear();
                                    }

                                }

                            }

                        }

                        listLabel.add(new ValueBlock(spaceSplitLine[1], description.toString(), PaCo._T_CA, fonction.toString(), unite,
                                new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        break;
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            try {
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Variable> getListLabel() {
        return this.listLabel;
    }

    @Override
    public void exportToExcel(File file) {

    }

    @Override
    public void exportToTxt(File file) {

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getNbLabel() {
        return this.listLabel.size();
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

}
