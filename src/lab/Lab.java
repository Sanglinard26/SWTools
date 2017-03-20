package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;

import tools.Preference;

public final class Lab {
    private String path = "";
    private String name = "";
    private File fileLab;
    private Variable var;
    private final ArrayList<Variable> listVariable = new ArrayList<Variable>();

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
