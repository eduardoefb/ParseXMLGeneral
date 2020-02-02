/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.mcRNC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Hw  extends DbCommon {

    private Connection con;
    private String tableName = "hw_mcrnc";
    private String unit_name;
    private String unit_id;
    private int cabinet;
    private int chassis;
    private String pos;
    private int cnumber;
    private String prod_manufacturer;
    private String prod_name;
    private String prod_partnumber;
    private String prod_version;
    private String prod_serial;
    private String prod_fileid;
    private String prod_custom;
    private String board_mfg_date;
    private String board_manufacturer;
    private String board_name;
    private String board_serial;
    private String board_part_number;

    
    public Hw(){
        super.setUuid();
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

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public int getCabinet() {
        return cabinet;
    }

    public void setCabinet(int cabinet) {
        this.cabinet = cabinet;
    }

    public int getChassis() {
        return chassis;
    }

    public void setChassis(int chassis) {
        this.chassis = chassis;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public int getCnumber() {
        return cnumber;
    }

    public void setCnumber(int cnumber) {
        this.cnumber = cnumber;
    }

    public String getProd_manufacturer() {
        return prod_manufacturer;
    }

    public void setProd_manufacturer(String prod_manufacturer) {
        this.prod_manufacturer = prod_manufacturer;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_partnumber() {
        return prod_partnumber;
    }

    public void setProd_partnumber(String prod_partnumber) {
        this.prod_partnumber = prod_partnumber;
    }

    public String getProd_version() {
        return prod_version;
    }

    public void setProd_version(String prod_version) {
        this.prod_version = prod_version;
    }

    public String getProd_serial() {
        return prod_serial;
    }

    public void setProd_serial(String prod_serial) {
        this.prod_serial = prod_serial;
    }

    public String getProd_fileid() {
        return prod_fileid;
    }

    public void setProd_fileid(String prod_fileid) {
        this.prod_fileid = prod_fileid;
    }

    public String getProd_custom() {
        return prod_custom;
    }

    public void setProd_custom(String prod_custom) {
        this.prod_custom = prod_custom;
    }

    public String getBoard_mfg_date() {
        return board_mfg_date;
    }

    public void setBoard_mfg_date(String board_mfg_date) {
        this.board_mfg_date = board_mfg_date;
    }

    public String getBoard_manufacturer() {
        return board_manufacturer;
    }

    public void setBoard_manufacturer(String board_manufacturer) {
        this.board_manufacturer = board_manufacturer;
    }

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }

    public String getBoard_serial() {
        return board_serial;
    }

    public void setBoard_serial(String board_serial) {
        this.board_serial = board_serial;
    }

    public String getBoard_part_number() {
        return board_part_number;
    }

    public void setBoard_part_number(String board_part_number) {
        this.board_part_number = board_part_number;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;

        Statement statement = null;

        try {
            statement = con.createStatement();            
            statement.execute("INSERT INTO " + this.tableName + " ("
                    + "unit_name, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "unit_id, "
                    + "cabinet, "
                    + "chassis, "
                    + "pos, "
                    + "rnc_cnumber, "
                    + "prod_manufacturer, "
                    + "prod_name, "
                    + "prod_partnumber, "
                    + "prod_version, "
                    + "prod_serial, "
                    + "prod_fileid, "
                    + "prod_custom, "
                    + "board_mfg_date, "
                    + "board_manufacturer, "
                    + "board_name, "
                    + "board_serial, "
                    + "board_part_number) "
                    + "VALUES("
                    + "'" + this.unit_name + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.unit_id + "', "
                    + "'" + this.cabinet + "', "
                    + "'" + this.chassis + "', "
                    + "'" + this.pos + "', "
                    + "'" + this.cnumber + "', "
                    + "'" + this.prod_manufacturer + "', "
                    + "'" + this.prod_name + "', "
                    + "'" + this.prod_partnumber + "', "
                    + "'" + this.prod_version + "', "
                    + "'" + this.prod_serial + "', "
                    + "'" + this.prod_fileid + "', "
                    + "'" + this.prod_custom + "', "
                    + "'" + this.board_mfg_date + "', "
                    + "'" + this.board_manufacturer + "', "
                    + "'" + this.board_name + "', "
                    + "'" + this.board_serial + "', "
                    + "'" + this.board_part_number + "')");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }
}
