package pamiec_wirtualna;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Interpreter.Output;
import obsluga_dysku.FlorekFileSystem;
import zarzadzanie_procesami.Management;
import zarzadzanie_procesami.Proces;


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
        //MemoryManagement mm = new MemoryManagement();


        Proces p = Management.fork();

        FlorekFileSystem.Create_File("Program1", "mv RA,01mv RB,05ad RA,RBj1 00uuuuuuuuuuuuuuuuuuooooooooooooooooooooaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeiiiiiiiiiiiiiiiiiiiii");
        readProgram("Program1",p.PID);
        readMemory(2,2,p.PID);
        readMemory(60,10,p.PID);
        displayAddressSpace(p.PID);

       /*FlorekFileSystem.Create_File("testowy","mv RA,10ml RA,05sb RA,01");
        readProgram("testowy",p.PID);
        Output.write("czy adres 6 jest w pammieci "+String.valueOf(inMemory(6,p.PID)));
        Output.write(readMemory(0,5,p.PID));
        Output.write("czy adres 6 jest w pammieci "+String.valueOf(inMemory(6,p.PID)));
        displayStatus();*/


        /*mm.readProgramtTest(1);
        mm.displayStatus();

        Output.write("odczyt pamieci:"+String.valueOf(mm.readMemory(1,100,1)));
        mm.displayStatus();
        //String testinput = new String("xxxxxxxxxxyyyyyyyyyyzzzzzzzzzz");
        String testinput = new String("0123456789ABCDEF0123456789ABCDEF");
        Output.write("zapis do pamieci \""+testinput+"\" od poczatku");
        writeMemory(0,testinput.toCharArray(),1);
        mm.displayStatus();
        Output.write("koniec?");
        readMemory(50,2,1);
        readMemory(70,2,1);
        readMemory(90,2,1);
        readMemory(110,2,1);
        mm.displayStatus();*/

        /*Output.write("odczyt pamieci:"+String.valueOf(mm.readMemory(64,5,1)));
        mm.displayStatus();
        Output.write("odczyt pamieci:"+String.valueOf(mm.readMemory(80,5,1)));
        mm.displayStatus();*/
    }

public static void displayAddressSpace(int pid) {
    Proces pcb = Management.processLookup(pid);
    if(pcb == null){
        Output.write("Nie znaleziono szukanego procesu.");
        return;
    }
    int max = pcb.ptable.getHighestPage();
    for(int i =0;i<=max;i++){
        for(Frame temp : frameTable){
            if(temp==null){continue;}
            if(temp.processID == pid && temp.page==i){
                Output.write(temp.toString());
            }
        }
        for(SwapFileEntry tempsf: swapFile){
            if(tempsf==null){continue;}
            if(tempsf.processID == pid && tempsf.page == i) {
                Output.write(tempsf.toString());
            }
        }
    }
}

    public  static void readProgram(String programName, int processID) {
        char[] bufor = new char[pagesize];
        /* TODO function_returning_file_content(programFile)*/

        char[] file = FlorekFileSystem.F_Read(programName,-1,-1);
        if(file==null){
            Output.write("Proba wczytania nie powiodla sie");
        }


        long length = file.length;
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
    public static void readProgramtTest(int processID){
        String s = new String("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuooooooooooooooooooooooooooooooooooooooooooooooooooooooooooodddddddddddddddddddddddddddddddddddddddddddddd");
        char[] file = s.toCharArray();
        char[] bufor = new char[pagesize];
        //!
        Output.write("\"FILE\""+String.valueOf(file));
        Output.write("niezainicjalizowany bufor:"+String.valueOf(bufor));
        //!
        //char[] file = new char[10]; /* TODO function_returning_file_content(programFile)*/
        long length = file.length;
        //!
        Output.write("length:"+length+" pagesize:"+pagesize);
        //!
        double k = (double) length / (double) pagesize;
        Proces pcb = Management.processLookup(processID);
        try {
            for (int i = 0; i < k; i++) {
                // CharBuffer cb = CharBuffer.wrap(file);
                // cb.get(bufor, i * pagesize, pagesize);
                bufor = Arrays.copyOfRange(file,i*pagesize,i*pagesize+pagesize);
                //!
                Output.write("Próba!"+new SwapFileEntry(bufor,i,processID));
                Output.write("k: "+k+" i: "+i);
                Output.write(bufor.toString());
                //!
                swapFile.add(new SwapFileEntry(bufor, i, processID));
                pcb.ptable.map.put(i, new PageTableEntry(i, 0));
            }


        } catch (Exception e) {//TODO
        }
    }





    //TODO what if the page is in the swapFile
    public  static char[] readMemory(int virtualAddress, int size, int processID) { //rename to memoryRead ?
        //Proces pcb = Management.processLookup(processID);

        /*if(size>physicalMemory.length){
            String wynik = new String("Argument size większy od rozmiaru fizycznej pamięci ("+physicalMemory.length+")");
            return wynik.toCharArray() ;
        }*/

        int paddress = translateAddress(virtualAddress, processID);
        //!

        // Output.write("paddress: "+paddress);
        // Output.write("znak pod paddress:"+String.valueOf(physicalMemory[paddress]));
        // Output.write("strona "+paddress/pagesize);
        //!
        char[] output = new char[size];


        //int index = paddress & 0b00110000 * 16 + paddress & 0b00001111;

        int offset = virtualAddress % pagesize;

        if(offset+size>pagesize) {
            int leftToRead = size;

            String result = new String(readMemory(virtualAddress, pagesize - offset, processID)); //read till the end of current frame
            leftToRead = leftToRead - (pagesize - offset);
            //if(paddress+leftToRead>physicalMemory.length){
/*            if(translateAddress(virtualAddress+leftToRead-1,processID)>physicalMemory.length){
                String napis = new String("Przekroczono zakres tablicy");
                //!
               MemoryManagement.displayStatus();
               int VA = translateAddress(virtualAddress+leftToRead-1,processID);
               Output.write(VA+"VA <- PA:"+translateAddress(virtualAddress+leftToRead-1,processID)); 
                //!
                return napis.toCharArray();
            }*/
            int wholePages = leftToRead / pagesize;
            for (int i = 0; i < wholePages; i++) {
                result = result + new String(readMemory(virtualAddress + i*pagesize - offset, pagesize, processID)); //chyba i powinno zaczynać od 1
                leftToRead = leftToRead - pagesize;
            }
            if (leftToRead > 0){
                //result = result + new String(readMemory(paddress + size - leftToRead, leftToRead, processID));
                result = result + new String(readMemory(virtualAddress+size-leftToRead, leftToRead, processID));
            }
            //!
            //Output.write("wynik długiego odczytu (vaddress = "+virtualAddress+": "+result);
            //!
            output = result.toCharArray();

            return output;
        }

        output = Arrays.copyOfRange(physicalMemory,paddress,paddress+size);

        return output;


    }


    public static void writeMemory(int virtualAddress, char[] input, int processID) { //rename to memoryRead ?
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

        int offset = virtualAddress%pagesize;
        if(offset+input.length>pagesize){
            int leftToWrite = input.length;

            overwrite(input,0,physicalMemory,paddress, pagesize -offset);
            leftToWrite = leftToWrite - (pagesize - offset);

            int wholePages = leftToWrite/pagesize;

            for(int i = 0;i<wholePages;i++){
                //translateAddress(virtualAddress+input.length - leftToWrite,processID)
                paddress = translateAddress(virtualAddress+input.length - leftToWrite,processID);
                frameNumber = paddress/pagesize;
                frameTable[frameNumber].flags = (byte) (frameTable[frameNumber].flags | dirtyandused);
                overwrite(input,input.length - leftToWrite,physicalMemory,paddress,pagesize);
                leftToWrite = leftToWrite - pagesize;
            }
            if(leftToWrite>0){
                //overwrite(input,input.length - leftToWrite, physicalMemory, translateAddress(virtualAddress+input.length - leftToWrite,processID),leftToWrite);
                paddress = translateAddress(virtualAddress+input.length - leftToWrite,processID);
                frameNumber = paddress/pagesize;
                frameTable[frameNumber].flags = (byte) (frameTable[frameNumber].flags | dirtyandused);
                overwrite(input,input.length - leftToWrite, physicalMemory, paddress,leftToWrite);
            }


        }
        else{
            overwrite(input,0,physicalMemory,paddress,input.length);
        }

        //overwrite(input,0,physicalMemory,frameNumber*pagesize,input.length);






    }

    public static void releaseMemory(int processid) {
        Proces pcb = Management.processLookup(processid); //will be needed in an indexed swapfile
        Output.write("Zwalnianie pamieci");
        for (Frame temp : MemoryManagement.frameTable) {

            if(temp == null){continue;}

            //TODO release frames
            if (temp.processID == processid) {
                int frameToBeReleased = temp.number;

                frameTable[frameToBeReleased] = new Frame();
                boolean sanityCheck = freeFrames.add(temp.number);
                if (sanityCheck == false) {
                    try {
                        // throw new Exception("Zwalniana ramka znajdowała się na liscie wolnych ramek!");
                    } catch (Exception e) {
                        Output.write(e.toString());
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

    public static int findVictim(){ //TODO check if f.flags changes inside if statements
        byte dirtyUsed = (byte) (dirty|used);

        Output.write("Stan poczatkowy:");
        for(int i = 0;i<frameCount;i++){
            Output.write(frameTable[i].toString());
            if(MemoryManagement.clockHand == i){
                Output.write("Na powyzsza ramke wskazuje wskazowka zegara");
            }
        }
        Output.write("________________________________");

        while(true){
            Frame f = MemoryManagement.frameTable[MemoryManagement.clockHand%frameCount];

            Output.write(f.toString());

            if(f.flags==0 || f.flags == 4){
                Output.write("Ramka "+f.number+" zostanie wymieniona");
                return f.number;
            } else if ((f.flags & dirtyUsed) == 3) {
            	f.flags = (byte)(f.flags&5);

            }
            else if((f.flags & dirty) != 0 || (f.flags & used) != 0) {
                f.flags = (byte) (f.flags&clear);
            }
            MemoryManagement.clockHand++;
            MemoryManagement.clockHand=MemoryManagement.clockHand % 4;
            /*if((f.flags&swappable)==0){ //dirty == 0 & used == 0
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
            MemoryManagement.clockHand++;*/
        }
    }

    public static int getFreeFrame(){
        Iterator<Integer> it = freeFrames.iterator();
        if(it.hasNext()){
            Integer element = it.next();
            it.remove();
            return element;
        }
        else return -1;
    }


    public  static void displayStatus (){
        if(freeFrames.size()!=frameCount) {

            for(Frame temp : MemoryManagement.frameTable){

                if(temp == null)
                {
                    continue;
                }
                Output.write("=======");
                Output.write("Ramka numer " + temp.number + " (strona:" + temp.page + " proces:" + temp.processID);
                Output.write("Zawartosc:");
                // CharBuffer cb = CharBuffer.wrap(physicalMemory);
                char[] content = new char[pagesize];
                // cb.get(content, temp.number * pagesize, pagesize);
                content = Arrays.copyOfRange(physicalMemory,temp.number*pagesize,temp.number*pagesize+pagesize);
                Output.write(String.valueOf(content));
                Output.write("=======");
            }
        }
        else{
            Output.write("Wszystkie ramki sa wolne");

        }

        for(SwapFileEntry temp : swapFile){
            Output.write("fragment pliku wymiany:"+temp.processID+" "+temp.page+" "+String.valueOf(temp.data));
        }
        //
       // Proces pcb = Management.processLookup(1);
        //Output.write(pcb.ptable.toString());

    }

    public static void overwrite(char[] source, char[] dst, int dstoffset, int length){

        for(int i = 0;i<length;i++){
            dst[dstoffset+i]=source[i];
        }

    }

    public static void overwrite(char[] source,int srcoffset, char[] dst, int dstoffset, int length) {
        for (int i = 0; i < length; i++) { // for (int i = 0; i < length-srcoffset; i++)
            dst[dstoffset + i] = source[i + srcoffset];
        }
    }

    public static boolean inMemory(int virtualaddress, int procesID){
        int pagenumber = virtualaddress/pagesize;
        if(freeFrames.size()==frameCount){
            translateAddress(virtualaddress,procesID);
            return false;
        }
        for(Frame temp : frameTable){
            if(temp == null){continue;}
            if(temp.page == pagenumber && temp.processID == procesID){
                return true;
            }
        }
        translateAddress(virtualaddress,procesID);
        return false;
    }




public static int newAddress (int vaddress,int pid) {

    Proces pcb = Management.processLookup(pid);
    int paddress;
    int pagenumber = vaddress / pagesize;
    int offset = vaddress % pagesize;

    int temp = pagenumber;

        if (pcb.ptable.map.get(pagenumber) == null) {
            Output.write("Dodano nowy wpis do tabeli stronic.");
            pcb.ptable.map.put(pagenumber, new PageTableEntry(temp, 0));
            swapFile.add(new SwapFileEntry(pagenumber, pid));
            //jesli wolna to laduj
            boolean loaded = false;
            loaded = loadToFree(vaddress,pid);
            if(loaded==true){
                pcb.ptable.map.get(pagenumber).memoryOrSwapFile = 1;
                return pcb.ptable.map.get(pagenumber).pageSizeUnits*pagesize+offset;
            }
            else{
                paddress = translateAddress(vaddress,pid);
                return paddress;
            }
        }
    System.out.println("Tlumaczenie adresu nie udalo sie (newAddress)"+vaddress+pid);
        return -1;
    }

        public static int addressInPhysicalMemory(int vaddress, int pid){
        Proces pcb = Management.processLookup(pid);

        int pagenumber = vaddress/pagesize;
        int offset  = vaddress%pagesize;

                if(pcb.ptable.map.get(pagenumber).memoryOrSwapFile==1){
                    Output.write("Szukane dane znajdują się w pamięci");
                    int paddress = pcb.ptable.map.get(pagenumber).pageSizeUnits*pagesize+offset;
                    return paddress;
                }
            else return -1;
        }

        public static boolean loadToFree(int vaddress, int pid){

            int pagenumber = vaddress/pagesize;
            int offset  = vaddress%pagesize;

            int temp = pagenumber;

            Proces pcb = Management.processLookup(pid);

            if(freeFrames.size()!=0){
                int theFreeFrame = getFreeFrame();
                Output.write("Konieczne jest wczytanie stronicy z dysku do wolnej ramki ("+theFreeFrame+")");

                Frame fFrame = new Frame(theFreeFrame,getSFE(vaddress,pid));

                frameTable[theFreeFrame] = fFrame;
                pcb.ptable.map.put(pagenumber, new PageTableEntry(fFrame.number,1));

                return  true;
            }
            return  false;
        }


    public static SwapFileEntry getSFE(int vaddress, int pid){ //moze zwracac null

        int pagenumber = vaddress/pagesize;


        Iterator<SwapFileEntry> it = swapFile.iterator();

        while(it.hasNext()){
            SwapFileEntry sf = it.next();
            if(sf == null){continue;}
            if(sf.page == pagenumber && sf.processID == pid){
                it.remove();
                return sf;
            }
        }
        System.out.println("zwrócony został null (getSFE)"+vaddress+" "+pid);
        return null;
    }


    public static int pageFault(int vaddress, int pid){
        int pagenumber = vaddress/pagesize;
        int offset  = vaddress%pagesize;

        int temp = pagenumber;

        Proces pcb = Management.processLookup(pid);
        Output.write("Konieczne jest znalezienie ramki ofiary.");
        int victimnumber = findVictim();
        Frame victimFrame = frameTable[victimnumber];

        Proces victimpcb = Management.processLookup(victimFrame.processID);
        if(victimpcb == null){
            swapFile.add(new SwapFileEntry(victimFrame));
            temp = victimFrame.page;

            Frame newFrame = new Frame(victimnumber,getSFE(vaddress,pid));
            frameTable[victimnumber]=newFrame;
            Proces newpcb =Management.processLookup(newFrame.processID); //newpcb == pcb?
            int newtemp = newFrame.page;
            newpcb.ptable.map.put(newFrame.page,new PageTableEntry(newFrame.number,1));
            return newFrame.number*pagesize+offset;

        }

        swapFile.add(new SwapFileEntry(victimFrame));
        temp = victimFrame.page;

        victimpcb.ptable.map.put(victimFrame.page,new PageTableEntry(temp,0));

        Frame newFrame = new Frame(victimnumber,getSFE(vaddress,pid));
        frameTable[victimnumber]=newFrame;
        Proces newpcb =Management.processLookup(newFrame.processID); //newpcb == pcb?
        int newtemp = newFrame.page;
        newpcb.ptable.map.put(newFrame.page,new PageTableEntry(newFrame.number,1));
        return newFrame.number*pagesize+offset;

    }

    public static int translateAddress(int vaddress, int pid) {
        int pagenumber = vaddress/pagesize;
        int offset  = vaddress%pagesize;
        int paddress;
        int temp = pagenumber;

        Proces pcb = Management.processLookup(pid);

        if(pcb.ptable.map.get(pagenumber) == null){
            paddress =  newAddress(vaddress, pid);
           return paddress;
        }

        if(pcb.ptable.map.get(pagenumber).memoryOrSwapFile==1)
        {
            paddress =pcb.ptable.map.get(pagenumber).pageSizeUnits*pagesize+offset;
            return paddress;
        }
        else{ //PAGEFAULT
            boolean loaded=false;
            if(freeFrames.size()!=0) {
                loaded = loadToFree(vaddress, pid);
            }
            if(loaded == true){
                paddress =pcb.ptable.map.get(pagenumber).pageSizeUnits*pagesize+offset;
                return paddress;
                //return translateAddress(vaddress,pid);
            }
            else{

                paddress = pageFault(vaddress,pid);
                return paddress;

            }

        }

    }


}
