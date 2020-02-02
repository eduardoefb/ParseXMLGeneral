package Nokia.FNS;

import Nokia.Common.PLMN;
import Nokia.Common.PLMN_PARAMETER;
import Nokia.DbObjects;
import Nokia.Plataforms.DX200_Atca;
import Nokia.PrintMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author eduabati
 */
public class FNS extends DX200_Atca implements DbObjects {

    private String message;
    private File dire;
    private String type = "FlexiNS";
    private final String tableName = "fns";

    public FNS() {
        this.setUuid();
    }

    public FNS(File dire, int custId, Connection con) {
        this.setUuid();
        this.customerId = custId;
        this.con = con;
        this.dire = dire;
        this.findBasicIdent(dire);

        this.type = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "PAGE",
                0,
                1);

        if (this.type.equals("Flexi")) {
            this.type = "FlexiNS";
        }

        this.date = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "PAGE",
                0,
                4);

        if (this.date.contains(":")) {
            this.date = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                    "PAGE",
                    0,
                    3);
        }
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
        new PrintMessage("\n" + this.message + " - " + this.name + "...");
    }

    @Override
    public void getPlmn(File dire, String tableName, int targetID) {

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;
        String extra_name = null;
        String extra_s_name = null;
        PLMN plmn = null;
        PLMN_PARAMETER plmn_parameter = null;
        Map<String, String> iugb = new HashMap<String, String>();

        //Adding the parameters that has GB and IUI parameters:
        iugb.put("MNC LENGTH", "MNCL");
        iugb.put("AUTHENTIC REPRATE FOR GPRS ATTACH", "GPRS");
        iugb.put("AUTHENTIC REPRATE FOR IMSI ATTACH", "IMSI");
        iugb.put("AUTHENTIC REPRATE FOR COMBINED IMSI AND GPRS ATTACH", "COMB");
        iugb.put("AUTHENTIC REPRATE FOR NORMAL RA UPDATE", "NRA");
        iugb.put("AUTHENTIC REPRATE FOR NORMAL RA UPDATE, NEW VISITOR", "NRAVIS");
        iugb.put("AUTHENTIC REPRATE FOR PERIODIC RA UPDATE", "PRA");
        iugb.put("AUTHENTIC REPRATE FOR COMBINED RA AND LA UPDATE", "RALA");
        iugb.put("AUTHENTIC REPRATE FOR MO PDP CONTEXT ACTIVATION", "MOA");
        iugb.put("AUTHENTIC REPRATE FOR MO PDP CONTEXT DEACTIVATION", "MOD");
        iugb.put("AUTHENTIC REPRATE FOR MO SMS", "MOSMS");
        iugb.put("AUTHENTIC REPRATE FOR MT SMS", "MTSMS");
        iugb.put("AUTHENTIC REPRATE IN MO GPRS DETACH", "MOGPRS");
        iugb.put("AUTHENTIC REPRATE FOR SERVICE REQUEST", "SREQ");
        iugb.put("IMEI CHECK REPRATE FOR GPRS ATTACH", "IGPRS");
        iugb.put("IMEI CHECK REPRATE FOR IMSI ATTACH", "IIMSI");
        iugb.put("IMEI CHECK REPRATE FOR COMBINED IMSI AND GPRS ATTACH", "ICOMB");
        iugb.put("IMEI CHECK REPRATE FOR NORMAL RA UPDATE", "INRA");
        iugb.put("IMEI CHECK REPRATE FOR NORMAL RA UPDATE, NEW VISITOR", "INRAVIS");
        iugb.put("IMEI CHECK REPRATE FOR PERIODIC RA UPDATE", "IPRA");
        iugb.put("IMEI CHECK REPRATE FOR COMBINED RA AND LA UPDATE", "IRALA");
        iugb.put("IMEI CHECK REPRATE FOR MO PDP CONTEXT ACTIVATION", "IMOA");
        iugb.put("IMEI CHECK REPRATE FOR MO PDP CONTEXT DEACTIVATION", "IMOD");
        iugb.put("IMEI CHECK REPRATE FOR MO SMS", "IMOSMS");
        iugb.put("IMEI CHECK REPRATE FOR MT SMS", "IMTSMS");
        iugb.put("IMEI CHECK REPRATE IN MO GPRS DETACH", "IMOGPRS");
        iugb.put("IMEI CHECK REPRATE FOR SERVICE REQUEST", "ISREQ");
        iugb.put("PTMSI SIG REPRATE FOR GPRS ATTACH", "PGPRS");
        iugb.put("PTMSI SIG REPRATE FOR NORMAL RA UPDATE", "PNRA");
        iugb.put("PTMSI SIG REPRATE FOR NORMAL RA UP NEW VISITOR", "PNRAVIS");
        iugb.put("PTMSI SIG REPRATE FOR PERIODIC RA UPDATE", "PPRA");
        iugb.put("PTMSI ALLOC REPRATE FOR PERIODIC RA UPDATE", "PPARA");
        iugb.put("PTMSI ALLOC REPRATE FOR SERVICE REQUEST", "PSREQ");

        String par_value = null;
        System.out.println("");

        //ZMXP_FULL.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZMXP_FULL.log");
            tr = new BufferedReader(fp);
            Nokia.FNS.Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("PLMN: ")) {
                    plmn = new PLMN();
                    plmn.setCon(con);
                    plmn.setTarget_id(targetId);
                    plmn.setTableName(tableName);                    
                    scan = new Scanner(line);
                    plmn.setType(scan.next());
                    scan.next();
                    plmn.setName(scan.next());
                    plmn.setId(targetId + "," + plmn.getType() + "," + plmn.getName());
                    System.out.println("\t   ->" + plmn.getName() + "...");
                    plmn.SendToMysql();
                } else if (line.contains("(") && line.contains(")")) {
                    String par_name = line.split("\\(")[0].replace(".", "").trim();
                    String par_s_name = line.split("\\(")[1].split("\\)")[0].trim();
                    try {
                        par_value = line.split("\\)")[1].trim();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        par_value = "-";
                    }

                    plmn_parameter = new PLMN_PARAMETER();
                    plmn_parameter.setTableName(tableName + "_parameter");
                    plmn_parameter.setCon(con);
                    plmn_parameter.setPlmn_id(targetId + "," + plmn.getType() + "," + plmn.getName());
                    if (iugb.containsKey(par_name)) {
                        par_value = line.substring(67);
                        plmn_parameter.setName(par_name + " (GB)");
                        plmn_parameter.setShort_name(par_s_name + " (GB)");
                        scan = new Scanner(par_value);
                        try {
                            plmn_parameter.setValue(par_value.substring(0, 6).trim());
                            if (plmn_parameter.getValue().trim().length() == 0) {
                                plmn_parameter.setValue("-");
                            }
                        } catch (StringIndexOutOfBoundsException ex) {
                            plmn_parameter.setValue(par_value.trim());
                        }
                        plmn_parameter.SendToMysql();

                        plmn_parameter = new PLMN_PARAMETER();
                        plmn_parameter.setTableName(tableName + "_parameter");
                        plmn_parameter.setCon(con);
                        plmn_parameter.setPlmn_id(targetId + "," + plmn.getType() + "," + plmn.getName());
                        plmn_parameter.setName(par_name + " (IU)");
                        plmn_parameter.setShort_name(par_s_name + " (IU)");
                        try {
                            plmn_parameter.setValue(par_value.substring(7).trim());
                        } catch (StringIndexOutOfBoundsException ex) {
                            plmn_parameter.setValue("-");
                        }
                        plmn_parameter.SendToMysql();
                    } else {

                        plmn_parameter.setName(par_name);
                        plmn_parameter.setShort_name(par_s_name);

                        //PLMN SPECIFIC QOS ROAMING PARAMETERS..
                        if (line.contains("PLMN SPECIFIC QOS ROAMING PARAMETERS..")) {
                            extra_name = par_s_name;
                            plmn_parameter.setValue(par_value.trim());
                            plmn_parameter.SendToMysql();
                            while ((line = tr.readLine()) != null) {
                                if (line.trim().length() == 0) {
                                    break;
                                }
                                par_name = line.split("\\(")[0].replace(".", "").trim();
                                par_s_name = line.split("\\(")[1].split("\\)")[0].trim();
                                try {
                                    par_value = line.split("\\)")[1].trim();
                                } catch (ArrayIndexOutOfBoundsException ex) {
                                    par_value = "-";
                                }

                                plmn_parameter = new PLMN_PARAMETER();
                                plmn_parameter.setCon(con);
                                plmn_parameter.setTableName(tableName + "_parameter");
                                plmn_parameter.setPlmn_id(targetId + "," + plmn.getType() + "," + plmn.getName());
                                plmn_parameter.setValue(par_value.trim());
                                plmn_parameter.setName(par_name + " (" + extra_name + ")");
                                plmn_parameter.setShort_name(extra_name + "." + par_s_name);
                                plmn_parameter.SendToMysql();

                            }
                        } else if (line.contains("DEFAULT APN OPERATOR IDENTIFIER    .")
                                || line.contains("HSS DESTINATION REALM.")
                                || line.contains("HSS HOST NAME.")
                                || line.contains("AUC DESTINATION REALM.")
                                || line.contains("AUC HOST NAME.")
                                || line.contains("ROAMING CONTROL PARAMETER .")                                
                                ) {
                            line = tr.readLine();
                            plmn_parameter.setValue(line.trim());
                            if(plmn_parameter.getValue().trim().isEmpty()){
                                plmn_parameter.setValue("-");
                            }
                            while (plmn_parameter.getValue().contains("..")) {
                                plmn_parameter.setValue(plmn_parameter.getValue().replace("..", "."));
                                if (plmn_parameter.getValue().equals(".")) {
                                    plmn_parameter.setValue("-");
                                }
                            }
                            plmn_parameter.SendToMysql();
                        } else{
                            plmn_parameter.setValue(par_value);
                            plmn_parameter.SendToMysql();
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE " + this.tableName + ".cnumber = " + this.targetId);
            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }
            if (this.name != null && linCount == 0) {
                Cmd = "INSERT INTO " + this.tableName + "( customer_fk,"
                        + "\n\tname,"
                        + "\n\tfile_uuid,"
                        + "\n\tuuid,"
                        + "\n\tparent_uuid,"
                        + "\n\tspc,"
                        + "\n\ttype,"
                        + "\n\tcnumber,"
                        + "\n\tlocation,"
                        + "\n\tdate,"
                        + "\n\tip,"
                        + "\n\tswver,"
                        + "\n\tstate)"
                        + "\n\tVALUES('" + this.customerId + "',"
                        + "\n\t'" + this.name + "',"
                        + "\n\t'" + this.file_uuid + "',"
                        + "\n\t'" + this.uuid + "',"
                        + "\n\t'" + this.parent_uuid + "',"
                        + "\n\t'" + this.spc + "',"
                        + "\n\t'" + this.type + "',"
                        + "\n\t'" + this.targetId + "',"
                        + "\n\t'" + this.location + "',"
                        + "\n\t'" + this.date + "',"
                        + "\n\t'" + this.ip + "',"
                        + "\n\t'" + this.enviroment_delivery + "',"
                        + "\n\t'" + this.state + "')";
            } else {
                Cmd = "SHOW TABLES";
            }
            statement.execute(Cmd);

        } catch (SQLException ex) {
            System.out.println("\n***Error " + ex.getErrorCode() + "***"
                    + "\n" + this.tableName.toUpperCase() + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
        new PrintMessage("\n" + this.message + " - " + this.name + " - PLMN...");
        this.getPlmn(this.dire, "fns_plmn", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Prfile...");
        this.getPrfile(this.dire, "fns_prfile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Fifile...");
        this.getFifile(this.dire, "fns_fifile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - RNC...");
        this.getRNC(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - ENB...");
        this.getENB(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Features...");
        this.getFeatures(this.dire, "licence_fns", "fns_to_licence", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Units...");
        this.getUnits(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Licences...");
        this.getLicenseFiles(this.dire, "fns_lk_file", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Applications...");
        this.getApplication(this.dire);

        new PrintMessage("\n" + this.message + " - " + this.name + " - Finished!");

        return ret;
    }

    @Override
    public boolean RemoveFromMysql() {
        Statement statement = null;
        List<String> Cmd = new ArrayList();
        try {
            statement = con.createStatement();

            //Delete ENB:
            statement.execute("DELETE fns_enb FROM fns_enb "
                    + "WHERE fns_enb.target_id = '" + this.targetId + "';");

            //Delete RNC:
            statement.execute("DELETE fns_rnc FROM fns_rnc "
                    + "WHERE fns_rnc.target_id = '" + this.targetId + "';");

            //Delete APPLICATION:
            statement.execute("DELETE fns_application FROM fns_application "
                    + "WHERE fns_application.target_id = '" + this.targetId + "';");

            //Delete Licences:
            statement.execute("DELETE fns_to_licence FROM fns_to_licence "
                    + "WHERE fns_to_licence.cnumber_fk = '" + this.targetId + "';");

            //Delete Licences files:
            statement.execute("DELETE fns_lk_file FROM fns_lk_file "
                    + "WHERE fns_lk_file.targetid = '" + this.targetId + "';");

            //Delete Plug-in units:
            statement.execute("DELETE plugin_units_fns FROM plugin_units_fns "
                    + "WHERE plugin_units_fns.cnumber = '" + this.targetId + "';");

            //Delete units:
            statement.execute("DELETE units_fns FROM units_fns "
                    + "WHERE units_fns.cnumber = '" + this.targetId + "';");

            //Delete PRFILEGX values:
            statement.execute("DELETE fns_prfile FROM fns_prfile "
                    + "WHERE fns_prfile.target_id = '" + this.targetId + "';");

            //Delete FIFILEGX values:
            statement.execute("DELETE fns_fifile FROM fns_fifile "
                    + "WHERE fns_fifile.target_id = '" + this.targetId + "';");

            //Delete PLMN parameters:
            statement.execute("DELETE fns_plmn_parameter FROM fns_plmn_parameter "
                    + "INNER JOIN fns_plmn ON fns_plmn_parameter.plmn_id = fns_plmn.id "
                    + "AND fns_plmn.target_id = " + this.targetId + ";");

            //Delete PLMNs:
            statement.execute("DELETE fns_plmn FROM fns_plmn "
                    + "WHERE fns_plmn.target_id = '" + this.targetId + "';");

            statement.execute("DELETE  FROM " + this.tableName + " WHERE "
                    + this.tableName + ".cnumber = '" + this.targetId + "';");

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("\n***Error " + ex.getErrorCode() + "***"
                    + "\n" + this.tableName.toUpperCase() + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
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
            Nokia.FNS.Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("WO-") || line.contains("SP-") || line.contains("TE-") || line.contains("SE-")) {
                    unit = new Nokia.FNS.Unit();
                    unit.setFile_uuid(this.file_uuid);
                    this.setParent_uuid(this.uuid);
                    unit.set_cnumber(this.targetId);
                    unit.setCon(this.con);
                    unit.setState(line.substring(17, 23).trim());
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
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZDOI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZDOI.log");
            tr = new BufferedReader(fp);
            Nokia.FNS.Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("UNIT:")) {
                    scan = new Scanner(line);
                    scan.next();
                    String l = scan.next().trim();
                    if (!l.contains("-")) {
                        l = l + "-0";
                    }
                    unit = (Nokia.FNS.Unit) unitList.get(l);
                    if (unit != null) {
                        tr.readLine();
                        line = tr.readLine();
                        scan = new Scanner(line);
                        scan.next();
                        int pool = Integer.parseInt(scan.next().trim());

                        if (pool < 256) {
                            unit.setMemory(-1);
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
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZWTI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZWTI.log");
            tr = new BufferedReader(fp);
            Nokia.FNS.Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("PIU TYPE INDEX")) {
                    tr.readLine();
                    while ((line = tr.readLine()) != null) {
                        if (line.trim().length() < 3) {
                            break;
                        } else if (line.contains("-")) {
                            Nokia.FNS.PluginUnit piu = new Nokia.FNS.PluginUnit();
                            piu.setFile_uuid(this.file_uuid);
                            piu.setParent_uuid(this.uuid);
                            piu.set_cnumber(this.targetId);
                            piu.setCon(this.con);
                            scan = new Scanner(line);
                            piu.setType(scan.next().trim());
                            piu.setId(Integer.parseInt(scan.next().trim()));
                            String[] loc = scan.next().trim().split("-");
                            if (loc.length < 2) {
                                piu.setRack("-");
                                piu.setCartridge("-");
                                piu.setTrack("-");
                            } else {
                                piu.setRack(loc[0]);
                                piu.setCartridge(loc[1]);
                                piu.setTrack(loc[2]);
                            }
                            try {
                                String unitD = scan.next().trim();
                                if (unitD.contains("-")) {
                                    String[] s = unitD.split("-");
                                    piu.setUnit_type(s[0]);
                                    piu.setUnit_id(Integer.parseInt(s[1]));
                                } else {
                                    piu.setUnit_type(unitD);
                                    piu.setUnit_id(0);
                                }
                            } catch (NoSuchElementException ex) {
                                piu.setUnit_type("-");
                                piu.setUnit_id(-1);
                            }

                            try {
                                piu.setSen(scan.next().trim());
                            } catch (NoSuchElementException ex) {
                                piu.setSen("-");
                            }

                            try {
                                String _iti = scan.next().trim();
                                piu.setIti(_iti);
                            } catch (NoSuchElementException ex) {
                                piu.setIti("-");
                            }

                            unit = (Nokia.FNS.Unit) unitList.get(piu.getUnit_type() + "-" + piu.getUnit_id());
                            if (unit != null) {
                                if (piu.getType().contains("CP6")
                                        || piu.getType().contains("CP7")
                                        || piu.getType().contains("CP8")
                                        || piu.getType().contains("CP1")
                                        || piu.getType().contains("ACPI")
                                        || piu.getType().contains("BJC")) {
                                    unit.setMain_piu(piu.getType());
                                }
                                unit.SendToMysql();
                            }
                            piu.SendToMysql();
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        }

    }

    private void getApplication(File dire) {

        HashMap unitList = new HashMap();
        List<Integer> unitIlist = new ArrayList();

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;

        //ZKAI.log:
        try {
            Nokia.FNS.Application app = null;
            fp = new FileReader(dire.getAbsoluteFile() + "/ZKAI.log");
            tr = new BufferedReader(fp);
            Nokia.FNS.Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("-") && !line.contains(" -") && !line.contains("LOADING PROGRAM") && !line.contains("Flexi NS")) {
                    app = new Nokia.FNS.Application();
                    app.setTargetId(this.targetId);
                    app.setCon(con);
                    app.setUnit_type(line.split("-")[0].trim());
                    app.setUnit_id(line.split("-")[1].trim());
                } else if ((line.contains("-") || line.contains(".")) && app != null) {
                    scan = new Scanner(line);
                    try {
                        app.setApplication_type(scan.next());
                        app.setApplication_ipv4(scan.next());
                    } catch (Exception ex) {
                        app.setApplication_ipv4("-");
                        app.setApplication_ipv6("-");
                    }

                    try {
                        app.setApplication_ipv6(scan.next());
                    } catch (Exception ex) {
                        app.setApplication_ipv6("-");
                    }

                    app.SendToMysql();
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
    }

    private void getENB(File dire) {

        HashMap unitList = new HashMap();
        List<Integer> unitIlist = new ArrayList();

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;

        //ZB6I_FULL.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZB6I_FULL.log");
            tr = new BufferedReader(fp);
            Nokia.FNS.ENB enb = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("MOBILE COUNTRY CODE")) {
                    enb = new Nokia.FNS.ENB();
                    enb.setParent_uuid(this.uuid);
                    enb.setFile_uuid(this.file_uuid);
                    enb.setTargetId(this.targetId);
                    enb.setCon(con);
                    enb.setMcc(line.split(":")[1].trim());
                    enb.setTac("");
                    enb.setPlmn("");
                } else if (line.contains("MOBILE NETWORK CODE")) {
                    enb.setMnc(line.split(":")[1].trim());
                } else if (line.contains("ENB IDENTIFICATION..")) {
                    enb.setId(line.split(":")[1].trim());
                } else if (line.contains("ENB IP ADDRESS.")) {
                    enb.setIp(line.split(":")[1].trim());
                } else if (line.contains("TRACKING AREA CODE.")) {
                    enb.setTac((line.split(":")[1].trim().replace("[", " ").split(" ")[0]).trim());
                } else if (line.contains("PLMN#")) {
                    enb.setPlmn(line.split(":")[1].trim().replace(" ", ""));
                    enb.SendToMysql();
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
    }

    private void getRNC(File dire) {

        HashMap unitList = new HashMap();
        List<Integer> unitIlist = new ArrayList();

        FileReader fp = null;
        BufferedReader tr = null;
        String line;
        Scanner scan;

        //ZKAI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZE6I_FULL.log");
            tr = new BufferedReader(fp);
            Nokia.FNS.RNC rnc = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("MOBILE COUNTRY CODE............(MCC)")) {
                    rnc = new Nokia.FNS.RNC();
                    rnc.setFile_uuid(this.file_uuid);
                    rnc.setParent_uuid(this.uuid);
                    rnc.setTargetId(this.targetId);
                    rnc.setCon(con);
                    rnc.setMcc(getTextId(line, 5).trim());
                    line = tr.readLine();
                    rnc.setMnc(getTextId(line, 5).trim());
                    line = tr.readLine();
                    rnc.setId(getTextId(line, 4).trim());
                    line = tr.readLine();
                    rnc.setName(getTextId(line, 4).trim());
                } else if (line.contains("NETWORK INDICATOR..............(NI)......")) {
                    rnc.setNi(getTextId(line, 4).trim());
                    line = tr.readLine();
                    try {
                        rnc.setSpc(Integer.valueOf(getTextId(line, 5).trim(), 16).toString());
                    } catch (NumberFormatException ex) {
                        rnc.setSpc("-");
                    }
                } else if (line.contains("MCC  MNC  LAC    RAC")) {
                    line = tr.readLine();
                    line = tr.readLine();
                    while (line.trim().length() > 1) {
                        Scanner sc = new Scanner(line);
                        rnc.setMcc(sc.next());
                        rnc.setMnc(sc.next());
                        rnc.setLac(sc.next());
                        rnc.setRac(sc.next());
                        rnc.SendToMysql();
                        line = tr.readLine();
                    }
                }

            }
        } catch (FileNotFoundException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\n" + this.tableName + " Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }
    }

    private String getTextId(String str, int id) {
        String ret = "-";
        Scanner sc = new Scanner(str);
        for (int i = 1; i <= id; i++) {
            try {
                ret = sc.next();
            } catch (NoSuchElementException ex) {
                ret = "-";
                break;
            }
        }
        return ret;
    }

}
