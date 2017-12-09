package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;

public final class Utilitaire {

    public final static String xml = "xml";
    public final static String lab = "lab";
    public final static String a2l = "a2l";
    public final static String dcm = "dcm";
    public final static String m = "m";

    private static final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";

    /*
     * Get the extension of a file.
     */
    public static final String getExtension(File f) {
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

    public static final Boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    public static final void createDtd(String pathFolder) {
        File dtd = new File(pathFolder + "/" + DTD);
        dtd.deleteOnExit();
        if (!dtd.exists()) {
            final InputStream myDtd = Utilitaire.class.getResourceAsStream("/" + DTD);
            try {
                final OutputStream out = new FileOutputStream(pathFolder + "/" + DTD);
                final byte[] buffer = new byte[1024];
                int len = myDtd.read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    len = myDtd.read(buffer);
                }
                myDtd.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    
    public static final String getFolder(String title, String defautPath) {
        final JFileChooser fileChooser = new JFileChooser(defautPath);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        final int reponse = fileChooser.showDialog(null, "Select");
        if (reponse == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        }
        return defautPath;
    }
}
