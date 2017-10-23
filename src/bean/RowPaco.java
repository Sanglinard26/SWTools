/*
 * Creation : 23 oct. 2017
 */
package bean;

public final class RowPaco {

    private int id = 0;
    private String name = "";
    private int nbLabel = 0;
    private float minScore = 0;
    private float maxScore = 0;

    public RowPaco() {
    }

    public RowPaco(int id, String name, int nbLabel, float minScore, float maxScore) {
        this.id = id;
        this.name = name;
        this.nbLabel = nbLabel;
        this.minScore = minScore;
        this.maxScore = maxScore;
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

    public int getNblabel() {
        return nbLabel;
    }

    public void setNblabel(int nblabel) {
        this.nbLabel = nblabel;
    }

    public float getMinscore() {
        return minScore;
    }

    public void setMinscore(float minscore) {
        this.minScore = minscore;
    }

    public float getMaxscore() {
        return maxScore;
    }

    public void setMaxscore(float maxscore) {
        this.maxScore = maxscore;
    }

}
