package lab;

import java.util.HashMap;

public final class Variable {

    private final String nom;
    private final String nomLab;
    private final String type;
    private static final HashMap<String, String> mapTypeVar = new HashMap<String, String>(7) {
        private static final long serialVersionUID = 1L;
        {
            put("C", "SCALAIRE");
            put("T", "CURVE");
            put("CUR", "CURVE");
            put("M", "MAP");
            put("MAP", "MAP");
            put("GMAP", "MAP");
            put("CA", "VALUEBLOCK");
        }
    };

    public Variable(String nom, String nomLab) {
        this.nom = nom;
        this.nomLab = nomLab;
        this.type = findType();
    }

    public final String getNom() {
        return this.nom;
    }

    public final String getNomLab() {
        return nomLab;
    }

    public final String getType() {
        return type;
    }

    private final String findType() {

        final StringBuffer stringbuffer = new StringBuffer(this.nom);
        final int idxUnderscore = stringbuffer.lastIndexOf("_");

        if (idxUnderscore > -1) {
            final String lettreType = stringbuffer.substring(idxUnderscore + 1, nom.length());
            final String type = mapTypeVar.get(lettreType);

            if (type != null) {
                return type;
            }
            return "INCONNU";
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
