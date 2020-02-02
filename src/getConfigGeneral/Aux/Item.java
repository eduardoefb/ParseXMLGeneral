package getConfigGeneral.Aux;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private List<pName> pNameList = new ArrayList();

    public void addPname(pName arg) {
        this.pNameList.add(arg);        
    }

    public List<pName> getpNameList() {
        return pNameList;
    }

    public void setpNameList(List<pName> pNameList) {
        this.pNameList = pNameList;
    }

    @Override
    public String toString() {
        return "Item{" + "pNameList=" + pNameList + '}';
    }

    public Item() {
    }

}
