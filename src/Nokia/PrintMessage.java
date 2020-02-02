/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nokia;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintMessage {
    public PrintMessage(String msg){
        Logger LOGGER = Logger.getLogger("Message: ");
        LOGGER.log(Level.INFO,msg);
        //System.out.printf(msg);
    }
}
