/*
 * Creation : 10 nov. 2017
 */
package bdd;

import java.util.ArrayList;

import bean.XmlInfo;

public final class XmlFolder {

    private String name;
    private ArrayList<XmlInfo> listXmlInfo;

    public XmlFolder(String name) {
        this.name = name;
    }

    public ArrayList<XmlInfo> getListXmlInfo() {
        return listXmlInfo;
    }

    public void setListXmlInfo(ArrayList<XmlInfo> listXmlInfo) {
        this.listXmlInfo = listXmlInfo;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
