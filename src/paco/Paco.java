package paco;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import utils.Utilitaire;

public final class Paco implements Cdf {

    private String name;
    private boolean valid;
    private int nbLabel = 0;
    private List<Variable> listLabel;
    private Set<String> listCategory;
    private final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(5);
    private int minScore = Byte.MAX_VALUE;
    private int maxScore = Byte.MIN_VALUE;
    private double checkSum = 0;

    private static final String NO_FONCTION = "Pas de fonction definie";
    private static final DocumentBuilderFactory factory;

    static {
        factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setValidating(false);
    }

    public Paco(final File file) {

        DocumentBuilder builder;
        Document document = null;
        try {

            long start = System.currentTimeMillis();

            builder = factory.newDocumentBuilder();

            builder.setEntityResolver(new EntityResolver() {

                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

                    final String dtdName = systemId.substring(systemId.lastIndexOf("/") + 1, systemId.length());
                    final InputStream dtdStream = Paco.class.getResourceAsStream("/" + dtdName);

                    return new InputSource(dtdStream);
                }
            });

            document = builder.parse(new File(file.toURI())); // Permet de virer l'exception <java.net.malformedurlexception unknown protocol c>

            if (document.getDoctype() != null) {

                this.name = Utilitaire.getFileNameWithoutExtension(file);

                parse(document);

                SWToolsMain.getLogger().info(System.currentTimeMillis() - start + " ms");

                document = null;

            } else {
                JOptionPane.showMessageDialog(null, "Format de PaCo non valide !" + "\nNom : " + file.getName(), "ERREUR", JOptionPane.ERROR_MESSAGE);
                valid = false;
                return;
            }

        } catch (Exception e) {

            if (e instanceof SAXException) {
                Logger.getLogger("MyLogger").severe(e.toString());

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

                        JOptionPane.showMessageDialog(null, "Le PaCo a ete enregistre a l'adresse suivante :\n" + fileBis.getPath());

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);
        }
    }

    private final void parse(Document document) {

        // PaCo Keywords
        final String SW_INSTANCE = "SW-INSTANCE";
        final String SW_UNIT = "SW-UNIT";
        final String SHORT_NAME = "SHORT-NAME";
        final String SW_UNIT_DISPLAY = "SW-UNIT-DISPLAY";
        final String SW_FEATURE_REF = "SW-FEATURE-REF";
        final String SW_CS_ENTRY = "SW-CS-ENTRY";
        final String LONG_NAME = "LONG-NAME";
        final String CATEGORY = "CATEGORY";
        final String SW_AXIS_CONT = "SW-AXIS-CONT";
        //

        final Element racine = document.getDocumentElement();
        final NodeList listSwInstance = racine.getElementsByTagName(SW_INSTANCE);
        final NodeList listSwUnit = racine.getElementsByTagName(SW_UNIT);
        Element eUnit;
        String swFeatureRef;
        String[] swUnitRef = null;
        NodeList swCsEntry, swAxisCont;
        nbLabel = listSwInstance.getLength();
        Element label;
        String shortName, longName, category;

        listLabel = new ArrayList<Variable>(nbLabel);
        listCategory = new HashSet<String>();

        final int nbUnit = listSwUnit.getLength();
        final HashMap<String, String> unit = new HashMap<String, String>(nbUnit);
        int nbAxe;

        // Remplissage de la HashMap des unites
        // Test String.intern()
        for (short u = 0; u < nbUnit; u++) {
            eUnit = (Element) listSwUnit.item(u);
            unit.put(eUnit.getElementsByTagName(SHORT_NAME).item(0).getTextContent().intern(),
                    eUnit.getElementsByTagName(SW_UNIT_DISPLAY).item(0).getTextContent().intern());
        }

        String fullAttributAxe;
        String attributAxe;
        String[] splitAttributAxe;

        for (int i = 0; i < nbLabel; i++) {
            label = (Element) listSwInstance.item(i);

            if (label.getElementsByTagName(SW_FEATURE_REF).item(0) != null) {
                swFeatureRef = label.getElementsByTagName(SW_FEATURE_REF).item(0).getTextContent().intern();
            } else {
                swFeatureRef = NO_FONCTION.intern();
            }

            swAxisCont = label.getElementsByTagName(SW_AXIS_CONT);
            nbAxe = swAxisCont.getLength();
            swUnitRef = new String[swAxisCont.getLength()];

            // A finir d'implementer pour les ValueBlock
            splitAttributAxe = null; // Le tableau est cense avoir trois elements
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

                // Test String.intern()
                swUnitRef[n] = unit.get(swAxisCont.item(n).getFirstChild().getTextContent().intern());
            }
            // _________________________________________

            swCsEntry = label.getElementsByTagName(SW_CS_ENTRY);

            shortName = label.getElementsByTagName(SHORT_NAME).item(0).getTextContent();
            longName = label.getElementsByTagName(LONG_NAME).item(0).getTextContent();
            category = label.getElementsByTagName(CATEGORY).item(0).getTextContent().intern(); // Test String.intern()

            // System.out.println(shortName);

            switch (category) {
            case ASCII:
                listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readValue(swAxisCont)));
                break;
            case VALUE:
                listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readValue(swAxisCont)));
                break;
            case CURVE_INDIVIDUAL:
                listLabel.add(new Curve(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readCurve(swAxisCont)));
                break;
            case CURVE_FIXED: // Modif
                listLabel.add(new Curve(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readCurve(swAxisCont)));
                break;
            case AXIS_VALUES:
                listLabel.add(new Axis(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readAxis(swAxisCont)));
                break;
            case CURVE_GROUPED:
                listLabel.add(new Curve(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readCurve(swAxisCont)));
                break;
            case VALUE_BLOCK:
                listLabel.add(new ValueBlock(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry),
                        readValueBlock(splitAttributAxe, swAxisCont)));
                break;
            case MAP_INDIVIDUAL:
                listLabel.add(new Map(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readMap(swAxisCont)));
                break;
            case MAP_GROUPED:
                listLabel.add(new Map(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readMap(swAxisCont)));
                break;
            case MAP_FIXED:
                listLabel.add(new Map(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readMap(swAxisCont)));
                break;
            case "SW_COMPONENT": // Rustine vite fait pour poursuivre la lecture du fichier
                listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), new Values(1, 1)));
                break;
            }

            listCategory.add(category);

            checkSum += listLabel.get(i).getChecksum();
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

        this.valid = true;

    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final int getNbLabel() {
        return this.nbLabel;
    }

    @Override
    public final List<Variable> getListLabel() {
        return Collections.unmodifiableList(this.listLabel);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(obj.toString());
    }

    private final History[] readEntry(NodeList swCsEntry) {

        final String DATE = "DATE";
        final String SW_CS_PERFORMED_BY = "SW-CS-PERFORMED-BY";
        final String SW_CS_STATE = "SW-CS-STATE";
        final String REMARK = "REMARK";

        final int nbEntry = swCsEntry.getLength();
        final History[] entry = new History[nbEntry];
        Element aEntry;
        Node nodeRemark;
        int nbParagraphe = 0;
        String remark = null;

        for (byte n = 0; n < nbEntry; n++) {
            aEntry = (Element) swCsEntry.item(n);

            nodeRemark = aEntry.getElementsByTagName(REMARK).item(0);

            if (nodeRemark != null && nodeRemark.hasChildNodes()) {

                final StringBuilder sb = new StringBuilder();
                nbParagraphe = nodeRemark.getChildNodes().getLength();
                for (short x = 0; x < nbParagraphe; x++) {
                    sb.append(nodeRemark.getChildNodes().item(x).getTextContent());

                    if (x < nbParagraphe - 1)
                        sb.append("\n");
                }
                remark = sb.toString();
            } else {
                remark = "";
            }

            entry[n] = new History(aEntry.getElementsByTagName(DATE).item(0).getTextContent().replace("T", " @ "),
                    aEntry.getElementsByTagName(SW_CS_PERFORMED_BY).item(0).getTextContent().intern(),
                    aEntry.getElementsByTagName(SW_CS_STATE).item(0).getTextContent().intern(), remark);

        }
        return entry;
    }

    private final Values readValue(NodeList swAxisCont) {

        final Values value = new Values(1, 1);

        value.setValue(0, 0, swAxisCont.item(0).getLastChild().getTextContent());

        return value;
    }

    private final Values readAxis(NodeList swAxisCont) {

        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

        final Element eAxisCont = (Element) swAxisCont.item(0);
        final Node swValuesPhys = eAxisCont.getElementsByTagName(SW_VALUES_PHYS).item(0);
        final NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

        final int nbVal = value.getLength();

        final Values values = new Values(nbVal, 1);

        for (short a = 0; a < nbVal; a++) {
            values.setValue(0, a, value.item(a).getTextContent());
        }

        return values;
    }

    private final Values readValueBlock(String[] dim, NodeList swAxisCont) {
        // A finir d'implementer pour les dimensions multiples

        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

        final Values values;

        if (dim[1].equals("0")) {
            values = new Values(Integer.parseInt(dim[0]) + 1, 2);
            values.setValue(0, 0, "X");
            values.setValue(1, 0, "Z");
        } else {
            values = new Values(Integer.parseInt(dim[0]) + 1, Integer.parseInt(dim[1]) + 1);
            values.setValue(0, 0, "Y \\ X");
        }

        final Element eAxisCont = (Element) swAxisCont.item(0);
        final Node swValuesPhys = eAxisCont.getElementsByTagName(SW_VALUES_PHYS).item(0);

        final NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(0).getNodeName());
        NodeList valueVg = null;
        final int nbVal = value.getLength();

        for (short i = 0; i < nbVal; i++) {
            switch (value.item(i).getNodeName()) {
            case "V":
                values.setValue(0, i + 1, Integer.toString(i));
                values.setValue(1, i + 1, value.item(i).getTextContent());
                break;
            case "VG":
                valueVg = value.item(i).getChildNodes();
                values.setValue(i + 1, 0, Integer.toString(i));
                for (short j = 0; j < valueVg.getLength(); j++) {
                    if (valueVg.item(j).getNodeName().equals("V")) {
                        values.setValue(0, j, Integer.toString(j - 1));
                        values.setValue(i + 1, j, valueVg.item(j).getTextContent());
                    }
                }
                break;
            case "VT":
                values.setValue(0, i + 1, Integer.toString(i));
                values.setValue(1, i + 1, value.item(i).getFirstChild().getTextContent());
                break;
            }
        }
        return values;
    }

    private final Values readCurve(NodeList swAxisCont) {

        final String SW_AXIS_INDEX = "SW-AXIS-INDEX";
        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

        final Values values = new Values(((Element) swAxisCont.item(0)).getLastChild().getChildNodes().getLength(), 2);

        Element eAxisCont;
        Node indexAxis;
        Node swValuesPhys;
        NodeList value;
        int nbVal;

        for (byte n = 0; n < 2; n++) {

            eAxisCont = (Element) swAxisCont.item(n);
            indexAxis = eAxisCont.getElementsByTagName(SW_AXIS_INDEX).item(0);
            swValuesPhys = eAxisCont.getElementsByTagName(SW_VALUES_PHYS).item(0);
            value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

            nbVal = value.getLength();

            switch (indexAxis.getTextContent()) {
            case "1":
                for (short x = 0; x < nbVal; x++) {
                    values.setValue(0, x, value.item(x).getTextContent());
                }
                break;
            case "0":
                for (short x = 0; x < nbVal; x++) {
                    values.setValue(1, x, value.item(x).getTextContent());
                }
                break;
            }
        }
        return values;
    }

    private final Values readMap(NodeList swAxisCont) {

        final String SW_AXIS_INDEX = "SW-AXIS-INDEX";
        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

        // Premiere dimension = Axe Y car nombre de ligne
        // Deuxieme dimension = Axe X car nombre de colonne
        final Values values = new Values(((Element) swAxisCont.item(0)).getLastChild().getChildNodes().getLength() + 1,
                ((Element) swAxisCont.item(1)).getLastChild().getChildNodes().getLength() + 1);

        values.setValue(0, 0, "Y \\ X");

        Element eAxisCont;
        Node indexAxis;
        Node swValuesPhys;
        NodeList nodeListV;
        int nbAxeVal;
        int nbVal;
        int nbNodeV;

        for (byte n = 0; n < 3; n++) {

            eAxisCont = (Element) swAxisCont.item(n);
            indexAxis = eAxisCont.getElementsByTagName(SW_AXIS_INDEX).item(0);
            swValuesPhys = eAxisCont.getElementsByTagName(SW_VALUES_PHYS).item(0);
            nodeListV = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

            nbAxeVal = nodeListV.getLength();

            switch (indexAxis.getTextContent()) {
            case "1": // Axe X

                for (short x = 0; x < nbAxeVal; x++) {

                    switch (nodeListV.item(x).getNodeName()) {
                    case "VT":
                        values.setValue(0, x + 1, nodeListV.item(x).getFirstChild().getTextContent());
                        break;
                    default:
                        values.setValue(0, x + 1, nodeListV.item(x).getTextContent());
                        break;
                    }
                }
                break;

            case "2": // Axe Y

                for (short y = 0; y < nbAxeVal; y++) {
                    switch (nodeListV.item(y).getNodeName()) {
                    case "VT":
                        values.setValue(y + 1, 0, nodeListV.item(y).getFirstChild().getTextContent());
                        break;
                    default:
                        values.setValue(y + 1, 0, nodeListV.item(y).getTextContent());
                        break;
                    }
                }
                break;
            case "0": // Valeur Z

                final NodeList vg = ((Element) swValuesPhys).getElementsByTagName("VG");
                NodeList nodeV;
                nbVal = vg.getLength();

                for (short nVG = 1; nVG < nbVal + 1; nVG++) {

                    nodeV = ((Element) vg.item(nVG - 1)).getElementsByTagName("V");
                    nbNodeV = nodeV.getLength();

                    if (nbNodeV > 0) {
                        for (short nV = 1; nV < nbNodeV + 1; nV++) {
                            values.setValue(nVG, nV, nodeV.item(nV - 1).getTextContent());
                        }
                    } else {

                        nodeV = ((Element) vg.item(nVG - 1)).getElementsByTagName("VT");
                        nbNodeV = nodeV.getLength();

                        for (short nV = 1; nV < nbNodeV + 1; nV++) {
                            values.setValue(nVG, nV, nodeV.item(nV - 1).getFirstChild().getTextContent());
                        }
                    }
                }
                break;
            }
        }

        return values;
    }

    @Override
    public final float getAvgScore() {
        return (float) (repartitionScore.get(0) * 0 + repartitionScore.get(25) * 25 + repartitionScore.get(50) * 50 + repartitionScore.get(75) * 75
                + repartitionScore.get(100) * 100) / listLabel.size();
    }

    @Override
    public final int getMinScore() {
        return this.minScore;
    }

    @Override
    public final int getMaxScore() {
        return this.maxScore;
    }

    private final void getScores() {

        repartitionScore.put(0, 0);
        repartitionScore.put(25, 0);
        repartitionScore.put(50, 0);
        repartitionScore.put(75, 0);
        repartitionScore.put(100, 0);

        if (!listLabel.isEmpty()) {

            int lastScore;

            for (int i = 0; i < this.nbLabel; i++) {
                lastScore = this.listLabel.get(i).getLastScore();

                if (repartitionScore.get(lastScore) != null)
                    repartitionScore.put(lastScore, repartitionScore.get(lastScore) + 1);
            }
        }
    }

    @Override
    public HashMap<Integer, Integer> getRepartitionScore() {
        return this.repartitionScore;
    }

    @Override
    public Set<String> getCategoryList() {
        return listCategory;
    }

    @Override
    public double getCheckSum() {
        return checkSum;
    }

    public Paco(String name, List<Variable> listComparaison) {

        this.name = name;
        this.listLabel = new ArrayList<Variable>(listComparaison.size());
        this.listLabel.addAll(listComparaison);

        this.listCategory = new HashSet<String>();

        for (Variable var : listComparaison) {
            this.listCategory.add(var.getCategory());
        }

        this.nbLabel = listComparaison.size();

        this.minScore = 0;
        this.maxScore = 0;

        getScores();
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

}
