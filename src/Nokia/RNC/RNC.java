/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.RNC;

import Nokia.Plataforms.IPA2800;
import Nokia.PrintMessage;
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

/**
 *
 * @author eduabati
 */
public class RNC extends IPA2800 {

    private String tableName = "rnc";
    private int rnc_id;
    private int icsus;
    private File dire;
    private String message;

    public File getDire() {
        return dire;
    }

    public void setDire(File dire) {
        this.dire = dire;
    }

    private String getRncType(File log) {
        String ret = "RNC2600";
        int icsu = 0;
        try {
            FileReader fp = new FileReader(log.getAbsoluteFile() + "/ZUSI.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                if (line.contains("GTPU-")) {
                    ret = "RNC450";
                } else if (line.contains("ICSU-")) {
                    icsu++;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Error: " + ex.getCause() + ""
                    + "\nMessage:" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getCause() + ""
                    + "\nMessage:" + ex.getMessage());

        }
        this.setIcsus(icsu);
        return ret;
    }

    public void setMessage(String message) {
        this.message = message;
        new PrintMessage("\n" + this.message + " - " + this.name + "...");
    }

    public RNC(File dire, int custId, Connection con) {

        this.sw = "-";
        this.customerId = custId;
        this.con = con;
        this.dire = dire;
        this.findBasicIdent(dire);

        //Get RNC Name from its OMS connection file:
        this.name = getString(dire.getAbsoluteFile() + "/RNC_ID.log",
                "RNC name:",
                0,
                3);

        try {
            this.rnc_id = Integer.parseInt(getString(dire.getAbsoluteFile() + "/RNC_ID.log",
                    "RNC ID:",
                    0,
                    3));
        } catch (Exception ex) {
            this.rnc_id = 0;
        }

        this.type = this.getRncType(dire);  //Get rnc type, and also the number of icsus on RNC        
        if (this.cd.startsWith("R9")) {
            this.sw = "RN9.0";
        } else if (this.cd.startsWith("R81")) {
            this.sw = "RN8.1";
        } else if (this.cd.startsWith("R80")) {
            this.sw = "RN8.0";
        } else if (this.cd.startsWith("R7")) {
            this.sw = "RN7.0";
        } else if (this.cd.startsWith("R6")) {
            this.sw = "RN6.0";
        } else if (this.cd.startsWith("R5")) {
            this.sw = "RN5.0";
        }
    }

    public int getRnc_id() {
        return rnc_id;
    }

    public void setRnc_id(int rnc_id) {
        this.rnc_id = rnc_id;
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;

        if (this.targetId == 0) {
            return ret;
        }

        Statement statement = null;

        try {
            statement = con.createStatement();

            statement.execute("INSERT INTO " + this.tableName + " ("
                    + "cliente_fk, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "rnc_id, "
                    + "oms_ip, "
                    + "oms_ip_sec, "
                    + "name, "
                    + "spc, "
                    + "type, "
                    + "cnumber, "
                    + "location, "
                    + "date, "
                    + "ip, "
                    + "swver, "
                    + "cd, "
                    + "state, "
                    + "icsus) "
                    + "VALUES("
                    + "'" + this.customerId + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.rnc_id + "', "
                    + "'" + this.oms_ip_prim + "', "
                    + "'" + this.oms_ip_sec + "', "
                    + "'" + this.name + "', "
                    + "'" + this.spc + "', "
                    + "'" + this.type + "', "
                    + "'" + this.targetId + "', "
                    + "'" + this.location + "', "
                    + "'" + this.date + "', "
                    + "'" + this.ip + "', "
                    + "'" + this.sw + "', "
                    + "'" + this.cd + "', "
                    + "'" + this.state + "', "
                    + "'" + this.icsus + "') ");

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getErrorCode() + ""
                    + "\nMessage: " + ex.getMessage()
            );
        }

        new PrintMessage("\n" + this.message + " - " + this.name + " - Prfile...");
        this.getPrfile(this.dire, "rnc_prfile", this.targetId);
        
        new PrintMessage("\n" + this.message + " - " + this.name + " - Fifile...");
        this.getFifile(this.dire, "rnc_fifile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Wbts...");
        this.parseWbts(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Units...");
        this.getUnits(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Features...");
        this.getFeatures(this.dire, "licence_rnc", "rnc_to_licence", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Licences...");
        this.getLicenseFiles(this.dire, "rnc_lk_file", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Finished!");

        return ret;
    }

    private void parseWbts(File dire) {

        //WBTS_IP.log:
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/WBTS_IP.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            Scanner scan;
            WBTS wbts = null;
            while ((line = tr.readLine()) != null) {

                if (line.contains("WBTS-") && !line.contains("Failed to get")) {
                    //Get only the TRM Type:D
                    scan = new Scanner(line);
                    wbts = new WBTS();
                    wbts.setFile_uuid(this.file_uuid);
                    wbts.setParent_uuid(this.uuid);
                    wbts.setTableName("wbts");
                    wbts.setRnc_cnumber(this.targetId);
                    wbts.setId(Integer.parseInt(scan.next().replace("WBTS-", "")));
                    wbts.setIdent();
                    scan.next();
                    scan.next();
                    wbts.setTrm(scan.next());
                    wbts.setCon(this.con);
                    wbts.SendToMysql();

                }
            }

        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error " + ex.getClass() + "***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
    }

    public boolean RemoveFromMysql() {
        Statement statement = null;
        List<String> Cmd = new ArrayList();

        Cmd.add("DELETE wcel FROM wcel INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wcel.wbts_ident_fk = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE unitListItem FROM unitListItem INNER JOIN wbtsMRBTS INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "unitListItem.MrBtsInd = wbtsMRBTS.ind AND "
                + "wbtsMRBTS.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsRmod FROM wbtsRmod INNER JOIN wbtsMRBTS INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsRmod.mrbtsInd = wbtsMRBTS.ind AND "
                + "wbtsMRBTS.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsSmod FROM wbtsSmod INNER JOIN wbtsMRBTS INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsSmod.mrbtsInd = wbtsMRBTS.ind AND "
                + "wbtsMRBTS.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        
        Cmd.add("DELETE wbtsMRBTS FROM wbtsMRBTS INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsMRBTS.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsUnit FROM wbtsUnit INNER JOIN wbtsFtm INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsUnit.ftmInd = wbtsFtm.ind AND "
                + "wbtsFtm.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsHwList FROM wbtsHwList INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsHwList.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        
        
        Cmd.add("DELETE wbtsSUBMODULE FROM wbtsSUBMODULE INNER JOIN wbtsMODULE INNER JOIN wbtsHW INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsSUBMODULE.wbtsMODULEInd = wbtsMODULE.ind AND "
                + "wbtsMODULE.wbtsHWInd = wbtsHW.ind AND "
                + "wbtsHW.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");             
                      
        
        Cmd.add("DELETE wbtsMODULE FROM wbtsMODULE INNER JOIN wbtsHW INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsMODULE.wbtsHWInd = wbtsHW.ind AND "
                + "wbtsHW.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");             
              
        
        Cmd.add("DELETE wbtsHW FROM wbtsHW INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsHW.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");        

        Cmd.add("DELETE wbtsNtp FROM wbtsNtp "
                + "INNER JOIN wbtsIntp "
                + "ON wbtsNtp.intpInd = wbtsIntp.ind "
                + "INNER JOIN wbtsIpno "
                + "ON wbtsIntp.ipnoInd = wbtsIpno.ind "
                + "INNER JOIN wbtsFtm "
                + "ON wbtsIpno.ftmInd = wbtsFtm.ind "
                + "INNER JOIN wbts "
                + "ON wbts.wbts_ident = wbtsFtm.wbtsInd "
                + "INNER JOIN rnc "
                + "ON wbts.rnc_cnumber = rnc.cnumber "
                + "AND rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsIntp FROM wbtsIntp "
                + "INNER JOIN wbtsIpno "
                + "ON wbtsIntp.ipnoInd = wbtsIpno.ind "
                + "INNER JOIN wbtsFtm "
                + "ON wbtsIpno.ftmInd = wbtsFtm.ind "
                + "INNER JOIN wbts "
                + "ON wbts.wbts_ident = wbtsFtm.wbtsInd "
                + "INNER JOIN rnc "
                + "ON wbts.rnc_cnumber = rnc.cnumber "
                + "AND rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsIpno FROM wbtsIpno "
                + "INNER JOIN wbtsFtm "
                + "ON wbtsIpno.ftmInd = wbtsFtm.ind "
                + "INNER JOIN wbts "
                + "ON wbts.wbts_ident = wbtsFtm.wbtsInd "
                + "INNER JOIN rnc "
                + "ON wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbtsFtm FROM wbtsFtm INNER JOIN wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbtsFtm.wbtsInd = wbts.wbts_ident AND "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE wbts FROM  wbts INNER JOIN rnc INNER JOIN cliente "
                + "WHERE "
                + "wbts.rnc_cnumber = rnc.cnumber AND "
                + "rnc.cliente_fk = cliente.id AND "
                + "rnc.cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE rnc_lk_file FROM rnc_lk_file "
                + "WHERE "
                + "rnc_lk_file.targetid = '" + this.targetId + "'");

        Cmd.add("DELETE rnc_to_licence FROM rnc_to_licence "
                + "WHERE "
                + "rnc_to_licence.cnumber_fk = '" + this.targetId + "'");

        Cmd.add("DELETE iucs_rnc FROM iucs_rnc "
                + "WHERE "
                + "iucs_rnc.rnc_cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE iups_rnc FROM iups_rnc "
                + "WHERE "
                + "iups_rnc.rnc_cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE iur_rnc FROM iur_rnc "
                + "WHERE "
                + "iur_rnc.rnc_cnumber = '" + this.targetId + "'");

        Cmd.add("DELETE FROM units_rnc WHERE cnumber =  '" + this.targetId + "'");

        Cmd.add("DELETE FROM hw_mcrnc WHERE rnc_cnumber =  '" + this.targetId + "'");

        //Delete PRFILEGX values:
        Cmd.add("DELETE rnc_prfile FROM rnc_prfile "
                + "WHERE rnc_prfile.target_id = '" + this.targetId + "';");

        //Delete FIFILEGX values:
        Cmd.add("DELETE rnc_fifile FROM rnc_fifile "
                + "WHERE rnc_fifile.target_id = '" + this.targetId + "';");

        Cmd.add("DELETE  FROM rnc WHERE "
                + "rnc.cnumber = '" + this.targetId + "';");

        try {
            statement = con.createStatement();
            for (int i = 0; i < Cmd.size(); i++) {
                statement.execute(Cmd.get(i));
            }
        } catch (SQLException ex) {

            System.out.println("\n***Error " + ex.getErrorCode() + "***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\nTrace: " + ex.getStackTrace()
                    + "\n"
            );
        }
        return true;
    }

    private void getUnits(File dire) {

        HashMap unitList = new HashMap();
        List<Integer> unitIlist = new ArrayList();

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;

        //ZUSI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZUSI.log");
            tr = new BufferedReader(fp);
            Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("WO-") || line.contains("SP-") || line.contains("TE-") || line.contains("SE-")) {
                    unit = new Unit();
                    unit.setFile_uuid(this.file_uuid);
                    unit.setParent_uuid(this.uuid);
                    unit.setCnumber(this.targetId);
                    unit.setCon(this.con);
                    unit.setState(line.substring(31, 38).trim());
                    scan = new Scanner(line);
                    String l = scan.next();
                    if (l.contains("-")) {
                        String[] ll = l.split("-");
                        unit.setType(ll[0]);
                        unit.setId(Integer.parseInt(ll[1]));

                    } else {
                        unit.setType(l);
                        unit.setId(0);
                    }
                    unitList.put(unit.getType() + "-" + unit.getId(), unit);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZDOI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZDOI.log");
            tr = new BufferedReader(fp);
            Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("UNIT:")) {
                    scan = new Scanner(line);
                    scan.next();
                    String l = scan.next().trim();
                    if (!l.contains("-")) {
                        l = l + "-0";
                    }
                    unit = (Unit) unitList.get(l);
                    if (unit != null) {
                        tr.readLine();
                        line = tr.readLine();
                        scan = new Scanner(line);
                        scan.next();
                        int pool = Integer.parseInt(scan.next().trim());

                        if (pool < 256) {
                            unit.setMemory(0);
                        } else if (pool < 513) {
                            unit.setMemory(512);
                        } else if (pool < 1025) {
                            unit.setMemory(1024);
                        } else if (pool < 2049) {
                            unit.setMemory(2048);
                        } else if (pool < 4097) {
                            unit.setMemory(4096);
                        } else if (pool < 8193) {
                            unit.setMemory(8192);
                        } else {
                            unit.setMemory(pool);
                        }
                        unitList.put(l, unit);
                    }

                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZWFI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZWFI.log");
            tr = new BufferedReader(fp);
            Unit unit = null;
            String piu = null;
            String ppa = null;
            String chms = null;
            String shms = null;
            String uunit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("CHMS:") && line.contains("SHMS:")) {
                    scan = new Scanner(line);
                    piu = scan.next().trim();
                    scan.next();
                    scan.next();
                    chms = scan.next();
                    scan.next();
                    shms = scan.next();
                    scan.next();
                    ppa = scan.next();
                    while ((line = tr.readLine()) != null) {
                        if (line.contains("PORTS OF PIU:")) {
                            break;
                        }
                        if (line.contains("MASTER:")) {
                            scan = new Scanner(line);
                            uunit = scan.next();
                            if (!uunit.contains("MASTER:")) {
                                unit = (Unit) unitList.get(uunit);
                                if (unit != null) {
                                    unit.setFile_uuid(this.file_uuid);
                                    unit.setParent_uuid(this.uuid);
                                    unit.setChms(Integer.parseInt(chms));
                                    unit.setShms(Integer.parseInt(shms));
                                    unit.setPpa(Integer.parseInt(ppa));
                                    unit.setPluginunit(piu);
                                    unitList.remove(uunit);

                                    unitList.put(unit.getPluginunit()
                                            + " CHMS:"
                                            + unit.getChms()
                                            + " SHMS:"
                                            + unit.getShms()
                                            + " PPA:"
                                            + unit.getPpa(), unit);
                                }
                            }
                        }

                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZWFL.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZWFL.log");
            tr = new BufferedReader(fp);
            Unit unit = null;
            String piu = null;

            String chms = null;
            String shms = null;
            String ppa = null;

            String uunit = null;
            String sen = null;
            String iti = null;
            String lant = null;
            while ((line = tr.readLine()) != null) {

                if (line.contains("sut=")) {
                    scan = new Scanner(line);
                    String tmp = scan.next();
                    chms = tmp.split(":")[0];
                    shms = tmp.split(":")[1];

                }
                if (line.contains("pit=")) {
                    String[] l = line.trim().split(" ");
                    if (l.length >= 5) {
                        ppa = l[0];
                        piu = l[1].replace("'pit=", "");
                        sen = l[3].replace("sen=", "");
                        iti = l[4].replace("iti=", "").replace("'", "");
                    } else if (l.length >= 4) {
                        ppa = lant.trim().split(" ")[0].trim();
                        piu = l[0].replace("'pit=", "");
                        sen = l[2].replace("sen=", "");
                        iti = l[3].replace("iti=", "").replace("'", "");
                    } else {
                        ppa = "-";
                        piu = "-";
                        sen = "-";
                        iti = "-";
                    }
                    unit = (Unit) unitList.get(piu.replace("-", "_")
                            + " CHMS:"
                            + Integer.parseInt(chms)
                            + " SHMS:"
                            + Integer.parseInt(shms)
                            + " PPA:"
                            + Integer.parseInt(ppa));

                    if (unit != null) {
                        unit.setIti(iti);
                        unit.setSen(sen);
                        unit.SendToMysql();
                    }

                } else {
                    lant = line;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nRNC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

    }

    public int getIcsus() {
        return icsus;
    }

    public void setIcsus(int icsus) {
        this.icsus = icsus;
    }

}
