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

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSwFeatureRef() {
		return swFeatureRef;
	}

	public void setSwFeatureRef(String swFeatureRef) {
		this.swFeatureRef = swFeatureRef;
	}

	public String[][] getSwCsHistory() {
		return swCsHistory;
	}

	public void setSwCsHistory(String[][] swCsHistory) {
		this.swCsHistory = swCsHistory;
	}
	
	

}
