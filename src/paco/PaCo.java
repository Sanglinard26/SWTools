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
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public final class PaCo extends Observable {

	public static final String ASCII = "ASCII";
	public static final String _C = "VALUE";
	public static final String _T = "CURVE_INDIVIDUAL";
	public static final String _M = "MAP_INDIVIDUAL";
	public static final String _A = "AXIS_VALUES";
	public static final String _T_CA = "VALUE_BLOCK";
	public static final String _T_GROUPED = "CURVE_GROUPED";
	public static final String _M_GROUPED = "MAP_GROUPED";

	Document document = null;
	DocumentBuilderFactory factory = null;

	private File file = null;
	private String name = "";
	private int nbLabel = 0;
	private HashMap<String, String> unit = new HashMap<String,String>();
	private ArrayList<Variable> listLabel = new ArrayList<Variable>();

	public PaCo(File file, JPanel panelPaco) {

		this.file = file;

		addObserver((Observer) panelPaco);

		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (document != null) {

			Element racine = document.getDocumentElement();

			NodeList enfantRacine = racine.getChildNodes();

			this.name = enfantRacine.item(0).getTextContent();

			NodeList listSwInstance = racine.getElementsByTagName("SW-INSTANCE");
			NodeList listSwUnit = racine.getElementsByTagName("SW-UNIT");
			Element eUnit;
			String shortName, category, swFeatureRef;
			NodeList swCsEntry, swAxisCont;
			nbLabel = listSwInstance.getLength();
			Element label;

			for(int u=0; u<listSwUnit.getLength(); u++)
			{
				eUnit = (Element) listSwUnit.item(u);
				unit.put(
						eUnit.getElementsByTagName("SHORT-NAME").item(0).getTextContent(), 
						eUnit.getElementsByTagName("SW-UNIT-DISPLAY").item(0).getTextContent());
			}

			for (int i = 0; i < nbLabel; i++) {
				label = (Element) listSwInstance.item(i);
				shortName = label.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
				category = label.getElementsByTagName("CATEGORY").item(0).getTextContent();

				if (label.getElementsByTagName("SW-FEATURE-REF").item(0) != null) {
					swFeatureRef = label.getElementsByTagName("SW-FEATURE-REF").item(0).getTextContent();
				} else {
					swFeatureRef = "Pas de fonction definie";
				}

				swAxisCont = label.getElementsByTagName("SW-AXIS-CONT");
				
				// A finir d'implementer pour les ValueBlock
				String fullAttributAxe;
				String attributAxe;
				for(int n = 0; n<swAxisCont.getLength(); n++)
				{
					if(swAxisCont.item(n).hasAttributes())
					{
						fullAttributAxe = swAxisCont.item(n).getAttributes().getNamedItem("SI").getTextContent();
						if(fullAttributAxe.indexOf(";")>-1)
						{
							attributAxe = fullAttributAxe.substring(fullAttributAxe.indexOf(";")+1);
						}
						
					}
				}
				// _________________________________________


				swCsEntry = label.getElementsByTagName("SW-CS-ENTRY");

				switch (category) {
				case PaCo.ASCII:
					listLabel.add(new Scalaire(shortName, category, swFeatureRef, readEntry(swCsEntry), readValue(swAxisCont)));
					break;
				case PaCo._C:
					listLabel.add(new Scalaire(shortName, category, swFeatureRef, readEntry(swCsEntry), readValue(swAxisCont)));
					break;
				case PaCo._T:
					listLabel.add(new Curve(shortName, category, swFeatureRef, readEntry(swCsEntry), readCurve(swAxisCont)));
					break;
				case PaCo._A:
					listLabel.add(new Axis(shortName, category, swFeatureRef, readEntry(swCsEntry), readAxis(swAxisCont)));
					break;
				case PaCo._T_GROUPED:
					listLabel.add(new Curve(shortName, category, swFeatureRef, readEntry(swCsEntry), readCurve(swAxisCont)));
					break;
				case PaCo._T_CA:
					listLabel.add(new ValueBlock(shortName, category, swFeatureRef, readEntry(swCsEntry), readValueBlock(swAxisCont)));
					break;
				case PaCo._M:
					listLabel.add(new Map(shortName, category, swFeatureRef, readEntry(swCsEntry), readMap(swAxisCont)));
					break;
				case PaCo._M_GROUPED:
					listLabel.add(new Map(shortName, category, swFeatureRef, readEntry(swCsEntry), readMap(swAxisCont)));
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

	private String[][] readEntry(NodeList swCsEntry) {
		String[][] entry = new String[swCsEntry.getLength()][4];

		Element aEntry;
		Node swCsState, swCsPerformedBy, remark, date;

		for (int n = 0; n < swCsEntry.getLength(); n++) {
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

	private String readValue(NodeList swAxisCont) {
		return swAxisCont.item(0).getLastChild().getTextContent();
	}

	private String[] readAxis(NodeList swAxisCont)
	{
		String val[] = null;

		Element eAxisCont = (Element) swAxisCont.item(0);
		Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
		NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

		val = new String[value.getLength()];

		for(int a = 0; a<value.getLength(); a++)
		{
			val[a] = value.item(a).getTextContent();
		}

		return val;
	}

	private String[][] readValueBlock(NodeList swAxisCont)
	{
		String val[][] = null;

		return val;
	}

	private String[][] readCurve(NodeList swAxisCont) {

		String val[][] = null;

		for (int n = 0; n < swAxisCont.getLength(); n++) {
			Element eAxisCont = (Element) swAxisCont.item(n);
			Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
			Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
			NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

			if (val == null)
				val = new String[swAxisCont.getLength()][value.getLength()];

			switch (indexAxis.getTextContent()) {
			case "1":

				for (int b = 0; b < value.getLength(); b++) {
					val[0][b] = value.item(b).getTextContent();
				}
				break;
			case "0":
				for (int a = 0; a < value.getLength(); a++) {
					val[1][a] = value.item(a).getTextContent();
				}
				break;
			}
		}
		return val;
	}

	private String[][] readMap(NodeList swAxisCont) {
		// Premiere dimension = Axe Y car nombre de ligne
		// Deuxieme dimension = Axe X car nombre de colonne
		String val[][] = new String[((Element) swAxisCont.item(1)).getLastChild().getChildNodes().getLength()
		                            + 1][((Element) swAxisCont.item(0)).getLastChild().getChildNodes().getLength() + 1];

		val[0][0] = "Y \\ X";

		for (int n = 0; n < swAxisCont.getLength(); n++) {
			Element eAxisCont = (Element) swAxisCont.item(n);
			Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
			Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
			NodeList nodeListV = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

			switch (indexAxis.getTextContent()) {
			case "1": // Axe X

				for (int x = 0; x < nodeListV.getLength(); x++) {

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

				for (int y = 0; y < nodeListV.getLength(); y++) {
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

					for (int nV = 1; nV < nodeV.getLength() + 1; nV++) {
						val[nVG][nV] = nodeV.item(nV - 1).getTextContent();
					}
				}
				break;
			}
		}

		return val;
	}

	public void exportToExcel(File file) {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("Export", 0);
			WritableFont arial10Bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
			WritableCellFormat arial10format = new WritableCellFormat(arial10Bold);
			arial10format.setBackground(Colour.GRAY_25);

			WritableCellFormat borderFormat = new WritableCellFormat();
			borderFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

			int row = 0;

			for (Variable var : listLabel) {

				if (var instanceof Scalaire) {
					Scalaire variableType = (Scalaire) var;
					writeCell(sheet, 0, row, variableType.getShortName(), arial10format);
					writeCell(sheet, 1, row, variableType.getValue(), borderFormat);
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
							writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
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
							writeCell(sheet, col, row, variableType.getValue(y, x), borderFormat);
							col += 1;
						}

					}
					row += 2;
				}
			}

			workbook.write();
			workbook.close();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (WriteException e) {

			e.printStackTrace();
		}

	}

	private void writeCell(WritableSheet sht, int col, int row, String txtValue, WritableCellFormat format)
			throws RowsExceededException, WriteException {
		try {
			Double value = Double.parseDouble(txtValue);
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
				printWriter.print(var.getShortName());

				if (var instanceof Scalaire) {
					Scalaire variableType = (Scalaire) var;
					printWriter.print(" = " + variableType.getValue());
					printWriter.println();
				}
				if (var instanceof Axis)
				{
					Axis variableType = (Axis) var;
					printWriter.println();

					for (int x = 0; x < variableType.getDim(); x++) {
						printWriter.print("|" + variableType.getzValues(x) + "|");
					}
					printWriter.println();

				}
				if (var instanceof Curve) {
					Curve variableType = (Curve) var;
					printWriter.println();

					for (int y = 0; y < 2; y++) {
						for (int x = 0; x < variableType.getDimX(); x++) {
							printWriter.print("|" + variableType.getValue(y, x) + "|");
						}
						printWriter.println();
					}
				}
				if (var instanceof Map) {
					Map variableType = (Map) var;
					printWriter.println();

					for (int y = 0; y < variableType.getDimY(); y++) {
						for (int x = 0; x < variableType.getDimX(); x++) {
							printWriter.print("|" + variableType.getValue(y, x) + "|");
						}
						printWriter.println();
					}
				}
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
			e.printStackTrace();
		}
	}

}
