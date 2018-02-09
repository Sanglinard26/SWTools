package paco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

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
import tools.Utilitaire;

public final class StAXPaco implements Cdf {

    private String name;
    private boolean valid;
    private ArrayList<Variable> listLabel;
    private final HashSet<String> listCategory = new HashSet<String>();
    private int minScore = Byte.MAX_VALUE;
    private int maxScore = Byte.MIN_VALUE;
    private double checkSum = 0;

    private final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(5);

    public StAXPaco(File file) {

        this.name = file.getName().substring(0, file.getName().length() - 4);

        long start = System.currentTimeMillis();
        parseStAX(file);
        SWToolsMain.getLogger().info(System.currentTimeMillis() - start + " ms");

    }

    private final void parseStAX(File xml) {

        final XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLEventReader xmler = null;

        try {

            final BufferedReader reader = new BufferedReader(new FileReader(xml));
            int nbLabel = 0;
            String line;
            while ((line = reader.readLine()) != null)
                if (line.indexOf("<SW-INSTANCE>") > -1) {
                    nbLabel++;
                }
            reader.close();

            xmler = xmlif.createXMLEventReader(new FileReader(xml));
            XMLEvent event;

            listLabel = new ArrayList<Variable>(nbLabel);

            String shortName = null, longName = null, category = null, swFeatureRef = null;
            String[] unite = null;
            History[] history = null;
            Values valeur = null;
            byte numAxe;
            int nbDim;
            byte numUnit;
            byte nbEntry;
            // Pour les valeurs
            final List<String> tmpAxeX = new ArrayList<String>();
            final List<String> tmpAxeY = new ArrayList<String>();
            final List<String> tmpValues = new ArrayList<String>();
            //
            // Pour les commentaires
            final List<String> tmpDate = new ArrayList<String>();
            final List<String> tmpAuteur = new ArrayList<String>();
            final List<String> tmpScore = new ArrayList<String>();
            final List<String> tmpRemark = new ArrayList<String>();
            //

            final HashMap<String, String> unit = new HashMap<String, String>();
            final StringBuilder shortNameUnit = new StringBuilder();
            final StringBuilder swUnitDisplay = new StringBuilder();
            final StringBuilder pRemark = new StringBuilder();

            int cntLabel = 0;

            while (xmler.hasNext()) {

                event = xmler.nextEvent();

                switch (event.toString()) {

                case "<SW-UNIT>":

                    // On reinitialise les donnees
                    shortNameUnit.setLength(0);
                    swUnitDisplay.setLength(0);
                    //

                    while (!event.toString().equals("</SW-UNIT>")) {

                        if (event.isCharacters()) {
                            if (!event.asCharacters().getData().equals("\n")) {
                                if (shortNameUnit.length() == 0) {
                                    shortNameUnit.append(event.asCharacters().getData());
                                } else {
                                    swUnitDisplay.append(event.asCharacters().getData());
                                    break;
                                }
                            }
                        }
                        event = xmler.nextEvent();
                    }

                    if (swUnitDisplay.toString().equals("tbd")) {
                        swUnitDisplay.setLength(0);
                        swUnitDisplay.append("");
                    }

                    unit.put(shortNameUnit.toString(), swUnitDisplay.toString());

                    break;

                case "<SW-INSTANCE>":

                    // On reinitialise les donnees
                    numAxe = 0;
                    nbDim = 0;
                    numUnit = 0;
                    nbEntry = 0;

                    tmpAxeX.clear();
                    tmpAxeY.clear();
                    tmpValues.clear();

                    tmpDate.clear();
                    tmpAuteur.clear();
                    tmpScore.clear();
                    tmpRemark.clear();
                    history = null;
                    pRemark.setLength(0);
                    //

                    while (!event.toString().equals("</SW-INSTANCE>")) {

                        if (event.isStartElement()) {

                            switch (event.asStartElement().getName().toString()) {
                            case "SHORT-NAME":

                                do {
                                    event = xmler.nextEvent();
                                    if (event.isCharacters()) {
                                        shortName = event.asCharacters().getData();
                                    }

                                } while (!event.isCharacters());

                                break;
                            case "LONG-NAME":

                                do {
                                    event = xmler.nextEvent();
                                    if (event.isCharacters()) {
                                        if (!event.asCharacters().getData().equals("\n")) {
                                            longName = event.asCharacters().getData();
                                        } else {
                                            longName = "";
                                        }
                                    }

                                } while (!event.isCharacters());

                                break;
                            case "CATEGORY":

                                do {
                                    event = xmler.nextEvent();
                                    if (event.isCharacters()) {
                                        category = event.asCharacters().getData();
                                    }

                                } while (!event.isCharacters());

                                break;
                            case "SW-FEATURE-REF":

                                do {
                                    event = xmler.nextEvent();
                                    if (event.isCharacters()) {
                                        swFeatureRef = event.asCharacters().getData();
                                    }

                                } while (!event.isCharacters());

                                break;

                            case "SW-UNIT-REF":

                                if (numUnit == 0) {
                                    switch (category) {
                                    case "VALUE":
                                        unite = new String[1];
                                        break;
                                    case "ASCII":
                                        unite = new String[1];
                                        break;
                                    case "VALUE_BLOCK":
                                        unite = new String[1];
                                        break;
                                    case "AXIS_VALUES":
                                        unite = new String[1];
                                        break;
                                    case "CURVE_INDIVIDUAL":
                                        unite = new String[2];
                                        break;
                                    case "CURVE_FIXED":
                                        unite = new String[2];
                                        break;
                                    case "CURVE_GROUPED":
                                        unite = new String[2];
                                        break;
                                    case "MAP_INDIVIDUAL":
                                        unite = new String[3];
                                        break;
                                    case "MAP_FIXED":
                                        unite = new String[3];
                                        break;
                                    case "MAP_GROUPED":
                                        unite = new String[3];
                                        break;
                                    }
                                }

                                do {
                                    event = xmler.nextEvent();
                                    if (event.isCharacters()) {
                                        unite[numUnit] = unit.get(event.asCharacters().getData());
                                    }

                                } while (!event.isCharacters());

                                numUnit++;

                                break;

                            case "SW-VALUES-PHYS":

                                switch (category) {
                                case "VALUE":

                                    valeur = new Values(1, 1);

                                    while (!event.isCharacters()) {
                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n")) {
                                                valeur.setValue(0, 0, Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }

                                        }
                                    }

                                    break;

                                case "ASCII":

                                    valeur = new Values(1, 1);

                                    while (!event.isCharacters()) {
                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n")) {
                                                valeur.setValue(0, 0, Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }

                                        }
                                    }

                                    break;

                                case "VALUE_BLOCK":

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();

                                        if (event.toString().equals("<LABEL>")) {
                                            nbDim++;
                                        }

                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {
                                                tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }
                                        }
                                    }

                                    if (nbDim == 0) {
                                        nbDim = 2;
                                        valeur = new Values(tmpValues.size() + 1, nbDim);
                                    } else {
                                        valeur = new Values((tmpValues.size() / nbDim) + 1, nbDim + 1);
                                    }

                                    if (nbDim == 2) {
                                        valeur.setValue(0, 0, "X");
                                        valeur.setValue(1, 0, "Z");
                                        for (int i = 0; i < tmpValues.size(); i++) {
                                            valeur.setValue(0, i + 1, Integer.toString(i));
                                            valeur.setValue(1, i + 1, tmpValues.get(i));
                                        }
                                    } else {
                                        valeur.setValue(0, 0, "Y \\ X");
                                        for (int i = 0; i < tmpValues.size(); i++) {
                                            valeur.setValue((int) ((double) (i + 1) / (double) ((tmpValues.size() / nbDim) + 1)) + 1,
                                                    (i + 1) % ((tmpValues.size() / nbDim) + 1), tmpValues.get(i));
                                        }
                                    }

                                    break;

                                case "CURVE_INDIVIDUAL":

                                    tmpValues.clear();

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {
                                                tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }

                                        }

                                    }

                                    switch (numAxe) {
                                    case 0:
                                        if (tmpValues.size() > 0) {
                                            valeur = new Values(tmpValues.size(), 2);
                                            for (int i = 0; i < tmpValues.size(); i++) {
                                                valeur.setValue(0, i, tmpValues.get(i));
                                            }
                                        }

                                        break;
                                    case 1:
                                        if (tmpValues.size() > 0) {
                                            for (int i = 0; i < tmpValues.size(); i++) {
                                                valeur.setValue(1, i, tmpValues.get(i));
                                            }

                                        }

                                        break;
                                    }

                                    numAxe++;

                                    break;

                                case "AXIS_VALUES":

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {
                                                tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }

                                        }

                                    }

                                    if (tmpValues.size() > 0) {
                                        valeur = new Values(tmpValues.size(), 1);
                                        for (int i = 0; i < tmpValues.size(); i++) {
                                            valeur.setValue(0, i, tmpValues.get(i));
                                        }

                                    }

                                    numAxe++;

                                    break;

                                case "CURVE_FIXED":

                                    tmpValues.clear();

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {
                                                tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }

                                        }

                                    }

                                    switch (numAxe) {
                                    case 0:
                                        if (tmpValues.size() > 0) {
                                            valeur = new Values(tmpValues.size(), 2);
                                            for (int i = 0; i < tmpValues.size(); i++) {
                                                valeur.setValue(0, i, tmpValues.get(i));
                                            }
                                        }

                                        break;
                                    case 1:
                                        if (tmpValues.size() > 0) {
                                            for (int i = 0; i < tmpValues.size(); i++) {
                                                valeur.setValue(1, i, tmpValues.get(i));
                                            }

                                        }

                                        break;
                                    }

                                    numAxe++;

                                    break;

                                case "CURVE_GROUPED":

                                    tmpValues.clear();

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {
                                                tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                            } else {
                                                event = xmler.peek();
                                            }

                                        }

                                    }

                                    switch (numAxe) {
                                    case 0:
                                        if (tmpValues.size() > 0) {
                                            valeur = new Values(tmpValues.size(), 2);
                                            for (int i = 0; i < tmpValues.size(); i++) {
                                                valeur.setValue(0, i, tmpValues.get(i));
                                            }
                                        }

                                        break;
                                    case 1:
                                        if (tmpValues.size() > 0) {
                                            for (int i = 0; i < tmpValues.size(); i++) {
                                                valeur.setValue(1, i, tmpValues.get(i));
                                            }

                                        }

                                        break;
                                    }

                                    numAxe++;

                                    break;

                                case "MAP_INDIVIDUAL":

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {

                                                switch (numAxe) {
                                                case 0:
                                                    tmpAxeX.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                case 1:
                                                    tmpAxeY.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                case 2:
                                                    tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                }

                                            } else {
                                                event = xmler.peek();
                                            }
                                        }

                                    }

                                    if (numAxe == 2) {
                                        valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
                                        valeur.setValue(0, 0, "Y \\ X");

                                        for (int i = 0; i < tmpAxeX.size(); i++) {
                                            valeur.setValue(0, i + 1, tmpAxeX.get(i));
                                        }

                                        for (int i = 0; i < tmpValues.size(); i++) {
                                            valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
                                                    tmpValues.get(i));
                                        }
                                    }

                                    numAxe++;

                                    break;

                                case "MAP_FIXED":

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {

                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {

                                                switch (numAxe) {
                                                case 0:
                                                    tmpAxeX.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                case 1:
                                                    tmpAxeY.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                case 2:
                                                    tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                }

                                            } else {
                                                event = xmler.peek();
                                            }
                                        }

                                    }

                                    if (numAxe == 2) {
                                        valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
                                        valeur.setValue(0, 0, "Y \\ X");

                                        for (int i = 0; i < tmpAxeX.size(); i++) {
                                            valeur.setValue(0, i + 1, tmpAxeX.get(i));
                                        }

                                        for (int i = 0; i < tmpValues.size(); i++) {
                                            valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
                                                    tmpValues.get(i));
                                        }
                                    }

                                    numAxe++;

                                    break;

                                case "MAP_GROUPED":

                                    while (!event.toString().equals("</SW-VALUES-PHYS>")) {

                                        event = xmler.nextEvent();
                                        if (event.isCharacters()) {
                                            if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {

                                                switch (numAxe) {
                                                case 0:
                                                    tmpAxeX.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                case 1:
                                                    tmpAxeY.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                case 2:
                                                    tmpValues.add(Utilitaire.cutNumber(event.asCharacters().getData()));
                                                    break;
                                                }

                                            } else {
                                                event = xmler.peek();
                                            }
                                        }

                                    }

                                    if (numAxe == 2) {
                                        valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
                                        valeur.setValue(0, 0, "Y \\ X");

                                        for (int i = 0; i < tmpAxeX.size(); i++) {
                                            valeur.setValue(0, i + 1, tmpAxeX.get(i));
                                        }

                                        for (int i = 0; i < tmpValues.size(); i++) {
                                            valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
                                                    tmpValues.get(i));
                                        }
                                    }

                                    numAxe++;

                                    break;
                                }
                                break;

                            case "SW-CS-HISTORY":

                                while (!event.toString().equals("</SW-CS-HISTORY>")) {

                                    event = xmler.nextEvent();

                                    if (event.isStartElement()) {
                                        if (event.asStartElement().getName().toString().equals("SW-CS-ENTRY")) {
                                            nbEntry++;
                                        }
                                    }

                                    if (event.isStartElement()) {

                                        switch (event.asStartElement().getName().toString()) {
                                        case "DATE":
                                            event = xmler.nextEvent();
                                            tmpDate.add(event.asCharacters().getData().replace("T", " @ "));
                                            break;
                                        case "SW-CS-PERFORMED-BY":
                                            event = xmler.nextEvent();
                                            tmpAuteur.add(event.asCharacters().getData());
                                            break;
                                        case "SW-CS-STATE":
                                            event = xmler.nextEvent();
                                            tmpScore.add(event.asCharacters().getData());
                                            break;
                                        case "REMARK":
                                            pRemark.setLength(0);
                                            while (!event.toString().equals("</REMARK>")) {
                                                event = xmler.nextEvent();

                                                if (event.isCharacters()) {
                                                    if (!event.asCharacters().getData().equals("\n")) {
                                                        pRemark.append(event.asCharacters().getData());
                                                    }
                                                }

                                                if (event.isEndElement() && pRemark.length() > 0) {
                                                    pRemark.append("\n");
                                                }
                                            }
                                            if (pRemark.length() > 0) {
                                                tmpRemark.add(pRemark.toString().substring(0, pRemark.toString().length() - 2));
                                            } else {
                                                tmpRemark.add("");
                                            }

                                            break;

                                        }
                                    }

                                }

                                if (nbEntry > 0) {
                                    history = new History[nbEntry];
                                    for (int i = 0; i < nbEntry; i++) {
                                        history[i] = new History(tmpDate.get(i), tmpAuteur.get(i), tmpScore.get(i), tmpRemark.get(i));
                                    }
                                }

                                break;
                            }

                        }
                        event = xmler.nextEvent();
                    }

                    // System.out.println(shortName);

                    if (history == null) {
                        history = new History[0];
                    }

                    // On cree la variable
                    switch (category) {
                    case Cdf.VALUE:
                        this.listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.VALUE);
                        break;
                    case Cdf.ASCII:
                        this.listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.ASCII);
                        break;
                    case Cdf.VALUE_BLOCK:
                        this.listLabel.add(new ValueBlock(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.VALUE_BLOCK);
                        break;
                    case Cdf.AXIS_VALUES:
                        this.listLabel.add(new Axis(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.AXIS_VALUES);
                        break;
                    case Cdf.CURVE_INDIVIDUAL:
                        this.listLabel.add(new Curve(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.CURVE_INDIVIDUAL);
                        break;
                    case Cdf.CURVE_FIXED:
                        this.listLabel.add(new Curve(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.CURVE_FIXED);
                        break;
                    case Cdf.CURVE_GROUPED:
                        this.listLabel.add(new Curve(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.CURVE_GROUPED);
                        break;
                    case Cdf.MAP_INDIVIDUAL:
                        this.listLabel.add(new Map(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.MAP_INDIVIDUAL);
                        break;
                    case Cdf.MAP_FIXED:
                        this.listLabel.add(new Map(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.MAP_FIXED);
                        break;
                    case Cdf.MAP_GROUPED:
                        this.listLabel.add(new Map(shortName, longName, category, swFeatureRef, unite, history, valeur));
                        listCategory.add(Cdf.MAP_GROUPED);
                        break;
                    }

                    checkSum += listLabel.get(cntLabel).getChecksum();

                    cntLabel++;

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

            this.valid = true;

            shortNameUnit.setLength(0);
            swUnitDisplay.setLength(0);
            pRemark.setLength(0);

            tmpAxeX.clear();
            tmpAxeY.clear();
            tmpValues.clear();

            tmpDate.clear();
            tmpAuteur.clear();
            tmpScore.clear();
            tmpRemark.clear();

        } catch (Exception e) {
            e.printStackTrace();
            SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);
        } finally {
            try {
                xmler.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
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
    public HashSet<String> getCategoryList() {
        return this.listCategory;
    }

    @Override
    public List<Variable> getListLabel() {
        return this.listLabel;
    }

    @Override
    public HashMap<Integer, Integer> getRepartitionScore() {
        return this.repartitionScore;
    }

    private final void getScores() {

        repartitionScore.put(0, 0);
        repartitionScore.put(25, 0);
        repartitionScore.put(50, 0);
        repartitionScore.put(75, 0);
        repartitionScore.put(100, 0);

        if (!listLabel.isEmpty()) {

            int lastScore;

            for (int i = 0; i < this.listLabel.size(); i++) {
                lastScore = this.listLabel.get(i).getLastScore();

                if (repartitionScore.get(lastScore) != null)
                    repartitionScore.put(lastScore, repartitionScore.get(lastScore) + 1);
            }
        }
    }

    @Override
    public float getAvgScore() {
        return (float) (repartitionScore.get(0) * 0 + repartitionScore.get(25) * 25 + repartitionScore.get(50) * 50 + repartitionScore.get(75) * 75
                + repartitionScore.get(100) * 100) / listLabel.size();
    }

    @Override
    public int getMinScore() {
        return this.minScore;
    }

    @Override
    public int getMaxScore() {
        return this.maxScore;
    }

    @Override
    public double getCheckSum() {
        return this.checkSum;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

}
