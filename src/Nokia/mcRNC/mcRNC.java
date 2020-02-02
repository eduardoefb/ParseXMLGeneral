/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.mcRNC;

import Nokia.Plataforms.Flexi;
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
public class mcRNC extends Flexi {

    private String tableName = "rnc";
    private int rnc_id;
    private File dire;
    private String message;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getRnc_id() {
        return rnc_id;
    }

    public void setRnc_id(int rnc_id) {
        this.rnc_id = rnc_id;
    }

    public File getDire() {
        return dire;
    }

    public void setDire(File dire) {
        this.dire = dire;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public mcRNC(){
        super.setUuid();
    }
    
    public mcRNC(File dire, int custId, Connection con) {
        super.setUuid();
        this.sw = "-";
        this.customerId = custId;
        this.con = con;
        this.dire = dire;
        this.findBasicIdent(dire);
        update_rnc_id(dire);
        this.type = "mcRNC";

    }

    private String getUnitinfo(String line, String sep, int pos) {
        String ret = "-";
        String tmp[] = line.split(sep);
        if (tmp.length > (pos)) {
            ret = tmp[pos].trim();
        }
        return ret;
    }

    private void update_rnc_id(File dire) {

        HashMap unitList = new HashMap();
        List<Integer> unitIlist = new ArrayList();

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;

        //hw_state.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/hw_state.log");
            tr = new BufferedReader(fp);
            Hw hw = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("@") && line.contains("-")) {
                    try {
                        this.setRnc_id(Integer.parseInt(line.split("RNC-")[1].split(" ")[0]));
                    } catch (Exception ex) {
                        this.setRnc_id(0);
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
    }

    private void getUnits(File dire) {

        HashMap unitList = new HashMap();
        List<Integer> unitIlist = new ArrayList();

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;

        //hw_state.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/hw_state.log");
            tr = new BufferedReader(fp);
            Hw hw = null;
            while ((line = tr.readLine()) != null) {

                if (line.contains("node available")) {
                    hw = new Hw();
                    hw.setFile_uuid(this.file_uuid);
                    hw.setParent_uuid(this.uuid);
                    scan = new Scanner(line);
                    String unitstr = scan.next();
                    hw.setUnit_name(unitstr.split("-")[0]);
                    hw.setUnit_id(unitstr.split("-")[1]);
                    scan.next();
                    scan.next();
                    scan.next();
                    String[] pos = scan.next().split("/");
                    hw.setCabinet(Integer.parseInt(pos[1].replace("cabinet-", "").trim()));
                    hw.setChassis(Integer.parseInt(pos[2].replace("chassis-", "").trim()));
                    hw.setPos((pos[4].trim()));
                    String key = hw.getChassis() + "-" + hw.getPos().replace("addin-", "slot-");
                    unitList.put(key, hw);
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

        //hw_inv.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/hw_inv.log");
            tr = new BufferedReader(fp);
            Hw hw = null;
            Hw hwt = new Hw();
            hwt.setParent_uuid(this.uuid);
            hwt.setFile_uuid(this.file_uuid);
            while ((line = tr.readLine()) != null) {
                if (line.contains("chassis-")) {
                    hwt.setCabinet(1);
                    hwt.setChassis(Integer.parseInt(line.split("/")[1].replace("chassis-", "")));
                    hwt.setPos(line.split("/")[2].split(" ")[0]);
                    hwt.setUnit_name("-");
                    hwt.setUnit_id("-");
                    hwt.setProd_custom("-");

                    //Product Manufacturer    : Advantech Co.                                        
                    hwt.setProd_manufacturer(getUnitinfo(tr.readLine(), ": ", 1));

                    //Product Name            : MIC-5401
                    hwt.setProd_name(getUnitinfo(tr.readLine(), ": ", 1));

                    //Product Part Number     : MIC-5401-0000E
                    hwt.setProd_partnumber(getUnitinfo(tr.readLine(), ": ", 1));

                    //Product Version         : A1.02
                    hwt.setProd_version(getUnitinfo(tr.readLine(), ": ", 1));

                    //Product Serial Number   : AKO2358241
                    hwt.setProd_serial(getUnitinfo(tr.readLine(), ": ", 1));

                    //Product Asset Tag       :   //Not used
                    tr.readLine();

                    //Product File Id         : hdsam-a_ad_frud_01_10_0000.bin
                    hwt.setProd_fileid(getUnitinfo(tr.readLine(), ": ", 1));

                    if (hwt.getPos().contains("AMC-")
                            || hwt.getPos().contains("power-supply-")
                            || hwt.getPos().contains("motherboard-")
                            || hwt.getPos().contains("slot-")) {

                        if (hwt.getPos().contains("slot-")) {
                            //Product Custom          : MAC=00:80:82:5A:3A:40
                            hwt.setProd_custom(getUnitinfo(tr.readLine(), ": ", 1));
                        } else {
                            hwt.setProd_custom("-");
                        }
                        //Board   Mfg Datetime    : 2014.07.14 04:16:00
                        hwt.setBoard_mfg_date(getUnitinfo(tr.readLine(), ": ", 1));

                        //Board   Manufacturer    : ADVANTEC
                        hwt.setBoard_manufacturer(getUnitinfo(tr.readLine(), ": ", 1));

                        //Board   Name            : HDSAM-A
                        hwt.setBoard_name(getUnitinfo(tr.readLine(), ": ", 1));

                        //Board   Serial Number   : AT142800555
                        hwt.setBoard_serial(getUnitinfo(tr.readLine(), ": ", 1));

                        //Board   Part Number     : C110598.B3A
                        hwt.setBoard_part_number(getUnitinfo(tr.readLine(), ": ", 1));

                        //Board   File Id         :  //Not implemented
                        tr.readLine();

                        if (hwt.getPos().contains("slot-")) {
                            String key = hwt.getChassis() + "-" + hwt.getPos();
                            hw = (Hw) unitList.get(key);
                            if (hw != null) {
                                hwt.setUnit_name(hw.getUnit_name());
                                hwt.setUnit_id(hw.getUnit_id());
                            }
                        }

                        hwt.setCnumber(this.targetId);
                        hwt.setCon(this.con);
                        hwt.SendToMysql();
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
                    + "'" + this.oms_ip + "', "
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
                    + "'0') ");

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getErrorCode() + ""
                    + "\nMessage: " + ex.getMessage()
            );
        }

        new PrintMessage("\n" + this.message + " - " + this.name + " - Prfile...");
        this.getPrfile(this.dire, "rnc_prfile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Fifile...");
        this.getFifile(this.dire, "rnc_fifile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Units...");
        this.getUnits(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Features...");
        this.getFeatures(this.dire, "licence_rnc", "rnc_to_licence", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Licences...");
        this.getLicenseFiles(this.dire, "rnc_lk_file", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Finished!");

        return ret;
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
}
