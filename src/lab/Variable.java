package lab;
import java.util.*;

public class Variable
{
	private String nameLab;
	private String nom;
	private String type = "";
	private static HashMap<String,String> mapTypeVar = new HashMap<String,String>();
	
	public Variable(String nameLab, String nom)
	{
		this.nameLab = nameLab;
		this.nom=nom;
		initMapTypeVar();
		this.type = findType();
	}
	
	private static void initMapTypeVar()
	{
		mapTypeVar.put("C","SCALAIRE");
		mapTypeVar.put("T","CURVE");
		mapTypeVar.put("M","MAP");
		mapTypeVar.put("CA","MATRICE");
	}
	
	public String getNameLab()
	{
		return this.nameLab;
	}
	
	public String getNom()
	{
		return this.nom;
	}
	
	public String getType()
	{
		return type;
	}
	
	private String findType()
	{
		StringBuffer stringbuffer = new StringBuffer(this.nom);
		String lettreType = stringbuffer.substring(nom.length()-2,nom.length());
		lettreType = lettreType.replace("_","");
		
		if(mapTypeVar.get(lettreType)!=null)
		{
			return mapTypeVar.get(lettreType);
		}else{
			return "INCONNU";
		}
	}

	@Override
	public String toString()
	{
		return nom;
	}

	@Override
	public boolean equals(Object obj)
	{
		return nom.equals(obj.toString());
	}
	
	
	
	
}
