/*
 * Creation : 23 oct. 2017
 */
package bdd;

import java.sql.Connection;

import bean.RowPaco;

public final class PacoDAO {

    private Connection connection;

    public PacoDAO(Connection connection) {
        this.connection = connection;
    }

    public Boolean create(RowPaco paco) {
        return null;
    }

    public Boolean delete(RowPaco paco) {
        return null;
    }

    public Boolean update(RowPaco paco) {
        return null;
    }

    public RowPaco find(int id) {
        return null;
    }

}
