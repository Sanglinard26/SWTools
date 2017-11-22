package tools;

import java.io.File;

public final class Utilitaire {

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String xml = "xml";
    public final static String lab = "lab";
    public final static String a2l = "a2l";
    public final static String dcm = "dcm";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();

        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        } else {
            ext = ""; // Fix le NPE si un fichier n'a pas d'extension
        }
        return ext;
    }

    public static String cutNumber(String number) {
        return number.indexOf(".") < 0 ? number : number.replaceAll("0*$", "").replaceAll("\\.$", "");
    } // Fin methode

    public static Boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
