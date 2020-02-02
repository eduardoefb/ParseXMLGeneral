package getConfigGeneral.Aux;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mObject {

    private List<Listi> ListItemList = new ArrayList();
    private List<pName> pNameList = new ArrayList();
    private String classType;
    private String version;
    private String distName;
    private String dateTime;
    private String id;
    private String file_uuid;
    private int customerId;
    private final int STR_SIZE = 100;
    private final int DIST_STR_SIZE = 100;
    private final String sql_dir = "/tmp/sql/";
    private Connection con;
    private String prefix = "xml";
    private boolean used = false;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean getUsed() {
        return used;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void addPName(pName arg) {
        this.pNameList.add(arg);
    }

    public void addListItem(Listi arg) {
        this.ListItemList.add(arg);
    }

    public List<Listi> getListItemList() {
        return ListItemList;
    }

    public void setListItemList(List<Listi> ListItemList) {
        this.ListItemList = ListItemList;
    }

    public List<pName> getpNameList() {
        return pNameList;
    }

    public void setpNameList(List<pName> pNameList) {
        this.pNameList = pNameList;
    }

    public mObject(Connection con) {
        this.con = con;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = this.prefix + "." + classType;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile_uuid() {
        return file_uuid;
    }

    public void setFile_uuid(String file_uuid) {
        this.file_uuid = file_uuid;
    }
    
    

    public void printAll() {
        System.out.println("Table name: " + this.classType + ", Distname: " + this.distName);
        for (int i = 0; i < this.pNameList.size(); i++) {
            System.out.println("   Attribute:" + this.pNameList.get(i).getName() + " Value: " + this.pNameList.get(i).getValue());
        }

        for (int i = 0; i < this.ListItemList.size(); i++) {
            for (int j = 0; j < this.ListItemList.get(i).getValueList().size(); j++) {
                System.out.println("   List: " + this.ListItemList.get(i).getName() + " Value " + j + ": " + this.ListItemList.get(i).getValueList().get(j));
            }
            for (int j = 0; j < this.ListItemList.get(i).getItemList().size(); j++) {
                for (int k = 0; k < this.ListItemList.get(i).getItemList().get(j).getpNameList().size(); k++) {
                    System.out.println("   List: " + this.ListItemList.get(i).getName() + " Attribute " + j + ": " + this.ListItemList.get(i).getItemList().get(j).getpNameList().get(k).getName() + " Value:" + this.ListItemList.get(i).getItemList().get(j).getpNameList().get(k).getValue());
                }
            }
        }
    }

    public void save(Statement st, sqlCommands cmdList) {
        String cmd = null;

        //Create table:  
        try {
            cmd = "CREATE TABLE IF NOT EXISTS " + this.classType + "(";
            if (this.pNameList.size() > 0) {
                cmd = cmd + "distName VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "dateTime VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "file_uuid VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "customerId VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "_version VARCHAR(" + this.DIST_STR_SIZE + "), ";
            } else {
                cmd = cmd + "distName VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "dateTime VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "file_uuid VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "customerId VARCHAR(" + this.DIST_STR_SIZE + "), "
                        + "_version VARCHAR(" + this.DIST_STR_SIZE + "))";
            }
            
            for (int i = 0; i < this.pNameList.size(); i++) {
                if (i == this.pNameList.size() - 1) {
                    cmd = cmd + " " + this.pNameList.get(i).getName() + " VARCHAR(" + this.STR_SIZE + ") DEFAULT \'-\')";
                } else {
                    cmd = cmd + " " + this.pNameList.get(i).getName() + " VARCHAR(" + this.STR_SIZE + ") DEFAULT \'-\', ";
                }
            }
            cmd = cmd + ";";
            addAlterSaveCommands(cmd, cmdList, st);

  
            String genenralDistName = "";
            if (this.distName.split("/").length > 2) {
                genenralDistName = this.distName.split("/")[0] + "/" + this.distName.split("/")[1] + "/";
            } else {
                genenralDistName = this.distName;

            }
            cmd = "DELETE FROM " + this.classType + " WHERE " + this.classType + 
                    ".customerId = \"" + this.customerId + 
                    "\" AND " + this.classType + 
                    ".distName LIKE \"" + genenralDistName + "%\";";
            
            addAlterSaveCommands(cmd, cmdList, st);
            if (this.ListItemList.size() > 0) {
                cmd = "DELETE FROM " + this.classType + "_" + this.ListItemList.get(0).getName() + 
                        " WHERE " + this.classType + "_" + this.ListItemList.get(0).getName() + 
                        ".customerId = \"" + this.ListItemList.get(0).getCustomerId() + 
                        "\" AND " + this.classType + "_" + this.ListItemList.get(0).getName() + 
                        ".distName LIKE \"" + genenralDistName + "%\";";
                addAlterSaveCommands(cmd, cmdList, st);
            }

            //Check if new column is added. If column is not in the table, than it will be added:            
            for (int i = 0; i < this.pNameList.size(); i++) {
                cmd = "ALTER TABLE " + this.classType + " ADD COLUMN " + this.pNameList.get(i).getName() + " VARCHAR(" + this.STR_SIZE + ") DEFAULT '-';";
                addAlterSaveCommands(cmd, cmdList, st);
            }

            if (this.pNameList.size() > 0) {
                cmd = "INSERT INTO " + this.classType + "(distName, "
                        + "dateTime, "
                        + "file_uuid, "
                        + "customerId, "
                        + "_version, ";

                for (int i = 0; i < this.pNameList.size(); i++) {
                    if (i == this.pNameList.size() - 1) {
                        cmd = cmd + this.pNameList.get(i).getName() + ")";
                    } else {
                        cmd = cmd + this.pNameList.get(i).getName() + ", ";
                    }
                }
                cmd = cmd + " VALUES('" + this.getDistName() + "', "
                        + "'" + this.getDateTime() + "', "
                        + "'" + this.getFile_uuid() + "', "
                        + "'" + this.getCustomerId() + "', "
                        + "'" + this.getVersion() + "', ";

                for (int i = 0; i < this.pNameList.size(); i++) {
                    if (i == this.pNameList.size() - 1) {
                        cmd = cmd + "'" + this.pNameList.get(i).getValue() + "')";
                    } else {
                        cmd = cmd + "'" + this.pNameList.get(i).getValue() + "', ";
                    }
                }
                cmd = cmd + ";";
                st.addBatch(cmd);

            } else {
                cmd = "INSERT INTO " + this.classType + "(distName)";
                cmd = cmd + " VALUES('" + this.getDistName() + "')";
                st.addBatch(cmd);
            }

            for (int i = 0; i < this.ListItemList.size(); i++) {
                addAlterSaveCommands("CREATE TABLE IF NOT EXISTS "
                        + this.classType + "_"
                        + this.ListItemList.get(i).getName()
                        + "(distName VARCHAR(" + this.DIST_STR_SIZE
                        + "), _values VARCHAR(" + this.STR_SIZE
                        + "), file_uuid VARCHAR(" + this.STR_SIZE
                        + "), customerId VARCHAR(" + this.STR_SIZE
                        + "));", cmdList, st);

                for (int j = 0; j < this.ListItemList.get(i).getValueList().size(); j++) {
                    
                    st.addBatch("INSERT INTO "
                            + this.classType
                            + "_" + this.ListItemList.get(i).getName()
                            + "(distName, file_uuid, _values) VALUES('"
                            + this.getDistName() + "', '"
                            + this.ListItemList.get(i).getFile_uuid() + "', '"
                            + this.ListItemList.get(i).getValueList().get(j)
                            + "');");
                }
                for (int j = 0; j < this.ListItemList.get(i).getItemList().size(); j++) {
                    String cmd1 = "INSERT INTO " + this.classType + "_" + this.ListItemList.get(i).getName() + "(distName, customerId, file_uuid";
                    String cmd2 = " VALUES('" + this.getDistName() + "'" + ",'" + this.getCustomerId() + "','" + this.ListItemList.get(i).getFile_uuid() + "'" ;

                    for (int k = 0; k < this.ListItemList.get(i).getItemList().get(j).getpNameList().size(); k++) {
                        addAlterSaveCommands("ALTER TABLE " + this.classType + "_" + this.ListItemList.get(i).getName() + " ADD COLUMN " + this.ListItemList.get(i).getItemList().get(j).getpNameList().get(k).getName() + " VARCHAR(" + this.STR_SIZE + ") DEFAULT '-';", cmdList, st);
                        cmd1 = cmd1 + ", " + this.ListItemList.get(i).getItemList().get(j).getpNameList().get(k).getName();
                        String s = this.ListItemList.get(i).getItemList().get(j).getpNameList().get(k).getValue();
                        if (s.equals("'")) {
                            s = "''";
                        }
                        cmd2 = cmd2 + ", '" + s + "'";
                    }
                    cmd1 = cmd1 + ")";
                    cmd2 = cmd2 + ")";
                    cmd = cmd1 + cmd2 + ";";
                    st.addBatch(cmd);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(mObject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String toString() {
        return "mObject{" + "ListItemList=" + ListItemList + ", pNameList=" + pNameList + '}';
    }

    private int addAlterSaveCommands(String cmd, sqlCommands cmdList, Statement st) {
        int error = 0;
        cmdList.addCmd(cmd);
        System.out.printf("");
        return error;
    }

    public int getSTR_SIZE() {
        return STR_SIZE;
    }

    public int getDIST_STR_SIZE() {
        return DIST_STR_SIZE;
    }

    public String getSql_dir() {
        return sql_dir;
    }

    public Connection getCon() {
        return con;
    }

}
