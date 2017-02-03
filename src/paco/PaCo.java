package paco;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PaCo {
	
	public static final String _C = "VALUE";
	public static final String _T = "CURVE_INDIVIDUAL";
	public static final String _M = "MAP_INDIVIDUAL";
	public static final String _A = "AXIS_VALUES";
	public static final String _T_GROUPED = "CURVE_GROUPED";
	public static final String _M_GROUPED = "MAP_GROUPED";
	
	Document document = null;
	DocumentBuilderFactory factory = null;
	
	public String[] listLabel=null;
	public Label[] variable=null;
	public ArrayList<Scalaire> scalaires = new ArrayList<>();
	public ArrayList<Curve> curves = new ArrayList<>();
	
	public PaCo(String path) {
		
		try
		{
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(path);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (document!=null)
		{
			Element racine = document.getDocumentElement();
			
			NodeList enfantRacine = racine.getChildNodes();
			Node enfant;
			
			System.out.println("Nom du PaCo : " + enfantRacine.item(0).getTextContent() + "\n");
			
			NodeList listSwInstance = racine.getElementsByTagName("SW-INSTANCE");
			Node shortName, category, swFeatureRef;
			NodeList swCsEntry,swAxisCont;
			int nbLabel = listSwInstance.getLength();
			Element label;
			
			System.out.println("Nombre de label(s) : " + nbLabel + "\n");
			
			listLabel = new String[nbLabel];
			variable = new Label[nbLabel];
			
			for (int i=0; i<nbLabel; i++)
			{
				label = (Element) listSwInstance.item(i);
				shortName = label.getElementsByTagName("SHORT-NAME").item(0);
				category = label.getElementsByTagName("CATEGORY").item(0);
				swFeatureRef = label.getElementsByTagName("SW-FEATURE-REF").item(0);
				swAxisCont = label.getElementsByTagName("SW-AXIS-CONT");
				swCsEntry = label.getElementsByTagName("SW-CS-ENTRY");
				
//				variable[i] = new Label(
//						shortName.getTextContent(), 
//						category.getTextContent(), 
//						swFeatureRef.getTextContent(), 
//						ReadEntryTab(swCsEntry));
				
				listLabel[i]=shortName.getTextContent();
				
				System.out.println("*****");
				
				System.out.println(shortName.getTextContent() + " / "
						+ category.getTextContent() + " / "
						+ swFeatureRef.getTextContent() + "\n"
						+ "Nombre d'axe : " + swAxisCont.getLength() + "\n"
						+ "Nombre de commentaire : " + swCsEntry.getLength());
				
				switch (category.getTextContent()) {
				case PaCo._C:
					
					variable[i] = new Scalaire(
							shortName.getTextContent(), 
							category.getTextContent(), 
							swFeatureRef.getTextContent(), 
							ReadEntryTab(swCsEntry));
					
//					scalaires.add(new Scalaire(
//							shortName.getTextContent(), 
//							category.getTextContent(), 
//							swFeatureRef.getTextContent(), 
//							ReadEntryTab(swCsEntry),ReadValue(swAxisCont)));
					break;
				case PaCo._T:
					
					variable[i] = new Curve(
							shortName.getTextContent(), 
							category.getTextContent(), 
							swFeatureRef.getTextContent(), 
							ReadEntryTab(swCsEntry));
					
//					curves.add(new Curve(
//							shortName.getTextContent(), 
//							category.getTextContent(), 
//							swFeatureRef.getTextContent(), 
//							ReadEntryTab(swCsEntry),ReadCurve(swAxisCont)));
					break;
				case PaCo._T_GROUPED:
					
					variable[i] = new Curve(
							shortName.getTextContent(), 
							category.getTextContent(), 
							swFeatureRef.getTextContent(), 
							ReadEntryTab(swCsEntry));
					
//					curves.add(new Curve(
//							shortName.getTextContent(), 
//							category.getTextContent(), 
//							swFeatureRef.getTextContent(), 
//							ReadEntryTab(swCsEntry),ReadCurve(swAxisCont)));
					break;
				case PaCo._M:
					
					variable[i] = new Map(
							shortName.getTextContent(), 
							category.getTextContent(), 
							swFeatureRef.getTextContent(), 
							ReadEntryTab(swCsEntry));
					
					//ReadMap(swAxisCont);
					break;
				default:
					break;
				}
			}
		}

	}
	
	private HashMap<StringBuffer, StringBuffer> ReadEntry(NodeList swCsEntry)
	{
		HashMap<StringBuffer, StringBuffer> entry = new HashMap<StringBuffer, StringBuffer>();
		StringBuffer titleEntry = new StringBuffer();
		StringBuffer valEntry = new StringBuffer();
		
		Element aEntry;
		Node swCsState, swCsPerformedBy, remark, date;
		
		for (int n=0; n<swCsEntry.getLength(); n++)
		{
			aEntry = (Element) swCsEntry.item(n);
			
			swCsPerformedBy = aEntry.getElementsByTagName("SW-CS-PERFORMED-BY").item(0);
			date = aEntry.getElementsByTagName("DATE").item(0);
			swCsState = aEntry.getElementsByTagName("SW-CS-STATE").item(0);
			remark = aEntry.getElementsByTagName("REMARK").item(0);
			
			titleEntry.append(date.getNodeName() + " : \n");
			titleEntry.append(swCsPerformedBy.getNodeName() + " : \n");
			titleEntry.append(swCsState.getNodeName() + " : \n");
			titleEntry.append(remark.getNodeName() + " : \n");
			
			valEntry.append(date.getTextContent() + "\n");
			valEntry.append(swCsPerformedBy.getTextContent() + "\n");
			valEntry.append(swCsState.getTextContent() + "\n");
			valEntry.append(remark.getTextContent() + "\n");
			
			entry.put(titleEntry,valEntry);
		}
		return entry;
	}
	
	private String[][] ReadEntryTab(NodeList swCsEntry)
	{
		String[][] entry = new String[swCsEntry.getLength()][4];
		
		Element aEntry;
		Node swCsState, swCsPerformedBy, remark, date;
		
		for (int n=0; n<swCsEntry.getLength(); n++)
		{
			aEntry = (Element) swCsEntry.item(n);
			
			swCsPerformedBy = aEntry.getElementsByTagName("SW-CS-PERFORMED-BY").item(0);
			date = aEntry.getElementsByTagName("DATE").item(0);
			swCsState = aEntry.getElementsByTagName("SW-CS-STATE").item(0);
			remark = aEntry.getElementsByTagName("REMARK").item(0);
			
			entry[n][0] = date.getTextContent();
			entry[n][1] = swCsPerformedBy.getTextContent();
			entry[n][2] = swCsState.getTextContent();
			entry[n][3] = remark.getTextContent();
		}
		return entry;
	}
	
	private Object ReadValue(NodeList swAxisCont)
	{
		Element eAxisCont = (Element) swAxisCont.item(0);
		Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
		Node value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName()).item(0);
		System.out.print(value.getTextContent());
		System.out.println("");
		return value.getTextContent();
	}
	
	private Object[][] ReadCurve(NodeList swAxisCont)
	{
		Object val[][] = null;
		
		for (int n=0; n<swAxisCont.getLength(); n++)
		{
			Element eAxisCont = (Element) swAxisCont.item(n);
			Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
			Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
			NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName());
			
			if (val==null) val = new Object[swAxisCont.getLength()][value.getLength()];
			
			switch (indexAxis.getTextContent()) {
			case "1":
				
				for (int b=0; b<value.getLength(); b++)
				{
					val[0][b]=value.item(b).getTextContent();
					System.out.print(value.item(b).getTextContent() + "|");
				}
				System.out.println("");
				break;
			case "0":
				for (int a=0; a<value.getLength(); a++)
				{
					val[1][a]=value.item(a).getTextContent();
					System.out.print(value.item(a).getTextContent() + "|");
				}
				System.out.println("");
				break;
			}
		}
		return val;
	}
	
	private Object[][] ReadMap(NodeList swAxisCont)
	{
		Object val[][] = null;
		
		for (int n=0; n<swAxisCont.getLength(); n++)
		{
			Element eAxisCont = (Element) swAxisCont.item(n);
			Node indexAxis = eAxisCont.getElementsByTagName("SW-AXIS-INDEX").item(0);
			Node swValuesPhys = eAxisCont.getElementsByTagName("SW-VALUES-PHYS").item(0);
			NodeList value = eAxisCont.getElementsByTagName(swValuesPhys.getChildNodes().item(1).getNodeName()); // Tag "V"
			
			if (val==null) val = new Object[swAxisCont.getLength()][value.getLength()];
			
			switch (indexAxis.getTextContent()) {
			case "1": // Axe X
				
				for (int b=0; b<value.getLength(); b++)
				{
					val[0][b]=value.item(b).getTextContent();
					System.out.print(value.item(b).getTextContent() + "|");
				}
				System.out.println("");
				break;
			case "2": // Axe Y
				
				for (int c=0; c<value.getLength(); c++)
				{
					val[0][c]=value.item(c).getTextContent();
					System.out.print(value.item(c).getTextContent() + "|");
				}
				System.out.println("");
				break;
			case "0": // Valeur Z
				
				
				
				for (int a=0; a<value.getLength(); a++)
				{
					val[1][a]=value.item(a).getTextContent();
					System.out.print(value.item(a).getTextContent() + "|");
				}
				System.out.println("");
				break;
			}
		}
		
		return val;
	}

}
