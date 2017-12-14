package bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class BddConnexion {

    private static final String DRIVER_CLASS = "org.h2.Driver";
    private static final String DRIVER = "jdbc:h2:";
    private static Connection connection = null;
    private static String dbPath = null;

    public static final void setDbPath(String dbPath) {
        String oldDbPath = BddConnexion.dbPath;
        if (!dbPath.equals(oldDbPath) & connection != null) {
            close();
        }
        BddConnexion.dbPath = dbPath;
    }

    public static final Connection getInstance() {

        if (connection == null) {
            try {

                Class.forName(DRIVER_CLASS);
                connection = DriverManager.getConnection(DRIVER + BddConnexion.dbPath);

                System.out.println("Connexion a " + dbPath + " avec succes");

            } catch (ClassNotFoundException notFoundException) {
                notFoundException.printStackTrace();
                System.out.println("Erreur de connection");
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                System.out.println("Erreur de connection");
            }
        }

        return connection;
    }

    public static final void close() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Fermeture de la connection : " + BddConnexion.dbPath);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static final Boolean createTable(Connection connection, String tableName) {

        // XmlInfo(int id, String name, int nbLabel, float meanScore, String com, String state, String path)

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER PRIMARY KEY AUTO_INCREMENT," + " name TEXT NOT NULL,"
                + " nblabel INTEGER NOT NULL," + " meanscore FLOAT NOT NULL," + " com TEXT," + " state TEXT," + " path TEXT NOT NULL)";

        try {
            // return getInstance().createStatement().execute(sql);
            return connection.createStatement().execute(sql);
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return false;
    }

    /*
     * public final int emptyTable(String tableName) { String sql;
     * 
     * sql = "TRUNCATE TABLE " + tableName;
     * 
     * try { return statement.executeUpdate(sql); } catch (SQLException e) { e.printStackTrace(); return 0; } }
     */

    /*
     * public final Boolean closeBdd() { try { statement.close(); connection.close(); return true;
     * 
     * } catch (SQLException e) { e.printStackTrace(); return false; } }
     */

    /*
     * public ResultSet query(String requet) {
     * 
     * ResultSet resultat = null;
     * 
     * try { resultat = statement.executeQuery(requet); } catch (SQLException e) { e.printStackTrace(); System.out.println("Erreur dans la requet : "
     * + requet); } return resultat;
     * 
     * }
     */

}
