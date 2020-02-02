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

public class Fifilegx  extends DbCommon {
    int targetId;
    String tableName;
    String parameter_class;
    String parameter_name;
    String identifier;
    String name_of_parameter;
    String activation_status;
    Connection con;

    public Fifilegx(){
        this.setUuid();
    }
    
    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getParameter_class() {
        return parameter_class;
    }

    public void setParameter_class(String parameter_class) {
        this.parameter_class = parameter_class;
    }

    public String getParameter_name() {
        return parameter_name;
    }

    public void setParameter_name(String parameter_name) {
        this.parameter_name = parameter_name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName_of_parameter() {
        return name_of_parameter;
    }

    public void setName_of_parameter(String name_of_parameter) {
        this.name_of_parameter = name_of_parameter;
    }

    public String getActivation_status() {
        return activation_status;
    }

    public void setActivation_status(String activation_status) {
        this.activation_status = activation_status;
    }
    

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;
        try {
            statement = con.createStatement();
            Cmd = "INSERT INTO " + this.tableName + "("
                    + "target_id, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "parameter_class, "
                    + "parameter_name, "
                    + "identifier, "
                    + "name_of_parameter, "
                    + "activation_status) "
                    + "VALUES("
                    + "'" + this.targetId + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.parameter_class + "', "
                    + "'" + this.parameter_name + "', "
                    + "'" + this.identifier + "', "
                    + "'" + this.name_of_parameter + "', "
                    + "'" + this.activation_status + "')";
            statement.executeQuery(Cmd);
            
            
        } catch (SQLException ex) {            
            System.out.println(ex.getMessage());
        }
        return ret;
    }
    
    
    
    
    
}
