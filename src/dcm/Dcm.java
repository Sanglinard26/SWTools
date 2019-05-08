/*
 * Creation : 19 juin 2017
 */
package dcm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import cdf.Axis;
import cdf.Cdf;
import cdf.Curve;
import cdf.History;
import cdf.Map;
import cdf.Scalaire;
import cdf.ValueBlock;
import cdf.Values;
import cdf.Variable;
import gui.SWToolsMain;

import static dcm.PrimaryKeyword.*;
import static dcm.PropertieKeyword.*;

public final class Dcm implements Cdf {

	// Corriger les variables qui ont des axes avec du texte

	private static final String SPACE = " ";
	private static final String THREE_SPACE = "   ";
	private static final String QUOTE = "\"";
	private static final String TAB = "\t";

	private static final Pattern PATTERN_THREE_SPACE = Pattern.compile(THREE_SPACE);

	private static final History[] EMPTY_COMMENT = new History[0];

	private double checkSum = 0;

	private final String name;
	private boolean valid;
	private final List<Variable> listLabel = new ArrayList<Variable>();
	private static final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(1);

	static
	{
		repartitionScore.put(0,0);
	}

	private final HashSet<String> listCategory = new HashSet<String>();

	public Dcm(final File file) {

		this.name = file.getName().substring(0, file.getName().length() - 4);

		long start = System.currentTimeMillis();

		this.parse(file);

		SWToolsMain.getLogger().info(System.currentTimeMillis() - start + " ms");

	}

	private final void parse(File file) {

		try (BufferedReader buf = new BufferedReader(new FileReader(file))) {

			String[] spaceSplitLine;
			String line = null;

			while ((line = buf.readLine()) != null) {
				
				if(line.length() == 0 || line.charAt(0) == '*')
				{
					continue;
				}

				spaceSplitLine = line.split(SPACE);

				if (spaceSplitLine.length > 0) {

					switch (getPrimaryKeyword(spaceSplitLine[0])) {

					case FESTWERT:
						readValue(buf, spaceSplitLine[1], VALUE);
						break;

					case TEXTSTRING:
						readValue(buf, spaceSplitLine[1], ASCII);
						break;

					case KENNLINIE:
						readCurve(buf, spaceSplitLine, CURVE_INDIVIDUAL);
						break;

					case FESTKENNLINIE:
						readCurve(buf, spaceSplitLine, CURVE_FIXED);
						break;

					case GRUPPENKENNLINIE:
						readCurve(buf, spaceSplitLine, CURVE_GROUPED);
						break;

					case KENNFELD:
						readMap(buf, spaceSplitLine, MAP_INDIVIDUAL);
						break;

					case GRUPPENKENNFELD:
						readMap(buf, spaceSplitLine, MAP_GROUPED);
						break;

					case FESTKENNFELD:
						readMap(buf, spaceSplitLine, MAP_FIXED);
						break;

					case STUETZSTELLENVERTEILUNG:
						readAxis(buf, spaceSplitLine, AXIS_VALUES);
						break;

					case FESTWERTEBLOCK:
						readValueBlock(buf, spaceSplitLine, VALUE_BLOCK);
						break;
					default:
						break;
					}
				}
			}

			repartitionScore.put(0, listLabel.size());

			this.valid = true;

		} catch (Exception e) {

			e.printStackTrace();

			SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);

		}
	}
	
	private final void readValue(BufferedReader buf, String name, String type) throws IOException
	{
		String[] unite = new String[]{ SPACE.intern() };
		Values valeur = new Values(1, 1);

		String line;
		String[] spaceSplitLine2;
		String[] quotesSplitLine;
		
		String description = "";
		String fonction = "";
		
		while (!(line = buf.readLine()).equals(END.name())) {

			spaceSplitLine2 = line.split(SPACE);
			quotesSplitLine = line.split(QUOTE);

			if (line.contains(LANGNAME.getName()) && quotesSplitLine.length > 1) {
				description = quotesSplitLine[quotesSplitLine.length - 1];
			}
			if (line.contains(FUNKTION.getName()) && spaceSplitLine2.length > 1) {
				fonction = spaceSplitLine2[spaceSplitLine2.length - 1];
			}
			if (line.contains(EINHEIT_W.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[0] = SPACE.intern();
				}
			}

			if (line.contains(WERT.getName())) {
				valeur.setValue(0, 0, spaceSplitLine2[spaceSplitLine2.length - 1]);
			} else if (line.contains(TEXT.getName())) {
				valeur.setValue(0, 0, replaceQuote(quotesSplitLine[quotesSplitLine.length - 1]));
			}

		}

		// System.out.println(spaceSplitLine[1]);

		listLabel.add(new Scalaire(name, description, type.intern(), fonction.intern(), unite,
				EMPTY_COMMENT, valeur));
		
		listCategory.add(type);

		checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
	}
	
	private final void readCurve(BufferedReader buf, String[] spaceSplit, String type) throws IOException
	{
		String line;
		String[] spaceSplitLine2;
		String[] quotesSplitLine;
		String[] threeSpaceSplitLine;
		String[] tabSplitLine;
		
		String[] unite = new String[2];
		Values valeur = new Values(Integer.parseInt(spaceSplit[spaceSplit.length - 1]), 2);
		
		String description = "";
		String fonction = "";
		
		short cntX = 0;
		short cntZ = 0;
		
		String[] sharedAxis = new String[1];

		while (!(line = buf.readLine()).equals(END.name())) {

			spaceSplitLine2 = line.split(SPACE);
			quotesSplitLine = line.split(QUOTE);
			tabSplitLine = line.split(TAB);

			if (line.contains(LANGNAME.getName()) && quotesSplitLine.length > 1) {
				description = quotesSplitLine[quotesSplitLine.length - 1];
			}

			if (line.contains(FUNKTION.getName()) && spaceSplitLine2.length > 1) {
				fonction = spaceSplitLine2[spaceSplitLine2.length - 1];
			}

			if (line.contains(EINHEIT_X.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[0] = SPACE.intern();
				}
			}

			if (line.contains(EINHEIT_W.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[1] = SPACE.intern();
				}
			}
			
			if (line.contains(SSTX.getName()) && tabSplitLine.length > 1) {
				sharedAxis[0] = tabSplitLine[tabSplitLine.length - 1].intern();
		}
			
			int nbSplit;
			String tmpValue;
			
			if (line.contains(ST_X.getName()) || line.contains(ST_TX_X.getName())) {

				threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

				nbSplit = threeSpaceSplitLine.length;

				for (short i = 0; i < nbSplit; i++) {
					tmpValue = threeSpaceSplitLine[i];
					if (tmpValue.length() != 0 && !tmpValue.equals(ST_X.getName()) && !tmpValue.equals(ST_TX_X.getName())) {
						if (cntX < valeur.getDimX()) {
							valeur.setValue(0, cntX, replaceQuote(tmpValue));
							cntX++;
						}
					}
				}
			}

			if (line.contains(WERT.getName()) || line.contains(TEXT.getName())) {

				threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

				nbSplit = threeSpaceSplitLine.length;
				for (short i = 0; i < nbSplit; i++) {
					tmpValue = threeSpaceSplitLine[i];
					if (tmpValue.length() != 0 && !tmpValue.equals(WERT.getName()) && !tmpValue.equals(TEXT.getName()) && cntZ < valeur.getDimX()) {
						valeur.setValue(1, cntZ, replaceQuote(tmpValue));
						cntZ++;
					}
				}
			}

		}

		if(!CURVE_GROUPED.equals(type))
		{
			listLabel.add(new Curve(spaceSplit[1], description, type.intern(), fonction.intern(),
					unite, EMPTY_COMMENT, valeur));
		}else{
			listLabel.add(new Curve(spaceSplit[1], description.toString(), CURVE_GROUPED.intern(), fonction.toString().intern(),
					unite, EMPTY_COMMENT, valeur, sharedAxis));
		}
		
		listCategory.add(type);

		checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
	}
	
	private final void readMap(BufferedReader buf, String[] spaceSplit, String type) throws IOException
	{
		String line;
		String[] spaceSplitLine2;
		String[] quotesSplitLine;
		String[] threeSpaceSplitLine;
		String[] tabSplitLine;
		
		short cntX = 1;
		short cntZ = 0;

		String[] unite = new String[3];
		String[] sharedAxis = new String[2];
		Values valeur = new Values(Integer.parseInt(spaceSplit[spaceSplit.length - 2]) + 1,
				Integer.parseInt(spaceSplit[spaceSplit.length - 1]) + 1);
		
		String description = "";
		String fonction = "";

		valeur.setValue(0, 0, "Y \\ X");

		while (!(line = buf.readLine()).equals(END.name())) {

			spaceSplitLine2 = line.split(SPACE);
			quotesSplitLine = line.split(QUOTE);
			tabSplitLine = line.split(TAB);

			if (line.contains(LANGNAME.getName()) && quotesSplitLine.length > 1) {
				description = quotesSplitLine[quotesSplitLine.length - 1];
			}

			if (line.contains(FUNKTION.getName()) && spaceSplitLine2.length > 1) {
				fonction = spaceSplitLine2[spaceSplitLine2.length - 1];
			}

			if (line.contains(EINHEIT_X.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[0] = SPACE.intern();
				}
			}

			if (line.contains(EINHEIT_Y.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[1] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[1] = SPACE.intern();
				}
			}

			if (line.contains(EINHEIT_W.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[2] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[2] = SPACE.intern();
				}
			}

			// Implementer les axes partages
			if (line.contains(SSTX.getName())) {

				if (tabSplitLine.length > 1) {
					sharedAxis[0] = tabSplitLine[tabSplitLine.length - 1].intern();
				}
			} else if (line.contains(SSTY.getName()) && tabSplitLine.length > 1) {
				sharedAxis[1] = tabSplitLine[tabSplitLine.length - 1].intern();
			}
			//
			
			int nbSplit;
			String tmpValue;
			
			if (line.contains(ST_X.getName()) || line.contains(ST_TX_X.getName())) {

				threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

				nbSplit = threeSpaceSplitLine.length;
				for (short i = 0; i < nbSplit; i++) {
					tmpValue = threeSpaceSplitLine[i];
					if (tmpValue.length() != 0 && !tmpValue.equals(ST_X.getName()) && !tmpValue.equals(ST_TX_X.getName())) {
						if (cntX < valeur.getDimX()) {
							valeur.setValue(0, cntX, replaceQuote(tmpValue));
							cntX++;
						}
					}
				}
			}

			if (cntX == valeur.getDimX()) {
				cntX = 0;
				cntZ++;
			}

			if (line.contains(ST_Y.getName()) || line.contains(ST_TX_Y.getName()) || line.contains(WERT.getName()) || line.contains(TEXT.getName())) {

				threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

				nbSplit = threeSpaceSplitLine.length;
				for (short i = 0; i < nbSplit; i++) {
					tmpValue = threeSpaceSplitLine[i];
					if (tmpValue.length() != 0 && !tmpValue.equals(ST_Y.getName()) && !tmpValue.equals(ST_TX_Y.getName())
							&& !tmpValue.equals(WERT.getName()) && !tmpValue.equals(TEXT.getName())) {
						if (cntX < valeur.getDimX()) {
							valeur.setValue(cntZ, cntX, replaceQuote(tmpValue));
							cntX++;
						}
					}
				}
			}
		}

		if(!MAP_GROUPED.equals(type))
		{
			listLabel.add(new Map(spaceSplit[1], description, type.intern(), fonction.intern(), unite,
					EMPTY_COMMENT, valeur));
		}else{
			listLabel.add(new Map(spaceSplit[1], description, type.intern(), fonction.intern(), unite,
					EMPTY_COMMENT, valeur, sharedAxis));
		}
		
		listCategory.add(type);

		checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
	}
	
	private final void readAxis(BufferedReader buf, String[] spaceSplit, String type) throws IOException
	{
		String line;
		String[] spaceSplitLine2;
		String[] quotesSplitLine;
		String[] threeSpaceSplitLine;
		String description = "";
		String fonction = "";
		String[] unite = new String[1];

		Values valeur = new Values(Integer.parseInt(spaceSplit[spaceSplit.length - 1]), 1);

		short cnt = 0;

		while (!(line = buf.readLine()).equals(END.name())) {

			spaceSplitLine2 = line.split(SPACE);
			quotesSplitLine = line.split(QUOTE);

			if (line.contains(LANGNAME.getName()) && quotesSplitLine.length > 1) {
				description = quotesSplitLine[quotesSplitLine.length - 1];
			}

			if (line.contains(FUNKTION.getName()) && spaceSplitLine2.length > 1) {
				fonction = spaceSplitLine2[spaceSplitLine2.length - 1];
			}

			if (line.contains(EINHEIT_X.getName())) {

				if (quotesSplitLine.length > 1) {
					unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
				} else {
					unite[0] = SPACE.intern();
				}
			}

			if (line.contains(ST_X.getName()) || line.contains(ST_TX_X.getName())) {

				threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

				int nbSplit = threeSpaceSplitLine.length;
				for (short i = 0; i < nbSplit; i++) {
					String tmpValue = threeSpaceSplitLine[i];
					if (tmpValue.length() != 0 && !tmpValue.equals(ST_X.getName()) && !tmpValue.equals(ST_TX_X.getName()) && cnt < valeur.getDimX()) {
						valeur.setValue(0, cnt, replaceQuote(tmpValue));
						cnt++;
					}
				}
			}

		}

		listLabel.add(new Axis(spaceSplit[1], description, type.intern(), fonction.intern(), unite,
				EMPTY_COMMENT, valeur));

		listCategory.add(type);

		checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
	}
	
	private final void readValueBlock(BufferedReader buf, String[] spaceSplit, String type) throws IOException
	{
		String line;
		String[] spaceSplitLine2;
		String[] quotesSplitLine;
		String[] threeSpaceSplitLine;
		
		String description = "";
		String fonction = "";
		String[] unite;
		Values valeur;
		
		if (spaceSplit[spaceSplit.length - 2].equals("@")) {

			short cntX = 1;
			short cntZ = 1;

			unite = new String[1];
			valeur = new Values(Integer.parseInt(spaceSplit[spaceSplit.length - 3]) + 1,
					Integer.parseInt(spaceSplit[spaceSplit.length - 1]) + 1);

			valeur.setValue(0, 0, "Y \\ X");

			for (int x = 1; x < valeur.getDimX(); x++) {
				valeur.setValue(0, x, Integer.toString(x - 1));
			}

			while (!(line = buf.readLine()).equals(END.name())) {

				spaceSplitLine2 = line.split(SPACE);
				quotesSplitLine = line.split(QUOTE);

				if (line.contains(LANGNAME.getName()) && quotesSplitLine.length > 1) {
					description = quotesSplitLine[quotesSplitLine.length - 1];
				}

				if (line.contains(FUNKTION.getName()) && spaceSplitLine2.length > 1) {
					fonction = spaceSplitLine2[spaceSplitLine2.length - 1];
				}

				if (line.contains(EINHEIT_W.getName())) {

					if (quotesSplitLine.length > 1) {
						unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
					} else {
						unite[0] = SPACE.intern();
					}
				}

				if (cntX == valeur.getDimX()) {
					cntX = 1;
					cntZ++;
				}

				if (line.contains(WERT.getName()) || line.contains(TEXT.getName())) {

					threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

					valeur.setValue(cntZ, 0, Integer.toString(cntZ - 1));

					int nbSplit = threeSpaceSplitLine.length;
					for (short i = 0; i < nbSplit; i++) {
						String tmpValue = threeSpaceSplitLine[i];
						if (tmpValue.length() != 0 && !tmpValue.equals(WERT.getName()) && !tmpValue.equals(TEXT.getName()) && cntX < valeur.getDimX()) {
							valeur.setValue(cntZ, cntX, replaceQuote(tmpValue));
							cntX++;
						}
					}
				}
			}

		} else {

			short cntX = 1;

			unite = new String[1];
			valeur = new Values(Integer.parseInt(spaceSplit[spaceSplit.length - 1]) + 1, 2);

			valeur.setValue(0, 0, "X");

			for (int x = 1; x < valeur.getDimX(); x++) {
				valeur.setValue(0, x, Integer.toString(x - 1));
			}

			valeur.setValue(1, 0, "Z");

			while (!(line = buf.readLine()).equals(END.name())) {

				spaceSplitLine2 = line.split(SPACE);
				quotesSplitLine = line.split(QUOTE);

				if (line.contains(LANGNAME.getName()) && quotesSplitLine.length > 1) {
					description = quotesSplitLine[quotesSplitLine.length - 1];
				}

				if (line.contains(FUNKTION.getName()) && spaceSplitLine2.length > 1) {
					fonction = spaceSplitLine2[spaceSplitLine2.length - 1];
				}

				if (line.contains(EINHEIT_W.getName())) {

					if (quotesSplitLine.length > 1) {
						unite[0] = quotesSplitLine[quotesSplitLine.length - 1].intern();
					} else {
						unite[0] = SPACE.intern();
					}
				}

				if (line.contains(WERT.getName()) || line.contains(TEXT.getName())) {

					threeSpaceSplitLine = PATTERN_THREE_SPACE.split(line);

					int nbSplit = threeSpaceSplitLine.length;
					for (short i = 0; i < nbSplit; i++) {
						String tmpValue = threeSpaceSplitLine[i];
						if (tmpValue.length() != 0 && !tmpValue.equals(WERT.getName()) && !tmpValue.equals(TEXT.getName()) && cntX < valeur.getDimX()) {
							valeur.setValue(1, cntX, replaceQuote(tmpValue));
							cntX++;

						}
					}
				}

			}

		}

		listLabel.add(new ValueBlock(spaceSplit[1], description, type.intern(), fonction.intern(),
				unite, EMPTY_COMMENT, valeur));


		listCategory.add(type);

		checkSum += listLabel.get(listLabel.size() - 1).getChecksum();
	}

	private static final String replaceQuote(String sentence) {
		if (sentence.charAt(0) == '"' && sentence.charAt(sentence.length() - 1) == '"') {
			return sentence.substring(1, sentence.length() - 1);
		}
		return sentence;
	}

	@Override
	public List<Variable> getListLabel() {
		return this.listLabel;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int getNbLabel() {
		return this.listLabel.size();
	}

	@Override
	public float getAvgScore() {
		return 0f;
	}

	@Override
	public int getMinScore() {
		return 0;
	}

	@Override
	public int getMaxScore() {
		return 0;
	}

	@Override
	public HashMap<Integer, Integer> getRepartitionScore() {
		return repartitionScore;
	}

	@Override
	public Set<String> getCategoryList() {
		return listCategory;
	}

	@Override
	public double getCheckSum() {
		return checkSum;
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

}
