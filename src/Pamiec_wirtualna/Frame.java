package Pamiec_wirtualna;

import ZarzadzanieProcesami.Management;
import ZarzadzanieProcesami.Proces;

import java.nio.CharBuffer;
import java.util.Arrays;

public class Frame {

    int processID;
    int page;
    int number;

    static int init = 0; //!remove


    byte flags = 2; //bit odwo�ania, bit "brudny"
    // dirty = 0b0000000000000001 = 1
    // used = 0b0000000000000010 = 2
    // hastToBeSaved = 0b0000000000000100 = 4

    Frame(){

    }


    Frame(int number, SwapFileEntry s){
        this.number = number;
        this.page = s.page;
        this.processID = s.processID;

        //CharBuffer cb = CharBuffer.wrap(MemoryManagement.physicalMemory);
        //cb.put(s.data,page*MemoryManagement.pagesize,s.data.length);
        //TODO overwrite destination offset
        //MemoryManagement.overwrite(MemoryManagement.physicalMemory,s.data,page*MemoryManagement.pagesize,MemoryManagement.pagesize);
        //MemoryManagement.overwrite(s.data,0,MemoryManagement.physicalMemory,page*MemoryManagement.pagesize,s.data.length);
        MemoryManagement.overwrite(s.data,0,MemoryManagement.physicalMemory,number*MemoryManagement.pagesize,s.data.length);
        //TODO make it work with blank spaces ?
    }

    Frame(int number, int processID){
        this.number = number;
        this.processID = processID;
    }

    Frame(int number, int page, int processID){
        this.number = number;
        this.page = page;
        this.processID = processID;
    }




    void swap (int pagenumber, int newprocessID){
        MemoryManagement.swapFile.add(new SwapFileEntry(this,processID));
        Proces pcb = Management.processLookup(processID);
        PageTableEntry modifiedEntry = pcb.ptable.map.get(MemoryManagement.frameTable[number].page);
        modifiedEntry.memoryOrSwapFile=0; //0 - out of physical memory
        pcb.ptable.map.put(MemoryManagement.frameTable[number].page, modifiedEntry );

        if((flags & MemoryManagement.mustSave) != 0){
            MemoryManagement.swapFile.add(new SwapFileEntry(this));
        }


        for(SwapFileEntry temp : MemoryManagement.swapFile){
            if(temp.page == pagenumber && temp.processID == newprocessID){
                //CharBuffer cb  = CharBuffer.wrap(MemoryManagement.physicalMemory);
                //cb.put(temp.data,MemoryManagement.physicalMemory[number*MemoryManagement.pagesize],MemoryManagement.pagesize); //loading new data into the physical memory
                MemoryManagement.overwrite(temp.data,0,MemoryManagement.physicalMemory,number*MemoryManagement.pagesize,temp.data.length);
            }
        }


    }


    @Override
    public String toString() {

        //CharBuffer cb = CharBuffer.wrap(MemoryManagement.physicalMemory);
        char[] content = new char[MemoryManagement.pagesize];
        //cb.get(content, this.number * MemoryManagement.pagesize, MemoryManagement.pagesize);

        content = Arrays.copyOfRange(MemoryManagement.physicalMemory,this.number*MemoryManagement.pagesize,this.number*MemoryManagement.pagesize+MemoryManagement.pagesize);

        String s = new String("======="+"\n"+"Ramka numer " + this.number + " (strona:" + this.page + " proces:" + this.processID+"\n"+"Zawartość:\n"+String.valueOf(content)+"\n"+"=======");
        return s;
    }
}

