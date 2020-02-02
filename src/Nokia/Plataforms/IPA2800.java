/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.Plataforms;

import Nokia.Common.DXIPACommon;
import java.io.File;

public abstract class IPA2800 extends DXIPACommon{
    protected String oms_ip_prim = "-";   //OMS IP is not updated here, its updated from XML File information
    protected String oms_ip_sec = "-";

    public String get_oms_ip_sec() {
        return oms_ip_sec;
    }

    public void set_oms_ip_sec(String oms_ip_sec) {
        this.oms_ip_sec = oms_ip_sec;
    }
        
    public String get_oms_ip_prim() {
        return oms_ip_prim;
    }

    public void set_oms_ip_prim(String oms_ip) {
        this.oms_ip_prim = oms_ip;
    }
    
    private String getRNCType(File file){
        String ret = "RNC";        
        return ret;
    }
            
    protected void findBasicIdent(File dire) {
        
        this.state = getSingleLineFromFile(dire.getAbsoluteFile() + "/STATE");
        this.ip =  getSingleLineFromFile(dire.getAbsoluteFile() + "/RNC_IP");
        this.name =  getSingleLineFromFile(dire.getAbsoluteFile() + "/RNC_NAME");
        this.location = getSingleLineFromFile(dire.getAbsoluteFile() + "/RNC_LOC");   
        this.date = this.findDate(dire.getAbsoluteFile() + "/ZNET.log");
        
        this.targetId = Integer.parseInt(getString(dire.getAbsoluteFile() + "/TARGET_ID.log",
                "C_NUMBER:",
                0,
                2));
                                     
        String tmp = getString(dire.getAbsoluteFile() + "/ZNET.log",
                "OWN SIGNALLING POINT",
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
    
    
}
