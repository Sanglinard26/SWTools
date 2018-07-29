package lab;

import java.io.BufferedReader;
import java.io.File;
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

import cdf.Cdf;
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
        }
    }

    private final void parseFromLab(File file) {

        this.name = Utilitaire.getFileNameWithoutExtension(file);

        try {
            final BufferedReader buf = new BufferedReader(new FileReader(file));
            String line;

            while ((line = buf.readLine()) != null) {
                if (!line.equals("[Label]") && !line.isEmpty()) {
                    listVariable.add(new Variable(line, this.name));
                }
            }
            buf.close();
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
            String category = null;

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

                            case "CATEGORY":

                                do {
                                    event = xmler.nextEvent();
                                    if (event.isCharacters()) {
                                        category = event.asCharacters().getData();
                                    }

                                } while (!event.isCharacters());

                                break;
                            }
                        }
                        event = xmler.nextEvent();
                    }
                    if (!category.equals(Cdf.AXIS_VALUES)) {
                        listVariable.add(new Variable(shortName.toString(), this.name));
                    }
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
                case TEXTSTRING:
                    listVariable.add(new Variable(spaceSplitLine[1], this.name));
                    break;
                }
            }
            buf.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
            multiLab.name = multiLab.name + " / " + lab.getName();
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

    public static final void ecrireRapport(Lab ref, Lab work) {
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
