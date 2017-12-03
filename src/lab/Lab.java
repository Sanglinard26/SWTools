package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tools.Preference;

public final class Lab {
	private String path = "";
	private String name = "";
	private File fileLab;
	private Variable var;
	private final ArrayList<Variable> listVariable = new ArrayList<Variable>();

	// PaCo
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public Lab() {
	}

	public Lab(ArrayList<Variable> list) {
		this.listVariable.addAll(list);
	}

	public Lab(String path) {
		this.path = path;
		this.fileLab = new File(path);
		this.name = fileLab.getName();

		try {
			String line;
			BufferedReader buf;

			buf = new BufferedReader(new FileReader(fileLab));
			while ((line = buf.readLine()) != null) {
				if (!line.equals("[Label]")) {
					var = new Variable(line, fileLab.getName());
					listVariable.add(var);
				}
			}
			buf.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public Lab(final File file)
	{
		DocumentBuilder builder;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(file.toURI())); // Permet de virer l'exception <java.net.malformedurlexception unknown
			// protocol c>
			if (document.getDoctype() == null) {
				JOptionPane.showMessageDialog(null, "Format de PaCo non valide !" + "\nNom : " + this.name, "ERREUR", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
		} catch (

				IOException e) {
			e.printStackTrace();
		}

		if (document != null) {

			this.fileLab = file;
			this.name = fileLab.getName();

			final Element racine = document.getDocumentElement();
			final NodeList listSwInstance = racine.getElementsByTagName("SW-INSTANCE");
			final int nbLabel = listSwInstance.getLength();
			Element label;

			for (int i = 0; i < nbLabel; i++) {

				label = (Element) listSwInstance.item(i);
				if (!label.getElementsByTagName("CATEGORY").item(0).getTextContent().equals("AXIS_VALUES"))
				{
					var = new Variable(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(), fileLab.getName());
					listVariable.add(var);
				}
			}
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		return this.name.equals(obj.toString());
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Variable> getListVariable() {
		return listVariable;
	}

	public static Lab compilLab(ArrayList<Lab> arrayLab) {
		Lab multiLab = new Lab();
		ArrayList<Variable> listVarMultiLab = multiLab.getListVariable();
		for (Lab lab : arrayLab) {
			multiLab.name = multiLab.name + " / " + lab.getName();
			listVarMultiLab.addAll(lab.getListVariable());
		}
		return multiLab;
	}

	// Donne les labels qu'il y a en plus
	public ArrayList<Variable> getDiffLab(Lab lab) {
		ArrayList<Variable> diffLab = new ArrayList<Variable>(this.listVariable);
		diffLab.removeAll(lab.getListVariable());
		return diffLab;
	}

	public static void ecrireRapport(Lab ref, Lab work) {
		try {
			final ArrayList<Variable> labelSup = work.getDiffLab(ref);
			final ArrayList<Variable> labelDisp = ref.getDiffLab(work);

			JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_RESULT_LAB));
			fileChooser.setDialogTitle("Enregistement du rapport");
			fileChooser.setSelectedFile(new File(".txt"));
			int rep = fileChooser.showSaveDialog(null);

			if (rep == JFileChooser.APPROVE_OPTION) {
				File rapport = fileChooser.getSelectedFile();

				PrintWriter printWriter = new PrintWriter(rapport);

				printWriter.println(" -------------------------------");
				printWriter.println("| RAPPORT DE COMPARAISON DE LAB |");
				printWriter.println(" -------------------------------");

				printWriter.println("\n" + "Lab de reference : " + ref.getName());
				printWriter.println("Lab de travail : " + work.getName());
				printWriter.println("\n" + "Label(s) disparu(s) (" + labelDisp.size() + ") : ");

				for (Variable label : labelDisp) {
					printWriter.println("\t -" + label.getNom() + " =======> " + "<< " + label.getNomLab() + " >>");
				}

				printWriter.println("\n----------");
				printWriter.println("\n" + "Label(s) supplementaire(s) (" + labelSup.size() + ") : ");

				for (Variable label : labelSup) {
					printWriter.println("\t -" + label.getNom() + " =======> " + "<< " + label.getNomLab() + " >>");
				}

				printWriter.println("\n" + " -----");
				printWriter.println("| FIN |");
				printWriter.println(" -----");
				printWriter.println("\n" + "\n" + "Fichier cree par SWTools, " + new Date().toString());

				printWriter.close();

			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
