package bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import cdf.Cdf;

public final class BddConnexion {

    private final String dbPath;
    private Connection connection = null;
    private Statement statement = null;

    private ArrayList<String> listTable = new ArrayList<String>();

    public BddConnexion(String dbPath) {
        this.dbPath = dbPath;
    }

    public ArrayList<String> getListTable() {
        return listTable;
    }

    public final Boolean connectBdd() {
        try {

            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(dbPath);

            ResultSet rs = connection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });

            while (rs.next()) {
                listTable.add(rs.getString("TABLE_NAME"));
            }

            System.out.println(listTable);

            statement = connection.createStatement();
            System.out.println("Connexion a " + dbPath + " avec succès");
            return true;

        } catch (ClassNotFoundException notFoundException) {
            notFoundException.printStackTrace();
            System.out.println("Erreur de connection");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Erreur de connection");
        }
        return false;
    }

    public final Boolean createTable(String tableName) {

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(id INTEGER PRIMARY KEY AUTO_INCREMENT," + " name TEXT NOT NULL,"
                + " nblabel INTEGER," + " listlabel TEXT," + " minscore REAL," + " maxscore REAL)";

        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public final int emptyTable(String tableName) {
        String sql;

        sql = "TRUNCATE TABLE " + tableName;

        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public final Boolean closeBdd() {
        try {
            statement.close();
            connection.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet query(String requet) {

        ResultSet resultat = null;

        try {
            resultat = statement.executeQuery(requet);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur dans la requet : " + requet);
        }
        return resultat;

    }

    public final int addPaCo(Cdf cdf, Boolean autoImport) {

        String sql;
        String[] splitNameCdf = null;
        String nomTable = null;

        if (autoImport) {
            System.out.println("Auto import");
            splitNameCdf = cdf.getName().split("_");
            System.out.println(splitNameCdf.length);
            if (splitNameCdf.length == 9) {
                nomTable = splitNameCdf[1];
            } else {
                nomTable = "PACO_" + Long.toString(Math.round(Math.random() * 100));
            }
        } else {
            nomTable = "PACO_" + Long.toString(Math.round(Math.random() * 100));
        }

        if (!listTable.contains(nomTable)) {
            createTable(nomTable);
        }

        if (cdf != null) {
            sql = "INSERT INTO " + nomTable + "(name,nblabel,minscore, maxscore)" + " VALUES ('" + cdf.getName() + "','" + cdf.getNbLabel() + "','"
                    + cdf.getMinScore() + "','" + cdf.getMaxScore() + "')";
        } else {
            sql = "INSERT INTO " + nomTable + "(name,nblabel,minscore, maxscore)" + " VALUES ('" + PaCoFictif.name + "','" + PaCoFictif.nbLabel
                    + "','" + PaCoFictif.listLabel.toString() + "','" + PaCoFictif.minScore + "','" + PaCoFictif.maxScore + "')";
        }

        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public final int modifPaCo(String tableName, int id) {

        String sql;

        sql = "UPDATE " + tableName + " SET name=" + "'changement nom'" + " WHERE id=" + id;

        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public final int deletePaCo(String tableName, int id) {

        String sql;

        sql = "DELETE " + tableName + " WHERE id=" + id;

        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    private static final class PaCoFictif {

        private static final String name = "PaCo_TestBis";
        private static final int nbLabel = 3;
        private static final ArrayList<String> listLabel = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("Scalaire");
                add("Curve");
                add("Map");
            }
        };
        private static final float minScore = 25;
        private static final float maxScore = 93.7f;

    }

}
