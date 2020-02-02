package getConfigGeneral.Aux;

import java.util.ArrayList;
import java.util.List;

public class Listi {

    private String name;
    private int customerId;
    private String file_uuid;
    private List<Item> ItemList = new ArrayList();
    private List<String> ValueList = new ArrayList();

    public String getFile_uuid() {
        return file_uuid;
    }

    public void setFile_uuid(String file_uuid) {
        this.file_uuid = file_uuid;
    }

    
    public void addValue(String arg) {        
        this.ValueList.add(arg);
    }

    public void addItem(Item arg) {
        this.ItemList.add(arg);
    }

    public List<Item> getItemList() {
        return ItemList;
    }

    public void setItemList(List<Item> ItemList) {
        this.ItemList = ItemList;
    }

    public Listi() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValueList() {
        return ValueList;
    }

    public void setValueList(List<String> ValueList) {
        this.ValueList = ValueList;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
        
    
    @Override
    public String toString() {
        return "Listi{" + "name=" + name + ", ItemList=" + ItemList + ", ValueList=" + ValueList + '}';
    }

}
