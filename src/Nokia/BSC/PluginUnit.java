package Nokia.BSC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PluginUnit  extends DbCommon {

    private String tableName = "plugin_units_bsc";
    private Connection con;
    private int id;
    private String type;
    private String unit_type;
    private int unit_id;
    private int cnumber;
    private String rack;
    private String cartridge;
    private String track;

    public PluginUnit(){
        this.setUuid();
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

    public int getId() {
        return id;
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

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public int getBsc_cnumber() {
        return cnumber;
    }

    public void setBsc_cnumber(int bsc_cnumber) {
        this.cnumber = bsc_cnumber;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getCartridge() {
        return cartridge;
    }

    public void setCartridge(String cartridge) {
        this.cartridge = cartridge;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
    
    public boolean AddSqlCmd(Statement statement) {
        String Cmd = "";
        boolean ret = false;        
        try {            
            statement.addBatch("INSERT INTO " + this.tableName + " ("
                    + "id, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "type, "
                    + "unit_type, "
                    + "rack, "
                    + "cartridge, "
                    + "track, "
                    + "unit_id, "
                    + "cnumber) "
                    + "VALUES("
                    + "'" + this.id + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.type + "', "
                    + "'" + this.unit_type + "', "
                    + "'" + this.rack + "', "
                    + "'" + this.cartridge + "', "
                    + "'" + this.track + "', "
                    + "'" + this.unit_id + "', "
                    + "'" + this.cnumber + "')");
            
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return ret;
    }    

}
