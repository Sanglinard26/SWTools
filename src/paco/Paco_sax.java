package paco;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class Paco_sax {

	public Paco_sax(final File file) {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(new File(file.toURI()), new MyXmlHandler());
		} catch (ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

final class MyXmlHandler extends DefaultHandler
{
	//Nous nous servirons de cette variable plus tard
	private String node = null;

	//début du parsing
	public void startDocument() throws SAXException {
		System.out.println("Début du parsing");
	}

	/**
	 * Redéfinition de la méthode pour intercepter les événements
	 */
	public void startElement(String namespaceURI, String lname,
			String qname, Attributes attrs) throws SAXException {

		
		System.out.println("---------------------------------------------");
		
		
		//cette variable contient le nom du nœud qui a créé l'événement
		System.out.println("namespaceURI = " + namespaceURI + " / qname = " + qname + " / lname = " + lname);
		node = qname;

		//Cette dernière contient la liste des attributs du nœud
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				//nous récupérons le nom de l'attribut
				String aname = attrs.getLocalName(i);
				//Et nous affichons sa valeur
				System.out.println("Attribut " + aname + " valeur : " + attrs.getValue(i));
			}
		}
	}   

	/**
	 * permet de récupérer la valeur d'un nœud
	 */  
	public void characters(char[] data, int start, int end){   
		System.out.println("***********************************************");
		//La variable data contient tout notre fichier.
		//Pour récupérer la valeur, nous devons nous servir des limites en paramètre
		//"start" correspond à l'indice où commence la valeur recherchée
		//"end" correspond à la longueur de la chaîne
		String str = new String(data, start, end);
		System.out.println("Donnée du nœud " + node + " : " + str);

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException{
		System.out.println("Fin de l'élément " + qName);       
	}

	//fin du parsing
	public void endDocument() throws SAXException {
		System.out.println("Fin du parsing");
	}   
}
