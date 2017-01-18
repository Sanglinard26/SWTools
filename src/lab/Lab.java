package lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import tools.Preference;

public final class Lab {
    private String pathLab = "";
    private String nameLab = "";
    private File fileLab;
    private Variable var;
    private final ArrayList<Variable> listVariable = new ArrayList<Variable>();

    public Lab() {
    }

    public Lab(String pathLab) {
        this.pathLab = pathLab;
        this.fileLab = new File(pathLab);
        this.nameLab = fileLab.getName();

        try {
            String line;
            BufferedReader buf;

            buf = new BufferedReader(new FileReader(fileLab));
            while ((line = buf.readLine()) != null) {
                if (!line.equals("[Label]")) {
                    var = new Variable(nameLab, line);
                    listVariable.add(var);
                }
            }
            var = null;
            buf.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return this.nameLab;
    }

    @Override
    public boolean equals(Object obj) {
        return this.nameLab.equals(obj.toString());
    }

    public String getPathLab() {
        return pathLab;
    }

    public String getNameLab() {
        return nameLab;
    }

    public ArrayList<Variable> getListVariable() {
        return listVariable;
    }

    public static Lab compilLab(ArrayList<Lab> arrayLab) {
        Lab multiLab = new Lab();
        ArrayList<Variable> listVarMultiLab = multiLab.getListVariable();
        for (Lab lab : arrayLab) {
            multiLab.nameLab = multiLab.nameLab + " / " + lab.getNameLab();
            listVarMultiLab.addAll(lab.getListVariable());
        }
        return multiLab;
    }

    public static String[] getTabVarNom(Lab lab) {
        ArrayList<String> tabVarNom = new ArrayList<String>();
        for (Variable v : lab.getListVariable()) {
            tabVarNom.add(v.getNom());
        }
        return tabVarNom.toArray(new String[lab.getListVariable().size()]);
    }

    public static String[] getTabVarNom(ArrayList<Variable> list) {
        ArrayList<String> tabVarNom = new ArrayList<String>();
        for (Variable v : list) {
            tabVarNom.add(v.getNom());
        }
        return tabVarNom.toArray(new String[list.size()]);
    }

    public static Variable[] getTabVar(ArrayList<Variable> list) {
        return list.toArray(new Variable[list.size()]);
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

            final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            final String dateFormatee = df.format(new Date()).replace("/", "");
            File rapport = new File(Preference.getPreference("pathExportLabResult") + "/" + dateFormatee + "_ComparaisonLab_Rapport.txt");

            PrintWriter printWriter;

            if (rapport.exists()) {

                final int reponse = JOptionPane.showConfirmDialog(null, "Fichier déjà existant, écraser?", "Question", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (reponse == JOptionPane.OK_OPTION) {
                    printWriter = new PrintWriter(rapport);
                } else {
                    rapport = new File(Preference.getPreference("pathExportLabResult") + "/" + dateFormatee + "_ComparaisonLab_Rapport_1.txt");
                    printWriter = new PrintWriter(rapport);
                }
            } else {
                printWriter = new PrintWriter(rapport);
            }

            printWriter.println(" -------------------------------");
            printWriter.println("| RAPPORT DE COMPARAISON DE LAB |");
            printWriter.println(" -------------------------------");

            printWriter.println("\n" + "Lab de référence : " + ref.getNameLab());
            printWriter.println("Lab de travail : " + work.getNameLab());
            printWriter.println("\n" + "Label(s) disparu(s) (" + labelDisp.size() + ") : ");

            for (Variable label : labelDisp) {
                printWriter.println("\t -" + label.getNom());
            }

            printWriter.println("\n----------");
            printWriter.println("\n" + "Label(s) supplémentaire(s) (" + labelSup.size() + ") : ");

            for (Variable label : labelSup) {
                printWriter.println("\t -" + label.getNom());
            }

            printWriter.println("\n" + " -----");
            printWriter.println("| FIN |");
            printWriter.println(" -----");
            printWriter.println("\n" + "\n" + "Fichier crée par SWTools, " + new Date().toString());

            JOptionPane.showMessageDialog(null, "Fichier créé : \n" + rapport.getAbsolutePath());

            printWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
