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

    public BddConnexion(String dbPath) {
        this.dbPath = dbPath;
    }

    public final void connectBdd() {
        try {

            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:C:/" + "testDbH2");

            ResultSet rs = connection.getMetaData().getTables(null, null, "PACO", null);

            System.out.println(rs.next());

            // connection.setAutoCommit(false);
            statement = connection.createStatement();
            System.out.println("Connexion a " + dbPath + " avec succès");

        } catch (ClassNotFoundException notFoundException) {
            notFoundException.printStackTrace();
            System.out.println("Erreur de connection");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Erreur de connection");
        }
    }

    public final Boolean createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS paco" + "(id INTEGER PRIMARY KEY AUTO_INCREMENT," + " name TEXT NOT NULL," + " nblabel INTEGER,"
                + " listlabel TEXT," + " minscore REAL," + " maxscore REAL)";

        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public final int emptyTable() {
        String sql;

        sql = "TRUNCATE TABLE paco";

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

    public final int addPaCo(Cdf cdf) {

        String sql;

        if (cdf != null) {
            sql = "INSERT INTO paco (name,nblabel,listlabel,minscore, maxscore)" + " VALUES ('" + cdf.getName() + "','" + cdf.getNbLabel() + "','"
                    + "..." + "','" + cdf.getMinScore() + "','" + cdf.getMaxScore() + "')";
        } else {
            sql = "INSERT INTO paco (name,nblabel,listlabel,minscore, maxscore)" + " VALUES ('" + PaCoFictif.name + "','" + PaCoFictif.nbLabel + "','"
                    + PaCoFictif.listLabel.toString() + "','" + PaCoFictif.minScore + "','" + PaCoFictif.maxScore + "')";
        }

        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public final int modifPaCo(int id) {

        String sql;

        sql = "UPDATE paco SET name=" + "'toto'" + " WHERE id=" + id;

        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public final int deletePaCo(int id) {

        String sql;

        sql = "DELETE paco" + " WHERE id=" + id;

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
