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

public class BTS  extends DbCommon {

    private final int MAX_BTS_PER_BSC = 10000;

    private final String tableName = "bts";
    private double bcf_ident_fk;
    private double bts_ident;
    private int bts_id;
    private int seg_id;
    private String bts_name;
    private String seg_name;
    private String lac;
    private String ci;
    private String band;
    private Connection con;
    private int bscTargetId;

    
    public BTS(){
        this.setUuid();        
    }
    public int getBscTargetId() {
        return bscTargetId;
    }

    public void setBscTargetId(int bscTargetId) {
        this.bscTargetId = bscTargetId;
    }

    public int getMAX_BTS_PER_BCF() {
        return MAX_BTS_PER_BSC;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public String getAll() {
        return "\nbcf_ident_fk = " + this.bcf_ident_fk
                + "bts_ident = " + this.bts_ident
                + "bts_id = " + this.bts_id
                + "seg_id = " + this.seg_id
                + "bts_name = " + this.bts_name
                + "seg_name = " + this.seg_name
                + "lac = " + this.lac
                + "ci = " + this.ci
                + "band = " + this.band
                + "";
    }

    public double getBcf_ident_fk() {
        return bcf_ident_fk;
    }

    public void setBcf_ident_fk(double bcf_ident_fk) {
        this.bcf_ident_fk = bcf_ident_fk;
    }

    public double getBts_ident() {
        return bts_ident;
    }

    public void setBts_ident(double bts_ident) {
        this.bts_ident = bts_ident;
    }

    public int getBts_id() {
        return bts_id;
    }

    public void setBts_id(int bts_id) {
        this.bts_id = bts_id;
    }

    public int getSeg_id() {
        return seg_id;
    }

    public void setSeg_id(int seg_id) {
        this.seg_id = seg_id;
    }

    public String getBts_name() {
        return bts_name;
    }

    public void setBts_name(String bts_name) {
        this.bts_name = bts_name;
    }

    public String getSeg_name() {
        return seg_name;
    }

    public void setSeg_name(String seg_name) {
        this.seg_name = seg_name;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public void setBts_ident() {
        this.bts_ident = ((long) this.bscTargetId * (long) MAX_BTS_PER_BSC) + this.bts_id;
    }

    public boolean AddSqlCmd(Statement statement) {
        String Cmd = "";
        boolean ret = false;

        try {

            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE "
                    + this.tableName + ".bts_ident = " + this.bts_ident + ";");
            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }

            if (this.bts_ident > 0 && linCount == 0) {
                Cmd = "INSERT INTO " + this.tableName + "( "
                        + "bcf_ident_fk, "
                        + "uuid, "
                        + "parent_uuid, "
                        + "file_uuid, "
                        + "bts_ident, "
                        + "bts_id, "
                        + "bts_name, "
                        + "seg_id, "
                        + "seg_name, "
                        + "lac, "
                        + "ci, "
                        + "band)"
                        + "VALUES( "
                        + "'" + this.bcf_ident_fk + "',"
                        + "'" + this.uuid + "',"
                        + "'" + this.parent_uuid + "',"
                        + "'" + this.file_uuid + "',"
                        + "'" + this.bts_ident + "',"
                        + "'" + this.bts_id + "',"
                        + "'" + this.bts_name + "',"
                        + "'" + this.seg_id + "',"
                        + "'" + this.seg_name + "',"
                        + "'" + this.lac + "',"
                        + "'" + this.ci + "',"
                        + "'" + this.band + "')";
            } else {
                Cmd = "SHOW TABLES";
            }            
            statement.addBatch(Cmd);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

}
