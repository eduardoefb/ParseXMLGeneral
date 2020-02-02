package getConfigGeneral;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatch {

    private String in;
    private String out;

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    @Override
    public String toString() {
        return this.out;
    }

    public RegexMatch(String in, String regex) {
        this.in = in;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(this.in);        
        if (m.find()) {
            this.out = m.group(1);
        } else {
            this.out = "";
        }
        
    }

}
