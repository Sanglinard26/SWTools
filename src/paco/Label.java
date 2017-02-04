package paco;

public abstract class Label {
	
	private String shortName;
	private String category;
	private String swFeatureRef;
	private String[][] swCsHistory;
	
	public Label(String shortName, String category, String swFeatureRef, String[][] swCsHistory) {
		this.shortName = shortName;
		this.category = category;
		this.swFeatureRef = swFeatureRef;
		this.swCsHistory = swCsHistory;
	}

	public String getShortName() {
		return shortName;
	}

	public String getCategory() {
		return category;
	}

	public String getSwFeatureRef() {
		return swFeatureRef;
	}

	public String[][] getSwCsHistory() {
		return swCsHistory;
	}

	public abstract void showView();
	

}
