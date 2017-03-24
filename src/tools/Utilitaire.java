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

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    // public static String cutNumber(String number) {
    //
    // try {
    // if (!(number.lastIndexOf(".") < 0)) {
    // String txtDec = number.substring(number.lastIndexOf(".") + 1);
    //
    // if (Integer.valueOf(txtDec) == 0) {
    // return String.valueOf(Double.valueOf(number).intValue());
    // }
    // int i = number.length() - 1;
    // while (number.charAt(i) == '0') {
    // i -= 1;
    // }
    // return number.substring(0, i + 1);
    // }
    // } catch (Exception e) {
    // return number;
    // }
    // return number;
    // } // Fin methode

    public static String cutNumber(String number) {

        try {
            if (!(number.lastIndexOf(".") < 0)) {
                final String txtDec = number.substring(number.lastIndexOf(".") + 1);

                if (Integer.parseInt(txtDec) == 0) {
                    return String.valueOf(Double.valueOf(number).intValue());
                }
                int i = number.length() - 1;
                while (number.charAt(i) == '0') {
                    i -= 1;
                }
                return number.substring(0, i + 1);
            }
        } catch (Exception e) {
            return number;
        }
        return number;
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
