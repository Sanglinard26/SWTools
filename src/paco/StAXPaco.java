package paco;

import java.io.File;
import java.io.FileReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public final class StAXPaco {


	public StAXPaco() {
		File xml = new File("C:\\Users\\tramp\\Desktop\\Tmp\\PaCo\\AP_BooCtl_D-MAP-REGULS_DV5RC_C4xx_TTBVA_170626_KeRa_B16.XML");
		System.out.println(xml.getName());
		
		long start = System.currentTimeMillis();
		parse(xml);
		System.out.println("tps = " + (System.currentTimeMillis()-start) + "ms");
	}

	private final void parse(File xml){

		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLEventReader xmler = null;

		try {
			xmler = xmlif.createXMLEventReader(new FileReader(xml));

			XMLEvent event;
			while (xmler.hasNext()) {

				event = xmler.nextEvent();

				switch (event.toString()) {
				case "<SW-INSTANCE>":

					while (!event.toString().equals("</SW-INSTANCE>")) {

						if (event.isStartElement()) {
							
							//System.out.println("print event ==> " + event);
							//System.out.println("getName = " + event.asStartElement().getName());

							if(event.asStartElement().getName().equals("SHORT-NAME")){

								//System.out.println(event.asStartElement().getName());
								
								
								/*
								if (event.isCharacters()) {

									if (!event.asCharacters().isWhiteSpace()) {

										System.out.println("\t>" + event.asCharacters().getData());

									}
								}
								*/
								
							}
						}
						event = xmler.nextEvent();
					}


					break;

				default:
					break;
				}


			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				xmler.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
	}


}
