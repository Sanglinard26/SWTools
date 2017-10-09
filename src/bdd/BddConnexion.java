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
            statement = connection.createStatement();
            System.out.println("Connexion a " + dbPath + " avec succès");

            createTable();

        } catch (ClassNotFoundException notFoundException) {
            notFoundException.printStackTrace();
            System.out.println("Erreur de connecxion");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Erreur de connecxion");
        }
    }

    public final void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS paco (\n" + " id integer PRIMARY KEY,\n" + " name text NOT NULL,\n" + " capacity real\n" + ");";

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

    }

    public final void deletePaCo() {

    }

    private static final class PaCoFictif {

        private static final String name = "PaCo_Test";
        private static final int nbLabel = 3;
        private static final ArrayList<String> listLabel = new ArrayList<String>() {
            {
                add("Scalaire");
                add("Curve");
                add("Map");
            }
        };
        private static final int minScore = 25;
        private static final int maxScore = 93;

    }

}
