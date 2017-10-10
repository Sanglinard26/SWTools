package bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public final class BddConnexion {

    private final String dbPath;
    private Connection connection = null;
    private Statement statement = null;

    public BddConnexion(String dbPath) {
        this.dbPath = dbPath;
    }

    public final void connectBdd() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:C:/" + dbPath);
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

    public final void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS paco" + "(id INTEGER PRIMARY KEY AUTOINCREMENT," + " name TEXT NOT NULL," + " nblabel INTEGER,"
                + " listlabel TEXT," + " minscore REAL," + " maxscore REAL)";

        try {
            statement.execute(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public final void closeBdd() {
        try {
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
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

    public final void addPaCo() {

        String sql = "INSERT INTO paco (name,nblabel,listlabel,minscore, maxscore)" + " VALUES ('" + PaCoFictif.name + "','" + PaCoFictif.nbLabel
                + "','" + PaCoFictif.listLabel.toString() + "','" + PaCoFictif.minScore + "','" + PaCoFictif.maxScore + "')";

        try {
            int res = statement.executeUpdate(sql);
            System.out.println("Resultat insert = " + res);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public final void deletePaCo() {

    }

    private static final class PaCoFictif {

        private static final String name = "PaCo_TestBis";
        private static final int nbLabel = 3;
        private static final ArrayList<String> listLabel = new ArrayList<String>() {
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
