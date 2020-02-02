
package Nokia.Common;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PLMN  extends DbCommon {
    private int target_id;
    private String id;
    private String name;
    private String type;        
    private String tableName;
    private Connection con;

    public PLMN(){
        this.setUuid();
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
       
    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                    + "target_id, "
                    + "name, "                    
                    + "type) "
                    + "VALUES("
                    + "'" + this.id + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.target_id + "', "
                    + "'" + this.name + "', "                    
                    + "'" + this.type + "')";
            statement.executeQuery(Cmd);                        
        } catch (SQLException ex) {            
            System.out.println(ex.getMessage());
        }
        return ret;
    }

    
}
