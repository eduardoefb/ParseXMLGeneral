/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.FNS;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RNC  extends DbCommon {

    private Connection con;
    private int targetId;
    private String tableName = "fns_rnc";
    private String name;
    private String id;
    private String mcc;
    private String mnc;
    private String ni;
    private String spc;
    private String lac;
    private String rac;

    public RNC(){
        super.setUuid();
    }
    
    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getRac() {
        return rac;
    }

    public void setRac(String rac) {
        this.rac = rac;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();

            Cmd = "INSERT INTO " + this.tableName + "(target_id, "
                    + "name, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "id, "
                    + "mcc, "
                    + "mnc, "
                    + "ni, "
                    + "lac, "
                    + "rac, "
                    + "spc) "
                    + "VALUES('" + this.targetId + "', "
                    + "'" + this.name + "',"
                    + "'" + this.uuid + "',"
                    + "'" + this.parent_uuid + "',"
                    + "'" + this.file_uuid + "',"
                    + "'" + this.id + "',"
                    + "'" + this.mcc + "',"
                    + "'" + this.mnc + "',"
                    + "'" + this.ni + "',"                    
                    + "'" + this.lac + "',"
                    + "'" + this.rac + "',"                    
                    + "'" + this.spc + "')";
            statement.execute(Cmd);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

}
