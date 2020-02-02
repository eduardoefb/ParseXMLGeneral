package parselog;

import Nokia.DbObjects;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer implements DbObjects {

    private static String tableName = "cliente";
    private String name;
    private int id;
    private Connection con;

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public Customer() {
    }
    public Customer(int id, String name, Connection con) {
        this.id = id;
        this.con = con;
        this.name = name;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAll() {
        return ("Customer ID: " + this.id
                + "\nCustomer Name: " + this.name);
    }

    public boolean SendToMysql() {
        String Cmd = "";
        boolean ret = false;
        Statement statement = null;

        try {
            statement = con.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT * FROM " + this.tableName + " WHERE cliente.id = " + this.id);
            int linCount = 0;
            while (resultset.next()) {
                linCount++;
            }
            
            
            if (this.name != null && linCount == 0) {                                                
                Cmd = "INSERT INTO "+ this.tableName +" (id,"
                        + "\n\tname)"
                        + "\n\tVALUES('" + this.id + "',"
                        + "\n\t'" + this.name + "')";
            } else {
                Cmd = "SHOW TABLES";
            }
            statement.execute(Cmd);


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    @Override
    public boolean RemoveFromMysql() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
