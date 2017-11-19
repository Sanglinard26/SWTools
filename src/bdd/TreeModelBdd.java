/*
 * Creation : 10 nov. 2017
 */
package bdd;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public final class TreeModelBdd extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;

    private final DefaultMutableTreeNode root;
    private final File pathDb;
    private static ArrayList<Bdd> listBdd;

    public TreeModelBdd(TreeNode root, File pathDb) {
        super(root);

        this.root = (DefaultMutableTreeNode) super.getRoot();
        this.pathDb = pathDb;

        FilenameFilter fnamefilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mv.db");
            }
        };

        for (String db : pathDb.list(fnamefilter)) {
            this.root.add(new DefaultMutableTreeNode(new Bdd(db.replace(".mv.db", ""))));
        }
        reload();

    }

    public final void populateFromBdd(Connection bddConnexion, DefaultMutableTreeNode bddNode) {
    	try {
            ResultSet rs = bddConnexion.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
            while (rs.next()) {
            	insertNodeInto(new DefaultMutableTreeNode(new XmlFolder(rs.getString("TABLE_NAME").toLowerCase())), bddNode, bddNode.getChildCount());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public final int removeXmlFolder(Connection bddConnexion, DefaultMutableTreeNode xmlFolderNode)
    {
    	String sql = "DROP TABLE " + xmlFolderNode.getUserObject();
    	
    	try {
            return bddConnexion.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    	
		return 0;
    }

	public File getPathDb() {
		return pathDb;
	}

	public static ArrayList<Bdd> getListBdd() {
		return listBdd;
	}

}
