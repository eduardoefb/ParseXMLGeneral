/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia.BSC;

import Nokia.DbCommon;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TRX  extends DbCommon {

    private final String tableName = "trx";
    private double bts_ident_fk;
    private double trx_ident;
    private int bts_id;
    private int trx_id;
    private String mbcch;
    private String edge_supported;
    private String freq;
    private Connection con;
    private int bcf_id;
    private int bsc_cnumber;
    private final int MAX_TRX_PER_BTS = 100;
    private int max_bts_per_bcf;
    private int max_bcf_per_bsc;

    public TRX(){
        this.setUuid();
    }
    
    public void setMax_bts_per_bcf(int max_bts_per_bcf) {
        this.max_bts_per_bcf = max_bts_per_bcf;
    }

    public void setMax_bcf_per_bsc(int max_bcf_per_bsc) {
        this.max_bcf_per_bsc = max_bcf_per_bsc;
    }

    public int getBcf_id() {
        return bcf_id;
    }

    public void setBcf_id(int bcf_id) {
        this.bcf_id = bcf_id;
    }

    public int getBsc_cnumber() {
        return bsc_cnumber;
    }

    public void setBsc_cnumber(int bsc_cnumber) {
        this.bsc_cnumber = bsc_cnumber;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public double getBts_ident_fk() {
        return bts_ident_fk;
    }

    public void setBts_ident_fk(double bts_ident_fk) {
        this.bts_ident_fk = bts_ident_fk;
    }

    public double getTrx_ident() {
        return trx_ident;
    }

    public void setTrx_ident(double trx_ident) {
        this.trx_ident = trx_ident;
    }

    public int getBts_id() {
        return bts_id;
    }

    public void setBts_id(int bts_id) {
        this.bts_id = bts_id;
    }

    public int getTrx_id() {
        return trx_id;
    }

    public void setTrx_id(int trx_id) {
        this.trx_id = trx_id;
    }

    public String getMbcch() {
        return mbcch;
    }

    public void setMbcch(String mbcch) {
        this.mbcch = mbcch;
    }

    public String getEdge_supported() {
        return edge_supported;
    }

    public void setEdge_supported(String edge_supported) {
        this.edge_supported = edge_supported;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public void setTrx_ident() {
        this.bts_ident_fk = ((long) this.bsc_cnumber * (long) this.max_bts_per_bcf) + this.bts_id;
        this.trx_ident = ((long) this.bts_ident_fk * (long) MAX_TRX_PER_BTS) + (long) this.trx_id;
    }
    
    public boolean AddSqlCmd(Statement statement) {
        String Cmd = "";
        boolean ret = false;        
        try {
            
            Cmd = "INSERT INTO " + this.tableName + " ("
                    + "bts_ident_fk, "
                    + "uuid, "
                    + "parent_uuid, "
                    + "file_uuid, "
                    + "trx_ident, "
                    + "bts_id, "
                    + "trx_id, "
                    + "trx_mbcch, "
                    + "trx_edge_supported, "
                    + "freq) "
                    + "VALUES( "
                    + "'" + this.bts_ident_fk + "', "
                    + "'" + this.uuid + "', "
                    + "'" + this.parent_uuid + "', "
                    + "'" + this.file_uuid + "', "
                    + "'" + this.trx_ident + "', "
                    + "'" + this.bts_id + "', "
                    + "'" + this.trx_id + "', "
                    + "'" + this.mbcch + "', "
                    + "'" + this.edge_supported + "', "
                    + "'" + this.freq + "') ";                                
            statement.addBatch(Cmd);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage() + ""
                    + "\nBSC = " + this.bsc_cnumber 
                    + "\nBCF = " + this.bcf_id
                    + "\nBTS = " + this.bts_id                    
                    + "\nTRX = " + this.trx_id    
                    + "\nTRX_Ident = " + this.trx_ident
                    + "\nBTS Ident = " + this.bts_ident_fk                    
            );
            
        }

        return ret;
    }

}
