/*
 * Creation : 24 oct. 2017
 */
package bean;

public final class PacoInfo {

    private int id;
    private String name;
    private int nbLabel;
    private float meanScore;
    private String com;
    private String state;
    private String path;

    public PacoInfo() {
    }

    public PacoInfo(int id, String name, int nbLabel, float meanScore, String com, String state, String path) {
        this.id = id;
        this.name = name;
        this.nbLabel = nbLabel;
        this.meanScore = meanScore;
        this.com = com;
        this.state = state;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbLabel() {
        return nbLabel;
    }

    public void setNbLabel(int nbLabel) {
        this.nbLabel = nbLabel;
    }

    public float getMeanScore() {
        return meanScore;
    }

    public void setMeanScore(float meanScore) {
        this.meanScore = meanScore;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
