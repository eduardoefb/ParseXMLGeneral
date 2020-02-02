package getConfigGeneral;

import Nokia.BSC.BSC;
import Nokia.FNS.FNS;
import Nokia.FNS.SGSN;
import Nokia.PrintMessage;
import Nokia.RNC.RNC;
import Nokia.mcRNC.mcRNC;
import getConfig.config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import parselog.Customer;

public class logParser {

    private String dbUser;
    private String dbPass;
    private final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private String work_dir = "/opt/nokia/nedata/logs/";
    private String dbUrl = "";
    private final String currenDirString = "-current_database";
    private final String Version = "0.0.14";

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

    public String getWork_dir() {
        return work_dir;
    }

    public void setWork_dir(String work_dir) {
        this.work_dir = work_dir;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public logParser(String direName, int custId) {
        config conf = new config();
        this.dbUser = conf.getDbUser();
        this.dbPass = conf.getDbPass();
        this.dbUrl = "jdbc:mariadb://127.0.0.1:3306/" + conf.getDb1();

        List<Customer> custList = new ArrayList();
        int curr_dir = 0;
        int tot_dir = 0;

        System.out.println("ParseLog  Version: " + Version);
        try {
            Connection connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);

            //Get customers:
            custList = getCustomerList(connection);
            tot_dir = getDirectories();

            //Check directories inside customer directories:
            long startTime = System.currentTimeMillis();

            String custDire = direName;

            File[] elist = new File(custDire).listFiles();
            for (int k = 0; k < elist.length; k++) {

                if (elist[k].isDirectory() && !elist[k].getAbsoluteFile().toString().endsWith(currenDirString)) {                    
                    String file_uuid = elist[k].getAbsoluteFile().getName().split("-")[0];                    
                    curr_dir++;
                    try {
                        FileReader fp = new FileReader(elist[k] + "/ELEMENT_TYPE");
                        BufferedReader tr = new BufferedReader(fp);
                        String elType = tr.readLine();
                        switch (elType) {
                            case "BSC":
                                BSC bsc = new BSC(elist[k], custId, connection);                                
                                bsc.setFile_uuid(file_uuid);
                                bsc.setMessage("(" + curr_dir + " of " + tot_dir + ")");
                                bsc.RemoveFromMysql();
                                bsc.SendToMysql();
                                break;

                            case "FlexiNS":
                                FNS fns = new FNS(elist[k], custId, connection);
                                fns.setFile_uuid(file_uuid);
                                fns.setMessage("(" + curr_dir + " of " + tot_dir + ")");
                                fns.RemoveFromMysql();
                                fns.SendToMysql();
                                break;

                            case "SGSN":
                                SGSN sgsn = new SGSN(elist[k], custId, connection);
                                sgsn.setFile_uuid(file_uuid);
                                sgsn.setMessage("(" + curr_dir + " of " + tot_dir + ")");
                                sgsn.RemoveFromMysql();
                                sgsn.SendToMysql();
                                break;

                            case "RNC":
                                FileReader rfp = new FileReader(elist[k] + "/RNC_TYPE");                                
                                BufferedReader rtr = new BufferedReader(rfp);
                                String rtype = rtr.readLine();
                                rfp.close();
                                switch (rtype) {
                                    case "RNC":
                                        RNC rnc = new RNC(elist[k], custId, connection);
                                        rnc.setFile_uuid(file_uuid);
                                        rnc.setMessage("( " + curr_dir + " of " + tot_dir + ")");
                                        rnc.RemoveFromMysql();
                                        rnc.SendToMysql();
                                        break;

                                    case "mcRNC":
                                        mcRNC mcrnc = new mcRNC(elist[k], custId, connection);
                                        mcrnc.setFile_uuid(file_uuid);
                                        mcrnc.setMessage("( " + curr_dir + " of " + tot_dir + ")");
                                        mcrnc.RemoveFromMysql();
                                        mcrnc.SendToMysql();

                                        break;

                                    default:
                                        break;
                                }
                                break;

                            default:
                                //System.out.println(elist[k] + " " + elType + "is not yet supported!");
                                break;
                        }

                        File ftemp = new File(elist[k].getAbsoluteFile() + currenDirString);
                        if (ftemp.exists()) {
                            File ftempLk = new File(elist[k].getAbsoluteFile() + currenDirString + "/LICENCE/");
                            if (ftempLk.exists()) {
                                String[] files = ftempLk.list();
                                for (String s : files) {
                                    File lkfile = new File(ftempLk.getPath(), s);
                                    lkfile.delete();
                                }
                            }
                            String[] files = ftemp.list();
                            for (String s : files) {
                                File ft = new File(ftemp.getPath(), s);
                                ft.delete();
                            }

                        }
                        elist[k].renameTo(new File(elist[k].getAbsoluteFile() + currenDirString));

                        if (curr_dir > 0) {

                            long totalTime = (long) System.currentTimeMillis() - (long) startTime;
                            long timeByNe = (long) totalTime / curr_dir;
                            long expectedTime = (long) timeByNe * (long) (tot_dir - curr_dir);

                            int tsec = (int) ((long) totalTime / 1000);
                            int thour = (int) (tsec / 3600);
                            int tmin = (tsec % 3600) / 60;
                            tsec = (tsec % 60);

                            int esec = (int) ((long) expectedTime / 1000);
                            int ehour = (int) (esec / 3600);
                            int emin = (esec % 3600) / 60;
                            esec = (esec % 60);

                            new PrintMessage("\n\n--> Total time:      " + thour + "h " + tmin + "min " + tsec + "sec"
                                    + "\n--> Expected time:   " + ehour + "h " + emin + "min " + esec + "sec\n");

                        }

                    } catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

            }

            //Get BSCs:            
            connection.close();
        } catch (SQLException ex) {
            //System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    private int getDirectories() {
        int ret = 0;

        List<Customer> custList = new ArrayList();

        try {
            Connection con = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);

            //Get customers:
            custList = getCustomerList(con);

            //Check directories inside customer directories:            
            for (int j = 0; j < custList.size(); j++) {
                String custDire = this.work_dir + "/" + custList.get(j).getName();
                File[] elist = new File(custDire).listFiles();
                for (int k = 0; k < elist.length; k++) {
                    if (elist[k].isDirectory() && !elist[k].getAbsoluteFile().toString().endsWith(this.currenDirString)) {
                        ret++;
                    }
                }
            }
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return ret;
    }

    private List<Customer> getCustomerList(Connection con) {
        List<Customer> ret = new ArrayList();
        File dire = new File(this.work_dir);
        File[] dlist = dire.listFiles();
        for (int i = 0; i < dlist.length; i++) {
            File idf = new File(dlist[i] + "/.id");
            try {
                FileReader fp = new FileReader(dlist[i] + "/.id");
                BufferedReader tr = new BufferedReader(fp);
                Customer c = new Customer();
                c.setId(Integer.parseInt(tr.readLine()));
                c.setCon(con);
                c.setName(dlist[i].getName());
                c.SendToMysql();
                ret.add(c);

            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }
        return ret;
    }

}
