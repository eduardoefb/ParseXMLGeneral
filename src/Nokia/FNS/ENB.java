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

public class ENB extends DbCommon {

    private Connection con;
    private int targetId;
    private String tableName = "fns_enb";
    private String ip;
    private String id;
    private String plmn;
    private String tac;
    private String mcc;
    private String mnc;

    public String getPlmn() {
        return plmn;
    }

    public void setPlmn(String plmn) {
        this.plmn = plmn;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public ENB() {
        this.setUuid();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();

            Cmd = "INSERT INTO " + this.tableName + "(target_id, "
                    + "ip, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "id, "
                    + "tac,"
                    + "plmn,"
                    + "mcc, "
                    + "mnc) "
                    + "VALUES('" + this.targetId + "', "
                    + "'" + this.ip + "',"
                    + "'" + this.uuid + "',"
                    + "'" + this.parent_uuid + "',"
                    + "'" + this.file_uuid + "',"
                    + "'" + this.id + "',"
                    + "'" + this.tac + "',"
                    + "'" + this.plmn + "',"
                    + "'" + this.mcc + "',"
                    + "'" + this.mnc + "')";
            statement.execute(Cmd);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

}
