package Pamiec_wirtualna;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;


import ZarzadzanieProcesami.Management;
import ZarzadzanieProcesami.Proces;


public class MemoryManagement {

    static int frameCount = 4;
    static int pagesize = 16;

    static char[] physicalMemory = new char[frameCount*pagesize];
    //public static LinkedList<Integer> freeFrames = new LinkedList<>(Arrays.asList(1,2,3,4));
    public static Set<Integer> freeFrames = new HashSet<>(Arrays.asList(0,1, 2, 3));
    public static Frame[] frameTable = new Frame[frameCount];

    public static LinkedList<SwapFileEntry> swapFile = new LinkedList<>();

    public static int clockHand = 0;

    static byte dirty = 1;
    static byte used = 2;
    static byte mustSave = 4;
    static byte clear = 0b1111100;
    static byte swappable = 0b0000011;


    //enum location {file, mem}//TODO

    public static void main(String[] args){
        MemoryManagement mm = new MemoryManagement();
        Management.fork();

        mm.readProgramtTest(1);
        mm.displayStatus();

        System.out.println("odczyt pamieci:"+String.valueOf(mm.readMemory(1,30,1)));
        mm.displayStatus();
        System.out.println("odczyt pamieci:"+String.valueOf(mm.readMemory(64,5,1)));
        mm.displayStatus();
        System.out.println("odczyt pamieci:"+String.valueOf(mm.readMemory(80,5,1)));
        mm.displayStatus();
    }



    void readProgram(String programFile, int processID) {
        char[] bufor = new char[pagesize];
        char[] file = new char[10]; /* TODO function_returning_file_content(programFile)*/
        long length = programFile.length();
        double k = (double) length / (double) pagesize;
        Proces pcb = Management.processLookup(processID);
        try {
            for (int i = 0; i < k; i++) {
                //CharBuffer cb = CharBuffer.wrap(file);
               // cb.get(bufor, i * pagesize, pagesize);
                bufor = Arrays.copyOfRange(file,i*pagesize,i*pagesize+pagesize);
                swapFile.add(new SwapFileEntry(bufor, i, processID));
                pcb.ptable.map.put(i, new PageTableEntry(i, 0));
            }


        } catch (Exception e) {//TODO
        }
    }


    int translateAddress(int virtualAddress, int processID) {

        Proces pcb = Management.processLookup(processID);
        int paddress;
        int pagenumber = virtualAddress / pagesize;
        int offset = virtualAddress % pagesize;
        try {

            if(pcb.ptable.map.get(pagenumber) == null){
                //!
                System.out.println("stronicy nie ma w tabeli stronic");
                //!
                SwapFileEntry sfe = new SwapFileEntry(pagenumber,processID);
                pcb.ptable.map.put(pagenumber,new PageTableEntry(pagenumber,0));

                translateAddress(virtualAddress,processID);
            }

            if (pcb.ptable.map.get(pagenumber).memoryOrSwapFile == 1) { //page in memory
                paddress = pcb.ptable.map.get(pagenumber).pageSizeUnits + offset; //TODO check it
                //!
                System.out.println("weszło do ifa1");
                //!
                return paddress;
            }
            //else if (pcb.ptable.map.get(pagenumber) == null) {
                //add SwapFileEntry to SwapFile and an entry to the PageTable
                if (freeFrames.size() != 0) {//page can be loaded into memory
                    //TODO fill up a free frame
                    /*Iterator<Integer> it = freeFrames.iterator();
                    int freeFrameNumber = it.next(); deprecated*/
                    int freeFrameNumber = getFreeFrame();
                    //!
                    System.out.println("freeframenumber "+freeFrameNumber);
                    //!
                    /*freeFrames.remove(freeFrameNumber); deprecated*/
                    System.out.println("szukana strona to:"+pagenumber+"procesu "+processID);
                    for(SwapFileEntry temp : swapFile){
                        if(temp.processID == processID && temp.page == pagenumber)
                        {
                           // Iterator<Integer> it2 = freeFrames.iterator();
                           // int freeFrameNumber2 = it.next(); //TODO make finding free frames a function
                            frameTable[freeFrameNumber] = new Frame(freeFrameNumber, temp);
                            //TODO update page table
                            pcb.ptable.map.put(pagenumber,new PageTableEntry(freeFrameNumber,1));
                            paddress = pcb.ptable.map.get(pagenumber).pageSizeUnits*MemoryManagement.pagesize+offset;//test - result of get() should be the same as f.number
                            //!
                            System.out.println("weszło do odpowiedniego ifa(2)");
                            //!
                            return paddress;
                        }
                    }
                    System.out.println("Nie znaleziono szukanego fragmetnu pliku wymiany");
                    /*frameTable[freeFrameNumber] = new Frame(freeFrameNumber, pagenumber, processID); // TODO PageFault happening
                    pcb.ptable.map.put(pagenumber, new PageTableEntry(freeFrameNumber, 1));
                    paddress = freeFrameNumber*MemoryManagement.pagesize + offset;

                    return paddress;*/
                }
                else
                {
                    pcb.ptable.map.put(pagenumber, new PageTableEntry(pagenumber/*to be changed if switched to iterative swapfile*/, 0));/*integer signyfying that page is in swapfile*/
                    swapFile.add(new SwapFileEntry(pagenumber, processID)); //
                    //!
                    System.out.println("weszło do ifa2");
                    //!
                }
            //}

            //TODO PageFault
            //!
            System.out.println("Page fault z wymiana");
            //!

            Frame f = frameTable[findVictim()];

            if((f.flags& mustSave) !=0){
                f.swap(pagenumber,processID);
                //TODO update page table
                pcb.ptable.map.put(pagenumber,new PageTableEntry(f.number,1));
                paddress = pcb.ptable.map.get(pagenumber).pageSizeUnits*MemoryManagement.pagesize+offset;//test - result of get() should be the same as f.number
                return paddress;
            }
            else
            {
                for(SwapFileEntry temp : MemoryManagement.swapFile){
                    if(temp.processID == processID && temp.page == pagenumber)
                    {
                        f = new Frame(f.number,temp);
                        //TODO update page table
                        pcb.ptable.map.put(pagenumber,new PageTableEntry(f.number,1));
                        paddress = pcb.ptable.map.get(pagenumber).pageSizeUnits*MemoryManagement.pagesize+offset;//test - result of get() should be the same as f.number
                        return paddress;
                    }
                }
            }

        }

        catch (NullPointerException ne) {
        }
        catch (Exception e) {
        }
        //!
        //System.out.println(pcb.ptable.map);
        System.out.println(pcb.ptable);
        //!

        return -1; //something went wrong
    }


    //TODO what if the page is in the swapFile
    char[] readMemory(int virtualAddress, int size, int processID) { //rename to memoryRead ?
        //Proces pcb = Management.processLookup(processID);

        int paddress = translateAddress(virtualAddress, processID);
        //!

        System.out.println("paddress: "+paddress);
        System.out.println("znak pod paddress:"+String.valueOf(physicalMemory[paddress]));
        System.out.println("strona "+paddress/pagesize);
        //!
        char[] output = new char[size];


        //int index = paddress & 0b00110000 * 16 + paddress & 0b00001111;

        int offset = paddress % MemoryManagement.pagesize;

        if(offset+size>pagesize) {
            int leftToRead = size;

            String result = new String(readMemory(virtualAddress, pagesize - offset, processID)); //read till the end of current frame
            leftToRead = leftToRead - (pagesize - offset);
            int wholePages = leftToRead / pagesize;
            for (int i = 0; i < wholePages; i++) {
                result = result + new String(readMemory(paddress + pagesize - offset, pagesize, processID));
                leftToRead = leftToRead - pagesize;
            }
            if (leftToRead > 0){
                result = result + new String(readMemory(paddress + size - leftToRead, leftToRead, processID));
            }
            //!
            System.out.println("wynik długiego odczytu: "+result);
            //!
            output = result.toCharArray();

            return output;
        }

        output = Arrays.copyOfRange(physicalMemory,paddress,paddress+size); //TODO make it work properly (using virtual address)

        return output;


    }


    void writeMemory(int virtualAddress, char[] input, int processID) { //rename to memoryRead ?
        //Proces pcb = Management.processLookup(processID);

        int paddress = translateAddress(virtualAddress, processID);
        int frameNumber = paddress/pagesize;
        byte dirtyandused = 3;
        frameTable[frameNumber].flags = (byte) (frameTable[frameNumber].flags | dirtyandused);

        //CharBuffer cb = CharBuffer.wrap(physicalMemory);
        //int index = paddress & 0b00110000 * 16 + paddress & 0b00001111;

        //cb.put(input,frameNumber*pagesize,input.length);

        //
        //overwrite(physicalMemory,input,frameNumber*pagesize,input.length);
        //TODO make it work properly (recursively, using virtual ddresses)

        overwrite(input,0,physicalMemory,frameNumber*pagesize,input.length);






    }

    void releaseMemory(int processid) {
        Proces pcb = Management.processLookup(processid); //will be needed in an indexed swapfile
        for (Frame temp : MemoryManagement.frameTable) {
            //TODO release frames
            if (temp.processID == processid) {
                boolean sanityCheck = freeFrames.add(temp.number);
                if (sanityCheck == false) {
                    try {
                        throw new Exception("Zwalniana ramka znajdowała się na liscie wolnych ramek!");
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
                //temp.
            }
        }
        //TODO delete swap file entries associated with this process
        Iterator<SwapFileEntry> it = swapFile.iterator();
        while (it.hasNext()) {

            if(it.next().processID == processid){
                it.remove();
            }

        }
        /*for(SwapFileEntry temp : swapFile){

            if(temp.processID == processid){
                temp.
            }
        }*/

    }

    int findVictim(){
        byte dirtyUsed = (byte) (dirty|used);
        while(true){
            Frame f = MemoryManagement.frameTable[MemoryManagement.clockHand%frameCount];
            if((f.flags&swappable)==0){ //dirty == 0 & used == 0
                return f.number; //f.number should equal clockHand%frameCount
            }
            else if((f.flags & dirtyUsed) != 0 ) // dirty == 1 & used == 1
            {
                f.flags = dirty;
            }
            else if ((f.flags&dirty)!=0||(f.flags&used)!=0) // dirty == 1 | used == 1
            {
                f.flags = (byte)(f.flags&clear);
            }
            MemoryManagement.clockHand++;
        }
    }

    static int getFreeFrame(){
        Iterator<Integer> it = freeFrames.iterator();
        if(it.hasNext()){
            Integer element = it.next();
            it.remove();
            return element;
        }
        else return -1;
    }

    void readProgramtTest(int processID){
        String s = new String("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        char[] file = s.toCharArray();
        char[] bufor = new char[pagesize];
        //!
        System.out.println("\"FILE\""+String.valueOf(file));
        System.out.println("niezainicjalizowany bufor:"+String.valueOf(bufor));
        //!
        //char[] file = new char[10]; /* TODO function_returning_file_content(programFile)*/
        long length = file.length;
        //!
        System.out.println("length:"+length+" pagesize:"+pagesize);
        //!
        double k = (double) length / (double) pagesize;
        Proces pcb = Management.processLookup(processID);
        try {
            for (int i = 0; i < k; i++) {
               // CharBuffer cb = CharBuffer.wrap(file);
               // cb.get(bufor, i * pagesize, pagesize);
                bufor = Arrays.copyOfRange(file,i*pagesize,i*pagesize+pagesize);
                //!
                System.out.println("Próba!"+new SwapFileEntry(bufor,i,processID));
                System.out.println("k: "+k+" i: "+i);
                System.out.println(bufor);
                //!
                swapFile.add(new SwapFileEntry(bufor, i, processID));
                pcb.ptable.map.put(i, new PageTableEntry(i, 0));
            }


        } catch (Exception e) {//TODO
        }
    }

    void displayStatus (){
        if(freeFrames.size()!=frameCount) {

        for(Frame temp : MemoryManagement.frameTable){

                if(temp == null)
                {
                    continue;
                }
                System.out.println("=======");
                System.out.println("Ramka numer " + temp.number + " (strona:" + temp.page + " proces:" + temp.processID);
                System.out.println("Zawartość:");
               // CharBuffer cb = CharBuffer.wrap(physicalMemory);
                char[] content = new char[pagesize];
               // cb.get(content, temp.number * pagesize, pagesize);
                content = Arrays.copyOfRange(physicalMemory,temp.number,temp.number+pagesize);
                System.out.println(String.valueOf(content));
                System.out.println("=======");
            }
        }
        else{
            System.out.println("Wszystkie ramki są wolne");

        }

        for(SwapFileEntry temp : swapFile){
            System.out.println("fragment pliku wymiany:"+temp.processID+" "+temp.page+" "+String.valueOf(temp.data));
        }
        //
        Proces pcb = Management.processLookup(1);
        System.out.println(pcb.ptable);

    }

    public static void overwrite(char[] source, char[] dst, int dstoffset, int length){

        for(int i = 0;i<length;i++){
            dst[dstoffset+i]=source[i];
        }

    }

    public static void overwrite(char[] source,int srcoffset, char[] dst, int dstoffset, int length) {
        for (int i = 0; i < length-srcoffset; i++) {
            dst[dstoffset + i] = source[i + srcoffset];
        }
    }

}