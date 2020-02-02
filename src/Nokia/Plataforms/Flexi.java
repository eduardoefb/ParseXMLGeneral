/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.Plataforms;

import Nokia.Common.DXIPACommon;
import Nokia.Common.LicenseFeature;
import Nokia.Common.LicenseFile;
import Nokia.Common.NeToFea;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author eduabati
 */
public abstract class Flexi extends DXIPACommon {

    protected String oms_ip;

    public String getOms_ip() {
        return oms_ip;
    }

    public void setOms_ip(String oms_ip) {
        this.oms_ip = oms_ip;
    }

    @Override
    protected void getFeatures(File dire, String FeaTableName, String NeTableName, int targetId) {
        LicenseFeature fea = null;
        NeToFea ne = null;

        //License features
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/features.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            Scanner scan;
            while ((line = tr.readLine()) != null) {
                //Feature Name               : AMR Capacity
                if (line.contains("Feature Name ")) {
                    fea = new LicenseFeature();
                    fea.setFile_uuid(this.getFile_uuid());
                    ne = new NeToFea();
                    ne.setFile_uuid(this.getFile_uuid());
                    ne.setTargetId(targetId);
                    ne.setLk_usage("-");
                    ne.setCon(this.con);
                    fea.setCon(this.con);
                    fea.setTableName(FeaTableName);
                    fea.setFea_name(line.split(":")[1].trim());
                    ne.setTableName(NeTableName);

                    //Feature Code               : 0000000959
                    fea.setFea_code(Integer.parseInt(tr.readLine().split(":")[1].trim()));
                    fea.SendToMysql();

                    ne.setFea_code_fk(fea.getFea_code());
                    ne.setId();

                    //Allowed Capacity           : 3500                                        
                    ne.setCapacity(tr.readLine().split(":")[1].trim());
                    if (ne.getCapacity().contains("not available")) {
                        ne.setCapacity("-");
                    }

                    //Used Capacity              : 1249
                    ne.setLk_usage(tr.readLine().split(":")[1].trim());
                    if (ne.getLk_usage().contains("not available")) {
                        ne.setLk_usage("-");
                    }
                    //Combined Operational State : on //Not implemented                    
                    //Feature Description        : RAN1197,RAN1198 AMR capacity 1 Erl //Not implemented                                 
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

    @Override
    protected void getLicenseFiles(File dire, String tableName, int targetId) {
        LicenseFile lk = null;
        LicenseFeature fea = null;

        //License features
        try {
            FileReader fp = new FileReader(dire.getAbsoluteFile() + "/licences.log");
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                if (line.contains("Unique ID:")) {
                    try {
                        //Unique ID:                      K3604472
                        lk = new LicenseFile();
                        lk.setFile_uuid(this.file_uuid);
                        lk.setCon(this.con);
                        lk.setTableName(tableName);
                        lk.setTargetid(this.targetId);
                        lk.setFilename(line.replace("Unique ID:", "").trim() + ".XML");

                        //----------------------------------------------------  //Nothing to do
                        tr.readLine();

                        //  //Nothing to do
                        tr.readLine();

                        //License State                   :Operational
                        lk.setLfstate(tr.readLine().split(":")[1].trim());

                        //License Code                    :RU00091 //Nothing to do
                        tr.readLine();

                        //License Name                    :HSDPA cell upg from 10 to 15 Codes LK
                        lk.setLicencename(tr.readLine().split(":")[1].trim());

                        //License Serial Number           :2001239042593
                        lk.setSerialnumber(tr.readLine().split(":")[1].trim());

                        //License Installation Time       :2015-01-07T15:13:14 //nothing to do
                        tr.readLine();

                        //License Start Time              :2014-09-05T02:10:37                    
                        lk.setStart_date(tr.readLine().split(":")[1].trim().split("T")[0]);

                        //License End Time                :license is valid forever (no expiration date included) //Nothing to do
                        tr.readLine();

                        //Order ID                        :1113114523
                        lk.setOrderid(tr.readLine().split(":")[1].trim());

                        //Customer ID                     :204981
                        lk.setCustomerId(tr.readLine().split(":")[1].trim());

                        //Customer Name                   :TIM do Brasil S/A
                        lk.setCustomername(tr.readLine().split(":")[1].trim());
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("\n***Error ***"
                                + "\nMesage: " + ex.getMessage()
                                + "\n"
                        );
                    }

                    List<String> feaCodeList = null;
                    List<String> feaNameList = null;
                    feaCodeList = new ArrayList();
                    feaNameList = new ArrayList();

                    boolean fc = false;
                    boolean fn = false;
                    while ((line = tr.readLine()) != null) {
                        //It will check first "Allowed capacity" because, if tr is here, it means that feature information has been already done
                        if (line.contains("Allowed Capacity")) {
                            fn = false;
                            fc = false;
                            int size = 0;
                            if (feaNameList.size() == feaCodeList.size()) {
                                size = feaNameList.size();
                            } else if (feaNameList.size() > feaCodeList.size()) {
                                size = feaCodeList.size();
                            } else {
                                size = feaNameList.size();
                            }

                            for (int i = 0; i < size; i++) {
                                fea = new LicenseFeature();
                                fea.setFile_uuid(this.getFile_uuid());
                                lk.addFea(feaCodeList.get(i), feaNameList.get(i));
                                fea.setFea_code(Integer.parseInt(feaCodeList.get(i)));
                                fea.setFea_name(feaNameList.get(i));
                                fea.setCon(con);
                                fea.setTableName("licence_rnc");
                                fea.SendToMysql();
                            }
                            try {
                                lk.setMax_value(Integer.parseInt(line.split(":")[1].trim()));
                            } catch (NumberFormatException ex) {
                                System.out.println(ex.getMessage());
                            }

                            //End of licence file information:
                            break;
                        } //Feature Code List               :0000000621
                        else if (line.contains("Feature Code List")) {
                            fc = true;
                            fn = false;
                            feaCodeList.add(line.split(":")[1].trim());
                        } else if (line.contains("Feature Name List")) {
                            fn = true;
                            fc = false;
                            feaNameList.add(line.split(":")[1].trim());
                        } else if (fn == true) {
                            feaNameList.add(line.trim());
                        } else if (fc == true) {
                            feaCodeList.add(line.trim());
                        }

                    }

                    //Target ID List                  :00000394963 //nothing to do                                        
                    //Target NE Type                  :RNC //Nothing to do
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

    protected void findBasicIdent(File dire) {

        this.state = getSingleLineFromFile(dire.getAbsoluteFile() + "/STATE");
        this.ip = getSingleLineFromFile(dire.getAbsoluteFile() + "/RNC_IP");
        this.name = getSingleLineFromFile(dire.getAbsoluteFile() + "/RNC_NAME");
        this.location = getSingleLineFromFile(dire.getAbsoluteFile() + "/RNC_LOC");

        try {
            this.targetId = Integer.parseInt(getSingleLineFromFile(dire.getAbsoluteFile() + "/TARGET_ID"));
        } catch (NumberFormatException ex) {
            this.targetId = 0;
            System.out.println(ex.getMessage());
        }

        this.setSpc(getString(dire.getAbsoluteFile() + "/ss7.log",
                "routing-key-dpc",
                0,
                3));

        this.cd = getString(dire.getAbsoluteFile() + "/sw_delivery_id.log",
                " (Active)",
                1,
                3);

    }

}
