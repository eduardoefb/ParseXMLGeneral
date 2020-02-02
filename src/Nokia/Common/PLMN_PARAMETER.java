/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.Common;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PLMN_PARAMETER  extends DbCommon {
    private String plmn_id;
    private String name;
    private String short_name;
    private String value;
    private String tableName;
    private Connection con;

    public PLMN_PARAMETER(){
        this.setUuid();
    }
    
    public String getPlmn_id() {
        return plmn_id;
    }

    public void setPlmn_id(String plmn_id) {
        this.plmn_id = plmn_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
        
    
    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;
        try {
            statement = con.createStatement();
            Cmd = "INSERT INTO " + this.tableName + "("
                    + "plmn_id, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "name, "
                    + "short_name, "                    
                    + "value) "
                    + "VALUES("
                    + "'" + this.plmn_id + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.name + "', "
                    + "'" + this.short_name + "', "                    
                    + "'" + this.value + "')";
            statement.executeQuery(Cmd);
                        
        } catch (SQLException ex) {            
            System.out.println(ex.getMessage());
        }
        return ret;
    }


    
}
