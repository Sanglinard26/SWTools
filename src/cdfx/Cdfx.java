package cdfx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cdf.Cdf;
import cdf.ComAxis;
import cdf.Curve;
import cdf.History;
import cdf.Map;
import cdf.TypeVariable;
import cdf.Value;
import cdf.ValueBlock;
import cdf.Values;
import cdf.Variable;
import gui.SWToolsMain;
import utils.Utilitaire;

public final class Cdfx implements Cdf {

    private String name;
    private boolean valid;
    private int nbLabel = 0;
    private List<Variable> listLabel;
    // private Set<String> listCategory;
    private EnumSet<TypeVariable> listCategory;
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

    public Cdfx(final File file) {

        DocumentBuilder builder;
        Document document = null;
        try {

            long start = System.currentTimeMillis();

            builder = factory.newDocumentBuilder();

            builder.setEntityResolver(new EntityResolver() {

                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

                    final String dtdName = systemId.substring(systemId.lastIndexOf("/") + 1, systemId.length());
                    final InputStream dtdStream = Cdfx.class.getResourceAsStream("/" + dtdName);

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

        } catch (SAXException | ParserConfigurationException | IOException e) {

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

            SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);
        }
    }

    private final void parse(Document document) {

        // PaCo Keywords
        final String SW_INSTANCE = "SW-INSTANCE";
        final String SHORT_NAME = "SHORT-NAME";
        final String LONG_NAME = "LONG-NAME";
        final String UNIT_DISPLAY_NAME = "UNIT-DISPLAY-NAME";
        final String SW_FEATURE_REF = "SW-FEATURE-REF";
        final String CS_ENTRY = "CS-ENTRY";
        final String CATEGORY = "CATEGORY";
        final String SW_VALUE_CONT = "SW-VALUE-CONT";
        final String SW_AXIS_CONT = "SW-AXIS-CONT";
        //

        final Element racine = document.getDocumentElement();
        final NodeList listSwInstance = racine.getElementsByTagName(SW_INSTANCE);

        String swFeatureRef;
        List<String> swUnitRef = new ArrayList<String>();
        NodeList swCsEntry;
        NodeList swValueCont;
        NodeList swAxisCont;
        nbLabel = listSwInstance.getLength();
        Element label;
        String shortName;
        String longName;
        String category;

        listLabel = new ArrayList<Variable>(nbLabel);
        // listCategory = new HashSet<String>();
        listCategory = EnumSet.noneOf(TypeVariable.class);

        String[] sharedAxis = null;

        for (int i = 0; i < nbLabel; i++) {
            label = (Element) listSwInstance.item(i);

            longName = label.getElementsByTagName(LONG_NAME).item(0) != null ? label.getElementsByTagName(LONG_NAME).item(0).getTextContent()
                    : "Not available";
            shortName = label.getElementsByTagName(SHORT_NAME).item(0).getTextContent();

            category = label.getElementsByTagName(CATEGORY).item(0).getTextContent().intern(); // Test String.intern()

            if (label.getElementsByTagName(SW_FEATURE_REF).item(0) != null) {
                swFeatureRef = label.getElementsByTagName(SW_FEATURE_REF).item(0).getTextContent().intern();
            } else {
                swFeatureRef = NO_FONCTION.intern();
            }

            swValueCont = label.getElementsByTagName(SW_VALUE_CONT);

            swUnitRef.clear();

            swAxisCont = label.getElementsByTagName(SW_AXIS_CONT);

            for (int nAxe = 0; nAxe < swAxisCont.getLength(); nAxe++) {
                swUnitRef.add(((Element) swAxisCont.item(nAxe)).getElementsByTagName(UNIT_DISPLAY_NAME).item(0).getTextContent());
            }

            swUnitRef.add(swValueCont.item(0).getFirstChild().getTextContent());

            swCsEntry = label.getElementsByTagName(CS_ENTRY);

            TypeVariable type = TypeVariable.getType(category);

            switch (type) {
            case ASCII:
                listLabel.add(new Value(shortName, longName, type, swFeatureRef, swUnitRef.toArray(new String[swUnitRef.size()]),
                        readEntry(swCsEntry), readValue(swValueCont)));
                break;
            case VALUE:
                listLabel.add(new Value(shortName, longName, type, swFeatureRef, swUnitRef.toArray(new String[swUnitRef.size()]),
                        readEntry(swCsEntry), readValue(swValueCont)));
                break;
            case COM_AXIS:
                listLabel.add(new ComAxis(shortName, longName, type, swFeatureRef, swUnitRef.toArray(new String[swUnitRef.size()]),
                        readEntry(swCsEntry), readComAxis(swValueCont)));
                break;
            case CURVE:
                listLabel.add(new Curve(shortName, longName, type, swFeatureRef, swUnitRef.toArray(new String[swUnitRef.size()]),
                        readEntry(swCsEntry), readCurve(listSwInstance, swValueCont, swAxisCont), sharedAxis));
                break;
            case VAL_BLK:
                listLabel.add(new ValueBlock(shortName, longName, type, swFeatureRef, swUnitRef.toArray(new String[swUnitRef.size()]),
                        readEntry(swCsEntry), readValueBlock(swValueCont)));
                break;
            case MAP:
                listLabel.add(new Map(shortName, longName, type, swFeatureRef, swUnitRef.toArray(new String[swUnitRef.size()]), readEntry(swCsEntry),
                        readMap(listSwInstance, swValueCont, swAxisCont), sharedAxis));
                break;
            default:
                break;
            }

            listCategory.add(type);

            // checkSum += listLabel.get(i).getChecksum();
        }

        swUnitRef.clear();

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
        final String CSUS = "CSUS";
        final String STATE = "STATE";
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
                    aEntry.getElementsByTagName(CSUS).item(0).getTextContent().intern(),
                    aEntry.getElementsByTagName(STATE).item(0).getTextContent().intern(), remark);

        }
        return entry;
    }

    private final Values readValue(NodeList swValueCont) {

        final Values value = new Values(1, 1);

        value.setValue(0, 0, swValueCont.item(0).getLastChild().getTextContent());

        return value;
    }

    private final Values readComAxis(NodeList swValueCont) {

        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

        final Element eValueCont = (Element) swValueCont.item(0);
        final Element swValuesPhys = (Element) eValueCont.getElementsByTagName(SW_VALUES_PHYS).item(0);
        final NodeList value = swValuesPhys.getElementsByTagName(swValuesPhys.getFirstChild().getNodeName());

        final int nbVal = value.getLength();

        final Values values = new Values(nbVal, 1);

        for (short a = 0; a < nbVal; a++) {
            values.setValue(0, a, value.item(a).getTextContent());
        }

        return values;
    }

    private final Values readValueBlock(NodeList swValueCont) {
        // A finir d'implementer pour les dimensions multiples

        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";
        final String SW_ARRAYSIZE = "SW-ARRAYSIZE";

        final Values values;

        final Element eValueCont = (Element) swValueCont.item(0);
        final Element eArraySize = (Element) eValueCont.getElementsByTagName(SW_ARRAYSIZE).item(0);

        final NodeList dimList = eArraySize.getChildNodes();

        if (dimList.getLength() < 2) {
            values = new Values(Integer.parseInt(dimList.item(0).getTextContent()) + 1, 2);
            values.setValue(0, 0, "X");
            values.setValue(1, 0, "Z");
        } else {
            values = new Values(Integer.parseInt(dimList.item(0).getTextContent()) + 1, Integer.parseInt(dimList.item(1).getTextContent()) + 1);
            values.setValue(0, 0, "Y \\ X");
        }

        final Element swValuesPhys = (Element) eValueCont.getElementsByTagName(SW_VALUES_PHYS).item(0);

        final NodeList value = swValuesPhys.getElementsByTagName(swValuesPhys.getFirstChild().getNodeName());
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
            default:
                break;
            }
        }
        return values;
    }

    private final Values readCurve(NodeList listSwInstance, NodeList swValueCont, NodeList swAxisCont) {

        final String CATEGORY = "CATEGORY";
        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

        final Element eValueCont = (Element) swValueCont.item(0);
        final Element eAxisCont = (Element) swAxisCont.item(0);
        final NodeList zValues = eValueCont.getElementsByTagName(SW_VALUES_PHYS).item(0).getChildNodes();
        final Values values = new Values(zValues.getLength(), 2);
        final String axisType = eAxisCont.getElementsByTagName(CATEGORY).item(0).getTextContent();

        NodeList axisValues;

        if ("COM_AXIS".equals(axisType)) {
            NodeList listXvalues = eAxisCont.getElementsByTagName(SW_VALUES_PHYS);
            if (listXvalues != null && listXvalues.getLength() > 0) {
                axisValues = listXvalues.item(0).getChildNodes();
            } else {
                String axisRef = eAxisCont.getElementsByTagName("SW-INSTANCE-REF").item(0).getTextContent();
                axisValues = pickComAxisValues(listSwInstance, axisRef);
            }

        } else {
            axisValues = eAxisCont.getElementsByTagName(SW_VALUES_PHYS).item(0).getChildNodes();
        }

        for (int n = 0; n < zValues.getLength(); n++) {
            values.setValue(0, n, axisValues.item(n).getTextContent());
            values.setValue(1, n, zValues.item(n).getTextContent());
        }

        return values;
    }

    private final NodeList pickComAxisValues(NodeList listSwInstance, String axisRef) {
        for (int i = 0; i < listSwInstance.getLength(); i++) {
            if (listSwInstance.item(i).getFirstChild().getTextContent().equals(axisRef)) {
                return ((Element) listSwInstance.item(i)).getElementsByTagName("SW-VALUES-PHYS").item(0).getChildNodes();
            }
        }
        return null;
    }

    private final Values readMap(NodeList listSwInstance, NodeList swValueCont, NodeList swAxisCont) {

        final String CATEGORY = "CATEGORY";
        final String SW_VALUES_PHYS = "SW-VALUES-PHYS";
        final String VG = "VG";

        final Element eValueCont = (Element) swValueCont.item(0);
        final Element eAxisCont = (Element) swAxisCont.item(0);
        final NodeList vgList = eValueCont.getElementsByTagName(VG);
        final String axisType = eAxisCont.getElementsByTagName(CATEGORY).item(0).getTextContent();

        final Values values = new Values(((Element) vgList.item(0)).getChildNodes().getLength(), vgList.getLength() + 1);

        values.setValue(0, 0, "Y \\ X");

        NodeList axisValues;

        if ("COM_AXIS".equals(axisType)) {
            NodeList listXvalues = eAxisCont.getElementsByTagName(SW_VALUES_PHYS);
            if (listXvalues != null && listXvalues.getLength() > 0) {
                axisValues = listXvalues.item(0).getChildNodes();
            } else {
                String axisRef = eAxisCont.getElementsByTagName("SW-INSTANCE-REF").item(0).getTextContent();
                axisValues = pickComAxisValues(listSwInstance, axisRef);
            }

        } else {
            axisValues = eAxisCont.getElementsByTagName(SW_VALUES_PHYS).item(0).getChildNodes();
        }

        Node zValues;

        for (int n = 0; n < vgList.getLength(); n++) {

            zValues = vgList.item(n);

            values.setValue(1, n * (zValues.getChildNodes().getLength()), zValues.getFirstChild().getTextContent());

            for (int o = 1; o < zValues.getChildNodes().getLength(); o++) {
                if (n == 0) {
                    values.setValue(0, o, axisValues.item(o - 1).getTextContent());
                }
                values.setValue(n + 1, o, zValues.getChildNodes().item(o).getTextContent());
            }
        }

        return values;
    }

    @Override
    public final float getAvgScore() {
        return 0;
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
            Variable var;
            for (int i = 0; i < this.nbLabel; i++) {
                var = this.listLabel.get(i);
                if (var != null) {
                    int lastScore = var.getLastScore();
                    if (repartitionScore.get(lastScore) != null)
                        repartitionScore.put(lastScore, repartitionScore.get(lastScore) + 1);
                }
            }
        }
    }

    @Override
    public HashMap<Integer, Integer> getRepartitionScore() {
        return this.repartitionScore;
    }

    @Override
    public Set<TypeVariable> getCategoryList() {
        return listCategory;
    }

    @Override
    public double getCheckSum() {
        return checkSum;
    }

    public Cdfx(String name, List<Variable> listComparaison) {

        this.name = name;
        this.listLabel = new ArrayList<Variable>(listComparaison.size());
        this.listLabel.addAll(listComparaison);

        this.listCategory = EnumSet.noneOf(TypeVariable.class);

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
