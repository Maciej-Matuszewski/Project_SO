package pamiec_wirtualna;



import java.util.HashMap;
import java.util.Map;


public class PageTable {

    HashMap<Integer,PageTableEntry> map = new HashMap<Integer,PageTableEntry>();

    @Override
    public String toString() {
        String s = new String();
        for(Map.Entry<Integer, PageTableEntry> temp : map.entrySet()){
            s = s+ new String(temp.getKey()+" > "+temp.getValue())+"\n";
        }

        return s;
    }
}