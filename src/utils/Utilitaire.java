package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.swing.JFileChooser;

public abstract class Utilitaire {

    public final static String XML = "xml";
    public final static String LAB = "lab";
    public final static String A2L = "a2l";
    public final static String DCM = "dcm";
    public final static String M = "m";

    /*
     * Get the extension of a file.
     */
    public static final String getExtension(File f) {

        final String s = f.getName();

        final int i = s.lastIndexOf('.');

        return (i > 0 && i < s.length() - 1) ? s.substring(i + 1).toLowerCase() : "";
    }

    public static final String getFileNameWithoutExtension(File f) {

        final String fileNameWithExtension = f.getName();

        final int i = fileNameWithExtension.lastIndexOf('.');

        return (i > 0 && i < fileNameWithExtension.length() - 1) ? fileNameWithExtension.substring(0, i) : "";
    }

    public static final String cutNumber(String number) {
        return number.indexOf(".") < 0 ? number : number.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    public static final boolean isNumber(String s) {

        final String REGEX_NUMBER_SI = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
        // final String REGEX_NUMBER = "[+-]?\\d+(\\.\\d+)?";

        return s.matches(REGEX_NUMBER_SI);
    }

    public static final String formatStringNumber(double number) {

        final DecimalFormat format = new DecimalFormat();

        if (Math.floor(number) == number) {

            format.setMaximumFractionDigits(0);

        } else {

            String text = Double.toString(number);
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;

            if (decimalPlaces > 3) {

                return String.format("%1.3g", number).toLowerCase();
            }

            format.setMaximumFractionDigits(3);
        }

        return format.format(number);
    }

    public static final void createDtd(String pathFolder) {

        final String DTD = "msrsw_v222_lai_iai_normalized.xml.dtd";
        final File dtd = new File(pathFolder + "/" + DTD);

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
