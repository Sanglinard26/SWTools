package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tools.Preference;
import tools.Utilitaire;

public final class Lab {

    private String path = "";
    private String name = "";
    private File fileLab;
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

            final BufferedReader buf = new BufferedReader(new FileReader(fileLab));
            String line;

            while ((line = buf.readLine()) != null) {
                if (!line.equals("[Label]")) {
                    listVariable.add(new Variable(line.toString(), fileLab.getName()));
                }
            }
            buf.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Lab(final File file) {
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
                if (!label.getElementsByTagName("CATEGORY").item(0).getTextContent().equals("AXIS_VALUES")) {
                    listVariable.add(new Variable(label.getElementsByTagName("SHORT-NAME").item(0).getTextContent(), fileLab.getName()));
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

    public final String getPath() {
        return path;
    }

    public final String getName() {
        return name;
    }

    public final ArrayList<Variable> getListVariable() {
        return listVariable;
    }

    public static final Lab compilLab(ArrayList<Lab> arrayLab) {
        final Lab multiLab = new Lab();
        final ArrayList<Variable> listVarMultiLab = multiLab.getListVariable();
        for (Lab lab : arrayLab) {
            multiLab.name = multiLab.name + " / " + lab.getName();
            listVarMultiLab.addAll(lab.getListVariable());
        }
        return multiLab;
    }

    // Donne les labels qu'il y a en plus
    public final ArrayList<Variable> getDiffLab(Lab lab) {
        final ArrayList<Variable> diffLab = new ArrayList<Variable>(this.listVariable);
        diffLab.removeAll(lab.getListVariable());
        return diffLab;
    }

    public static final void ecrireRapport(Lab ref, Lab work) {
        try {
            final ArrayList<Variable> labelSup = work.getDiffLab(ref);
            final ArrayList<Variable> labelDisp = ref.getDiffLab(work);
            
            final Date date = new Date();
            final SimpleDateFormat formater = new SimpleDateFormat("yyMMdd");

            final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_RESULT_LAB));
            fileChooser.setDialogTitle("Enregistement du rapport");
            fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "Fichier texte (*.txt)";
				}
				
				@Override
				public boolean accept(File f) {
					return Utilitaire.getExtension(f).equals("txt");
				}
			});
            fileChooser.setSelectedFile(new File(formater.format(date) + "_SWTools_ComparaisonLab.txt"));
            final int rep = fileChooser.showSaveDialog(null);

            if (rep == JFileChooser.APPROVE_OPTION) {
                final File rapport = fileChooser.getSelectedFile();

                final PrintWriter printWriter = new PrintWriter(rapport);

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
                printWriter.println("\n" + "\n" + "Fichier cree par SWTools, " + date.toString());

                printWriter.close();
                
                JOptionPane.showMessageDialog(null, "Export termine !", null, JOptionPane.INFORMATION_MESSAGE);

            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
