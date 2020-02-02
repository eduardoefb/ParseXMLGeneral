package getConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class config {

    private final String FILENAME = "/opt/nokia/nedata/scripts/var.conf";
    private String dbUser;
    private String dbPass;
    private String db1;
    private String db2;

    
    public String getDb1() {
        return db1;
    }

    public void setDb1(String db1) {
        this.db1 = db1;
    }

    public String getDb2() {
        return db2;
    }

    public void setDb2(String db2) {
        this.db2 = db2;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getConfVariables() {
        String line;
        String work_dir = "", cf_dir = "", db_conf_file = "";

        try {
            FileReader fp = new FileReader(this.FILENAME);
            BufferedReader tr = new BufferedReader(fp);
            while ((line = tr.readLine()) != null) {

                if (line.contains("export WORK_DIR")) {
                    work_dir = line.split("=")[1].replace("\"", "");

                } else if (line.contains("export CF_FILES_DIR")) {
                    cf_dir = line.replace("$WORK_DIR", work_dir).split("=")[1].replace("\"", "");                    
                } else if (line.contains("export DB_CONFIG_FILE")) {
                    db_conf_file = line.replace("$CF_FILES_DIR", cf_dir).split("=")[1].replace("\"", "");
                } else if (line.contains("export DB1_NAME=")) {
                    this.db1 = line.replace("export DB1_NAME=", "").replace("\"", "").trim();
                } else if (line.contains("export DB2_NAME=")) {
                    this.db2 = line.replace("export DB2_NAME=", "").replace("\"", "").trim();
                }

            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return db_conf_file;
    }

    public config() {
        String line;
        String ret = "";
        String fName = this.getConfVariables();
        
        try {
            FileReader fp = new FileReader(fName);
            BufferedReader tr = new BufferedReader(fp);
            while ((line = tr.readLine()) != null) {
                if (line.contains("mysql_user = ")) {
                    this.setDbUser(line.replace("mysql_user = ", "").trim());
                } else if (line.contains("mysql_user_pw = ")) {
                    this.setDbPass(line.replace("mysql_user_pw = ", "").trim());
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
