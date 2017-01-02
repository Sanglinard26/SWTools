package lab;
import java.util.*;

import javax.swing.JOptionPane;
import java.io.*;
import java.text.*;

public class Lab
{
	private String pathLab;
	private String nameLab;
	private File fileLab;
	private Variable var;
	private ArrayList<Variable> listVariable = new ArrayList<Variable>();
	
	public Lab(){}
	
	public Lab(String pathLab)
	{
		this.pathLab = pathLab;
		this.fileLab = new File(pathLab);
		this.nameLab = fileLab.getName();
		
		try
		{
			String line;
			BufferedReader buf;
			
			buf = new BufferedReader(new FileReader(fileLab));
			while((line=buf.readLine()) !=null)
			{
				if(!line.equals("[Label]"))
				{
					var = new Variable(nameLab, line);
					listVariable.add(var);
				}
			}
			var = null;
			buf.close();
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.nameLab;
	}
	
	public String getPathLab() {return pathLab;}
	
	public String getNameLab() {return nameLab;}
	
	public ArrayList<Variable> getListVariable() {
		return listVariable;
	}
	
	public static Lab compilLab(ArrayList<Lab> arrayLab)
	{
		Lab multiLab = new Lab();
		ArrayList<Variable> listVarMultiLab = multiLab.getListVariable();
		for(Lab lab : arrayLab)
		{
			listVarMultiLab.addAll(lab.getListVariable());
		}
		return multiLab;
	}
	
	public static String[] getTabVarNom(Lab lab) {
		ArrayList<String> tabVarNom = new ArrayList<String>();
		for(Variable v : lab.getListVariable())
		{
			tabVarNom.add(v.getNom());
		}
		return (String[]) tabVarNom.toArray(new String[lab.getListVariable().size()]);
	}
	
	public static String[] getTabVarNom(ArrayList<Variable> list) {
		ArrayList<String> tabVarNom = new ArrayList<String>();
		for(Variable v : list)
		{
			tabVarNom.add(v.getNom());
		}
		return (String[]) tabVarNom.toArray(new String[list.size()]);
	}
	
	public static Variable[] getTabVar(ArrayList<Variable> list) {
		return (Variable[]) list.toArray(new Variable[list.size()]);
	}
	
	//Donne les labels qu'il y a en plus
	public ArrayList<Variable> getDiffLab(Lab lab)
	{
		ArrayList<Variable> diffLab = new ArrayList<Variable>(this.listVariable);
		diffLab.removeAll(lab.getListVariable());
		return diffLab;
	}
	
	public static HashMap<String,Integer> triType(ArrayList<Variable> list)
	{
		Integer nbScalaire = 0, nbCurve = 0, nbMap = 0, nbMatrice = 0, nbInconnu = 0;
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		
		map.put("SCALAIRE",nbScalaire);
		map.put("CURVE",nbCurve);
		map.put("MAP",nbMap);
		map.put("MATRICE",nbMatrice);
		map.put("INCONNU",nbInconnu);
		
		for(Variable v : list)
		{
			switch(v.getType())
			{
				case "SCALAIRE":
					map.put("SCALAIRE",++nbScalaire);
					break;
				case "CURVE":
					map.put("CURVE",++nbCurve);
					break;
				case "MAP":
					map.put("MAP",++nbMap);
					break;
				case "MATRICE":
					map.put("MATRICE",++nbMatrice);
					break;
				default :
					map.put("INCONNU",++nbInconnu);
					break;
			}
		}
		return map;
	}
		
	public static void ecrireRapport(Lab ref, Lab work)
	{
		try
		{
			ArrayList<Variable> labelSup = work.getDiffLab(ref);
			ArrayList<Variable> labelDisp = ref.getDiffLab(work);
			
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			String dateFormatee = df.format(new Date()).replace("/","");
			File rapport = new File("D:/" + dateFormatee + "_rapport.txt");
			
			PrintWriter printWriter = new PrintWriter(rapport);
			
			printWriter.println(" ---------");
			printWriter.println("| Rapport |");
			printWriter.println(" ---------");
			
			printWriter.println("\n" + "Lab de référence : " + ref.getNameLab());
			printWriter.println("Lab de travail : " + work.getNameLab());
			
			if (labelDisp.size()<2)
			{printWriter.println("\n" + "Label disparu (" + labelDisp.size() + ") : ");}
			else
			{printWriter.println("\n" + "Labels disparus (" + labelDisp.size() + ") : ");}
				
			for(Variable label:labelDisp)
			{
				printWriter.println("\t -" + label.getNom());
			}
			
			printWriter.println("\n----------");
			
			if (labelSup.size()<2)
			{printWriter.println("\n" + "Label supplémentaire (" + labelSup.size() + ") : ");}
			else
			{printWriter.println("\n" + "Labels supplémentaires (" + labelSup.size() + ") : ");}
			
			for(Variable label:labelSup)
			{
				printWriter.println("\t -" + label.getNom());
			}
			
			printWriter.println("\n" + " -----");
			printWriter.println("| Fin |");
			printWriter.println(" -----");
			
			JOptionPane.showMessageDialog(
					null,
					"Fichier créé : \n" + rapport.getAbsolutePath());
			
			printWriter.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	}
}

