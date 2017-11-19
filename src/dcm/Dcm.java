/*
 * Creation : 19 juin 2017
 */
package dcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import cdf.Axis;
import cdf.Cdf;
import cdf.Curve;
import cdf.ExportUtils;
import cdf.Map;
import cdf.Observable;
import cdf.Scalaire;
import cdf.ValueBlock;
import cdf.Variable;
import tools.Utilitaire;
import visu.Observer;
import visu.PanelCDF;

public final class Dcm implements Cdf, Observable {

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

    private static BufferedReader buf = null;
    private static String line;
    
    private double checkSum = 0;

    private static int numLine;

    private final String name;
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();
    private static final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(1);

    private final ArrayList<Observer> listObserver = new ArrayList<Observer>();

    private static final NumberFormat nbf = NumberFormat.getInstance();

    private final Vector<String> listCategory = new Vector<String>();

    public Dcm(final File file, PanelCDF panCdf) {

        numLine = 0;

        if (panCdf != null)
            addObserver(panCdf);

        nbf.setMaximumFractionDigits(1);

        this.name = file.getName().substring(0, file.getName().length() - 4);

        this.parse(file);

    }

    private final void parse(File file) {

        final StringBuilder description = new StringBuilder();
        final StringBuilder fonction = new StringBuilder();
        String[] unite;
        String[][] valeur;

        try {

            buf = new BufferedReader(new FileReader(file));

            final BufferedReader reader = new BufferedReader(new FileReader(file));
            int nbLines = 0;
            while (reader.readLine() != null)
                nbLines++;
            reader.close();

            // String line;
            String[] spaceSplitLine;
            String[] spaceSplitLine2;
            String[] quotesSplitLine;
            String[] threeSpaceSplitLine;

            // Pour les LINE
            final ArrayList<String> axeX = new ArrayList<String>();
            final ArrayList<String> axeY = new ArrayList<String>();

            // Pour les MAP
            int cnt;
            final ArrayList<String> axeTmp = new ArrayList<String>();

            while (readLineDcm() != null) {

                notifyObserver(this.name, "", nbf.format(((double) numLine / (double) (nbLines)) * 100) + "%");

                spaceSplitLine = line.split(" ");

                if (spaceSplitLine.length > 0) {

                    switch (spaceSplitLine[0]) {

                    case PARAMETER:

                        unite = new String[1];
                        valeur = new String[1][1];

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(
                                new Scalaire(spaceSplitLine[1], description.toString(), VALUE, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        if (!listCategory.contains(Cdf.VALUE))
                            listCategory.add(Cdf.VALUE);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case TEXTSTRING:

                        unite = new String[] { " " };
                        valeur = new String[1][1];

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(
                                new Scalaire(spaceSplitLine[1], description.toString(), ASCII, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        if (!listCategory.contains(Cdf.ASCII))
                            listCategory.add(Cdf.ASCII);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case LINE:

                        unite = new String[2];
                        valeur = new String[2][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(new Curve(spaceSplitLine[1], description.toString(), CURVE_INDIVIDUAL, fonction.toString(), unite,
                                new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeX.clear();
                        axeY.clear();

                        if (!listCategory.contains(Cdf.CURVE_INDIVIDUAL))
                            listCategory.add(Cdf.CURVE_INDIVIDUAL);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case FIXED_LINE:

                        unite = new String[2];
                        valeur = new String[2][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(new Curve(spaceSplitLine[1], description.toString(), CURVE_INDIVIDUAL, fonction.toString(), unite,
                                new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeX.clear();
                        axeY.clear();

                        if (!listCategory.contains(Cdf.CURVE_FIXED))
                            listCategory.add(Cdf.CURVE_FIXED);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case GROUP_LINE:

                        unite = new String[2];
                        valeur = new String[2][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(new Curve(spaceSplitLine[1], description.toString(), CURVE_GROUPED, fonction.toString(), unite,
                                new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeX.clear();
                        axeY.clear();

                        if (!listCategory.contains(Cdf.CURVE_GROUPED))
                            listCategory.add(Cdf.CURVE_GROUPED);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case MAP:

                        cnt = 1;

                        unite = new String[3];
                        valeur = new String[Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])
                                + 1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1];

                        axeTmp.add("Y \\ X");

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(new Map(spaceSplitLine[1], description.toString(), MAP_INDIVIDUAL, fonction.toString(), unite, new String[0][0],
                                valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        if (!listCategory.contains(Cdf.MAP_INDIVIDUAL))
                            listCategory.add(Cdf.MAP_INDIVIDUAL);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case GROUP_MAP:

                        cnt = 1;

                        unite = new String[3];
                        valeur = new String[Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])
                                + 1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1];

                        axeTmp.add("Y \\ X");

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(new Map(spaceSplitLine[1], description.toString(), MAP_GROUPED, fonction.toString(), unite, new String[0][0],
                                valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        if (!listCategory.contains(Cdf.MAP_GROUPED))
                            listCategory.add(Cdf.MAP_GROUPED);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case FIXED_MAP:

                        cnt = 1;

                        unite = new String[3];
                        valeur = new String[Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])
                                + 1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1];

                        axeTmp.add("Y \\ X");

                        while (!readLineDcm().equals(END)) {

                            ;

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
                                new Map(spaceSplitLine[1], description.toString(), MAP_FIXED, fonction.toString(), unite, new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        if (!listCategory.contains(Cdf.MAP_FIXED))
                            listCategory.add(Cdf.MAP_FIXED);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;

                    case DISTRIBUTION:

                        unite = new String[1];

                        valeur = new String[1][Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1])];

                        ArrayList<String> distribution = new ArrayList<String>();

                        while (!readLineDcm().equals(END)) {

                            ;

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

                        listLabel.add(new Axis(spaceSplitLine[1], description.toString(), AXIS_VALUES, fonction.toString(), unite, new String[0][0],
                                valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        if (!listCategory.contains(Cdf.AXIS_VALUES))
                            listCategory.add(Cdf.AXIS_VALUES);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

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

                            while (!readLineDcm().equals(END)) {

                                ;

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
                                                axeTmp.add(s.replace("\"", ""));
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

                            while (!readLineDcm().equals(END)) {

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
                                                axeTmp.add(s.replace("\"", ""));
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

                        listLabel.add(new ValueBlock(spaceSplitLine[1], description.toString(), VALUE_BLOCK, fonction.toString(), unite,
                                new String[0][0], valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        axeTmp.clear();

                        if (!listCategory.contains(Cdf.VALUE_BLOCK))
                            listCategory.add(Cdf.VALUE_BLOCK);
                        
                        checkSum += listLabel.get(listLabel.size()-1).getChecksum();

                        break;
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

    private final String readLineDcm() {
        try {
            numLine++;
            return line = buf.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Variable> getListLabel() {
        return this.listLabel;
    }

    @Override
    public Boolean exportToExcel(File file) {
        return ExportUtils.toExcel(this, file);
    }

    @Override
    public Boolean exportToTxt(File file) {
        return ExportUtils.toText(this, file);
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
    public HashMap<Integer, Integer> getRepartitionScore() {
        return repartitionScore;
    }

    @Override
    public Boolean exportToM(File file) {
        return ExportUtils.toM(this, file);
    }

    @Override
    public void addObserver(Observer obs) {
        listObserver.add(obs);
    }

    @Override
    public void notifyObserver(String cdf, String variable, String rate) {
        for (Observer obs : listObserver) {
            obs.update(cdf, variable, rate);
        }

    }

    @Override
    public Vector<String> getCategoryList() {
        return listCategory;
    }

	@Override
	public double getCheckSum() {
		return checkSum;
	}

	@Override
	public Dcm comparCdf(Cdf cdf) {
		if(this.getCheckSum() == cdf.getCheckSum())
		{
			return null;
		}
		return new Dcm(null, null);
	}

}
