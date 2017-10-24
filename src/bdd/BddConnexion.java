package bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class BddConnexion {

    private static final String DRIVER_CLASS = "org.h2.Driver";
    private static final String DRIVER = "jdbc:h2:";
    private static Connection connection = null;
    private static String dbPath = null;

    // Une table globale : WP, SWP, Owner, Nom PaCo
    // Une table par SWP : Nom PaCo, Nb label, Score moy, Commentaire, Etat, Lien

    public static final void setDbPath(String dbPath) {
        BddConnexion.dbPath = dbPath;
    }

    public static final Connection getInstance() {

        if (connection == null) {
            try {

                Class.forName(DRIVER_CLASS);
                connection = DriverManager.getConnection(DRIVER + BddConnexion.dbPath);

                // ResultSet rs = connection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });

                System.out.println("Connexion a " + dbPath + " avec succès");

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

    /*
     * public final Boolean createTable(String tableName) {
     * 
     * String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(id INTEGER PRIMARY KEY AUTO_INCREMENT," + " name TEXT NOT NULL," +
     * " nblabel INTEGER," + " listlabel TEXT," + " minscore REAL," + " maxscore REAL)";
     * 
     * try { return statement.execute(sql); } catch (SQLException e) { e.printStackTrace(); } return false; }
     */

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
