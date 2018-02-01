package paco;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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

public final class StAXPaco implements Cdf {

	private String name;
	private final ArrayList<Variable> listLabel = new ArrayList<Variable>();
	private final HashSet<String> listCategory = new HashSet<String>();

	private final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(1) {
		private static final long serialVersionUID = 1L;
		{
			put(0, 0);
		}
	};

	public StAXPaco(File file) {

		this.name = file.getName();

		long start = System.currentTimeMillis();
		parse(file);
		SWToolsMain.getLogger().info(System.currentTimeMillis() - start + " ms");
	}

	private final void parse(File xml) {

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLEventReader xmler = null;

		try {
			xmler = xmlif.createXMLEventReader(new FileReader(xml));

			XMLEvent event;

			String shortName = null, longName = null, category = null, swFeatureRef = null;
			String[] unite = null;
			Values valeur = null;
			byte numAxe;
			byte numUnit;
			List<String> tmpAxeX = new ArrayList<String>();
			List<String> tmpAxeY = new ArrayList<String>();
			List<String> tmpValues = new ArrayList<String>();

			final HashMap<String, String> unit = new HashMap<String, String>();
			StringBuilder shortNameUnit = new StringBuilder();
			StringBuilder swUnitDisplay = new StringBuilder();

			while (xmler.hasNext()) {

				tmpAxeX.clear();
				tmpAxeY.clear();
				tmpValues.clear();

				event = xmler.nextEvent();

				switch (event.toString()) {

				case "<SW-UNIT>":

					shortNameUnit.setLength(0);
					swUnitDisplay.setLength(0);

					while (!event.toString().equals("</SW-UNIT>")) {

						if(event.isCharacters()){
							if(!event.asCharacters().getData().equals("\n")){
								if(shortNameUnit.length() == 0){
									shortNameUnit.append(event.asCharacters().getData());
								}else{
									swUnitDisplay.append(event.asCharacters().getData());
									break;
								}
							}	
						}
						event = xmler.nextEvent();
					}

					if(swUnitDisplay.toString().equals("tbd")){
						swUnitDisplay.setLength(0);
						swUnitDisplay.append("");
					}

					unit.put(shortNameUnit.toString(), swUnitDisplay.toString());

					shortNameUnit.setLength(0);
					swUnitDisplay.setLength(0);

					break;


				case "<SW-INSTANCE>":

					numAxe = 0;
					numUnit = 0;

					while (!event.toString().equals("</SW-INSTANCE>")) {

						if (event.isStartElement()) {

							StartElement startElement = event.asStartElement();

							switch (startElement.getName().toString()) {
							case "SHORT-NAME":

								do {
									event = xmler.nextEvent();
									if (event.isCharacters()) {
										shortName = event.asCharacters().getData();
									}

								} while (!event.isCharacters());

								break;
							case "LONG-NAME":

								do {
									event = xmler.nextEvent();
									if (event.isCharacters()) {
										if (!event.asCharacters().getData().equals("\n")) {
											longName = event.asCharacters().getData();
										} else {
											longName = "";
										}
									}

								} while (!event.isCharacters());

								break;
							case "CATEGORY":

								do {
									event = xmler.nextEvent();
									if (event.isCharacters()) {
										category = event.asCharacters().getData();
									}

								} while (!event.isCharacters());

								break;
							case "SW-FEATURE-REF":

								do {
									event = xmler.nextEvent();
									if (event.isCharacters()) {
										swFeatureRef = event.asCharacters().getData();
									}

								} while (!event.isCharacters());

								break;

							case "SW-UNIT-REF":

								if(numUnit == 0){
									switch (category) {
									case "VALUE":
										unite = new String[1];
										break;
									case "VALUE_BLOCK":
										unite = new String[1];
										break;
									case "AXIS_VALUES":
										unite = new String[1];
										break;
									case "CURVE_INDIVIDUAL":
										unite = new String[2];
										break;
									case "CURVE_FIXED":
										unite = new String[2];
										break;
									case "CURVE_GROUPED":
										unite = new String[2];
										break;
									case "MAP_INDIVIDUAL":
										unite = new String[3];
										break;
									case "MAP_FIXED":
										unite = new String[3];
										break;
									case "MAP_GROUPED":
										unite = new String[3];
										break;
									}
								}

								do {
									event = xmler.nextEvent();
									if (event.isCharacters()) {
										unite[numUnit] = unit.get(event.asCharacters().getData());
									}

								} while (!event.isCharacters());

								numUnit++;

								break;

							case "SW-VALUES-PHYS":

								switch (category) {
								case "VALUE":


									valeur = new Values(1, 1);

									while (!event.isCharacters()) {
										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n")) {
												valeur.setValue(0, 0, event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}

										}
									}

									this.listLabel.add(new Scalaire(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
									listCategory.add(Cdf.VALUE);

									break;

								case "VALUE_BLOCK":

									tmpValues.clear();

									int nbDim = 0;

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();

										if(event.toString().equals("<LABEL>")){
											nbDim++;
										}

										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n")) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}
										}
									}

									if(nbDim == 0){
										nbDim = 2;
										valeur = new Values(tmpValues.size()+1, nbDim);
									}else{
										valeur = new Values((tmpValues.size()/nbDim)+1, nbDim + 1);
									}

									if(nbDim == 2){
										valeur.setValue(0, 0, "X");
										valeur.setValue(1, 0, "Z");
										for(int i = 0; i<tmpValues.size(); i++){
											valeur.setValue(0, i+1, Integer.toString(i));
											valeur.setValue(1, i+1, tmpValues.get(i));
										}
									}else{
										valeur.setValue(0, 0, "X \\ Y");
										for(int i = 0; i<tmpValues.size(); i++){
											valeur.setValue((int) ((double) (i+1) / (double) ((tmpValues.size()/nbDim) + 1)) + 1, (i+1) % ((tmpValues.size()/nbDim) + 1),
													tmpValues.get(i));
										}
									}


									this.listLabel.add(new ValueBlock(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
									listCategory.add(Cdf.VALUE_BLOCK);

									break;

								case "CURVE_INDIVIDUAL":

									tmpValues.clear();

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n")) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}

										}

									}

									switch (numAxe) {
									case 0:
										if (tmpValues.size() > 0) {
											valeur = new Values(tmpValues.size(), 2);
											for (int i = 0; i < tmpValues.size(); i++) {
												valeur.setValue(0, i, tmpValues.get(i));
											}
										}

										break;
									case 1:
										if (tmpValues.size() > 0) {
											for (int i = 0; i < tmpValues.size(); i++) {
												valeur.setValue(1, i, tmpValues.get(i));
											}

											this.listLabel.add(new Curve(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
											listCategory.add(Cdf.CURVE_INDIVIDUAL);
										}

										break;
									}

									numAxe++;

									break;

								case "AXIS_VALUES":

									tmpValues.clear();

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n")) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}

										}

									}

									if (tmpValues.size() > 0) {
										valeur = new Values(tmpValues.size(), 1);
										for (int i = 0; i < tmpValues.size(); i++) {
											valeur.setValue(0, i, tmpValues.get(i));
										}

										this.listLabel.add(new Axis(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
										listCategory.add(Cdf.AXIS_VALUES);
									}

									numAxe++;

									break;

								case "CURVE_FIXED":

									tmpValues.clear();

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n")) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}

										}

									}

									switch (numAxe) {
									case 0:
										if (tmpValues.size() > 0) {
											valeur = new Values(tmpValues.size(), 2);
											for (int i = 0; i < tmpValues.size(); i++) {
												valeur.setValue(0, i, tmpValues.get(i));
											}
										}

										break;
									case 1:
										if (tmpValues.size() > 0) {
											for (int i = 0; i < tmpValues.size(); i++) {
												valeur.setValue(1, i, tmpValues.get(i));
											}

											this.listLabel.add(new Curve(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
											listCategory.add(Cdf.CURVE_INDIVIDUAL);
										}

										break;
									}

									numAxe++;

									break;

								case "CURVE_GROUPED":

									tmpValues.clear();

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n")) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}

										}

									}

									switch (numAxe) {
									case 0:
										if (tmpValues.size() > 0) {
											valeur = new Values(tmpValues.size(), 2);
											for (int i = 0; i < tmpValues.size(); i++) {
												valeur.setValue(0, i, tmpValues.get(i));
											}
										}

										break;
									case 1:
										if (tmpValues.size() > 0) {
											for (int i = 0; i < tmpValues.size(); i++) {
												valeur.setValue(1, i, tmpValues.get(i));
											}

											this.listLabel.add(new Curve(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
											listCategory.add(Cdf.CURVE_GROUPED);
										}

										break;
									}

									numAxe++;

									break;

								case "MAP_INDIVIDUAL":

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {

												switch (numAxe) {
												case 0:
													tmpAxeX.add(event.asCharacters().getData());
													break;
												case 1:
													tmpAxeY.add(event.asCharacters().getData());
													break;
												case 2:
													tmpValues.add(event.asCharacters().getData());
													break;
												}

											} else {
												event = xmler.peek();
											}
										}

									}

									switch (numAxe) {
									case 0:

										break;
									case 1:

										break;
									case 2:
										valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
										valeur.setValue(0, 0, "X \\ Y");

										for (int i = 0; i < tmpAxeX.size(); i++) {
											valeur.setValue(0, i + 1, tmpAxeX.get(i));
										}

										for (int i = 0; i < tmpValues.size(); i++) {
											valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
													tmpValues.get(i));
										}

										this.listLabel.add(new Map(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
										listCategory.add(Cdf.MAP_INDIVIDUAL);

										tmpAxeX.clear();
										tmpAxeY.clear();
										tmpValues.clear();

										break;
									}

									numAxe++;

									break;

								case "MAP_FIXED":

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {

											if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {

												switch (numAxe) {
												case 0:
													tmpAxeX.add(event.asCharacters().getData());
													break;
												case 1:
													tmpAxeY.add(event.asCharacters().getData());
													break;
												case 2:
													tmpValues.add(event.asCharacters().getData());
													break;
												}

											} else {
												event = xmler.peek();
											}
										}

									}

									switch (numAxe) {
									case 0:

										break;
									case 1:

										break;
									case 2:
										valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
										valeur.setValue(0, 0, "X \\ Y");

										for (int i = 0; i < tmpAxeX.size(); i++) {
											valeur.setValue(0, i + 1, tmpAxeX.get(i));
										}

										for (int i = 0; i < tmpValues.size(); i++) {
											valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
													tmpValues.get(i));
										}

										this.listLabel.add(new Map(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
										listCategory.add(Cdf.MAP_FIXED);

										tmpAxeX.clear();
										tmpAxeY.clear();
										tmpValues.clear();

										break;
									}

									numAxe++;

									break;

								case "MAP_GROUPED":

									while (!event.toString().equals("</SW-VALUES-PHYS>")) {

										event = xmler.nextEvent();
										if (event.isCharacters()) {
											if (!event.asCharacters().getData().equals("\n") & !event.asCharacters().getData().equals("'")) {

												switch (numAxe) {
												case 0:
													tmpAxeX.add(event.asCharacters().getData());
													break;
												case 1:
													tmpAxeY.add(event.asCharacters().getData());
													break;
												case 2:
													tmpValues.add(event.asCharacters().getData());
													break;
												}

											} else {
												event = xmler.peek();
											}
										}

									}

									switch (numAxe) {
									case 0:

										break;
									case 1:

										break;
									case 2:
										valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
										valeur.setValue(0, 0, "X \\ Y");

										for (int i = 0; i < tmpAxeX.size(); i++) {
											valeur.setValue(0, i + 1, tmpAxeX.get(i));
										}

										for (int i = 0; i < tmpValues.size(); i++) {
											valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
													tmpValues.get(i));
										}

										this.listLabel.add(new Map(shortName, longName, category, swFeatureRef, unite, new History[0], valeur));
										listCategory.add(Cdf.MAP_GROUPED);

										tmpAxeX.clear();
										tmpAxeY.clear();
										tmpValues.clear();

										break;
									}

									numAxe++;

									break;
								}
								break;
							}

						}
						event = xmler.nextEvent();
					}

					break;

				default:
					break;
				}

			}

			tmpAxeX.clear();
			tmpAxeY.clear();
			tmpValues.clear();

		} catch (

				Exception e) {
			e.printStackTrace();
		} finally {
			try {
				xmler.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getNbLabel() {
		return this.listLabel.size();
	}

	@Override
	public HashSet<String> getCategoryList() {
		return this.listCategory;
	}

	@Override
	public List<Variable> getListLabel() {
		return this.listLabel;
	}

	@Override
	public HashMap<Integer, Integer> getRepartitionScore() {
		return this.repartitionScore;
	}

	@Override
	public float getAvgScore() {
		return 0;
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
	public double getCheckSum() {
		return 0;
	}

}
