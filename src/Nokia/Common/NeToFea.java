package Nokia.Common;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class NeToFea  extends DbCommon {

    private final int MAX_FEA_NUMBER = 100000;
    private String tableName = "null";
    private double id;
    private int targetId;
    private int feaCode;
    private String capacity;
    private String usage;
    private Connection con;

    public NeToFea(){
        this.setUuid();
    }
    
    public void setId() {
        this.id = ((double) this.targetId * (double) this.MAX_FEA_NUMBER) + (double) this.feaCode;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public int getTargetid() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getFea_code_fk() {
        return feaCode;
    }

    public void setFea_code_fk(int fea_code_fk) {
        this.feaCode = fea_code_fk;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getLk_usage() {
        return usage;
    }

    public void setLk_usage(String lk_usage) {
        this.usage = lk_usage;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;
        try {
            statement = con.createStatement();
            Cmd = "INSERT INTO " + this.tableName + "("
                    + "id, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "cnumber_fk, "
                    + "fea_code_fk, "
                    + "capacity, "
                    + "lk_usage) "
                    + "VALUES("
                    + "'" + this.id + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.targetId + "', "
                    + "'" + this.feaCode + "', "
                    + "'" + this.capacity + "', "
                    + "'" + this.usage + "')";
            statement.executeQuery(Cmd);
                        
        } catch (SQLException ex) {            
            System.out.println(ex.getMessage());
        }
        return ret;
    }

}
