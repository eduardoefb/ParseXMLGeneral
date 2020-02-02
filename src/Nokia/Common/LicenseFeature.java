package Nokia.Common;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LicenseFeature  extends DbCommon {

    private String tableName = "null";
    private int fea_code;
    private String fea_name;
    private Connection con;

    public LicenseFeature() {
        this.setUuid();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tablerName) {
        this.tableName = tablerName;
    }

    public int getFea_code() {
        return fea_code;
    }

    public void setFea_code(int fea_code) {
        this.fea_code = fea_code;
    }

    public String getFea_name() {
        return fea_name;
    }

    public void setFea_name(String fea_name) {
        this.fea_name = fea_name;
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
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE "
                    + this.tableName + ".fea_code = '" + this.fea_code + " '");

            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }

            if (linCount == 0) {
                
                Cmd = "INSERT INTO " + this.tableName + " "
                        + "( fea_code, "
                        + "uuid, "
                        + "parent_uuid, "
                        + "file_uuid, "
                        + "fea_name ) "
                        + "VALUES ("
                        + "'" + this.fea_code + "', "
                        + "'" + this.uuid + "', "
                        + "'" + this.parent_uuid + "', "
                        + "'" + this.file_uuid + "', "
                        + "'" + this.fea_name + "');";
            } else {
                Cmd = "SHOW TABLES";
            }         
            
            statement.execute(Cmd);
            
        } catch (SQLException ex) {               
            System.out.println(ex.getMessage());
        }

        return ret;
    }

}
