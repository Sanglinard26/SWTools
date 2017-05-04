/*
 * Creation : 4 mai 2017
 */
package soft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Soft {

    public Soft(final File file) {

        try {
            final InputStream is = new FileInputStream(file.getAbsolutePath());
            final byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            while (len != -1) {
                System.out.write(buffer, 0, len);
                len = is.read(buffer);
            }
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
