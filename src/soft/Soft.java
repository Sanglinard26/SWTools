/*
 * Creation : 4 mai 2017
 */
package soft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public final class Soft {
	
	private static final String SYSTEM_CONSTANT = "SYSTEM_CONSTANT";
	private static final String BEGIN_CHARACTERISTIC = "/begin CHARACTERISTIC";
	private static final String END_CHARACTERISTIC = "/end CHARACTERISTIC";
	private static final String BEGIN_MEASUREMENT = "/begin MEASUREMENT";
	private static final String END_MEASUREMENT = "/end MEASUREMENT";
	private static final String BEGIN_FUNCTION = "/begin FUNCTION";
	private static final String END_FUNCTION = "/end FUNCTION";
	private static final String BEGIN_COMPU_METHOD = "/begin COMPU_METHOD";
	private static final String END_COMPU_METHOD = "/end COMPU_METHOD";
	private static final String BEGIN_COMPU_VTAB = "/begin COMPU_VTAB";
	private static final String END_COMPU_VTAB = "/end COMPU_VTAB";

    public Soft(final File file) {

        try {
            
            BufferedReader buf = new BufferedReader(new FileReader(file));
            String line;
            String[] splitSystCst = null; //Cense avoir trois elements
            
            long tmp = System.currentTimeMillis();
            
            while ((line = buf.readLine()) != null) {
            	
            	
            	if(line.contains(SYSTEM_CONSTANT))
            	{
            		splitSystCst = line.split("\\s+");
            		System.out.println(splitSystCst[2] + " = " + splitSystCst[3]);
            	}
            	
            }
            
            System.out.println("\n" + (System.currentTimeMillis()-tmp));
            
            buf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
