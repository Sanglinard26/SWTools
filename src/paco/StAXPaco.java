package paco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import cdf.ComAxis;
import cdf.Cdf;
import cdf.Curve;
import cdf.History;
import cdf.Map;
import cdf.TypeVariable;
import cdf.Value;
import cdf.ValueBlock;
import cdf.Values;
import cdf.Variable;
import gui.SWToolsMain;
import utils.Utilitaire;

// Non robuste aux espaces après une balise de fin "</> "

public final class StAXPaco implements Cdf {

	private String name;
	private boolean valid;
	private List<Variable> listLabel;
	private final EnumSet<TypeVariable> listCategory = EnumSet.noneOf(TypeVariable.class);
	private int minScore = Byte.MAX_VALUE;
	private int maxScore = Byte.MIN_VALUE;
	private double checkSum = 0;

	private final HashMap<Integer, Integer> repartitionScore = new HashMap<Integer, Integer>(5);

	public StAXPaco(File file) {

		this.name = Utilitaire.getFileNameWithoutExtension(file);

		long start = System.currentTimeMillis();
		parseStAX(file);
		SWToolsMain.getLogger().info(System.currentTimeMillis() - start + " ms");
	}

	private final void parseStAX(File xml) {

		final XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLEventReader xmler = null;

		// PaCo Keywords
		final String SW_INSTANCE = "SW-INSTANCE";
		final String END_SW_INSTANCE = "</SW-INSTANCE>";
		final String SW_UNIT = "SW-UNIT";
		final String END_SW_UNIT = "</SW-UNIT>";
		final String SW_UNIT_REF = "SW-UNIT-REF";
		final String SW_VALUES_PHYS = "SW-VALUES-PHYS";
		final String END_SW_VALUES_PHYS = "</SW-VALUES-PHYS>";
		final String SHORT_NAME = "SHORT-NAME";
		final String END_SHORT_NAME = "</SHORT-NAME>";
		final String SW_FEATURE_REF = "SW-FEATURE-REF";
		final String SW_CS_ENTRY = "SW-CS-ENTRY";
		final String LONG_NAME = "LONG-NAME";
		final String END_LONG_NAME = "</LONG-NAME>";
		final String CATEGORY = "CATEGORY";
		final String SW_CS_HISTORY = "SW-CS-HISTORY";
		final String END_SW_CS_HISTORY = "</SW-CS-HISTORY>";
		final String SW_AXIS_CONT = "SW-AXIS-CONT";
		//

		try {

			xmler = xmlif.createXMLEventReader(new BufferedReader(new FileReader(xml)));
			XMLEvent event;

			listLabel = new ArrayList<Variable>();

			final StringBuilder shortName = new StringBuilder();
			final StringBuilder longName = new StringBuilder();
			String category = null;
			String swFeatureRef = null;
			String[] unite = null;
			History[] history = null;
			Values valeur = null;
			byte numAxe;
			int nbDim;
			byte numUnit;
			byte nbEntry;
			// Pour les valeurs
			final List<String> tmpAxeX = new ArrayList<String>();
			final List<String> tmpAxeY = new ArrayList<String>();
			final List<String> tmpValues = new ArrayList<String>();
			final StringBuilder tmpStringVal = new StringBuilder();
			final List<String> tmpSharedAxis = new ArrayList<String>();
			//
			// Pour les commentaires
			final List<String> tmpDate = new ArrayList<String>();
			final List<String> tmpAuteur = new ArrayList<String>();
			final List<String> tmpScore = new ArrayList<String>();
			final List<String> tmpRemark = new ArrayList<String>();
			//

			final HashMap<String, String> unit = new HashMap<String, String>();
			final StringBuilder shortNameUnit = new StringBuilder();
			final StringBuilder swUnitDisplay = new StringBuilder();
			final StringBuilder pRemark = new StringBuilder();

			int cntLabel = 0;

			String data = null;

			while (xmler.hasNext()) {

				event = xmler.nextEvent();

				if (event.isStartElement()) {

					switch (event.asStartElement().getName().getLocalPart()) { // Pour test

					case SW_UNIT:

						// On reinitialise les donnees
						shortNameUnit.setLength(0);
						swUnitDisplay.setLength(0);
						//

						while (!END_SW_UNIT.equals(event.toString())) {

							if (event.isCharacters() && isValidString(event.asCharacters().getData())) {

								data = event.asCharacters().getData();

								if (shortNameUnit.length() == 0) {
									shortNameUnit.append(data);
								} else {
									if (!"tbd".equals(data)) {
										swUnitDisplay.append(data);
									}
									break;
								}
							}

							event = xmler.nextEvent();
						}

						unit.put(shortNameUnit.toString(), swUnitDisplay.toString());

						break;

					case SW_INSTANCE:

						// On reinitialise les donnees
						numAxe = 0;
						nbDim = 0;
						numUnit = 0;
						nbEntry = 0;

						tmpSharedAxis.clear();
						tmpAxeX.clear();
						tmpAxeY.clear();
						tmpValues.clear();

						tmpDate.clear();
						tmpAuteur.clear();
						tmpScore.clear();
						tmpRemark.clear();
						history = null;
						pRemark.setLength(0);
						//

						while (!END_SW_INSTANCE.equals(event.toString())) {

							if (event.isStartElement()) {

								switch (event.asStartElement().getName().getLocalPart()) {
								case SHORT_NAME:

									shortName.setLength(0);

									do {
										event = xmler.nextEvent();
										if (event.isCharacters()) {
											shortName.append(event.asCharacters().getData());
										}

									} while (!END_SHORT_NAME.equals(event.toString()));

									// System.out.println(shortName);

									break;
								case LONG_NAME:

									longName.setLength(0);

									do {
										event = xmler.nextEvent();
										if (event.isCharacters() && !event.asCharacters().getData().equals("\n")) {
											longName.append(event.asCharacters().getData());
										}

									} while (!END_LONG_NAME.equals(event.toString()));

									break;
								case CATEGORY:

									do {
										event = xmler.nextEvent();
										if (event.isCharacters()) {
											category = event.asCharacters().getData();
										}

									} while (!event.isCharacters());

									break;
								case SW_FEATURE_REF:

									do {
										event = xmler.nextEvent();
										if (event.isCharacters()) {
											swFeatureRef = event.asCharacters().getData();
										}

									} while (!event.isCharacters());

									break;

								case SW_AXIS_CONT:

									if (category.contains("GROUPED") && event.asStartElement().getAttributeByName(new QName("SI")) != null) {
										tmpSharedAxis.add(event.asStartElement().getAttributeByName(new QName("SI")).getValue());
										if (tmpSharedAxis.get(tmpSharedAxis.size() - 1).indexOf(";") > -1) {
											tmpSharedAxis.set(tmpSharedAxis.size() - 1, tmpSharedAxis.get(tmpSharedAxis.size() - 1)
													.substring(tmpSharedAxis.get(tmpSharedAxis.size() - 1).indexOf(";") + 1));
										}

									}

									break;

								case SW_UNIT_REF:

									if (numUnit == 0) {
										switch (TypeVariable.getType(category)) {
										case VALUE:
											unite = new String[1];
											break;
										case ASCII:
											unite = new String[1];
											break;
										case VAL_BLK:
											unite = new String[1];
											break;
										case COM_AXIS:
											unite = new String[1];
											break;
										case CURVE:
											unite = new String[2];
											break;
										case MAP:
											unite = new String[3];
											break;
										default:
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

								case SW_VALUES_PHYS:

									int tmpValuesSize = 0;

									switch (TypeVariable.getType(category)) {
									case VALUE:

										valeur = new Values(1, 1);

										tmpStringVal.setLength(0);

										while (!event.isEndElement()) {
											event = xmler.nextEvent();
											if (event.isCharacters() && isValidString(event.asCharacters().getData())) {
												tmpStringVal.append(event.asCharacters().getData());
											}
										}

										valeur.setValue(0, 0, tmpStringVal.toString());

										break;

									case ASCII:

										valeur = new Values(1, 1);

										tmpStringVal.setLength(0);

										while (!event.isEndElement()) {
											event = xmler.nextEvent();
											if (event.isCharacters() && isValidString(event.asCharacters().getData())) {
												tmpStringVal.append(event.asCharacters().getData());
											}
										}

										valeur.setValue(0, 0, tmpStringVal.toString());

										break;

									case VAL_BLK:

										while (!END_SW_VALUES_PHYS.equals(event.toString())) {

											event = xmler.nextEvent();

											if (event.toString().equals("<LABEL>")) {
												tmpValues.add(Integer.toString(nbDim++));
											}

											if (event.isCharacters() && isValidString(event.asCharacters().getData())) {
												tmpStringVal.setLength(0);
												while (!event.isEndElement()) {
													tmpStringVal.append(event.asCharacters().getData());
													event = xmler.nextEvent();
												}
												tmpValues.add(tmpStringVal.toString());
											}
										}

										tmpValuesSize = tmpValues.size();

										if (nbDim == 0) {
											nbDim = 2;
											valeur = new Values(tmpValuesSize + 1, nbDim);
										} else {
											valeur = new Values((tmpValuesSize / nbDim) + 0, nbDim + 1);
										}

										if (nbDim == 2) {
											valeur.setValue(0, 0, "X");
											valeur.setValue(1, 0, "Z");

											for (int i = 0; i < tmpValuesSize; i++) {
												valeur.setValue(0, i + 1, Integer.toString(i));
												valeur.setValue(1, i + 1, tmpValues.get(i));
											}
										} else {

											valeur.setValue(0, 0, "Y \\ X");
											for (int i = 0; i < tmpValuesSize; i++) {
												if (i < valeur.getDimX() - 1) {
													valeur.setValue(0, i + 1, Integer.toString(i));
												}
												valeur.setValue((int) ((double) i / (double) (valeur.getDimX() + 0)) + 1, i % (valeur.getDimX()),
														tmpValues.get(i));
											}
										}

										break;

									case COM_AXIS:

										while (!END_SW_VALUES_PHYS.equals(event.toString())) {

											event = xmler.nextEvent();
											if (event.isCharacters() && isValidString(event.asCharacters().getData())) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}
										}

										tmpValuesSize = tmpValues.size();

										if (!tmpValues.isEmpty()) {
											valeur = new Values(tmpValuesSize, 1);
											for (int i = 0; i < tmpValuesSize; i++) {
												valeur.setValue(0, i, tmpValues.get(i));
											}
										}

										numAxe++;

										break;

									case CURVE:

										while (!END_SW_VALUES_PHYS.equals(event.toString())) {

											event = xmler.nextEvent();
											if (event.isCharacters() && isValidString(event.asCharacters().getData())) {
												tmpValues.add(event.asCharacters().getData());
											} else {
												event = xmler.peek();
											}
										}

										tmpValuesSize = tmpValues.size();

										if (numAxe == 1) {
											valeur = new Values(tmpValuesSize / 2, 2);
											for (int i = 0; i < tmpValuesSize; i++) {
												if (i < tmpValuesSize / 2) {
													valeur.setValue(0, i, tmpValues.get(i));
												} else {
													valeur.setValue(1, i % (tmpValuesSize / 2), tmpValues.get(i));
												}
											}
										}

										numAxe++;

										break;

									case MAP:

										while (!END_SW_VALUES_PHYS.equals(event.toString())) {

											event = xmler.nextEvent();
											if (event.isCharacters() && isValidString(event.asCharacters().getData())) {

												data = event.asCharacters().getData();

												switch (numAxe) {
												case 0:
													tmpAxeX.add(data);
													break;
												case 1:
													tmpAxeY.add(data);
													break;
												case 2:
													tmpValues.add(data);
													break;
												default:
													break;
												}

											} else {
												event = xmler.peek();
											}
										}

										tmpValuesSize = tmpValues.size();

										if (numAxe == 2) {
											valeur = new Values(tmpAxeX.size() + 1, tmpAxeY.size() + 1);
											valeur.setValue(0, 0, "Y \\ X");

											for (int i = 0; i < tmpAxeX.size(); i++) {
												valeur.setValue(0, i + 1, tmpAxeX.get(i));
											}

											for (int i = 0; i < tmpValuesSize; i++) {
												valeur.setValue((int) ((double) i / (double) (tmpAxeX.size() + 1)) + 1, i % (tmpAxeX.size() + 1),
														tmpValues.get(i));
											}
										}

										numAxe++;

										break;
									default:
										break;
									}
									break;

								case SW_CS_HISTORY:

									while (!END_SW_CS_HISTORY.equals(event.toString())) {

										event = xmler.nextEvent();

										if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(SW_CS_ENTRY)) {
											tmpDate.add("");
											tmpAuteur.add("");
											tmpScore.add("");
											tmpRemark.add("");
											nbEntry++;
										}

										if (event.isStartElement()) {

											switch (event.asStartElement().getName().getLocalPart()) {
											case "DATE":
												event = xmler.nextEvent();
												tmpDate.set(nbEntry - 1, event.asCharacters().getData().replace("T", " @ "));
												break;
											case "SW-CS-PERFORMED-BY":
												event = xmler.nextEvent();
												tmpAuteur.set(nbEntry - 1, event.asCharacters().getData());
												break;
											case "SW-CS-STATE":
												event = xmler.nextEvent();
												tmpScore.set(nbEntry - 1, event.asCharacters().getData());
												break;
											case "REMARK":
												pRemark.setLength(0);
												while (!event.toString().equals("</REMARK>")) {
													event = xmler.nextEvent();

													if (event.isCharacters() && isValidString(event.asCharacters().getData())) {
														pRemark.append(event.asCharacters().getData());
													}

													if (event.isEndElement() && pRemark.length() > 0) {
														pRemark.append("\n");
													}
												}
												if (pRemark.length() > 0) {
													tmpRemark.set(nbEntry - 1, pRemark.toString().substring(0, pRemark.toString().length() - 2));
												} else {
													tmpRemark.set(nbEntry - 1, "");
												}
												break;
											default:
												break;
											}
										}
									}

									if (nbEntry > 0) {
										history = new History[nbEntry];
										for (int i = 0; i < nbEntry; i++) {
											history[i] = new History(tmpDate.get(i), tmpAuteur.get(i), tmpScore.get(i), tmpRemark.get(i));
										}
									}

									break;
								default:
									break;
								}
							}
							event = xmler.nextEvent();
						}

						if (history == null) {
							history = new History[0];
						}
						
						TypeVariable type = TypeVariable.getType(category);

						// On cree la variable
						switch (type) {
						case VALUE:
							this.listLabel
							.add(new Value(shortName.toString(), longName.toString(), type, swFeatureRef, unite, history, valeur));
							break;
						case ASCII:
							this.listLabel
							.add(new Value(shortName.toString(), longName.toString(), type, swFeatureRef, unite, history, valeur));
							break;
						case VAL_BLK:
							this.listLabel
							.add(new ValueBlock(shortName.toString(), longName.toString(), type, swFeatureRef, unite, history, valeur));
							break;
						case COM_AXIS:
							this.listLabel.add(new ComAxis(shortName.toString(), longName.toString(), type, swFeatureRef, unite, history, valeur));
							break;
						case CURVE:
							this.listLabel.add(new Curve(shortName.toString(), longName.toString(), type, swFeatureRef, unite, history, valeur,
									tmpSharedAxis.toArray(new String[tmpSharedAxis.size()])));
							break;
						case MAP:
							this.listLabel.add(new Map(shortName.toString(), longName.toString(), type, swFeatureRef, unite, history, valeur,
									tmpSharedAxis.toArray(new String[tmpSharedAxis.size()])));
							break;
						default:
							break;
						}
						
						listCategory.add(type);

						checkSum += listLabel.get(cntLabel).getChecksum();

						cntLabel++;

						break;

					default:
						break;
					}

				}

			}

			getScores();

			for (int score = 0; score <= 100; score += 25) {
				if (repartitionScore.get(score) > 0) {
					if (score <= minScore)
						minScore = score;
					if (score >= maxScore)
						maxScore = score;
				}
			}

			if (!listLabel.isEmpty()) {
				this.valid = true;
			} else {
				SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);
				SWToolsMain.getLogger().severe("Aucun label de charge");
			}

			shortName.setLength(0);
			longName.setLength(0);
			shortNameUnit.setLength(0);
			swUnitDisplay.setLength(0);
			pRemark.setLength(0);

			tmpSharedAxis.clear();
			tmpAxeX.clear();
			tmpAxeY.clear();
			tmpValues.clear();
			tmpStringVal.setLength(0);

			tmpDate.clear();
			tmpAuteur.clear();
			tmpScore.clear();
			tmpRemark.clear();

			data = null;

		} catch (

				Exception e) {
			e.printStackTrace();
			SWToolsMain.getLogger().severe("Erreur sur l'ouverture de : " + this.name);
		} finally {
			try {
				xmler.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
	}

	private final boolean isValidString(String data) {
		return !"\n".equals(data) && !"'".equals(data) && !"-".equals(data) && !">".equals(data) && !data.contains("\t") && !data.matches("\\s+");
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
	public Set<TypeVariable> getCategoryList() {
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

	private final void getScores() {

		repartitionScore.put(0, 0);
		repartitionScore.put(25, 0);
		repartitionScore.put(50, 0);
		repartitionScore.put(75, 0);
		repartitionScore.put(100, 0);

		if (!listLabel.isEmpty()) {

			int lastScore;

			for (int i = 0; i < this.listLabel.size(); i++) {
				lastScore = this.listLabel.get(i).getLastScore();

				if (repartitionScore.get(lastScore) != null)
					repartitionScore.put(lastScore, repartitionScore.get(lastScore) + 1);
			}
		}
	}

	@Override
	public float getAvgScore() {
		return (float) (repartitionScore.get(0) * 0 + repartitionScore.get(25) * 25 + repartitionScore.get(50) * 50 + repartitionScore.get(75) * 75
				+ repartitionScore.get(100) * 100) / listLabel.size();
	}

	@Override
	public int getMinScore() {
		return this.minScore;
	}

	@Override
	public int getMaxScore() {
		return this.maxScore;
	}

	@Override
	public double getCheckSum() {
		return this.checkSum;
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

}
