/*
 * Creation : 5 mai 2017
 */
package soft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings("unused")
public final class A2LParser {

    // Top-Level Keyword
    private static final String ASAP2_VERSION = "ASAP2_VERSION";
    private static final String A2ML_VERSION = "A2ML_VERSION";
    private static final String HEADER = "HEADER";
    private static final String MODULE = "MODULE";
    private static final String PROJECT = "PROJECT";

    // Primary Keyword
    private static final String CHARACTERISTIC = "CHARACTERISTIC";
    private static final String COMPU_METHOD = "COMPU_METHOD";
    private static final String MEASUREMENT = "MEASUREMENT";

    // Secondary Keyword

    // Datatype

    // Parser tag
    private static final String BEGIN_TAG = "/begin";
    private static final String END_TAG = "/end";
    private static final String WHITESPACE = "\\s+";
    private static final String IN_COMMENT = "/*";
    private static final String OUT_COMMENT = "*/";

    public static final void parse(final File file) {

        try {

            BufferedReader buf = new BufferedReader(new FileReader(file));
            String line;

            long tmp = System.currentTimeMillis();

            while ((line = buf.readLine()) != null) {

                if (line.contains(ASAP2_VERSION)) {
                    System.out.println("Version d'ASAP : " + line.split(WHITESPACE, 2)[1].replaceFirst(" ", "."));
                }

                if (line.contains(BEGIN_TAG)) {
                    System.out.println(line);
                    while (!line.contains(END_TAG)) {
                        line = buf.readLine();
                        System.out.println(line);
                    }

                }

            }

            System.out.println("\n" + (System.currentTimeMillis() - tmp));

            buf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
