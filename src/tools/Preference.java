/*
 * Creation : 10 janv. 2017
 */
package tools;

import java.util.prefs.Preferences;

public final class Preference {

	private static final String DEF_PATH_LAB = "C:/User";
    private static final String DEF_PATH_RESULT_LAB = "C:/TEMP";
    private static final String DEF_PATH_PACO = "C:/User";
    public static final String KEY_ADD_LAB = "pathLab";
    public static final String KEY_RESULT_LAB = "pathExportLabResult";
    public static final String KEY_OPEN_PACO = "pathPaco";
    

    private static final Preferences preferences = Preferences.userRoot().node("swtools/lab");

    public static String getPreference(String key) {
        String defValue;
        switch (key) {
        case KEY_ADD_LAB:
            defValue = DEF_PATH_LAB;
            break;
        case KEY_RESULT_LAB:
            defValue = DEF_PATH_RESULT_LAB;
            break;
        case KEY_OPEN_PACO:
            defValue = DEF_PATH_PACO;
            break;
        default:
            defValue = "C:/";
            break;
        }
        return preferences.get(key, defValue);
    }

    public static void setPreference(String key, String value) {
        preferences.put(key, value);
    }

}
