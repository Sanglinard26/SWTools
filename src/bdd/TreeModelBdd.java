/*
 * Creation : 10 nov. 2017
 */
package bdd;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
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

        listBdd = new ArrayList<>();

        for (String db : pathDb.list(fnamefilter)) {
            this.root.add(new DefaultMutableTreeNode(new Bdd(db.replace(".mv.db", ""))));
        }
        reload();

    }

    public final void populateTree(Connection connection) {

    }

}
