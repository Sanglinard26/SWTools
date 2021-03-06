/*
 * Creation : 10 janv. 2017
 */
package utils;

import java.util.prefs.Preferences;

public abstract class Preference {

    private static final String DEF_PATH_LAB = "C:/User";
    private static final String DEF_PATH_RESULT_LAB = "C:/TEMP";
    private static final String DEF_PATH_CDF = "C:/User";
    private static final String DEF_ETAT_COLOR_MAP = "false";
    private static final String DEF_PATH_FOLDER_DB = "C:/User/SWTools/Database";
    private static final String DEF_LF = "Windows";
    private static final String DEF_XML_PARSEUR = "DOM";
    private static final String DEF_LANGUAGE = "en";

    public static final String KEY_ADD_LAB = "pathLab";
    public static final String KEY_RESULT_LAB = "pathExportLabResult";
    public static final String KEY_OPEN_CDF = "pathCdf";
    public static final String KEY_ETAT_COLOR_MAP = "flagColorMap";
    public static final String KEY_PATH_FOLDER_DB = "pathFolderDb";
    public static final String KEY_NOM_LF = "nomLF";
    public static final String KEY_XML_PARSEUR = "xmlParseur";
    public static final String KEY_LANGUAGE = "language";

    private static final Preferences preferences = Preferences.userRoot().node("swtools");

    public static final String getPreference(String key) {
        String defValue;
        switch (key) {
        case KEY_ADD_LAB:
            defValue = DEF_PATH_LAB;
            break;
        case KEY_RESULT_LAB:
            defValue = DEF_PATH_RESULT_LAB;
            break;
        case KEY_OPEN_CDF:
            defValue = DEF_PATH_CDF;
            break;
        case KEY_ETAT_COLOR_MAP:
            defValue = DEF_ETAT_COLOR_MAP;
            break;
        case KEY_PATH_FOLDER_DB:
            defValue = DEF_PATH_FOLDER_DB;
            break;
        case KEY_NOM_LF:
            defValue = DEF_LF;
            break;
        case KEY_XML_PARSEUR:
            defValue = DEF_XML_PARSEUR;
            break;
        case KEY_LANGUAGE:
            defValue = DEF_LANGUAGE;
            break;
        default:
            defValue = "";
            break;
        }
        return preferences.get(key, defValue);
    }

    public static final void setPreference(String key, String value) {
        preferences.put(key, value);
    }

}
