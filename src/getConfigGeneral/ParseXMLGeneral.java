package getConfigGeneral;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParseXMLGeneral {

    public static void main(String[] args) {
        String custId = "";
        if (args.length < 1) {
            System.out.println("Usage:"
                    + "\n\tParseXMLGeneral <dire>");
            System.exit(0);
        }

        try {

            FileReader fp = new FileReader(args[0] + "/.id");
            BufferedReader tr = new BufferedReader(fp);

            while ((custId = tr.readLine()) != null) {
                if (custId.length() > 0) {
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        new logParser(args[0], Integer.parseInt(custId));
        new XmlParser(args[0], Integer.parseInt(custId));
    }
}
