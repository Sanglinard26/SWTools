package dcm;

enum PropertieKeyword {

	LANGNAME("LANGNAME"),
	FUNKTION("FUNKTION"),
	EINHEIT_X("EINHEIT_X"),
	EINHEIT_Y("EINHEIT_Y"),
	EINHEIT_W("EINHEIT_W"),
	ST_X("ST/X"),
	ST_Y("ST/Y"),
	ST_TX_X("ST_TX/X"),
	ST_TX_Y("ST_TX/Y"),
	SSTX("*SSTX"),
	SSTY("*SSTY"),
	WERT("WERT"),
	TEXT("TEXT");
	
	private String name;
	
	private PropertieKeyword(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
