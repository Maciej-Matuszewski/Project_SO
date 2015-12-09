package pamiec_wirtualna;



import java.util.HashMap;
import java.util.Map;


public class PageTable {

    HashMap<Integer,PageTableEntry> map = new HashMap<Integer,PageTableEntry>();

    public int getHighestPage(){
        int max = 0;
        for(Map.Entry<Integer, PageTableEntry> temp : map.entrySet()){

            if(temp.getKey()>max){max = temp.getKey();}
        }
        return max;
    }

    @Override
    public String toString() {
        String s = "Strona: ";
        for(Map.Entry<Integer, PageTableEntry> temp : map.entrySet()){
            //s = "Strona: "+ new String(temp.getKey()+" w "+temp.getValue())+"\n";
            s = "Strona: "+ temp.getValue().toString();
            if(temp.getValue().memoryOrSwapFile==0){
                s=s+" w pliku wymiany\n";
            }
            else {
                s=s+"w ramce"+temp.getValue().pageSizeUnits+"\n";
            }
        }

        return s;
    }
}