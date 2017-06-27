package paco;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cdf.Axis;
import cdf.Cdf;
import cdf.Curve;
import cdf.ExportUtils;
import cdf.Map;
import cdf.Scalaire;
import cdf.ValueBlock;
import cdf.Variable;
import tools.Utilitaire;

public final class PaCo implements Cdf {

    public static final String ASCII = "ASCII";
    public static final String _C = "VALUE";
    public static final String _T = "CURVE_INDIVIDUAL";
    public static final String _M = "MAP_INDIVIDUAL";
    public static final String _A = "AXIS_VALUES";
    public static final String _T_CA = "VALUE_BLOCK";
    public static final String _T_GROUPED = "CURVE_GROUPED";
    public static final String _M_GROUPED = "MAP_GROUPED";
    public static final String _M_FIXED = "MAP_FIXED";

    private static Logger logger = Logger.getLogger("MyLogger");

    private final String name;
    private Boolean valid;
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private int nbLabel = 0;
    private final HashMap<String, String> unit = new HashMap<String, String>();
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();
    private final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(5);
    private int minScore = Byte.MAX_VALUE;
    private int maxScore = Byte.MIN_VALUE;

    public PaCo(final File file) {

        this.name = file.getName().substring(0, file.getName().length() - 4);

        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(file.toURI())); // Permet de virer l'exception <java.net.malformedurlexception unknown
            // protocol c>
            if (document.getDoctype() != null) {
                valid = Boolean.TRUE;
            } else {
                JOptionPane.showMessageDialog(null, "Format de PaCo non valide !" + "\nNom : " + this.name, "ERREUR", JOptionPane.ERROR_MESSAGE);
                valid = Boolean.FALSE;
                return;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            logger.severe(e.toString());

            final int reponse = JOptionPane.showConfirmDialog(null,
                    "Le PaCo contient des caracteres invalides." + "\nVoir le log pour plus de details." + "\n\nNom : " + this.name
                            + "\n\n Voulez-vous corriger les erreurs en creeant une copie du fichier?",
                    "ERREUR DE PARSING", JOptionPane.YES_NO_OPTION);

            if (reponse == JOptionPane.OK_OPTION) {
                String line;
                char[] chars;
                BufferedReader buf;
                BufferedWriter bw;
                File fileBis;
                StringBuilder sb = new StringBuilder();
                try {
                    buf = new BufferedReader(new FileReader(file));
                    fileBis = new File(file.getPath().replace(file.getPath().substring(file.getPath().lastIndexOf('.'), file.getPath().length()),
                            "_SWTools.XML"));
                    bw = new BufferedWriter(new FileWriter(fileBis));
                    while ((line = buf.readLine()) != null) {
                        chars = line.toCharArray();
                        sb.setLength(0);
                        for (int i = 0; i < chars.length; i++) {
                            if (chars[i] == 0x1a)
                                chars[i] = ' ';

                            sb.append(chars[i]);
                        }
                        bw.write(sb.toString());
                        bw.write("\n");
                    }
                    buf.close();
                    bw.close();

                    JOptionPane.showMessageDialog(null, "Le PaCo a ete enregistre à l'adresse suivante :\n" + fileBis.getPath());

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (

        IOException e) {
            e.printStackTrace();
        }

        if (document != null) {
            final Element racine = document.getDocumentElement();
            final NodeList listSwInstance = racine.getElementsByTagName("SW-INSTANCE");
            final NodeList listSwUnit = racine.getElementsByTagName("SW-UNIT");
            Element eUnit;
            String swFeatureRef;
            String[] swUnitRef = null;
            NodeList swCsEntry, swAxisCont;
            nbLabel = listSwInstance.getLength();
            Element label;

            final int nbUnit = listSwUnit.getLength();

            // Remplissage de la HashMap des unites
            for (short u = 0; u < nbUnit; u++) {
                eUnit = (Element) listSwUnit.item(u);
                unit.put(eUnit.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                        eUnit.getElementsByTagName("SW-UNIT-DISPLAY").item(0).getTextContent());
            }

            for (int i = 0; i < nbLabel; i++) {
                label = (Element) listSwInstance.item(i);

                // System.out.println(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent());

                if (label.getElementsByTagName("SW-FEATURE-REF").item(0) != null) {
                    swFeatureRef = label.getElementsByTagName("SW-FEATURE-REF").item(0).getTextContent();
                } else {
                    swFeatureRef = "Pas de fonction definie";
                }

                swAxisCont = label.getElementsByTagName("SW-AXIS-CONT");
                final int nbAxe = swAxisCont.getLength();
                swUnitRef = new String[swAxisCont.getLength()];

                // A finir d'implementer pour les ValueBlock
                String fullAttributAxe;
                String attributAxe;
                String[] splitAttributAxe = null; // Le tableau est cense avoir trois elements
                for (byte n = 0; n < nbAxe; n++) {
                    if (swAxisCont.item(n).hasAttributes()) {
                        fullAttributAxe = swAxisCont.item(n).getAttributes().getNamedItem("SI").getTextContent();
                        if (fullAttributAxe.indexOf(";") > -1) {
                            attributAxe = fullAttributAxe.substring(fullAttributAxe.indexOf(";") + 1);
                            if (attributAxe.indexOf("@") > -1) {
                                splitAttributAxe = attributAxe.split("@"); // nbColonne@nbLigne@nb?
                            }
                        }
                    }

                    swUnitRef[n] = unit.get(swAxisCont.item(n).getFirstChild().getTextContent());
                }
                // _________________________________________

                swCsEntry = label.getElementsByTagName("SW-CS-ENTRY");

                switch (label.getElementsByTagName("CATEGORY").item(0).getTextContent()) {
                case PaCo.ASCII:
                    listLabel.add(new Scalaire(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readValue(swAxisCont)));
                    break;
                case PaCo._C:
                    listLabel.add(new Scalaire(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readValue(swAxisCont)));
                    break;
                case PaCo._T:
                    listLabel.add(new Curve(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readCurve(swAxisCont)));
                    break;
                case PaCo._A:
                    listLabel.add(new Axis(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readAxis(swAxisCont)));
                    break;
                case PaCo._T_GROUPED:
                    listLabel.add(new Curve(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readCurve(swAxisCont)));
                    break;
                case PaCo._T_CA:
                    listLabel.add(new ValueBlock(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readValueBlock(splitAttributAxe, swAxisCont)));
                    break;
                case PaCo._M:
                    listLabel.add(new Map(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readMap(swAxisCont)));
                    break;
                case PaCo._M_GROUPED:
                    listLabel.add(new Map(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readMap(swAxisCont)));
                    break;
                case PaCo._M_FIXED:
                    listLabel.add(new Map(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("LONG-NAME").item(0).getTextContent(),
                            label.getElementsByTagName("CATEGORY").item(0).getTextContent(), swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readMap(swAxisCont)));
                    break;
                }
            }

            getScores();

            for (int score = 0; score <= 100; score += 25) {
                if (repartitionScore.get(score) > 0) {
                    if (score <= minScore)
                        minScore = score;
                    if (score >= maxScore)
                        maxScore = score;
                }
            }
        }

    }

    public final String getName() {
        return this.name;
    }

    public final Boolean isValid() {
        return valid;
    }

    public final int getNbLabel() {
        return this.nbLabel;
    }

    public final ArrayList<Variable> getListLabel() {
        return this.listLabel;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(obj.toString());
    }

    private final String[][] readEntry(NodeList swCsEntry) {

        final int nbEntry = swCsEntry.getLength();
        final String[][] entry = new String[nbEntry][4];
        Element aEntry;

        for (byte n = 0; n < nbEntry; n++) {
            aEntry = (Element) swCsEntry.item(n);

            try {
                entry[n][0] = aEntry.getElementsByTagName("DATE").item(0).getTextContent().replace("T", " @ ");
                entry[n][1] = aEntry.getElementsByTagName("SW-CS-PERFORMED-BY").item(0).getTextContent();
                entry[n][2] = aEntry.getElementsByTagName("SW-CS-STATE").item(0).getTextContent();

                if (aEntry.getElementsByTagName("REMARK").item(0).hasChildNodes()) {
                    final StringBuilder sb = new StringBuilder();
                    for (byte x = 0; x < aEntry.getElementsByTagName("REMARK").item(0).getChildNodes().getLength(); x++) {
                        sb.append(aEntry.getElementsByTagName("REMARK").item(0).getChildNodes().item(x).getTextContent());

                        if (x < aEntry.getElementsByTagName("REMARK").item(0).getChildNodes().getLength() - 1)
                            sb.append("\n");
                    }
                    entry[n][3] = sb.toString();
                } else {
                    entry[n][3] = aEntry.getElementsByTagName("REMARK").item(0).getTextContent();
                }
            } catch (NullPointerException npe) {
                for (int i = 0; i < 4; i++) {
                    if (entry[n][i] == null)
                        entry[n][i] = "";
                }
            }
        }
        return entry;
    }

    private final String[][] readValue(NodeList swAxisCont) {

        final String val[][] = new String[1][1];

        val[0][0] = Utilitaire.cutNumber(swAxisCont.item(0).getLastChild().getTextContent());

        return val;
    }

    private final String[][] readAxis(NodeList swAxisCont) {

        final Element eAxisCont = (Element) swAxisCont.item(0);
        final Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
        final NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

        final int nbVal = value.getLength();

        final String val[][] = new String[1][nbVal];

        for (short a = 0; a < nbVal; a++) {
            val[0][a] = Utilitaire.cutNumber(value.item(a).getTextContent());
        }

        return val;
    }

    private final String[][] readValueBlock(String[] dim, NodeList swAxisCont) {
        // A finir d'implementer pour les dimensions multiples

        final String val[][];

        if (dim[1].equals("0")) {
            val = new String[2][Integer.parseInt(dim[0]) + 1];
            val[0][0] = "X";
            val[1][0] = "Z";
        } else {
            val = new String[Integer.parseInt(dim[1]) + 1][Integer.parseInt(dim[0]) + 1];
            val[0][0] = "Y \\ X";
        }

        final Element eAxisCont = (Element) swAxisCont.item(0);
        final Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);

        final NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(0).getNodeName());
        NodeList valueVg = null;

        for (int i = 0; i < value.getLength(); i++) {
            switch (value.item(i).getNodeName()) {
            case "V":
                val[0][i + 1] = Integer.toString(i);
                val[1][i + 1] = Utilitaire.cutNumber(value.item(i).getTextContent());
                break;
            case "VG":
                valueVg = value.item(i).getChildNodes();
                val[i + 1][0] = Integer.toString(i);
                for (int j = 0; j < valueVg.getLength(); j++) {
                    if (valueVg.item(j).getNodeName().equals("V")) {
                        val[0][j] = Integer.toString(j - 1);
                        val[i + 1][j] = valueVg.item(j).getTextContent();
                    }
                }
                break;
            case "VT":
                val[0][i + 1] = Integer.toString(i);
                val[1][i + 1] = Utilitaire.cutNumber(value.item(i).getFirstChild().getTextContent());
                break;
            }
        }
        return val;
    }

    private final String[][] readCurve(NodeList swAxisCont) {

        final String val[][] = new String[2][((Element) swAxisCont.item(0)).getLastChild().getChildNodes().getLength()];

        for (byte n = 0; n < 2; n++) {

            final Element eAxisCont = (Element) swAxisCont.item(n);
            final Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
            final Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
            final NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

            final int nbVal = value.getLength();

            switch (indexAxis.getTextContent()) {
            case "1":
                for (short b = 0; b < nbVal; b++) {
                    val[0][b] = Utilitaire.cutNumber(value.item(b).getTextContent());
                }
                break;
            case "0":
                for (short a = 0; a < nbVal; a++) {
                    val[1][a] = Utilitaire.cutNumber(value.item(a).getTextContent());
                }
                break;
            }
        }
        return val;
    }

    private final String[][] readMap(NodeList swAxisCont) {
        // Premiere dimension = Axe Y car nombre de ligne
        // Deuxieme dimension = Axe X car nombre de colonne
        final String val[][] = new String[((Element) swAxisCont.item(1)).getLastChild().getChildNodes().getLength()
                + 1][((Element) swAxisCont.item(0)).getLastChild().getChildNodes().getLength() + 1];

        val[0][0] = "Y \\ X";

        for (byte n = 0; n < 3; n++) {
            final Element eAxisCont = (Element) swAxisCont.item(n);
            final Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
            final Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
            final NodeList nodeListV = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

            final int nbAxeVal = nodeListV.getLength();

            switch (indexAxis.getTextContent()) {
            case "1": // Axe X

                for (short x = 0; x < nbAxeVal; x++) {

                    switch (nodeListV.item(x).getNodeName()) {
                    case "VT":
                        val[0][x + 1] = Utilitaire.cutNumber(nodeListV.item(x).getFirstChild().getTextContent());
                        break;

                    default:
                        val[0][x + 1] = Utilitaire.cutNumber(nodeListV.item(x).getTextContent());
                        break;
                    }
                }
                break;

            case "2": // Axe Y

                for (short y = 0; y < nbAxeVal; y++) {
                    switch (nodeListV.item(y).getNodeName()) {
                    case "VT":
                        val[y + 1][0] = Utilitaire.cutNumber(nodeListV.item(y).getFirstChild().getTextContent());
                        break;
                    default:
                        val[y + 1][0] = Utilitaire.cutNumber(nodeListV.item(y).getTextContent());
                        break;
                    }
                }
                break;
            case "0": // Valeur Z

                final NodeList vg = ((Element) swValuesPhys).getElementsByTagName("VG");

                for (short nVG = 1; nVG < vg.getLength() + 1; nVG++) {
                    NodeList nodeV = ((Element) vg.item(nVG - 1)).getElementsByTagName("V");

                    if (nodeV.getLength() > 0) {
                        for (short nV = 1; nV < nodeV.getLength() + 1; nV++) {
                            val[nVG][nV] = Utilitaire.cutNumber(nodeV.item(nV - 1).getTextContent());
                        }
                    } else {
                        nodeV = ((Element) vg.item(nVG - 1)).getElementsByTagName("VT");

                        for (short nV = 1; nV < nodeV.getLength() + 1; nV++) {
                            val[nVG][nV] = Utilitaire.cutNumber(nodeV.item(nV - 1).getFirstChild().getTextContent());
                        }
                    }

                }
                break;
            }
        }

        return val;
    }

    public void exportToExcel(final File file) {
        ExportUtils.toExcel(this, file);
    }

    public final void exportToTxt(File file) {
        if (false) {
            ExportUtils.toText(this, file);
        } else {
            ExportUtils.toM(this, file);
        }
    }

    public final float getAvgScore() {
        return (float) (repartitionScore.get(0) * 0 + repartitionScore.get(25) * 25 + repartitionScore.get(50) * 50 + repartitionScore.get(75) * 75
                + repartitionScore.get(100) * 100) / listLabel.size();
    }

    public final int getMinScore() {
        return this.minScore;
    }

    public final int getMaxScore() {
        return this.maxScore;
    }

    private final void getScores() {
        if (!listLabel.isEmpty()) {

            repartitionScore.put(0, 0);
            repartitionScore.put(25, 0);
            repartitionScore.put(50, 0);
            repartitionScore.put(75, 0);
            repartitionScore.put(100, 0);

            for (Variable v : listLabel) {
                if (repartitionScore.get(v.getLastScore()) != null)
                    repartitionScore.put(v.getLastScore(), repartitionScore.get(v.getLastScore()) + 1);
            }
        }
    }

    @Override
    public HashMap<Integer, Integer> getRepartitionScore() {
        return this.repartitionScore;
    }

}
