/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.BSC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BCF extends DbCommon {

    private int bsc_cnumber;
    private long bcf_ident = 1000L;
    private int bcf_id;
    private String bcf_name;
    private int sw_ver;
    private String sw_build;
    private String type;    
    private final int MAX_BCF_NUMBER = 10000;
    private Connection con;
    private final String tableName = "bcf";

    public BCF(){
        super.setUuid();       
    }
    public int getMAX_BCF_NUMBER() {
        return MAX_BCF_NUMBER;
    }
    
    public void setCon(Connection con) {
        this.con = con;
    }

    public String getSw_build() {
        return sw_build;
    }

    public void setSw_build(String sw_build) {
        this.sw_build = sw_build;
    }

    public boolean AddSqlCmd(Statement statement) {
        String Cmd = "";
        boolean ret = false;
        

        try {            
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + tableName + " WHERE bcf.bcf_ident = " + bcf_ident);
            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }
            if (this.bcf_name != null && linCount == 0) {
                Cmd = "INSERT INTO " + this.tableName + "( bsc_cnumber, "
                        + "bcf_ident, "
                        + "uuid,"
                        + "parent_uuid,"
                        + "file_uuid, "
                        + "bcf_id, "
                        + "bcf_name, "
                        + "bcf_type, "
                        + "sw_ver) "
                        + "VALUES('" + this.bsc_cnumber + "', "
                        + "'" + this.bcf_ident + "',"
                        + "'" + this.uuid + "',"
                        + "'" + this.parent_uuid + "',"
                        + "'" + this.file_uuid + "',"
                        + "'" + this.bcf_id + "',"
                        + "'" + this.bcf_name + "',"
                        + "'" + this.type + "',"
                        + "'" + this.sw_ver + "')";
            } else {
                Cmd = "SHOW TABLES";
            }            
            statement.addBatch(Cmd);                                             
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public String getAll() {
        return "\nbsc_cnumber = " + this.bsc_cnumber
                + "\nbcf_ident = " + this.bcf_ident
                + "\nbcf_id = " + this.bcf_id
                + "\nbcf_name = " + this.bcf_name
                + "\nsw_buuld = " + this.sw_build
                + "\nsw_ver = " + this.sw_ver
                + "\ntype = " + this.type
                + "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBsc_cnumber() {
        return bsc_cnumber;
    }

    public void setBsc_cnumber(int bsc_cnumber) {
        this.bsc_cnumber = bsc_cnumber;
    }

    public double getBcf_ident() {
        return bcf_ident;
    }

    public void setBcf_ident() {
        this.bcf_ident = ((long) this.bsc_cnumber * (long) MAX_BCF_NUMBER) + (long) this.bcf_id;
    }

    public int getBcf_id() {
        return bcf_id;
    }

    public void setBcf_id(int bcf_id) {
        this.bcf_id = bcf_id;
    }

    public String getBcf_name() {
        return bcf_name;
    }

    public void setBcf_name(String bcf_name) {
        this.bcf_name = bcf_name;
    }

    public int getSw_ver() {
        return sw_ver;
    }

    public void setSw_ver(int sw_ver) {
        this.sw_ver = sw_ver;
    }

}
