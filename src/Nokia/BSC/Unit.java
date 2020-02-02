package Nokia.BSC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Unit  extends DbCommon {

    private final String tableName = "units_bsc";
    private int id;
    private String type;
    private String state;
    private String main_piu;
    private int memory;
    private int cnumber;
    private Connection con;
    
    public Unit(){
        this.setUuid();
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMain_piu() {
        return main_piu;
    }

    public void setMain_piu(String main_piu) {
        this.main_piu = main_piu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getBsc_cnumber() {
        return cnumber;
    }

    public void setBsc_cnumber(int bsc_cnumber) {
        this.cnumber = bsc_cnumber;
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
                    + "state, "
                    + "main_piu, "
                    + "memory, "
                    + "cnumber) "
                    + "VALUES("
                    + "'" + this.id + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.type + "', "
                    + "'" + this.state + "', "
                    + "'" + this.main_piu + "', "
                    + "'" + this.memory + "', "
                    + "'" + this.cnumber + "')");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }

}
