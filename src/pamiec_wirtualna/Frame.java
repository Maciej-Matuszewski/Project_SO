package pamiec_wirtualna;


import zarzadzanie_procesami.*;

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

    Frame(){}


    Frame(int number){

    }

    Frame(int number, pamiec_wirtualna.SwapFileEntry s){
        if(s==null){
            System.out.println("fragment pliku wymiany == null");
        }
        try {
            this.number = number;
            this.page = s.page;
            this.processID = s.processID;
        }
        catch (Exception e){
            System.out.println("fragment pliku wymiany > "+s);
        }


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

        String s = new String("======="+"\n"+"Ramka numer " + this.number + " (strona: " + this.page + " proces: " + this.processID);
        //String fstring = new String(Integer.toString((int)flags));
        String fstring = Integer.toBinaryString((int) flags);
        s=s+" ustawienienie flag: "+fstring;
        /*if(fstring.substring(fstring.length()-2)=="11"){
           s=s+" bit odwolania, bit brudny";
        }
        else if(fstring.substring(fstring.length()-2)=="01"){
            s=s+"bit brudny";
        }
        else if(fstring.substring(fstring.length()-2)=="10"){
            s=s+"odwolania";
        }
        else {
            s=s+"zadna flaga nie jest ustawiona";
        }*/
        s = s+")\n"+" Zawartosc: \n"+String.valueOf(content)+"\n"+"=======";
        return s;
    }
}

