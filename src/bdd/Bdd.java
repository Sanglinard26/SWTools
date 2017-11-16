/*
 * Creation : 10 nov. 2017
 */
package bdd;

import java.util.ArrayList;

public final class Bdd {

    private String name;
    private ArrayList<XmlFolder> listXmlFolder;

    public ArrayList<XmlFolder> getListXmlFolder() {
        return listXmlFolder;
    }

    public void setListXmlFolder(ArrayList<XmlFolder> listXmlFolder) {
        this.listXmlFolder = listXmlFolder;
    }

    public Bdd(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
