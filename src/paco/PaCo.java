package paco;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PaCo extends Observable {

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
	private ArrayList<Label> listLabel = new ArrayList<Label>();

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
			String shortName, category, swFeatureRef;
			NodeList swCsEntry, swAxisCont;
			nbLabel = listSwInstance.getLength();
			Element label;

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
				swCsEntry = label.getElementsByTagName("SW-CS-ENTRY");

				// NPE sur swFeatureRef car non présent pour les axes "_A"

				switch (category) {
				case PaCo._C:
					listLabel.add(new Scalaire(shortName, category, swFeatureRef, ReadEntry(swCsEntry), ReadValue(swAxisCont)));
					break;
				case PaCo._T:
					listLabel.add(new Curve(shortName, category, swFeatureRef, ReadEntry(swCsEntry), ReadCurve(swAxisCont)));
					break;
				case PaCo._A:
					// Non implemente
					break;
				case PaCo._T_GROUPED:
					listLabel.add(new Curve(shortName, category, swFeatureRef, ReadEntry(swCsEntry), ReadCurve(swAxisCont)));
					break;
				case PaCo._T_CA:
					// Non implemente
					break;
				case PaCo._M:
					listLabel.add(new Map(shortName, category, swFeatureRef, ReadEntry(swCsEntry), ReadMap(swAxisCont)));
					break;
				case PaCo._M_GROUPED:
					listLabel.add(new Map(shortName, category, swFeatureRef, ReadEntry(swCsEntry), ReadMap(swAxisCont)));
					break;
				}

				this.setChanged();
				this.notifyObservers(i + 1);
			}
		}

	}

	public Boolean checkName() {
		return this.file.getName().substring(0, this.file.getName().length()-4).equals(this.name);
	}

	public String getName() {
		return this.name;
	}

	public int getNbLabel() {
		return this.nbLabel;
	}

	public ArrayList<Label> getListLabel() {
		return this.listLabel;
	}

	private String[][] ReadEntry(NodeList swCsEntry) {
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

	private Object ReadValue(NodeList swAxisCont) {
		return swAxisCont.item(0).getLastChild().getTextContent();
	}

	private Object[][] ReadCurve(NodeList swAxisCont) {

		Object val[][] = null;

		for (int n = 0; n < swAxisCont.getLength(); n++) {
			Element eAxisCont = (Element) swAxisCont.item(n);
			Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
			Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
			NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());

			if (val == null)
				val = new Object[swAxisCont.getLength()][value.getLength()];

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

	private Object[][] ReadMap(NodeList swAxisCont) {
		// Premiere dimension = Axe Y car nombre de ligne
		// Deuxieme dimension = Axe X car nombre de colonne
		Object val[][] = new Object
				[((Element)swAxisCont.item(1)).getLastChild().getChildNodes().getLength()+1]
						[((Element)swAxisCont.item(0)).getLastChild().getChildNodes().getLength()+1];

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
						val[0][x+1] = nodeListV.item(x).getFirstChild().getTextContent();
						break;

					default:
						val[0][x+1] = nodeListV.item(x).getTextContent();
						break;
					}
				}
				break;

			case "2": // Axe Y

				for (int y = 0; y < nodeListV.getLength(); y++) {
					switch (nodeListV.item(y).getNodeName()) {
					case "VT":
						val[y+1][0] = nodeListV.item(y).getFirstChild().getTextContent();
						break;
					default:
						val[y+1][0] = nodeListV.item(y).getTextContent();
						break;
					}
				}
				break;
			case "0": // Valeur Z

				NodeList vg =((Element)swValuesPhys).getElementsByTagName("VG");

				for(int nVG = 1; nVG<vg.getLength()+1; nVG++)
				{
					NodeList nodeV = ((Element) vg.item(nVG-1)).getElementsByTagName("V");


					for(int nV = 1; nV<nodeV.getLength()+1; nV++)
					{
						val[nVG][nV] = nodeV.item(nV-1).getTextContent();
					}
				}
				break;
			}
		}

		return val;
	}

}
