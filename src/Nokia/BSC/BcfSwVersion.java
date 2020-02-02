package Nokia.BSC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class BcfSwVersion  extends DbCommon {

    private final String tableName = "bcf_sw_version";
    private int id;
    private String bcf_type;
    private String version_id;
    private String version_name;
    private Connection con;

    public void setCon(Connection con) {
        this.con = con;
    }

    public BcfSwVersion() {
        this.setUuid();
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getId() {
        Statement statement = null;
        int id = 0;
        try {
            statement = con.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE "
                    + this.tableName + ".bcf_type = '" + this.bcf_type + "' AND "
                    + this.tableName + ".version_id = '" + this.version_id + "'");
            ResultSetMetaData met = resultset.getMetaData();

            while (resultset.next()) {
                id = Integer.parseInt(resultset.getObject(1).toString());                
                break;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        this.id = id;
        return id;
    }

    public void setId() {
        Statement statement = null;
        int id = 0;
        try {
            statement = con.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName);

            while (resultset.next()) {
                id++;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        this.id = id;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE "
                    + this.tableName + ".bcf_type = '" + this.bcf_type + "' AND "
                    + this.tableName + ".version_id = '" + this.version_id + "'");

            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }
            if (this.version_name != null && linCount == 0) {

                Cmd = "INSERT INTO " + this.tableName + "( id, "
                        + "bcf_type, "
                        + "uuid, "
                        + "parent_uuid, "
                        + "file_uuid, "
                        + "version_id, "
                        + "version_name) "
                        + "VALUES('" + this.id + "', "
                        + "'" + this.bcf_type + "',"
                        + "'" + this.uuid + "',"
                        + "'" + this.parent_uuid + "',"
                        + "'" + this.file_uuid + "',"
                        + "'" + this.version_id + "',"
                        + "'" + this.version_name + "')";
            } else {
                Cmd = "SHOW TABLES";
            }

            statement.execute(Cmd);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBcf_type() {
        return bcf_type;
    }

    public void setBcf_type(String bcf_type) {
        this.bcf_type = bcf_type;
    }

    public String getVersion_id() {
        return version_id;
    }

    public void setVersion_id(String version_id) {
        this.version_id = version_id;
    }

}
