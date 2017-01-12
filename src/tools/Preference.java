/*
 * Creation : 10 janv. 2017
 */
package tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Preference {

    private static final Properties properties = new Properties();

    public static String getPreference(String key) {

        try {
            InputStream inputStream = new FileInputStream("resources/preferences.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }

}
