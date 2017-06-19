/*
 * Creation : 19 juin 2017
 */
package dcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import paco.PaCo;
import paco.Scalaire;
import paco.Variable;
import tools.Utilitaire;

public final class Dcm {

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
    public static final String VALEUR_NOMBRE = "WERT";
    public static final String VALEUR_TEXT = "TEXT";
    public static final String AXE_PARTAGE_X = "*SSTX";
    public static final String AXE_PARTAGE_Y = "*SSTY";

    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();

    public Dcm(final File file) {

        this.parse(file);

    }

    private final void parse(File file) {

        final StringBuilder description = new StringBuilder();
        final StringBuilder fonction = new StringBuilder();
        String[] unite;
        String[][] valeur;

        try {

            BufferedReader buf = new BufferedReader(new FileReader(file));
            String line;
            String[] spaceSplitLine;

            while ((line = buf.readLine()) != null) {

                spaceSplitLine = line.split(" ");

                if (spaceSplitLine.length > 0) {
                    switch (spaceSplitLine[0]) {
                    case PARAMETER:

                        unite = new String[1];
                        valeur = new String[1][1];

                        while (!(line = buf.readLine()).equals(END)) {
                            if (line.startsWith(DESCRIPTION)) {
                                description.append(line.split(" ")[1]);
                            }
                            if (line.startsWith(FONCTION)) {
                                fonction.append(line.split(" ")[1]);
                            }
                            if (line.startsWith(UNITE_W)) {

                                unite[0] = line.split(" ")[1];
                            }
                            if (line.startsWith(VALEUR_NOMBRE) | line.startsWith(VALEUR_TEXT)) {
                                valeur[0][0] = Utilitaire.cutNumber(line.split(" ")[1]);
                            }
                        }
                        listLabel.add(new Scalaire(spaceSplitLine[1], description.toString(), PaCo._C, fonction.toString(), unite, new String[0][0],
                                valeur));
                        break;
                    case LINE:
                        System.out.println("\n" + spaceSplitLine[1]);
                        while (!(line = buf.readLine()).equals(END)) {
                            System.out.println(line);
                        }
                        break;

                    case MAP:
                        System.out.println("\n" + spaceSplitLine[1]);
                        while (!(line = buf.readLine()).equals(END)) {
                            System.out.println(line);
                        }
                        break;
                    }
                }
            }

            buf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
