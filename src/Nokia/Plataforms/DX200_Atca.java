/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.Plataforms;

import Nokia.Common.DXIPACommon;
import Nokia.Common.PLMN;
import Nokia.Common.PLMN_PARAMETER;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
public abstract class DX200_Atca extends DX200 {

    @Override
    protected void findBasicIdent(File dire) {
        this.state = getSingleLineFromFile(dire.getAbsoluteFile() + "/STATE");
        this.targetId = Integer.parseInt(getString(dire.getAbsoluteFile() + "/ZQNI.log",
                "CON  TYPE     SW  C-NUM   ID  NAME         LOCATION",
                2,
                4));

        this.type = getSingleLineFromFile(dire.getAbsoluteFile() + "/ELEMENT_TYPE");

        switch (this.type) {
            case "FlexiNS":
                this.ip = getSingleLineFromFile(dire.getAbsoluteFile() + "/FNS_IP");
                break;
            case "SGSN":
                this.ip = getSingleLineFromFile(dire.getAbsoluteFile() + "/SGSN_IP");
                break;
            default:
                this.ip = "-";
                break;
        }

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

        String _sw = getString(dire.getAbsoluteFile() + "/ZWQO.log",
                "Y    Y",
                0,
                4);

        String _ver = getString(dire.getAbsoluteFile() + "/ZWQO.log",
                "Y    Y",
                0,
                5);

        this.enviroment_delivery = _sw + " " + _ver;

        this.cd = "-";
        this.sw = _sw;

    }

    protected void getPlmn(File dire, String tableName, int targetID) {
    }

}
