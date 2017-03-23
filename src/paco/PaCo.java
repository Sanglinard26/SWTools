package paco;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import visu.Main;

public final class PaCo extends Observable {

    public static final String ASCII = "ASCII";
    public static final String _C = "VALUE";
    public static final String _T = "CURVE_INDIVIDUAL";
    public static final String _M = "MAP_INDIVIDUAL";
    public static final String _A = "AXIS_VALUES";
    public static final String _T_CA = "VALUE_BLOCK";
    public static final String _T_GROUPED = "CURVE_GROUPED";
    public static final String _M_GROUPED = "MAP_GROUPED";

    private Document document = null;
    private DocumentBuilderFactory factory = null;

    private File file = null;
    private String name = "";
    private int nbLabel = 0;
    private final HashMap<String, String> unit = new HashMap<String, String>();
    private final ArrayList<Variable> listLabel = new ArrayList<Variable>();

    public PaCo(File file, JPanel panelPaco) {

        this.file = file;

        addObserver((Observer) panelPaco);

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file.getPath());
        } catch (Exception e) {
            Main.getLogger().severe(e.getMessage());
        }

        if (document != null) {

            final Element racine = document.getDocumentElement();

            final NodeList enfantRacine = racine.getChildNodes();

            this.name = enfantRacine.item(0).getTextContent();

            final NodeList listSwInstance = racine.getElementsByTagName("SW-INSTANCE");
            final NodeList listSwUnit = racine.getElementsByTagName("SW-UNIT");
            Element eUnit;
            String longName, shortName, category, swFeatureRef;
            String[] swUnitRef = null;
            NodeList swCsEntry, swAxisCont;
            nbLabel = listSwInstance.getLength();
            Element label;

            // Remplissage de la HashMap des unites
            for (int u = 0; u < listSwUnit.getLength(); u++) {
                eUnit = (Element) listSwUnit.item(u);
                unit.put(eUnit.getElementsByTagName("SHORT-NAME").item(0).getTextContent(),
                        eUnit.getElementsByTagName("SW-UNIT-DISPLAY").item(0).getTextContent());
            }

            for (int i = 0; i < nbLabel; i++) {
                label = (Element) listSwInstance.item(i);
                longName = label.getElementsByTagName("LONG-NAME").item(0).getTextContent();
                shortName = label.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                category = label.getElementsByTagName("CATEGORY").item(0).getTextContent();

                // System.out.println(shortName);

                if (label.getElementsByTagName("SW-FEATURE-REF").item(0) != null) {
                    swFeatureRef = label.getElementsByTagName("SW-FEATURE-REF").item(0).getTextContent();
                } else {
                    swFeatureRef = "Pas de fonction definie";
                }

                swAxisCont = label.getElementsByTagName("SW-AXIS-CONT");
                swUnitRef = new String[swAxisCont.getLength()];

                // A finir d'implementer pour les ValueBlock
                String fullAttributAxe;
                String attributAxe;
                String[] splitAttributAxe = null; // Le tableau est cense avoir trois elements
                for (int n = 0; n < swAxisCont.getLength(); n++) {
                    if (swAxisCont.item(n).hasAttributes()) {
                        fullAttributAxe = swAxisCont.item(n).getAttributes().getNamedItem("SI").getTextContent();
                        if (fullAttributAxe.indexOf(";") > -1) {
                            attributAxe = fullAttributAxe.substring(fullAttributAxe.indexOf(";") + 1);
                            if (attributAxe.indexOf("@") > -1) {
                                splitAttributAxe = attributAxe.split("@");
                            }
                        }
                    }

                    swUnitRef[n] = unit.get(swAxisCont.item(n).getFirstChild().getTextContent());

                }
                // _________________________________________

                swCsEntry = label.getElementsByTagName("SW-CS-ENTRY");

                switch (category) {
                case PaCo.ASCII:
                    listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readValue(swAxisCont)));
                    break;
                case PaCo._C:
                    listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readValue(swAxisCont)));
                    break;
                case PaCo._T:
                    listLabel.add(new Curve(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readCurve(swAxisCont)));
                    break;
                case PaCo._A:
                    listLabel.add(new Axis(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readAxis(swAxisCont)));
                    break;
                case PaCo._T_GROUPED:
                    listLabel.add(new Curve(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readCurve(swAxisCont)));
                    break;
                case PaCo._T_CA:
                    listLabel.add(new ValueBlock(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry),
                            readValueBlock(splitAttributAxe, swAxisCont)));
                    break;
                case PaCo._M:
                    listLabel.add(new Map(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readMap(swAxisCont)));
                    break;
                case PaCo._M_GROUPED:
                    listLabel.add(new Map(shortName, longName, category, swFeatureRef, swUnitRef, readEntry(swCsEntry), readMap(swAxisCont)));
                    break;
                }

                this.setChanged();
                this.notifyObservers(i + 1);
            }
        }

    }

    public Boolean checkName() {
        return this.file.getName().substring(0, this.file.getName().length() - 4).equals(this.name);
    }

    public String getName() {
        return this.name;
    }

    public int getNbLabel() {
        return this.nbLabel;
    }

    public ArrayList<Variable> getListLabel() {
        return this.listLabel;
    }

    private final String[][] readEntry(NodeList swCsEntry) {

        final int nbEntry = swCsEntry.getLength();
        String[][] entry = new String[nbEntry][4];

        Element aEntry;
        Node swCsState, swCsPerformedBy, remark, date;

        for (int n = 0; n < nbEntry; n++) {
            aEntry = (Element) swCsEntry.item(n);

            swCsPerformedBy = aEntry.getElementsByTagName("SW-CS-PERFORMED-BY").item(0);
            date = aEntry.getElementsByTagName("DATE").item(0);
            swCsState = aEntry.getElementsByTagName("SW-CS-STATE").item(0);
            remark = aEntry.getElementsByTagName("REMARK").item(0);

            entry[n][0] = date.getTextContent().replace("T", " @ ");
            entry[n][1] = swCsPerformedBy.getTextContent();
            entry[n][2] = swCsState.getTextContent();
            entry[n][3] = remark.getTextContent();
        }
        return entry;
    }

    private final String readValue(NodeList swAxisCont) {
        return swAxisCont.item(0).getLastChild().getTextContent();
    }

    private final String[] readAxis(NodeList swAxisCont) {
        String val[] = null;

        final Element eAxisCont = (Element) swAxisCont.item(0);
        final Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
        final NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

        final int nbVal = value.getLength();

        val = new String[nbVal];

        for (int a = 0; a < nbVal; a++) {
            val[a] = value.item(a).getTextContent();
        }

        return val;
    }

    private final String[][] readValueBlock(String[] dim, NodeList swAxisCont) {

        String val[][] = null;
        final int nbAxe = swAxisCont.getLength();

        for (int n = 0; n < nbAxe; n++) {
            Element eAxisCont = (Element) swAxisCont.item(n);
            Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
            Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);

            NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(0).getNodeName());

            final int nbVal = value.getLength();

            if (val == null)
                val = new String[2][nbVal];

            switch (indexAxis.getTextContent()) {
            case "0":
                for (int a = 0; a < nbVal; a++) {
                    val[0][a] = String.valueOf(a + 1);
                    if (value.item(a).getTextContent() != null) {
                        val[1][a] = value.item(a).getTextContent();
                    } else {
                        val[1][a] = value.item(a).getFirstChild().getTextContent();
                    }

                }
                break;
            }
        }

        return val;
    }

    private final String[][] readCurve(NodeList swAxisCont) {

        String val[][] = null;
        final int nbAxe = swAxisCont.getLength();

        for (int n = 0; n < nbAxe; n++) {

            Element eAxisCont = (Element) swAxisCont.item(n);
            Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
            Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
            NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

            final int nbVal = value.getLength();

            if (val == null)
                val = new String[nbAxe][nbVal];

            switch (indexAxis.getTextContent()) {
            case "1":

                for (int b = 0; b < nbVal; b++) {
                    val[0][b] = value.item(b).getTextContent();
                }
                break;
            case "0":
                for (int a = 0; a < nbVal; a++) {
                    val[1][a] = value.item(a).getTextContent();
                }
                break;
            }
        }
        return val;
    }

    private final String[][] readMap(NodeList swAxisCont) {
        // Premiere dimension = Axe Y car nombre de ligne
        // Deuxieme dimension = Axe X car nombre de colonne
        String val[][] = new String[((Element) swAxisCont.item(1)).getLastChild().getChildNodes().getLength()
                + 1][((Element) swAxisCont.item(0)).getLastChild().getChildNodes().getLength() + 1];

        final int nbAxe = swAxisCont.getLength();

        val[0][0] = "Y \\ X";

        for (int n = 0; n < nbAxe; n++) {
            Element eAxisCont = (Element) swAxisCont.item(n);
            Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
            Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
            NodeList nodeListV = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

            final int nbAxeVal = nodeListV.getLength();

            switch (indexAxis.getTextContent()) {
            case "1": // Axe X

                for (int x = 0; x < nbAxeVal; x++) {

                    switch (nodeListV.item(x).getNodeName()) {
                    case "VT":
                        val[0][x + 1] = nodeListV.item(x).getFirstChild().getTextContent();
                        break;

                    default:
                        val[0][x + 1] = nodeListV.item(x).getTextContent();
                        break;
                    }
                }
                break;

            case "2": // Axe Y

                for (int y = 0; y < nbAxeVal; y++) {
                    switch (nodeListV.item(y).getNodeName()) {
                    case "VT":
                        val[y + 1][0] = nodeListV.item(y).getFirstChild().getTextContent();
                        break;
                    default:
                        val[y + 1][0] = nodeListV.item(y).getTextContent();
                        break;
                    }
                }
                break;
            case "0": // Valeur Z

                NodeList vg = ((Element) swValuesPhys).getElementsByTagName("VG");

                for (int nVG = 1; nVG < vg.getLength() + 1; nVG++) {
                    NodeList nodeV = ((Element) vg.item(nVG - 1)).getElementsByTagName("V");

                    if (nodeV.getLength() > 0) {
                        for (int nV = 1; nV < nodeV.getLength() + 1; nV++) {
                            val[nVG][nV] = nodeV.item(nV - 1).getTextContent();
                        }
                    } else {
                        nodeV = ((Element) vg.item(nVG - 1)).getElementsByTagName("VT");

                        for (int nV = 1; nV < nodeV.getLength() + 1; nV++) {
                            val[nVG][nV] = nodeV.item(nV - 1).getFirstChild().getTextContent();
                        }
                    }

                }
                break;
            }
        }

        return val;
    }

    public void exportToExcel(File file) {
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(file);

            WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);

            WritableCellFormat borderFormat = new WritableCellFormat();
            borderFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            WritableCellFormat axisFormat = new WritableCellFormat(arial10Bold);
            axisFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            axisFormat.setBackground(Colour.VERY_LIGHT_YELLOW);

            WritableSheet shtInfo = workbook.createSheet("Infos", 0);

            writeCell(shtInfo, 0, 0, "Nom du PaCo : " + this.getName(), arial10format);
            writeCell(shtInfo, 0, 1, "Nombre de variables : " + String.valueOf(this.getNbLabel()), arial10format);
            writeCell(shtInfo, 0, 2, "Liste des variables : ", arial10format);

            WritableSheet sheet = workbook.createSheet("Export", 1);

            int row = 0;
            int cnt = 0;

            for (Variable var : listLabel) {

                shtInfo.addHyperlink(new WritableHyperlink(0, 3 + cnt, var.getShortName(), sheet, 0, row));

                if (var instanceof Scalaire) {
                    Scalaire variableType = (Scalaire) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);
                    row += 1;
                    writeCell(sheet, 0, row, variableType.getValue(), borderFormat);
                    row += 2;
                }
                if (var instanceof Axis) {
                    Axis variableType = (Axis) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    int col = 0;
                    row += 1;
                    for (int x = 0; x < variableType.getDim(); x++) {
                        writeCell(sheet, col, row, variableType.getzValues(x), borderFormat);
                        col += 1;
                    }
                    row += 2;
                }
                if (var instanceof Curve) {
                    Curve variableType = (Curve) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    for (int y = 0; y < 2; y++) {
                        int col = 0;
                        row += 1;
                        for (int x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0) {
                                writeCell(sheet, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
                            }

                            col += 1;
                        }

                    }
                    row += 2;
                }

                if (var instanceof ValueBlock) {
                    ValueBlock variableType = (ValueBlock) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    for (int y = 0; y < 2; y++) {
                        int col = 0;
                        row += 1;
                        for (int x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0) {
                                writeCell(sheet, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
                            }
                            col += 1;
                        }

                    }
                    row += 2;
                }

                if (var instanceof Map) {
                    Map variableType = (Map) var;
                    writeCell(sheet, 0, row, variableType.getShortName(), arial10format);

                    for (int y = 0; y < variableType.getDimY(); y++) {
                        int col = 0;
                        row += 1;
                        for (int x = 0; x < variableType.getDimX(); x++) {
                            if (y == 0 | x == 0) {
                                writeCell(sheet, col, row, variableType.getValue(y, x), axisFormat);
                            } else {
                                writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
                            }
                            col += 1;
                        }

                    }
                    row += 2;
                }

                cnt += 1;
            }

            workbook.write();
            workbook.close();
        } catch (IOException e) {
            Main.getLogger().severe(e.getMessage());
        } catch (WriteException e) {
            Main.getLogger().severe(e.getMessage());
            if (e instanceof RowsExceededException) {
                JOptionPane.showMessageDialog(null, "Trop de variables à exporter !", "ERREUR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private final void writeCell(WritableSheet sht, int col, int row, String txtValue, WritableCellFormat format)
            throws RowsExceededException, WriteException {
        try {
            final Double value = Double.parseDouble(txtValue);
            sht.addCell(new Number(col, row, value, format));
        } catch (NumberFormatException e) {
            sht.addCell(new Label(col, row, txtValue, format));
        }

    }

    public void exportToTxt(File file) {
        try {
            PrintWriter printWriter = new PrintWriter(file);

            printWriter.println(" -----------------");
            printWriter.println("| EXPORT TXT PACO |");
            printWriter.println(" -----------------");
            printWriter.println();
            printWriter.println("Nom du PaCo : " + getName());
            printWriter.println("Nombre de label(s) : " + getNbLabel());
            printWriter.println();

            for (Variable var : listLabel) {
                printWriter.println();

                printWriter.println(var.toString());
            }

            printWriter.println();
            printWriter.println();
            printWriter.println(" -----");
            printWriter.println("| FIN |");
            printWriter.println(" -----");
            printWriter.println();
            printWriter.println("Fichier cree par SWTools, " + new Date().toString());

            printWriter.close();

        } catch (FileNotFoundException e) {
            Main.getLogger().severe(e.getMessage());
        }
    }

}
