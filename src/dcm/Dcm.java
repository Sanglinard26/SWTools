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
import java.util.HashSet;
import java.util.Set;

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

public final class Dcm implements Cdf {

    // Corriger les variables qui ont des axes avec du texte

    private static final String SPACE = " ";
    private static final String THREE_SPACE = "   ";
    private static final String QUOTE = "\"";

    private static final History[] EMPTY_COMMENT = new History[0];

    private static BufferedReader buf = null;
    private static String line;

    private double checkSum = 0;

    private final String name;
    private boolean valid;
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();
    private static final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(1) {
        private static final long serialVersionUID = 1L;
        {
            put(0, 0);
        }
    };

    private static final NumberFormat nbf = NumberFormat.getInstance();

    private final HashSet<String> listCategory = new HashSet<String>();

    static {
        nbf.setMaximumFractionDigits(1);
    }

    public Dcm(final File file) {

        this.name = file.getName().substring(0, file.getName().length() - 4);

        long start = System.currentTimeMillis();

        this.parse(file);

        System.out.println(System.currentTimeMillis() - start);

    }

    private final void parse(File file) {

        // Mot cle global
        final String END = "END";

        // Mot cle pour les variables
        final String PARAMETER = "FESTWERT";
        final String MATRIX = "FESTWERTEBLOCK";
        final String LINE = "KENNLINIE";
        final String MAP = "KENNFELD";
        final String FIXED_LINE = "FESTKENNLINIE";
        final String FIXED_MAP = "FESTKENNFELD";
        final String GROUP_LINE = "GRUPPENKENNLINIE";
        final String GROUP_MAP = "GRUPPENKENNFELD";
        final String DISTRIBUTION = "STUETZSTELLENVERTEILUNG";
        final String TEXTSTRING = "TEXTSTRING";

        // Mot cle dans le bloc d'une variable
        final String DESCRIPTION = "LANGNAME";
        final String FONCTION = "FUNKTION";
        final String UNITE_X = "EINHEIT_X";
        final String UNITE_Y = "EINHEIT_Y";
        final String UNITE_W = "EINHEIT_W";
        final String AXE_X = "ST/X";
        final String AXE_Y = "ST/Y";
        final String AXE_X_TXT = "ST_TX/X";
        final String AXE_Y_TXT = "ST_TX/Y";
        final String VALEUR_NOMBRE = "WERT";
        final String VALEUR_TEXT = "TEXT";

        final StringBuilder description = new StringBuilder();
        final StringBuilder fonction = new StringBuilder();
        String[] unite;
        Values valeur;

        try {

            buf = new BufferedReader(new FileReader(file));

            // String line;
            String[] spaceSplitLine;
            String[] spaceSplitLine2;
            String[] quotesSplitLine;
            String[] threeSpaceSplitLine;

            int cnt;
            int cntX;
            int cntZ;
            int nbSplit;
            String tmpValue;

            while (readLineDcm() != null) {

                spaceSplitLine = line.split(SPACE);

                if (spaceSplitLine.length > 0) {

                    switch (spaceSplitLine[0]) {

                    case PARAMETER:

                        unite = new String[1];
                        valeur = new Values(1, 1);

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }
                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }
                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }

                            }

                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {
                                valeur.setValue(0, 0, spaceSplitLine2[spaceSplitLine2.length - 1].replace(QUOTE, ""));
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Scalaire(spaceSplitLine[1], description.toString(), VALUE.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.VALUE);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case TEXTSTRING:

                        unite = new String[] { SPACE.intern() };
                        valeur = new Values(1, 1);

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }
                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(VALEUR_TEXT)) {
                                valeur.setValue(0, 0, quotesSplitLine[quotesSplitLine.length - 1].replace(QUOTE, ""));
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Scalaire(spaceSplitLine[1], description.toString(), ASCII.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.ASCII);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case LINE:

                        unite = new String[2];
                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]), 2);

                        cntX = 0;
                        cntZ = 0;

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[1] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X) && !tmpValue.equals(AXE_X_TXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(0, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                        if (cntZ < valeur.getDimX()) {
                                            valeur.setValue(1, cntZ, tmpValue.replace(QUOTE, ""));
                                            cntZ++;
                                        }
                                    }
                                }
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Curve(spaceSplitLine[1], description.toString(), CURVE_INDIVIDUAL.intern(), fonction.toString().intern(),
                                unite, EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.CURVE_INDIVIDUAL);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case FIXED_LINE:

                        unite = new String[2];
                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]), 2);

                        cntX = 0;
                        cntZ = 0;

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[1] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X) && !tmpValue.equals(AXE_X_TXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(0, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                        if (cntZ < valeur.getDimX()) {
                                            valeur.setValue(1, cntZ, tmpValue.replace(QUOTE, ""));
                                            cntZ++;
                                        }
                                    }
                                }
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Curve(spaceSplitLine[1], description.toString(), CURVE_FIXED.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.CURVE_FIXED);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case GROUP_LINE:

                        unite = new String[2];
                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]), 2);

                        cntX = 0;
                        cntZ = 0;

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[1] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X) && !tmpValue.equals(AXE_X_TXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(0, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                            if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                        if (cntZ < valeur.getDimX()) {
                                            valeur.setValue(1, cntZ, tmpValue.replace(QUOTE, ""));
                                            cntZ++;
                                        }
                                    }
                                }
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Curve(spaceSplitLine[1], description.toString(), CURVE_GROUPED.intern(), fonction.toString().intern(),
                                unite, EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.CURVE_GROUPED);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case MAP:

                        cntX = 1;
                        cntZ = 0;

                        unite = new String[3];
                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1,
                                Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]) + 1);

                        valeur.setValue(0, 0, "Y \\ X");

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_Y)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[1] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[2] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[2] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X) && !tmpValue.equals(AXE_X_TXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(0, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                            if (cntX == valeur.getDimX()) {
                                cntX = 0;
                                cntZ++;
                            }

                            if (line.trim().startsWith(AXE_Y) | line.trim().startsWith(AXE_Y_TXT) | line.trim().startsWith(VALEUR_NOMBRE)
                                    | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_Y) && !tmpValue.equals(AXE_Y_TXT)
                                            && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(cntZ, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Map(spaceSplitLine[1], description.toString(), MAP_INDIVIDUAL.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.MAP_INDIVIDUAL);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case GROUP_MAP:

                        cntX = 1;
                        cntZ = 0;

                        unite = new String[3];
                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1,
                                Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]) + 1);

                        valeur.setValue(0, 0, "Y \\ X");

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_Y)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[1] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[2] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[2] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X) && !tmpValue.equals(AXE_X_TXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(0, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                            if (cntX == valeur.getDimX()) {
                                cntX = 0;
                                cntZ++;
                            }

                            if (line.trim().startsWith(AXE_Y) | line.trim().startsWith(AXE_Y_TXT) | line.trim().startsWith(VALEUR_NOMBRE)
                                    | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_Y) && !tmpValue.equals(AXE_Y_TXT)
                                            && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(cntZ, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }

                            }
                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Map(spaceSplitLine[1], description.toString(), MAP_GROUPED.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.MAP_GROUPED);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case FIXED_MAP:

                        cntX = 1;
                        cntZ = 0;

                        unite = new String[3];
                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 2]) + 1,
                                Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]) + 1);

                        valeur.setValue(0, 0, "Y \\ X");

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_Y)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[1] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(UNITE_W)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[2] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[2] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X) && !tmpValue.equals(AXE_X_TXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(0, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }

                            if (cntX == valeur.getDimX()) {
                                cntX = 0;
                                cntZ++;
                            }

                            if (line.trim().startsWith(AXE_Y) | line.trim().startsWith(AXE_Y_TXT) | line.trim().startsWith(VALEUR_NOMBRE)
                                    | line.trim().startsWith(VALEUR_TEXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_Y) && !tmpValue.equals(AXE_Y_TXT)
                                            && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                        if (cntX < valeur.getDimX()) {
                                            valeur.setValue(cntZ, cntX, tmpValue.replace(QUOTE, ""));
                                            cntX++;
                                        }
                                    }
                                }
                            }
                        }

                        // System.out.println(spaceSplitLine[1]);

                        listLabel.add(new Map(spaceSplitLine[1], description.toString(), MAP_FIXED.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.MAP_FIXED);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case DISTRIBUTION:

                        unite = new String[1];

                        valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]), 1);

                        cnt = 0;

                        while (!readLineDcm().equals(END)) {

                            spaceSplitLine2 = line.split(SPACE);
                            quotesSplitLine = line.split(QUOTE);

                            if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                            }

                            if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                            }

                            if (line.trim().startsWith(UNITE_X)) {

                                if (quotesSplitLine.length > 1) {
                                    unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                } else {
                                    unite[0] = SPACE.intern();
                                }
                            }

                            if (line.trim().startsWith(AXE_X) | line.trim().startsWith(AXE_X_TXT)) {

                                threeSpaceSplitLine = line.split(THREE_SPACE);

                                nbSplit = threeSpaceSplitLine.length;
                                for (int i = 0; i < nbSplit; i++) {
                                    tmpValue = threeSpaceSplitLine[i];
                                    if (tmpValue.length() != 0 && !tmpValue.equals(AXE_X)) {
                                        if (cnt < valeur.getDimX()) {
                                            valeur.setValue(0, cnt, tmpValue.replace(QUOTE, ""));
                                            cnt++;
                                        }
                                    }
                                }
                            }

                        }

                        listLabel.add(new Axis(spaceSplitLine[1], description.toString(), AXIS_VALUES.intern(), fonction.toString().intern(), unite,
                                EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.AXIS_VALUES);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;

                    case MATRIX:

                        if (spaceSplitLine[spaceSplitLine.length - 2].equals("@")) {

                            cntX = 1;
                            cntZ = 1;

                            unite = new String[1];
                            valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 3]) + 1,
                                    Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]) + 1);

                            valeur.setValue(0, 0, "Y \\ X");

                            for (int x = 1; x < valeur.getDimX(); x++) {
                                valeur.setValue(0, x, Integer.toString(x));
                            }

                            while (!readLineDcm().equals(END)) {

                                spaceSplitLine2 = line.split(SPACE);
                                quotesSplitLine = line.split(QUOTE);

                                if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                    description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                                }

                                if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                    fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                                }

                                if (line.trim().startsWith(UNITE_W)) {

                                    if (quotesSplitLine.length > 1) {
                                        unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                    } else {
                                        unite[0] = SPACE.intern();
                                    }
                                }

                                if (cntX == valeur.getDimX()) {
                                    cntX = 1;
                                    cntZ++;
                                }

                                if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                    threeSpaceSplitLine = line.split(THREE_SPACE);

                                    valeur.setValue(cntZ, 0, Integer.toString(cntZ));

                                    nbSplit = threeSpaceSplitLine.length;
                                    for (int i = 0; i < nbSplit; i++) {
                                        tmpValue = threeSpaceSplitLine[i];
                                        if (tmpValue.length() != 0 && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                            if (cntX < valeur.getDimX()) {
                                                valeur.setValue(cntZ, cntX, tmpValue.replace(QUOTE, ""));
                                                cntX++;
                                            }
                                        }
                                    }
                                }
                            }

                        } else {

                            cntX = 1;

                            unite = new String[1];
                            valeur = new Values(Integer.parseInt(spaceSplitLine[spaceSplitLine.length - 1]) + 1, 2);

                            valeur.setValue(0, 0, "X");

                            for (int x = 1; x < valeur.getDimX(); x++) {
                                valeur.setValue(0, x, Integer.toString(x));
                            }

                            valeur.setValue(1, 0, "Z");

                            while (!readLineDcm().equals(END)) {

                                spaceSplitLine2 = line.split(SPACE);
                                quotesSplitLine = line.split(QUOTE);

                                if (line.trim().startsWith(DESCRIPTION) && quotesSplitLine.length > 1) {
                                    description.append(quotesSplitLine[quotesSplitLine.length - 1]);
                                }

                                if (line.trim().startsWith(FONCTION) && spaceSplitLine2.length > 1) {
                                    fonction.append(spaceSplitLine2[spaceSplitLine2.length - 1]);
                                }

                                if (line.trim().startsWith(UNITE_W)) {

                                    if (quotesSplitLine.length > 1) {
                                        unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
                                    } else {
                                        unite[0] = SPACE.intern();
                                    }
                                }

                                if (line.trim().startsWith(VALEUR_NOMBRE) | line.trim().startsWith(VALEUR_TEXT)) {

                                    threeSpaceSplitLine = line.split(THREE_SPACE);

                                    nbSplit = threeSpaceSplitLine.length;
                                    for (int i = 0; i < nbSplit; i++) {
                                        tmpValue = threeSpaceSplitLine[i];
                                        if (tmpValue.length() != 0 && !tmpValue.equals(VALEUR_NOMBRE) && !tmpValue.equals(VALEUR_TEXT)) {
                                            if (cntX < valeur.getDimX()) {
                                                valeur.setValue(1, cntX, tmpValue.replace(QUOTE, ""));
                                                cntX++;
                                            }
                                        }
                                    }
                                }

                            }

                        }

                        listLabel.add(new ValueBlock(spaceSplitLine[1], description.toString(), VALUE_BLOCK.intern(), fonction.toString().intern(),
                                unite, EMPTY_COMMENT, valeur));

                        description.setLength(0);
                        fonction.setLength(0);

                        listCategory.add(Cdf.VALUE_BLOCK);

                        checkSum += listLabel.get(listLabel.size() - 1).getChecksum();

                        break;
                    }
                }
            }

            repartitionScore.put(0, listLabel.size());

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

    private final String readLineDcm() {
        try {
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
        return 0f;
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
    public Set<String> getCategoryList() {
        return listCategory;
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
