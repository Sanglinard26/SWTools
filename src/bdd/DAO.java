/*
 * Creation : 23 oct. 2017
 */
package bdd;

import java.sql.Connection;

public abstract class DAO<T> {

    private Connection connection = null;

    public DAO(Connection conn) {
        this.connection = conn;
    }

    public abstract Boolean create(T obj);

    public abstract Boolean delete(T obj);

    public abstract Boolean update(T obj);

    public abstract T find(int i);

}
