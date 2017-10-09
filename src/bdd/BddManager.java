package bdd;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class BddManager {

    public static void createBdd(String fileName) {
        String url = "jdbc:sqlite:C:/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void openBdd(String fileName) {

    }

    public static void closeBdd() {

    }

    public static void insert() {

    }

    public static void delete() {

    }

}
