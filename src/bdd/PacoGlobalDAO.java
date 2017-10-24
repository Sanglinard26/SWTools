/*
 * Creation : 23 oct. 2017
 */
package bdd;

import java.sql.Connection;

import bean.PacoGlobal;

public final class PacoGlobalDAO extends DAO<PacoGlobal> {

    public PacoGlobalDAO(Connection conn) {
        super(conn);
    }

    @Override
    public Boolean create(PacoGlobal obj) {

        /*
         * String sql = null; String[] splitNameCdf = null; String nomTable = null;
         * 
         * if (autoImport) { System.out.println("Auto import"); splitNameCdf = cdf.getName().split("_"); System.out.println(splitNameCdf.length); if
         * (splitNameCdf.length == 9) { nomTable = splitNameCdf[1]; } else { nomTable = "PACO_" + Long.toString(Math.round(Math.random() * 100)); } }
         * else { nomTable = "PACO_" + Long.toString(Math.round(Math.random() * 100)); }
         * 
         * if (!listTable.contains(nomTable)) { createTable(nomTable); }
         * 
         * if (cdf != null) { sql = "INSERT INTO " + nomTable + "(name,nblabel,minscore, maxscore)" + " VALUES ('" + cdf.getName() + "','" +
         * cdf.getNbLabel() + "','" + cdf.getMinScore() + "','" + cdf.getMaxScore() + "')"; }
         * 
         * try { return statement.executeUpdate(sql); } catch (SQLException e) { e.printStackTrace(); return 0; }
         */

        return null;
    }

    @Override
    public Boolean delete(PacoGlobal obj) {

        /*
         * String sql;
         * 
         * sql = "DELETE " + tableName + " WHERE id=" + id;
         * 
         * try { return statement.executeUpdate(sql); } catch (SQLException e) { e.printStackTrace(); return 0; }
         */

        return null;
    }

    @Override
    public Boolean update(PacoGlobal obj) {

        /*
         * String sql;
         * 
         * sql = "UPDATE " + tableName + " SET name=" + "'changement nom'" + " WHERE id=" + id;
         * 
         * try { return statement.executeUpdate(sql); } catch (SQLException e) { e.printStackTrace(); return 0; }
         */

        return null;
    }

    @Override
    public PacoGlobal find(int i) {
        return null;
    }

}
