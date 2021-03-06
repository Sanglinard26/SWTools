package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import gui.SWToolsMain;
import utils.Preference;
import utils.Utilitaire;

public final class Lab {

	private String name = "";
	private final Set<Variable> listVariable = new HashSet<Variable>();

	public Lab() {
	}

	public Lab(Set<Variable> list) {
		this.listVariable.addAll(list);
	}

	public Lab(File file) {

		switch (Utilitaire.getExtension(file)) {
		case Utilitaire.LAB:
			parseFromLab(file);
			break;
		case Utilitaire.XML:
			parseFromXml(file);
			break;
		case Utilitaire.DCM:
			parseFromDcm(file);
			break;
		default:
			break;
		}
	}

	private final void parseFromLab(File file) {

		this.name = Utilitaire.getFileNameWithoutExtension(file);

		try (final BufferedReader buf = new BufferedReader(new FileReader(file))) {

			String line;

			while ((line = buf.readLine()) != null) {
				if (!"[Label]".equals(line) && !line.isEmpty()) {
					listVariable.add(new Variable(line, this.name));
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private final void parseFromXml(File file) {

		final XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLEventReader xmler = null;

		try {

			this.name = Utilitaire.getFileNameWithoutExtension(file);

			xmler = xmlif.createXMLEventReader(new FileReader(file));
			XMLEvent event;
			StringBuilder shortName = new StringBuilder();
			// String category = null;

			while (xmler.hasNext()) {

				event = xmler.nextEvent();

				switch (event.toString()) {

				case "<SW-INSTANCE>":

					while (!event.toString().equals("</SW-INSTANCE>")) {

						if (event.isStartElement()) {

							switch (event.asStartElement().getName().toString()) {
							case "SHORT-NAME":

								shortName.setLength(0);

								do {
									event = xmler.nextEvent();
									if (event.isCharacters()) {
										shortName.append(event.asCharacters().getData());
									}
								} while (!event.toString().equals("</SHORT-NAME>"));

								break;

							default:
								break;
							}
						}
						event = xmler.nextEvent();
					}

					listVariable.add(new Variable(shortName.toString(), this.name));

					break;

				default:
					break;
				}

			}
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

	private final void parseFromDcm(File file) {

		// Mot cle pour les variables
		final String PARAMETER = "FESTWERT";
		final String MATRIX = "FESTWERTEBLOCK";
		final String LINE = "KENNLINIE";
		final String MAP = "KENNFELD";
		final String FIXED_LINE = "FESTKENNLINIE";
		final String FIXED_MAP = "FESTKENNFELD";
		final String GROUP_LINE = "GRUPPENKENNLINIE";
		final String GROUP_MAP = "GRUPPENKENNFELD";
		final String DISTRIBUTION = "STUETZSTELLENVERTEILUNG";
		final String TEXTSTRING = "TEXTSTRING";

		this.name = Utilitaire.getFileNameWithoutExtension(file);

		try {
			final BufferedReader buf = new BufferedReader(new FileReader(file));
			String line;
			String[] spaceSplitLine;

			while ((line = buf.readLine()) != null) {

				spaceSplitLine = line.split(" ");

				switch (spaceSplitLine[0]) {
				case PARAMETER:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case MATRIX:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case LINE:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case MAP:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case FIXED_LINE:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case FIXED_MAP:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case GROUP_LINE:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case GROUP_MAP:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case DISTRIBUTION:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				case TEXTSTRING:
					listVariable.add(new Variable(spaceSplitLine[1], this.name));
					break;
				default:
					break;
				}
			}
			buf.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Lab) {
			return this.name.equals(((Lab) obj).getName());
		}
		return false;
	}

	public final String getName() {
		return name;
	}

	public final Set<Variable> getListVariable() {
		return listVariable;
	}

	public static final Lab compilLab(List<Lab> arrayLab) {
		final Lab multiLab = new Lab();
		final Set<Variable> listVarMultiLab = multiLab.getListVariable();
		for (Lab lab : arrayLab) {
			multiLab.name = multiLab.name + ";" + lab.getName();
			listVarMultiLab.addAll(lab.getListVariable());
		}
		return multiLab;
	}

	// Donne les labels qu'il y a en plus
	public final List<Variable> getDiffLab(Lab lab) {
		final Set<Variable> diffLab = new HashSet<Variable>(this.listVariable);

		for (Variable var : lab.getListVariable()) {
			diffLab.remove(var);
		}

		return new ArrayList<>(diffLab);
	}

	public final void write(File file) {
		try (PrintWriter pw = new PrintWriter(file)) {
			final List<Variable> listVariable = new ArrayList<>(this.listVariable);

			Collections.sort(listVariable);

			pw.println("[Label]");
			for (Variable var : listVariable) {
				pw.println(var.getNom());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static final boolean rapportToHtml(Lab ref, Lab work) {

		try {
			final List<Variable> labelSup = work.getDiffLab(ref);
			final List<Variable> labelDisp = ref.getDiffLab(work);

			Collections.sort(labelSup);
			Collections.sort(labelDisp);

			final Date date = new Date();
			final SimpleDateFormat formater = new SimpleDateFormat("yyMMdd");

			final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_RESULT_LAB));
			fileChooser.setDialogTitle("Enregistement du rapport");
			fileChooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Fichier Html (*.html)";
				}

				@Override
				public boolean accept(File f) {
					return Utilitaire.getExtension(f).equals("html");
				}
			});
			fileChooser.setSelectedFile(new File(formater.format(date) + "_SWTools_ComparaisonLab.html"));
			final int rep = fileChooser.showSaveDialog(null);

			if (rep == JFileChooser.APPROVE_OPTION) {
				final File rapport = fileChooser.getSelectedFile();

				final PrintWriter printWriter = new PrintWriter(rapport);

				printWriter.println("<!DOCTYPE html>");
				printWriter.println("<html>");
				printWriter.println("<head>");
				printWriter.println("<title>SWTools - Export Html</title>");
				printWriter.println("<meta charset=utf-8/>");
				printWriter.println("</head>");
				printWriter.println("<body>");

				printWriter.println("<h1 align=center>" + "RAPPORT DE COMPARAISON DE LAB" + "</h1>");

				String[] splitRef = ref.getName().split(";");
				String[] splitWork = work.getName().split(";");

				int nbRef = splitRef.length;
				int nbWork = splitWork.length;
				int nbMaxLab = Math.max(nbRef, nbWork);

				printWriter.println("<table align=center border cellpadding=5>");

				printWriter.println("<tr>"); // Debut d'une ligne
				printWriter.println("<th align=center>" + "Lab de reference" + "</th>");
				printWriter.println("<th align=center>" + "Lab de travail" + "</th>");
				printWriter.println("</tr>"); // Fin d'une ligne

				for (int i = 0; i < nbMaxLab; i++) {
					printWriter.println("<tr>"); // Debut d'une ligne

					if (i < nbRef) {
						printWriter.println("<td align=center>" + splitRef[i] + "</td>");
					} else {
						printWriter.println("<td align=center>" + "" + "</td>");
					}

					if (i < nbWork) {
						printWriter.println("<td align=center>" + splitWork[i] + "</td>");
					} else {
						printWriter.println("<td align=center>" + "" + "</td>");
					}

					printWriter.println("</tr>"); // Fin d'une ligne
				}

				printWriter.println("</table><br>");

				printWriter.println("<hr align=center size=1 width=50%><br>");

				printWriter.println("<table align=center border cellpadding=5>");

				printWriter.println("<tr>"); // Debut d'une ligne
				printWriter.println("<th align=center>" + "Label(s) disparu(s) (" + labelDisp.size() + ")" + "</th>");
				printWriter.println("<th align=center>" + "Label(s) supplementaire(s) (" + labelSup.size() + ")" + "</th>");
				printWriter.println("</tr>"); // Fin d'une ligne

				int nbLabelDisp = labelDisp.size();
				int nbLabelSup = labelSup.size();
				int nbMaxLabel = Math.max(nbLabelDisp, nbLabelSup);

				for (int i = 0; i < nbMaxLabel; i++) {
					printWriter.println("<tr>"); // Debut d'une ligne

					if (i < nbLabelDisp) {
						printWriter.println(
								"<td align=center>" + labelDisp.get(i).getNom() + " dans " + "'" + labelDisp.get(i).getNomLab() + "'" + "</td>");
					} else {
						printWriter.println("<td align=center>" + "" + "</td>");
					}

					if (i < nbLabelSup) {
						printWriter.println(
								"<td align=center>" + labelSup.get(i).getNom() + " dans " + "'" + labelSup.get(i).getNomLab() + "'" + "</td>");
					} else {
						printWriter.println("<td align=center>" + "" + "</td>");
					}

					printWriter.println("</tr>"); // Fin d'une ligne
				}

				printWriter.println("</table>");

				printWriter.println("<p align=center><font color=black>" + "Fichier cree par SWTools, " + date.toString() + "</font></p>");

				printWriter.println("</body>");
				printWriter.println("</html>");

				printWriter.close();

				JOptionPane.showMessageDialog(null, "Export termine !", null, JOptionPane.INFORMATION_MESSAGE);

				return true;
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return false;

	}
}
