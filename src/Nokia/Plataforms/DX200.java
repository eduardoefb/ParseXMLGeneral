/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.Plataforms;

import Nokia.Common.DXIPACommon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public abstract class DX200 extends DXIPACommon {



    @Override
    public int getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getSpc() {
        return spc;
    }

    @Override
    public void setSpc(String spc) {
        this.spc = spc;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String getCd() {
        return cd;
    }

    @Override
    public void setCd(String cd) {
        this.cd = cd;
    }

    @Override
    public String getSw() {
        return sw;
    }

    @Override
    public void setSw(String sw) {
        this.sw = sw;
    }

    
    @Override
    public int getTargetId() {
        return targetId;
    }

    @Override
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
        
    protected void findBasicIdent(File dire) {
        if (chkX25(dire.getAbsoluteFile() + "/ZQLI.log")) {
            this.ip = "X.25";
        } else {
            this.ip = findOmuIp(dire.getAbsoluteFile() + "/ZQRIOMU.log");
        }
                
        this.state = getSingleLineFromFile(dire.getAbsoluteFile() + "/STATE");
        this.targetId = Integer.parseInt(getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "CON  TYPE     SW  C-NUM   ID  NAME         LOCATION",
                2,
                4));
        this.type = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "LOADING PROGRAM VERSION",
                2,
                1);

        this.location = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "CON  TYPE     SW  C-NUM   ID  NAME         LOCATION",
                2,
                6);

        this.name = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "CON  TYPE     SW  C-NUM   ID  NAME         LOCATION",
                2,
                5);
                

        this.date = getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "LOADING PROGRAM VERSION",
                2,
                3);

        String tmp = getString(dire.getAbsoluteFile() + "/ZNET.log",
                "OWN SIGNALLING POINT AS SIGNAL",
                0,
                1);

        if (tmp.contains("/")) {
            String[] tmp1 = tmp.split("/");
            this.spc = tmp1[1];
        }

        this.cd = getString(dire.getAbsoluteFile() + "/ZWQO.log",
                "Y    Y",
                2,
                1);

    }

    protected String findOmuIp(String log) {
        String ret = "0.0.0.0";
        try {
            FileReader fp = new FileReader(log);
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                line = line.replace("*", "");
                if ((line.contains(" P    ") || line.contains(" L    ")) && !line.contains("(") && !line.contains("192.168")) {
                    String[] r = line.substring(30, 55).trim().split("/");
                    ret = r[0];
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

    protected boolean chkX25(String log) {
        boolean ret = false;
        try {
            FileReader fp = new FileReader(log);
            BufferedReader tr = new BufferedReader(fp);
            String line;
            while ((line = tr.readLine()) != null) {
                if (line.contains("X.25") && line.contains("OMU") && line.contains("UNL-ENA")) {
                    ret = true;
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

   
    
}
