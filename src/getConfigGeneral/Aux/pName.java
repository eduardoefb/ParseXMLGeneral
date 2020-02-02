package getConfigGeneral.Aux;

import java.util.ArrayList;
import java.util.List;

public class pName {

    private String name;
    private String value;

    private List<String> RESERVED_WORDS;

    public pName(String name, String value) {
        this.RESERVED_WORDS = new ArrayList();
        this.RESERVED_WORDS.add("mod");
        this.RESERVED_WORDS.add("usage");

        this.setName(name);
        this.setValue(value);
    }

    public pName() {
        this.RESERVED_WORDS = new ArrayList();
        this.RESERVED_WORDS.add("mod");
        this.RESERVED_WORDS.add("usage");
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        if (this.RESERVED_WORDS.contains(name)) {
            this.name = name.replace(name, "_" + name);
        } else {
            this.name = name.replace("'", " ");
        }
    }

    public void setValue(String value) {
        if (this.RESERVED_WORDS.contains(value)) {
            this.value = value.replace(value, "_" + value);
        } else {
            this.value = value.replace("'", " ");
        }
    }

}
