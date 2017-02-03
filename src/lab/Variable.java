package lab;

import java.util.HashMap;

public final class Variable {
    private final String nom;
    private final String nomLab;
    private final String type;
    private static final HashMap<String, String> mapTypeVar = new HashMap<String, String>();

    public Variable(String nom, String nomLab) {
        this.nom = nom;
        this.nomLab = nomLab;
        initMapTypeVar();
        this.type = findType();
    }

    private static void initMapTypeVar() {
        mapTypeVar.put("C", "SCALAIRE");
        mapTypeVar.put("T", "CURVE");
        mapTypeVar.put("M", "MAP");
        mapTypeVar.put("CA", "MATRICE");
    }

    public String getNom() {
        return this.nom;
    }

    public String getNomLab() {
        return nomLab;
    }

    public String getType() {
        return type;
    }

    private String findType() {
        StringBuffer stringbuffer = new StringBuffer(this.nom);
        String lettreType = stringbuffer.substring(nom.length() - 2, nom.length());
        lettreType = lettreType.replace("_", "");

        if (mapTypeVar.get(lettreType) != null) {
            return mapTypeVar.get(lettreType);
        }
        return "INCONNU";
    }

    @Override
    public String toString() {
        return nom;
    }

    @Override
    public boolean equals(Object obj) {
        return nom.equals(obj.toString());
    }

}
