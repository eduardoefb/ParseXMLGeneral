package Nokia.BSC;

import parselog.RegexMatch;
import Nokia.Plataforms.DX200;
import Nokia.DbObjects;
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
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

public class BSC extends DX200 implements DbObjects {

    private static final String tableName = "bsc";
    private String capacity;
    private String hwcapacity;
    private String q3Version;
    private String message;
    private File dire;
    Statement statement;

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getHwcapacity() {
        return hwcapacity;
    }

    public void setHwcapacity(String hwcapacity) {
        this.hwcapacity = hwcapacity;
    }

    public String getQ3Version() {
        return q3Version;
    }

    public void setQ3Version(String q3Version) {
        this.q3Version = q3Version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        new PrintMessage("\n" + this.message + " - " + this.name + "...");
    }

    public File getDire() {
        return dire;
    }

    public void setDire(File dire) {
        this.dire = dire;
    }

    public BSC() {
    }

    public BSC(File dire, int custId, Connection con) {
        this.customerId = custId;
        this.con = con;
        this.dire = dire;
        this.findBasicIdent(dire);
        this.setUuid();

        try {
            this.statement = this.con.createStatement();
            con.setAutoCommit(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        sw = "-";
        if (cd.contains("SF0")) {
            sw = "S15";
        } else if (cd.contains("SG0")) {
            sw = "S16";
        } else if (cd.contains("SG1")) {
            sw = "S16.1";
        } else if (cd.contains("SG2")) {
            sw = "S16.2";
        } else if (cd.contains("SG3")) {
            sw = "S16.3";
        }
        this.q3Version = getString(dire.getAbsoluteFile() + "/Q3VERSION.log",
                "OMU      FILE N:O 088E0000 RECORD N:O 00000000",
                2,
                17);

        this.hwcapacity = getString(dire.getAbsoluteFile() + "/ZEEIBCSU.log",
                "HARDWARE SUPPORTED MAXIMUM TRX CAPACITY :",
                0,
                7);
        this.capacity = getString(dire.getAbsoluteFile() + "/ZEEIBCSU.log",
                "HW AND SW SUPPORTED MAXIMUM TRX CAPACITY:",
                0,
                8);

        switch (this.type) {
            case "BSC":
                this.type = "BSC2i";
                break;

            case "BSC3i":
                if (Integer.parseInt(this.hwcapacity) > 1000) {
                    this.type = "BSC3i_2000";
                } else if ((Integer.parseInt(this.hwcapacity) % 200) == 0) {
                    this.type = "BSC3i_1000";
                }

        }

        this.fifileVersion = getString(dire.getAbsoluteFile() + "/FIFILE_VER.log",
                "FIFILEGX.PAC",
                0,
                2) + "/"
                + getString(dire.getAbsoluteFile() + "/FIFILE_VER.log",
                        "FIFILEGX.PAC",
                        0,
                        7) + "/"
                + getString(dire.getAbsoluteFile() + "/FIFILE_VER.log",
                        "FIFILEGX.PAC",
                        0,
                        8);

        this.prfileVersion = getString(dire.getAbsoluteFile() + "/PRFILE_VER.log",
                "PRFILEGX.PAC",
                0,
                2) + "/"
                + getString(dire.getAbsoluteFile() + "/PRFILE_VER.log",
                        "PRFILEGX.PAC",
                        0,
                        7) + "/"
                + getString(dire.getAbsoluteFile() + "/PRFILE_VER.log",
                        "PRFILEGX.PAC",
                        0,
                        8);

    }

    @Override
    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE bsc.cnumber = " + this.targetId);
            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }
            if (this.name != null && linCount == 0) {
                Cmd = "INSERT INTO " + this.tableName + "( cliente_fk,"
                        + "\n\tname,"
                        + "\n\tuuid,"
                        + "\n\tparent_uuid,"
                        + "\n\tfile_uuid,"
                        + "\n\tspc,"
                        + "\n\ttype,"
                        + "\n\tcnumber,"
                        + "\n\tlocation,"
                        + "\n\tdate,"
                        + "\n\tip,"
                        + "\n\tq3version,"
                        + "\n\tswver,"
                        + "\n\tcd,"
                        + "\n\tstate,"
                        + "\n\tprfile_ver,"
                        + "\n\tfifile_ver,"
                        + "\n\tcapacity)"
                        + "\n\tVALUES('" + this.customerId + "',"
                        + "\n\t'" + this.name + "',"
                        + "\n\t'" + this.uuid + "',"
                        + "\n\t'" + this.parent_uuid + "',"
                        + "\n\t'" + this.file_uuid + "',"
                        + "\n\t'" + this.spc + "',"
                        + "\n\t'" + this.type + "',"
                        + "\n\t'" + this.targetId + "',"
                        + "\n\t'" + this.location + "',"
                        + "\n\t'" + this.date + "',"
                        + "\n\t'" + this.ip + "',"
                        + "\n\t'" + this.q3Version + "',"
                        + "\n\t'" + this.sw + "',"
                        + "\n\t'" + this.cd + "',"
                        + "\n\t'" + this.state + "',"
                        + "\n\t'" + this.prfileVersion + "',"
                        + "\n\t'" + this.fifileVersion + "',"
                        + "\n\t'" + this.capacity + "')";
            } else {
                Cmd = "SHOW TABLES";
            }
            statement.execute(Cmd);

        } catch (SQLException ex) {
            System.out.println("\n***Error " + ex.getErrorCode() + "***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        /*After send BSC information to the database, it will get all the informations 
         from BCFs and Licenses, and will send to database;
         */
        new PrintMessage("\n" + this.message + " - " + this.name + " - Prfile...");
        this.getPrfile(this.dire, "bsc_prfile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Fifile...");
        this.getFifile(this.dire, "bsc_fifile", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Features...");
        this.getFeatures(this.dire, "licence_bsc", "bsc_to_licence", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Units...");
        this.getUnits(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Licences...");
        this.getLicenseFiles(this.dire, "bsc_lk_file", this.targetId);
        new PrintMessage("\n" + this.message + " - " + this.name + " - BCF...");
        this.parseBcf(this.dire);
        new PrintMessage("\n" + this.message + " - " + this.name + " - Finished!");

        return ret;
    }

    @Override
    public boolean RemoveFromMysql() {
        Statement statement = null;
        List<String> Cmd = new ArrayList();
        //Delete TRX, BTS, BCF:
        try {
            statement = con.createStatement();
            statement.execute("DELETE trx FROM trx "
                    + "INNER JOIN bts ON trx.bts_ident_fk = bts.bts_ident "
                    + "INNER JOIN bcf ON bts.bcf_ident_fk = bcf.bcf_ident "
                    + "INNER JOIN bsc ON bcf.bsc_cnumber = bsc.cnumber "
                    + "AND bsc.cnumber = " + this.targetId);

            statement.execute("DELETE bts FROM bts "
                    + "INNER JOIN bcf ON bts.bcf_ident_fk = bcf.bcf_ident "
                    + "INNER JOIN bsc ON bcf.bsc_cnumber = bsc.cnumber "
                    + "AND bsc.cnumber = '" + this.targetId + "';");

            //Delete bcf units (I have disabled this option because bcf unit will not have any foreign key:
            /*
             statement.execute("DELETE bcfUnit FROM bcfUnit "
             + "INNER JOIN bcf "
             + "ON bcfUnit.bcfInd = bcf.bcf_ident "
             + "INNER JOIN bsc "
             + "ON bcf.bsc_cnumber = bsc.cnumber "
             + "AND bsc.cnumber = '" + this.targetId + "';");

             */
            statement.execute("DELETE bcf FROM bcf "
                    + "INNER JOIN bsc ON "
                    + "bcf.bsc_cnumber = bsc.cnumber AND "
                    + "bsc.cnumber = '" + this.targetId + "';");

            //Delete Plug-in units:
            statement.execute("DELETE plugin_units_bsc FROM plugin_units_bsc "
                    + "WHERE plugin_units_bsc.cnumber = '" + this.targetId + "';");

            //Delete units:
            statement.execute("DELETE units_bsc FROM units_bsc "
                    + "WHERE units_bsc.cnumber = '" + this.targetId + "';");

            //Delete Licences:
            statement.execute("DELETE bsc_to_licence FROM bsc_to_licence "
                    + "WHERE bsc_to_licence.cnumber_fk = '" + this.targetId + "';");

            //Delete Licences files:
            statement.execute("DELETE bsc_lk_file FROM bsc_lk_file "
                    + "WHERE bsc_lk_file.targetid = '" + this.targetId + "';");

            //Delete PRFILEGX values:
            statement.execute("DELETE bsc_prfile FROM bsc_prfile "
                    + "WHERE bsc_prfile.target_id = '" + this.targetId + "';");

            //Delete FIFILEGX values:
            statement.execute("DELETE bsc_fifile FROM bsc_fifile "
                    + "WHERE bsc_fifile.target_id = '" + this.targetId + "';");

            statement.execute("DELETE  FROM bsc WHERE "
                    + "bsc.cnumber = '" + this.targetId + "';");

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("\n***Error " + ex.getErrorCode() + "***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        }
        return true;
    }

    /*
     parseBCF will get the data from bcf and bts from ZEEI.log and will update the objects BTS and BCF;
     it will also get sw version from ZEWL/ZEWO
    
     */
    private void parseBcf(File dire) {

        HashMap bcfList = new HashMap();
        List<Integer> bcfIlist = new ArrayList();

        HashMap btsList = new HashMap();
        List<Integer> btsIlist = new ArrayList();

        //ZEEI.log:
        HashMap btsToUuid = new HashMap();
        HashMap bcfToUuid = new HashMap();
        String bcf_uuid;
        try {

            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/ZEEI.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            int seg = 0;
            String segname = "-";
            String lac = "-";
            String ci = "-";
            Scanner scan;
            BCF bcf = null;
            BTS bts = null;

            while ((line = tr.readLine()) != null) {

                //Find line:  SEG-
                if (line.contains("SEG-")) {
                    //Get LAC, CI, SEG and SEGNAME

                    scan = new Scanner(line);
                    try {
                        lac = scan.next();
                        ci = scan.next();
                        seg = Integer.parseInt(scan.next().replace("SEG-", ""));
                        segname = scan.next();
                    } catch (NoSuchElementException ex) {
                        /*
                         For cases when SEGname is empty, like last line, this exception must be ignored:
                         13161 25401 SEG-0397  IMPAPSOJ25401  
                         13161 25402 SEG-0398  IMPAPSOJ25402  
                         13161 25403 SEG-0399  IMPAPSOJ25403  
                         13161 30002 SEG-0998   
                         */
                    }

                } else if (line.contains("BCF-")) {
                    //BCF-0001 ULTRASITE  ....
                    bcf = new BCF();
                    bcf.setFile_uuid(this.file_uuid);

                    bcf.setBsc_cnumber(this.targetId);
                    scan = new Scanner(line);
                    bcf.setBcf_id(Integer.parseInt(scan.next().replace("BCF-", "")));

                    
                    if (bcfToUuid.containsKey(bcf.getBcf_id())) {                        
                        bcf.setUuid(bcfToUuid.get(bcf.getBcf_id()).toString());                        
                    }
                    else{
                        bcf.setUuid();
                        bcfToUuid.put(bcf.getBcf_id(), bcf.getUuid());
                    }
                    
                    bcf.setParent_uuid(this.uuid);
                    bcf.setType(line.substring(10, 23).trim());

                } else if (line.contains("BTS-")) {
                    //  BTS-0001             U WO   
                    scan = new Scanner(line);
                    bts = new BTS();
                    bts.setParent_uuid(bcf.getUuid());
                    bts.setFile_uuid(this.file_uuid);
                    bts.setLac(lac);
                    bts.setCi(ci);
                    bts.setSeg_id(seg);
                    bts.setSeg_name(segname);
                    bts.setBts_id(Integer.parseInt(new RegexMatch(line, "BTS-(.*?) ").toString()));
                    btsToUuid.put(bts.getBts_id(), bts.getUuid());
                    line = tr.readLine();

                    scan = new Scanner(line);
                    bts.setBts_name(scan.next());
                    bcf.setBcf_name(bts.getBts_name().substring(0, (bts.getBts_name().length() - 1)));

                    bcf.setBcf_ident();
                    bcfList.put(bcf.getBcf_id(), bcf);
                    bcfIlist.add(bcf.getBcf_id());
                    bts.setBscTargetId(this.targetId);
                    bts.setBcf_ident_fk(bcf.getBcf_ident());
                    bts.setBts_ident();
                    btsList.put(bts.getBts_id(), bts);
                    btsIlist.add(bts.getBts_id());
                }
            }

            //ZEQO.log --> Get BTS BAND information:
            fp = new FileReader(dire.getAbsoluteFile() + "/ZEQO.log");
            tr = new BufferedReader(fp);

            while ((line = tr.readLine()) != null) {
                //BCF-0001          NW    ULTRA_CX9-1       25.0-51    /PACK_3            NOT_DE
                if (line.contains("BTS-")) {
                    scan = new Scanner(line);
                    scan.next();
                    int btsid = (Integer.parseInt(scan.next().replace("BTS-", "")));
                    bts = (BTS) btsList.get(btsid);

                    if (bts != null) {
                        line = tr.readLine();
                        while (!line.contains("(BAND)") && !line.contains("BTS-")) {
                            line = tr.readLine();
                        }
                        if (line.contains("(BAND)")) {
                            scan = new Scanner(line);
                            scan.next();
                            scan.next();
                            scan.next();
                            scan.next();
                            bts.setBand(scan.next());
                        }
                        btsList.put(btsid, bts);

                    }
                }
            }

            //ZEWO.log --> Update BCF Software and bcf software versions:
            fp = new FileReader(dire.getAbsoluteFile() + "/ZEWO.log");
            tr = new BufferedReader(fp);

            while ((line = tr.readLine()) != null) {
                //BCF-0001          NW    ULTRA_CX9-1       25.0-51    /PACK_3            NOT_DE
                int bcf_ant = -1;
                if (line.contains("BCF-")) {
                    scan = new Scanner(line);
                    int bcfid = (Integer.parseInt(scan.next().replace("BCF-", "")));
                    bcf = (BCF) bcfList.get(bcfid);

                    BcfSwVersion bsw = new BcfSwVersion();
                    if (bcf_ant != bcfid) {
                        bsw.setFile_uuid(this.file_uuid);
                    }
                    bcf_ant = bcfid;
                    if (bcf != null) {
                        if (line.contains("DEFAULT")) {
                            scan.next();
                            bsw.setVersion_name(scan.next());
                            bsw.setVersion_id(scan.next());

                        } else {
                            line = tr.readLine();
                            if (line.contains(" DEFAULT") && line.contains(" BU ")) {
                                scan = new Scanner(line);
                                scan.next();
                                bsw.setVersion_name(scan.next());
                                bsw.setVersion_id(scan.next());

                            } else {
                                line = tr.readLine();
                                if (line.contains(" DEFAULT") && line.contains(" FB ")) {
                                    scan = new Scanner(line);
                                    scan.next();
                                    bsw.setVersion_name(scan.next());
                                    bsw.setVersion_id(scan.next());
                                }

                            }
                        }

                        bsw.setCon(con);
                        bsw.setBcf_type(bcf.getType());
                        bsw.setId();
                        bsw.SendToMysql();
                        bcf.setSw_ver(bsw.getId());
                        bcfList.put(bcfid, bcf);
                    }
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

        //Save BCF to database
        new PrintMessage("\n" + this.message + " - " + this.name + " - Sending BCF to database...");
        List<Integer> chk_list = new ArrayList();
        for (int i = 0; i < bcfIlist.size(); i++) {
            BCF bcf = (BCF) bcfList.get(bcfIlist.get(i));
            bcf.setFile_uuid(this.file_uuid);

            if (!chk_list.contains(bcf.getBcf_id())) {
                chk_list.add(bcf.getBcf_id());

                bcf.setCon(con);
                bcf.AddSqlCmd(this.statement);
            }
        }
        chk_list.clear();

        //Commit data:
        try {
            int[] count = statement.executeBatch();
            con.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //Save BTS to database
        new PrintMessage("\n" + this.message + " - " + this.name + " - Sending BTS to database...");
        for (int i = 0; i < btsIlist.size(); i++) {
            BTS bts = (BTS) btsList.get(btsIlist.get(i));
            bts.setFile_uuid(this.file_uuid);
            if (!chk_list.contains(bts.getBts_id())) {
                bts.setCon(con);
                bts.AddSqlCmd(statement);
            }
        }
        chk_list.clear();

        //Commit data:
        try {
            int[] count = statement.executeBatch();
            con.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //Process TRX:
        //ZERO.log
        new PrintMessage("\n" + this.message + " - " + this.name + " - TRX...");
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/ZERO.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                if (line.contains("BCF-")) {
                    TRX trx = new TRX();
                    trx.setFile_uuid(this.file_uuid);
                    trx.setCon(con);
                    trx.setEdge_supported("NO");
                    trx.setMbcch("NO");
                    Scanner scan = new Scanner(line);
                    trx.setBcf_id(Integer.parseInt(scan.next().replace("BCF-", "")));
                    while (!line.contains("BTS-")) {
                        line = tr.readLine();
                    }
                    if (line.contains("BTS-")) {
                        scan = new Scanner(line);
                        trx.setBts_id(Integer.parseInt(scan.next().replace("BTS-", "")));
                        trx.setParent_uuid(btsToUuid.get(trx.getBts_id()).toString());

                    }

                    while (!line.contains("TRX-")) {
                        line = tr.readLine();
                    }
                    if (line.contains("TRX-")) {
                        if (line.contains("EDGE")) {
                            trx.setEdge_supported("YES");
                        }
                        scan = new Scanner(line);
                        trx.setTrx_id(Integer.parseInt(scan.next().replace("TRX-", "")));

                    }

                    while (!line.contains("FREQ ")) {
                        line = tr.readLine();
                    }
                    if (line.contains("FREQ ")) {
                        scan = new Scanner(line);
                        scan.next();
                        trx.setFreq(scan.next());
                    }

                    while (!line.contains("RTSL  PCM-TSL  SUB_TSL")) {
                        scan = new Scanner(line);
                        line = tr.readLine();
                    }
                    line = tr.readLine();
                    for (int tsl = 0; tsl <= 7; tsl++) {
                        line = tr.readLine();
                        if (line.contains("MBCC")) {
                            trx.setMbcch("YES");
                        }
                    }
                    trx.setBsc_cnumber(this.getTargetId());
                    trx.setMax_bcf_per_bsc(new BCF().getMAX_BCF_NUMBER());
                    trx.setMax_bts_per_bcf(new BTS().getMAX_BTS_PER_BCF());
                    trx.setTrx_ident();

                    chk_list.add((int) trx.getTrx_ident());
                    trx.AddSqlCmd(statement);

                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        chk_list.clear();

        //Commit data:
        try {
            int[] count = statement.executeBatch();
            con.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

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
            Nokia.BSC.Unit unit = null;

            while ((line = tr.readLine()) != null) {
                if (line.contains("WO-") || line.contains("SP-") || line.contains("TE-") || line.contains("SE-")) {
                    unit = new Nokia.BSC.Unit();
                    unit.setParent_uuid(this.uuid);
                    unit.setFile_uuid(this.file_uuid);
                    unit.setBsc_cnumber(this.targetId);
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
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZDOI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZDOI.log");
            tr = new BufferedReader(fp);
            Nokia.BSC.Unit unit = null;
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
                        unit.setFile_uuid(this.file_uuid);
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
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        } catch (IOException ex) {
            System.out.println("\n***Error ***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );

        }

        //ZWTI.log:
        try {
            fp = new FileReader(dire.getAbsoluteFile() + "/ZWTI.log");
            tr = new BufferedReader(fp);
            Nokia.BSC.Unit unit = null;
            while ((line = tr.readLine()) != null) {
                if (line.contains("PIU TYPE INDEX")) {
                    tr.readLine();
                    while ((line = tr.readLine()) != null) {
                        if (line.trim().length() < 3) {
                            break;
                        } else if (line.contains("-")) {
                            PluginUnit piu = new PluginUnit();
                            piu.setFile_uuid(this.file_uuid);
                            piu.setBsc_cnumber(this.targetId);
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

                            String unitD = scan.next().trim();
                            if (unitD.contains("-")) {
                                String[] s = unitD.split("-");
                                piu.setUnit_type(s[0]);
                                piu.setUnit_id(Integer.parseInt(s[1]));
                            } else {
                                piu.setUnit_type(unitD);
                                piu.setUnit_id(0);
                            }

                            unit = (Unit) unitList.get(piu.getUnit_type() + "-" + piu.getUnit_id());

                            if (unit != null) {

                                if (piu.getType().contains("CP6")
                                        || piu.getType().contains("CP7")
                                        || piu.getType().contains("TR")
                                        || piu.getType().contains("CP8")
                                        || piu.getType().contains("CP1")
                                        || piu.getType().contains("TR3")
                                        || piu.getType().contains("TRCO")
                                        || piu.getType().contains("ACPI")
                                        || piu.getType().contains("BJC")) {
                                    unit.setMain_piu(piu.getType());
                                }
                                unit.AddSqlCmd(statement);
                            }
                            piu.AddSqlCmd(statement);
                        }
                    }
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
            System.out.println("\n***Error ***"
                    + "\nBSC Name: " + this.name
                    + "\nTarget id: " + this.targetId
                    + "\nMesage: " + ex.getMessage()
                    + "\n"
            );
        }

        //Commit data:
        try {
            int[] count = statement.executeBatch();
            con.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public String getAll() {
        return ("\nName: " + this.name
                + "\nTarget ID:" + this.targetId
                + "\nType: " + this.type
                + "\nDate: " + this.date
                + "\nLocation: " + this.location
                + "\nQ3: " + this.q3Version
                + "\nCapacity: " + this.capacity
                + "\nPRFILE Ver: " + this.prfileVersion
                + "\nFIFILE Ver: " + this.fifileVersion
                + "\nSPC: " + this.spc
                + "\nSTATE: " + this.state
                + "\nIp: " + this.ip
                + "\ncd: " + this.cd
                + "\nsw: " + this.sw);
    }
}
