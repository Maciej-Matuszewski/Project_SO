package pamiec_wirtualna;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;


public class SwapFileEntry {

    int processID;
    int page;
    char[] data = new char[MemoryManagement.pagesize];

   public SwapFileEntry(Frame frame){
        //CharBuffer cb = CharBuffer.wrap(MemoryManagement.physicalMemory);
       // cb.get(data,frame.number*MemoryManagement.pagesize,MemoryManagement.pagesize);

       data = Arrays.copyOfRange(MemoryManagement.physicalMemory,frame.number*MemoryManagement.pagesize,frame.number*MemoryManagement.pagesize+MemoryManagement.pagesize);
   }

    public SwapFileEntry(Frame frame, int id){
        //CharBuffer cb = CharBuffer.wrap(MemoryManagement.physicalMemory);
        //cb.get(data,frame.number*MemoryManagement.pagesize,MemoryManagement.pagesize);

        data = Arrays.copyOfRange(MemoryManagement.physicalMemory,frame.number*MemoryManagement.pagesize,frame.number*MemoryManagement.pagesize+MemoryManagement.pagesize);

        processID=id;

    }

    public SwapFileEntry(char[] text){
        data = text;
    }

    public SwapFileEntry(char[] text, int pagenumber, int id){
        data = text;
        page = pagenumber;
        processID = id;
    }

    public SwapFileEntry (int pagenumber){
        page=pagenumber;
    }

    public SwapFileEntry (int pagenumber,int id){
        page=pagenumber;
        processID=id;
    }

    void setProcessID(int id){processID=id;}

    @Override
    public String toString() {
        return new String("Fragment pliku wymiany(proces: "+this.processID+"strona: "+this.page+"):"+String.valueOf(this.data));
    }
}
