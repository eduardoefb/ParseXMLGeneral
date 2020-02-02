package getConfigGeneral.Aux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class sqlCommands {

    private List<String> create;
    private List<String> delete;
    private List<String> alter;
    private final int MAX_LIST_SIZE_CHECK = 900000;

    public sqlCommands() {
        this.create = new ArrayList();
        this.delete = new ArrayList();
        this.alter = new ArrayList();
    }

    public List<String> getCreate() {
        return create;
    }

    public void setCreate(List<String> create) {
        this.create = create;
    }

    public List<String> getAlter() {
        return alter;
    }

    public void setAlter(List<String> alter) {
        this.alter = alter;

    }

    public void addCreate(String str) {
        this.add(create, str);
    }

    public void addDelete(String str) {
        if (!delete.contains(str)) {
            this.add(delete, str);            
        }                       
    }

    public List<String> getDelete() {
        return delete;
    }

    public void setDelete(List<String> delete) {
        this.delete = delete;
    }

    public void addAlter(String str) {
        this.add(alter, str);
    }

    public void addCmd(String str) {

        if (str.startsWith("CREATE ")) {
            this.addCreate(str);
        }

        if (str.startsWith("DELETE ")) {
            this.addDelete(str);

        }

        if (str.startsWith("ALTER ")) {
            this.addAlter(str);
        }

    }

    private void add(List<String> lst, String str) {
        lst.add(str);
        System.out.printf("");
        if (lst.size() > this.MAX_LIST_SIZE_CHECK) {
            Set<String> uniqueGas = new HashSet<String>(lst);
            lst.clear();
            System.out.printf("");
            for (int i = 0; i < uniqueGas.size(); i++) {
                lst.add(uniqueGas.toArray()[i].toString());
            }
            System.out.printf("");
            System.out.println("Array size: " + uniqueGas.size());
        }
    }

}
