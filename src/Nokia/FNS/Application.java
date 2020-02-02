package Nokia.FNS;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application  extends DbCommon {

    private String tableName = "fns_application";
    private String unit_type;
    private String unit_id;
    private String application_type;
    private String application_ipv4;
    private String application_ipv6;
    private int targetId;
    Connection con;

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

    public Application() {
        this.setUuid();
    }

    public String getApplication_ipv4() {
        return application_ipv4;
    }

    public void setApplication_ipv4(String application_ipv4) {
        this.application_ipv4 = application_ipv4;
    }

    public String getApplication_ipv6() {
        return application_ipv6;
    }

    public void setApplication_ipv6(String application_ipv6) {
        this.application_ipv6 = application_ipv6;
    }

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getApplication_type() {
        return application_type;
    }

    public void setApplication_type(String application_type) {
        this.application_type = application_type;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();

            Cmd = "INSERT INTO " + this.tableName + "(target_id, "
                    + "unit_type, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "unit_id, "
                    + "application_type, "
                    + "application_ipv4, "
                    + "application_ipv6) "                    
                    + "VALUES('" + this.targetId + "', "
                    + "'" + this.unit_type + "',"
                    + "'" + this.uuid + "',"
                    + "'" + this.parent_uuid + "',"
                    + "'" + this.unit_id + "',"
                    + "'" + this.application_type + "',"
                    + "'" + this.application_ipv4 + "',"
                    + "'" + this.application_ipv6 + "')";                        
            statement.execute(Cmd);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

}
