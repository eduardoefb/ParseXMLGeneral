/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.RNC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author eduabati
 */
public class WBTS  extends DbCommon{
    private static int MAX_WBTS_NUMBER = 100000;
    private double ident;
    private int id;
    private int rnc_cnumber;
    private Connection con;
    private String tableName;
    private String trm;

    public String getTrm() {
        return trm;
    }

    public void setTrm(String trm) {
        this.trm = trm;
    }
    
    public void setIdent(){
         this.ident =  ((long)this.rnc_cnumber * (long)MAX_WBTS_NUMBER) + (long)this.id;          
    }
    
    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public double getIdent() {
        return ident;
    }

    public void setIdent(double ident) {
        this.ident = ident;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRnc_cnumber() {
        return rnc_cnumber;
    }

    public void setRnc_cnumber(int rnc_cnumber) {
        this.rnc_cnumber = rnc_cnumber;
    }

    public boolean SendToMysql() {        
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();
            statement.execute("INSERT INTO " + this.tableName + " ("
                    + "rnc_cnumber, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "wbts_ident, "
                    + "wbts_id, "
                    + "wbts_trm) "
                    + "VALUES("
                    + "'" + this.rnc_cnumber + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.ident + "', "
                    + "'" + this.id + "', "
                    + "'" + this.trm + "')");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }
}
