package Nokia.Common;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LicenseFile extends DbCommon {

    private String tableName;
    private Connection con;

    private String filename;
    private String lfstate;
    private String start_date;
    private String licencename;
    private String orderid;
    private String serialnumber;
    private String customerId;
    private String customername;
    private int max_value;
    private int targetid;

    private List<String> fea_name_list;
    private List<String> fea_code_list;

    public LicenseFile() {
        this.setUuid();
        fea_name_list = new ArrayList();
        fea_code_list = new ArrayList();

    }

    public void addFea(String fea_code, String fea_name) {
        if (fea_code != null && fea_name != null) {
            fea_name_list.add(fea_name);
            fea_code_list.add(fea_code);
        }
    }

    public List<String> getFea_name_list() {
        return fea_name_list;
    }

    public void setFea_name_list(List<String> fea_name_list) {
        this.fea_name_list = fea_name_list;
    }

    public List<String> getFea_code_list() {
        return fea_code_list;
    }

    public void setFea_code_list(List<String> fea_code_list) {
        this.fea_code_list = fea_code_list;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLfstate() {
        return lfstate;
    }

    public void setLfstate(String lfstate) {
        this.lfstate = lfstate;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getLicencename() {
        return licencename;
    }

    public void setLicencename(String licencename) {
        this.licencename = licencename;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public int getMax_value() {
        return max_value;
    }

    public void setMax_value(int max_value) {
        this.max_value = max_value;
    }

    public int getTargetid() {
        return targetid;
    }

    public void setTargetid(int targetid) {
        this.targetid = targetid;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;
        try {
            statement = con.createStatement();

            for (int i = 0; i < this.fea_code_list.size(); i++) {
                Cmd = "INSERT INTO " + this.tableName + " ("
                        + "filename, "
                        + "uuid, "
                        + "parent_uuid, "
                        + "file_uuid, "
                        + "lfstate, "
                        + "start_date, "
                        + "licencename, "
                        + "orderid, "
                        + "serialnumber, "
                        + "customerid, "
                        + "customername, "
                        + "max_value, "
                        + "targetid, "
                        + "featurename, "
                        + "fea_code) "
                        + "VALUES("
                        + "'" + this.filename + "', "
                        + "'" + this.uuid + "', "
                        + "'" + this.parent_uuid + "', "
                        + "'" + this.file_uuid + "', "
                        + "'" + this.lfstate + "', "
                        + "'" + this.start_date + "', "
                        + "'" + this.licencename + "', "
                        + "'" + this.orderid + "', "
                        + "'" + this.serialnumber + "', "
                        + "'" + this.customerId + "', "
                        + "'" + this.customername + "', "
                        + "'" + this.max_value + "', "
                        + "'" + this.targetid + "', "
                        + "'" + this.fea_name_list.get(i) + "', "
                        + "'" + this.fea_code_list.get(i) + "') ";
                statement.executeQuery(Cmd);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }

}
