package Nokia.FNS;

import Nokia.Plataforms.DX200;
import java.io.File;
import java.sql.Connection;

/**
 *
 * @author eduabati
 */
public class SGSN extends FNS {
    public SGSN(File dire, int custId, Connection con) {          
        super(dire, custId, con);
        this.setType("SGSN");
        super.setUuid();
    }
}
