package Nokia.RNC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Unit  extends DbCommon {

    private String tableName = "units_rnc";
    private Connection con;
    private int id;
    private String type;
    private String state;
    private String pluginunit;
    private int memory;
    private int cnumber;
    private String iti;
    private String sen;
    private int chms;
    private int shms;
    private int ppa;

    public Unit() {
    }
    
    public int getId() {
        return id;
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
    
    

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPluginunit() {
        return pluginunit;
    }

    public void setPluginunit(String pluginunit) {
        this.pluginunit = pluginunit;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getCnumber() {
        return cnumber;
    }

    public void setCnumber(int cnumber) {
        this.cnumber = cnumber;
    }

    public String getIti() {
        return iti;
    }

    public void setIti(String iti) {
        this.iti = iti;
    }

    public String getSen() {
        return sen;
    }

    public void setSen(String sen) {
        this.sen = sen;
    }

    public int getChms() {
        return chms;
    }

    public void setChms(int chms) {
        this.chms = chms;
    }

    public int getShms() {
        return shms;
    }

    public void setShms(int shms) {
        this.shms = shms;
    }

    public int getPpa() {
        return ppa;
    }

    public void setPpa(int ppa) {
        this.ppa = ppa;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();
            statement.execute("INSERT INTO " + this.tableName + " ("
                    + "id, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "type, "
                    + "state, "
                    + "pluginunit, "
                    + "memory, "
                    + "cnumber, "
                    + "iti, "
                    + "sen, "
                    + "chms, "
                    + "shms, "
                    + "ppa) "
                    + "VALUES("
                    + "'" + this.id + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.type + "', "
                    + "'" + this.state + "', "
                    + "'" + this.pluginunit + "', "
                    + "'" + this.memory + "', "
                    + "'" + this.cnumber + "', "
                    + "'" + this.iti + "', "
                    + "'" + this.sen + "', "
                    + "'" + this.chms + "', "
                    + "'" + this.shms + "', "
                    + "'" + this.ppa + "')");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
