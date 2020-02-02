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

public class Prfilegx  extends DbCommon {

    int targetId;
    String tableName;
    String parameter_class;
    String parameter_name;
    String identifier;
    String name_of_parameter;
    String value;
    String change_possibility;
    Connection con;

    public Prfilegx(){
        this.setUuid();
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChange_possibility() {
        return change_possibility;
    }

    public void setChange_possibility(String change_possibility) {
        this.change_possibility = change_possibility;
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

    public boolean AddSqlCmd(Statement statement) {
        String Cmd = "";
        boolean ret = false;
        //Statement statement = null;
        try {
            //statement = con.createStatement();
            Cmd = "INSERT INTO " + this.tableName + "("
                    + "target_id, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "parameter_class, "
                    + "parameter_name, "
                    + "identifier, "
                    + "name_of_parameter, "
                    + "value, "
                    + "change_possibility) "
                    + "VALUES("
                    + "'" + this.targetId + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.parameter_class + "', "
                    + "'" + this.parameter_name + "', "
                    + "'" + this.identifier + "', "
                    + "'" + this.name_of_parameter + "', "
                    + "'" + this.value + "', "
                    + "'" + this.change_possibility + "')";            
            statement.addBatch(Cmd);  
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }

}
