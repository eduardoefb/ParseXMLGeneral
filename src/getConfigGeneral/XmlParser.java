package getConfigGeneral;

import getConfig.config;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import getConfigGeneral.Aux.Item;
import getConfigGeneral.Aux.Listi;
import getConfigGeneral.Aux.mObject;
import getConfigGeneral.Aux.pName;
import getConfigGeneral.Aux.sqlCommands;
import java.io.File;

public final class XmlParser {

    private static final Logger LOGGER = Logger.getLogger(XmlParser.class.getName());
    private String fileName;
    private mObject mobject = null;
    private Item item = null;
    private Listi listi = null;
    private pName pname = null;
    private String value = null;
    private int depth;
    private int totObj = 0;
    private int currObj = 0;
    private float perc = 0;
    private String textOut = "", textPrev = "";
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static String dbUrl = "";
    private static String dbUser = "apache";
    private static String dbPass = "";
    static int CustomerId = 0;
    static final String Version = "0.0.14";
    static String work_dir = "/opt/nokia/nedata/logs/";
    private final String currenDirString = "-current_database";
    private config conf;

    Connection con;
    Statement statement;

    public XmlParser(String direName, int custId) {
        String custDire = direName;
        File[] elist = new File(custDire).listFiles();
        for (int i = 0; i < elist.length; i++) {
            if (elist[i].getName().endsWith(".xml")) {
                ParseXmlFile(direName + elist[i].getName(), custId);
                elist[i].renameTo(new File(elist[i].getAbsoluteFile() + currenDirString));

            }
        }

    }

    private void ParseXmlFile(String fileName, int custId) {
        sqlCommands cmdList = new sqlCommands();
        String dateTime = "";

        conf = new config();
        this.dbUser = conf.getDbUser();
        this.dbPass = conf.getDbPass();
        this.dbUrl = "jdbc:mariadb://127.0.0.1:3306/" + conf.getDb2();  //DB1 = xml,  DB2 = log

        try {
            this.con = DriverManager.getConnection(XmlParser.dbUrl, XmlParser.dbUser, XmlParser.dbPass);
            this.statement = this.con.createStatement();
            this.con.setAutoCommit(false);

            FileReader fp = null;
            this.setFileName(fileName);

            String line;

            try {
                //Count how many objects it has:
                fp = new FileReader(this.fileName);

                BufferedReader tr = new BufferedReader(fp);
                this.depth = 0;
                while ((line = tr.readLine()) != null) {
                    if (line.contains("<managedObject ")) {
                        this.totObj++;
                    } else if (line.contains("<log dateTime=")) {
                        dateTime = (new RegexMatch(line, "log dateTime=\"([^T ]*)").toString());
                    }
                }
                fp.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                this.fileName = fileName;
                fp = new FileReader(this.fileName);
                BufferedReader tr = new BufferedReader(fp);
                this.depth = 0;

                while ((line = tr.readLine()) != null) {
                    String file_uuid = null;
                    try {
                        file_uuid = this.fileName.split("/")[this.fileName.split("/").length - 1].split("-")[0];
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        file_uuid = "-1";
                    }

                    if (line.contains("<managedObject ")) {

                        this.depth = 0;
                        this.currObj++;
                        this.perc = (float) this.currObj / this.totObj;
                        textOut = String.valueOf((int) (100 * this.perc));

                        this.depth++;
                        mobject = new mObject(con);
                        mobject.setFile_uuid(file_uuid);
                        mobject.setCustomerId(custId);
                        mobject.setPrefix(conf.getDb2());
                        mobject.setDateTime(dateTime);
                        mobject.setClassType(new RegexMatch(line, "class=\"([^\" ]*)").toString());

                        if (!textOut.equals(textPrev)) {
                            LOGGER.log(Level.INFO, "File: {0}, {1} % completed (Obj: {2}).", new Object[]{this.fileName, textOut, mobject.getClassType()});
                            textPrev = textOut;
                        }

                        mobject.setVersion(new RegexMatch(line, "version=\"([^\" ]*)").toString());
                        mobject.setDistName(new RegexMatch(line, "distName=\"([^\" ]*)").toString());

                        String distName[] = mobject.getDistName().replace("PLMN-PLMN/", "").split("/");
                        for (int i = 0; i < distName.length; i++) {
                            this.pname = new pName();                           
                            this.pname.setName("_" + distName[i].split("-")[0]);
                            this.pname.setValue(distName[i].split("-")[1]);
                            this.mobject.addPName(this.pname);
                        }
                        mobject.setId(new RegexMatch(line, "id=\"([^\" ]*)").toString());
                        if (line.contains("/>")) {
                            this.depth--;
                        }

                    } else if (line.contains("<list name=") || line.contains("<item>")) {
                        if (line.contains("<list name=")) {
                            this.listi = new Listi();
                            this.listi.setFile_uuid(file_uuid);
                            this.listi.setCustomerId(CustomerId);
                            this.listi.setName(new RegexMatch(line, "list name=\"([^\" ]*)").toString());
                            this.mobject.addListItem(this.listi);
                        } else if (line.contains("<item>")) {
                            this.item = new Item();
                            this.listi.addItem(this.item);

                        }
                        this.depth++;
                    } else if (line.contains("</list>") || line.contains("</item>") || line.contains("</managedObject>")) {
                        this.depth--;
                        if (line.contains("</managedObject>")) {
                            this.mobject.save(this.statement, cmdList);
                            System.out.printf("");
                        }

                    } else if (line.contains("<p name=\"")) {
                        this.pname = new pName();
                        this.pname.setName(new RegexMatch(line, "<p name=\"([^\" ]*)").toString());
                        this.pname.setValue(new RegexMatch(line, "\">([^\"<\\/]*)").toString());

                        switch (this.depth) {
                            case 1:
                                this.mobject.addPName(this.pname);
                                break;
                            case 2:
                                this.mobject.addListItem(this.listi);
                                break;
                            case 3:
                                this.item.addPname(this.pname);
                                break;
                        }

                    } else if (line.contains("<p>")) {
                        this.listi.addValue(new RegexMatch(line, "<p>([^<]*)").toString());

                    }

                }

                //Process create table:
                Statement st = this.con.createStatement();
                try {
                    for (int i = 0; i < cmdList.getCreate().size(); i++) {
                        st.addBatch(cmdList.getCreate().get(i));
                    }
                    LOGGER.log(Level.INFO, "Creating tables...");
                    int[] count = st.executeBatch();
                    con.commit();
                    LOGGER.log(Level.INFO, "Tables have been created.");
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Delete if exists (removed):
                /*
                st = this.con.createStatement();
                try {                    
                    LOGGER.log(Level.INFO, "Deleting old entries...");
                    for (int i = 0; i < cmdList.getDelete().size(); i++) {
                        st.addBatch(cmdList.getDelete().get(i));
                    }

                    int[] count = st.executeBatch();
                    con.commit();
                    LOGGER.log(Level.INFO, "Old entries deleted. ");
                    st.close();

                } catch (SQLException ex) {
                    Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                */

                //Alter Tables
                st = this.con.createStatement();
                try {
                    System.out.printf("\nChanging tables...\n");
                    String text = "";
                    String textAnt = "1";
                    float perc;
                    for (int i = 0; i < cmdList.getAlter().size(); i++) {
                        perc = (float) (i / cmdList.getAlter().size()) * 100;
                        perc = (float) i / cmdList.getAlter().size();
                        text = String.valueOf((int) (100 * perc));

                        try {
                            st.execute(cmdList.getAlter().get(i));
                        } catch (SQLException ex) {
                            if (!ex.getMessage().contains("Duplicate column name")) {
                                System.out.println("Command: " + cmdList.getAlter().get(i) + "\nError: " + ex.getMessage());
                            }

                        }
                        if (!text.equals(textAnt)) {
                            LOGGER.log(Level.INFO, "Updating table columns... file: {0}, {1} % completed (Obj: {2}).", new Object[]{this.fileName, text, mobject.getClassType()});
                            textAnt = text;
                        }
                    }
                    int[] count = st.executeBatch();
                    con.commit();
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.printf("");

                try {
                    int[] count = statement.executeBatch();
                    con.commit();
                    System.out.printf("");
                } catch (BatchUpdateException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.printf("");

            } catch (FileNotFoundException ex) {
                Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);

            } finally {
                try {
                    fp.close();
                } catch (IOException ex) {
                    Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            con.close();
        } catch (IOException ex) {
            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;

    }

    public mObject getMobject() {
        return mobject;
    }

    public void setMobject(mObject mobject) {
        this.mobject = mobject;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Listi getListi() {
        return listi;
    }

    public void setListi(Listi listi) {
        this.listi = listi;
    }

    public pName getPname() {
        return pname;
    }

    public void setPname(pName pname) {
        this.pname = pname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getTotObj() {
        return totObj;
    }

    public void setTotObj(int totObj) {
        this.totObj = totObj;
    }

    public int getCurrObj() {
        return currObj;
    }

    public void setCurrObj(int currObj) {
        this.currObj = currObj;
    }

    public float getPerc() {
        return perc;
    }

    public void setPerc(float perc) {
        this.perc = perc;
    }

}
