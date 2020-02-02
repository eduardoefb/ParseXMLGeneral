/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.Common;

import Nokia.DbCommon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public abstract class DXIPACommon extends DbCommon {

    protected Connection con;
    protected int targetId;
    protected int customerId;
    protected String type;
    protected String name;
    protected String location;
    protected String date;
    protected String spc;
    protected String state;
    protected String ip;
    protected String cd;
    protected String sw;
    
    protected String enviroment_delivery;
    protected String prfileVersion;
    protected String fifileVersion;

    public String getEnviroment_delivery() {
        return enviroment_delivery;
    }

    public void setEnviroment_delivery(String enviroment_delivery) {
        this.enviroment_delivery = enviroment_delivery;
    }

    public String getPrfileVersion() {
        return prfileVersion;
    }

    public void setPrfileVersion(String prfileVersion) {
        this.prfileVersion = prfileVersion;
    }

    public String getFifileVersion() {
        return fifileVersion;
    }

    public void setFifileVersion(String fifileVersion) {
        this.fifileVersion = fifileVersion;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSpc() {
        return spc;
    }

    public void setSpc(String spc) {
        this.spc = spc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }
    
    public String getSw() {
        return sw;
    }

    public void setSw(String sw) {
        this.sw = sw;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    protected void getPrfile(File dire, String TableName, int targetId) {
        try {

            Statement statement = this.con.createStatement();
            this.con.setAutoCommit(false);

            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/ZWOI.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            Scanner scan;
            String par_class = null, par_name = null;
            while ((line = tr.readLine()) != null) {

                //Find line:  FEATURE CODE:
                if (line.contains("PARAMETER CLASS:")) {
                    scan = new Scanner(line);
                    try {
                        scan.next();
                        scan.next();
                        par_class = scan.next();
                    } catch (Exception ex) {
                        par_class = "log corrupted";
                        System.out.println("log corrupted");
                    }
                    try {
                        par_name = scan.next();
                    } catch (Exception ex) {
                        par_name = "loc corrupted";
                        System.out.println("log corrupted.");
                    }

                } else if (!line.contains("NAME OF PARAMETER") && line.trim().length() > 20 && par_class != null) {
                    Prfilegx prf = new Prfilegx();
                    prf.setParent_uuid(this.uuid);
                    prf.setCon(con);
                    prf.setFile_uuid(this.file_uuid);
                    prf.setTableName(TableName);
                    scan = new Scanner(line);
                    prf.setTargetId(targetId);
                    prf.setParameter_class(par_class);
                    prf.setParameter_name(par_name);
                    prf.setIdentifier(scan.next());
                    prf.setName_of_parameter(scan.next());
                    prf.setValue(scan.next());
                    prf.setChange_possibility(scan.next());
                    prf.AddSqlCmd(statement);
                    

                }
            }
            int[] count = statement.executeBatch();
            con.commit();
            System.out.println("");
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        }
    }

    protected void getFifile(File dire, String TableName, int targetId) {
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/ZWOS.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            Scanner scan;
            String par_class = null, par_name = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("PARAMETER CLASS:")) {
                    scan = new Scanner(line);
                    scan.next();
                    scan.next();
                    par_class = scan.next();
                    par_name = scan.next();
                } else if (!line.contains("NAME OF PARAMETER") && line.trim().length() > 20 && par_class != null) {
                    Fifilegx fif = new Fifilegx();
                    fif.setParent_uuid(this.uuid);
                    fif.setFile_uuid(this.file_uuid);
                    fif.setCon(con);
                    fif.setTableName(TableName);
                    scan = new Scanner(line);
                    fif.setTargetId(targetId);
                    fif.setParameter_class(par_class);
                    fif.setParameter_name(par_name);
                    fif.setIdentifier(scan.next());
                    fif.setName_of_parameter(scan.next());
                    fif.setActivation_status(scan.next());
                    if (fif.getActivation_status().equals("NOT")) {
                        fif.setActivation_status("NOT IN USE");
                    }
                    fif.SendToMysql();
                }
            }

        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        }
    }

    protected void getFeatures(File dire, String FeaTableName, String NeTableName, int targetId) {
        LicenseFeature fea = null;
        NeToFea ne = null;
        HashMap NeList = new HashMap();
        List NeFeaCode = new ArrayList();

        //License features
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/ZW7IFULL.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            Scanner scan;
            while ((line = tr.readLine()) != null) {
                //Find line:  FEATURE CODE:
                if (line.contains("FEATURE CODE:")) {
                    fea = new LicenseFeature();
                    fea.setFile_uuid(this.file_uuid);
                    fea.setParent_uuid(this.uuid);
                    ne = new NeToFea();
                    ne.setFile_uuid(this.file_uuid);
                    ne.setParent_uuid(this.uuid);
                    fea.setParent_uuid(this.uuid);
                    ne.setTargetId(targetId);
                    ne.setLk_usage("-");
                    ne.setCon(this.con);
                    fea.setCon(this.con);
                    fea.setTableName(FeaTableName);
                    ne.setTableName(NeTableName);

                    String code = line.replace("FEATURE CODE:", "").replace(".", "").trim();
                    fea.setFea_code(Integer.parseInt(code));
                    ne.setFea_code_fk(Integer.parseInt(code));
                    ne.setId();

                    //FEATURE NAME:
                    line = tr.readLine();
                    fea.setFea_name(line.replace("FEATURE NAME:", "").replace(".", "").trim());
                    fea.SendToMysql();

                    //FEATURE STATE:
                    line = tr.readLine();

                    //FEATURE CAPACITY:
                    line = tr.readLine();
                    ne.setCapacity(line.replace("FEATURE CAPACITY:", "").replace(".", "").trim());
                    NeList.put(ne.getFea_code_fk(), ne);
                    NeFeaCode.add(ne.getFea_code_fk());

                }
            }

            //Feature usage:
            if (!new File(dire.getAbsoluteFile() + "/ZW7IUCAP.log").exists()) {
                //Retrive the fea codes and send to mysql:
                for (int i = 0; i < NeFeaCode.size(); i++) {
                    ne = (NeToFea) NeList.get(NeFeaCode.get(i));
                    if (ne != null) {
                        ne.SendToMysql();
                    }
                }
            }

            fp = new FileReader(dire.getAbsoluteFile() + "/ZW7IUCAP.log");
            tr = new BufferedReader(fp);
            while ((line = tr.readLine()) != null) {
                //Find line:  FEATURE CODE:
                if (line.contains("REQUEST STATUS")) {
                    line = tr.readLine();

                    while ((line = tr.readLine()) != null) {
                        if (line.trim().length() < 4) {
                            break;
                        }
                        if (!line.contains("SUCCESS") && !line.contains("EXIST")) {
                            scan = new Scanner(line);
                            String fcode = scan.next();
                            String fusage = scan.next();
                            ne = (NeToFea) NeList.get(Integer.parseInt(fcode));
                            if (ne != null) {
                                ne.setLk_usage(fusage);
                                NeList.put(Integer.parseInt(fcode), ne);
                            }
                        }
                    }

                }
            }

            //Retrive the fea codes and send to mysql:
            for (int i = 0; i < NeFeaCode.size(); i++) {
                ne = (NeToFea) NeList.get(NeFeaCode.get(i));
                if (ne != null) {
                    ne.SendToMysql();
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (NumberFormatException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        }

    }

    protected void getLicenseFiles(File dire, String tableName, int targetId) {
        LicenseFile lk = null;
        //License features
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/ZW7ILIC.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            Scanner scan;
            while ((line = tr.readLine()) != null) {
                //Find line:  FEATURE CODE:
                if (line.contains("LICENCE CODE:")) {
                    //LICENCE CODE: - nothing to add to database, just create the object
                    lk = new LicenseFile();
                    lk.setFile_uuid(this.file_uuid);
                    lk.setParent_uuid(this.uuid);
                    lk.setCon(this.con);
                    lk.setTableName(tableName);
                } else if (line.contains("LICENCE NAME:")) {
                    //LICENCE NAME:                    
                    lk.setLicencename(line.replace("LICENCE NAME:", "").replace(".", "").trim());
                } else if (line.contains("LICENCE FILE NAME:")) {
                    //LICENCE FILE NAME:                    
                    lk.setFilename(line.replace("LICENCE FILE NAME:", "").replace(".", "").trim() + ".XML");
                } else if (line.contains("SERIAL NUMBER:")) {
                    //SERIAL NUMBER:                    
                    lk.setSerialnumber(line.replace("SERIAL NUMBER:", "").replace(".", "").trim());

                } else if (line.contains("ORDER IDENTIFIER:")) {
                    //ORDER IDENTIFIER:
                    lk.setOrderid(line.replace("ORDER IDENTIFIER:", "").replace(".", "").trim());

                } else if (line.contains("CUSTOMER ID:")) {
                    //CUSTOMER ID:
                    lk.setCustomerId(line.replace("CUSTOMER ID:", "").replace(".", "").trim());

                } else if (line.contains("CUSTOMER NAME:")) {
                    //CUSTOMER NAME:                    
                    lk.setCustomername(line.replace("CUSTOMER NAME:", "").replace(".", "").trim());

                } else if (line.contains("TARGET ID:")) {
                    //TARGET ID:                    
                    lk.setTargetid(Integer.parseInt(line.replace("TARGET ID:", "").replace(".", "").trim()));

                } else if (line.contains("LICENCE STATE:")) {
                    //LICENCE STATE:                    
                    lk.setLfstate(line.replace("LICENCE STATE:", "").replace(".", "").trim());

                } else if (line.contains("START DATE:")) {
                    //START DATE:                    
                    lk.setStart_date(line.replace("START DATE:", "").replace(".", "").trim());

                } else if (line.contains("LICENCE CAPACITY:")) {
                    //LICENCE CAPACITY:                    

                    lk.setMax_value(Integer.parseInt(line.replace("LICENCE CAPACITY:", "").replace(".", "").trim()));

                    //Open licence file and get information about licence code:
                    FileReader fpf = new FileReader(dire.getAbsoluteFile() + "/LICENCE/" + lk.getFilename());
                    BufferedReader trf = new BufferedReader(fpf);
                    String lin;
                    Scanner scanf;
                    while ((lin = trf.readLine()) != null) {
                        
                        //Find line:  FEATURE CODE:
                        if (lin.contains("featureCode=")) {
                            String fean;
                            String feac;
                            String[] l = lin.split("featureCode=");
                            scanf = new Scanner(l[1]);
                            feac = scanf.next().replace("\"", "");
                            l = lin.split("featureName=");
                            String[] ll = l[1].split("\"");
                            fean = ll[1].trim();
                            lk.addFea(feac, fean);
                            LicenseFeature f = new LicenseFeature();
                            f.setFile_uuid(this.file_uuid);
                            f.setParent_uuid(this.uuid);
                            f.setFea_code(Integer.parseInt(feac));
                            f.setFea_name(fean);
                            f.setCon(this.con);
                            f.setTableName("licence_rnc");

                            f.SendToMysql();
                        }
                    }

                    lk.setTableName(tableName);
                    lk.setTargetid(this.targetId);
                    lk.SendToMysql();
                }

            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
    }

    protected String getSingleLineFromFile(String log) {
        String ret = "0";
        try {
            FileReader fp = new FileReader(log);
            BufferedReader tr = new BufferedReader(fp);
            ret = tr.readLine();

        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nNE Name:: " + this.name
                    + "\nTargetId: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nNE Name:: " + this.name
                    + "\nTargetId: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
        return ret;
    }

    protected String getString(String log, String str, int after_lines, int index) {
        String ret = "0";
        try {
            FileReader fp = new FileReader(log);
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                if (line.contains(str)) {
                    for (int i = 0; i < after_lines; i++) {
                        line = tr.readLine();
                    }
                    Scanner scan = new Scanner(line);
                    for (int i = 1; i <= index; i++) {
                        line = scan.next();
                    }

                    ret = line;
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nNE Name:: " + this.name
                    + "\nTargetId: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nNE Name:: " + this.name
                    + "\nTargetId: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
        return ret;
    }

    protected String findDate(String log) {
        String ret = "0000-00-00";
        try {
            FileReader fp = new FileReader(log);
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                if (line.contains("-") && line.contains(":")) {
                    if (!line.contains("MAN>")) {
                        Scanner scan = new Scanner(line);
                        scan.next();
                        scan.next();
                        ret = scan.next().trim();
                        break;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nNE Name:: " + this.name
                    + "\nTargetId: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nNE Name:: " + this.name
                    + "\nTargetId: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
        return ret;
    }

}
