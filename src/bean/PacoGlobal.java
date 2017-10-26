/*
 * Creation : 23 oct. 2017
 */
package bean;

public final class PacoGlobal {

    private int id = 0;
    private String wp = "";
    private String swp = "";
    private String owner = "";
    private String name = "";

    public PacoGlobal() {
    }

    public PacoGlobal(int id, String wp, String swp, String owner, String name) {
        this.id = id;
        this.wp = wp;
        this.swp = swp;
        this.owner = owner;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWp() {
        return wp;
    }

    public void setWp(String wp) {
        this.wp = wp;
    }

    public String getSwp() {
        return swp;
    }

    public void setSwp(String swp) {
        this.swp = swp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
