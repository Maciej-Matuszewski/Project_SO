package pamiec_wirtualna;


public class PageTableEntry {
    Integer pageSizeUnits; //equivalent to frame.pagenumber!
    int memoryOrSwapFile = 0; //1 - in memory, 0 - in the swap file

    public PageTableEntry (Integer framenumber, int memorswap){
        pageSizeUnits= framenumber;
        memoryOrSwapFile = memorswap;
    }

    @Override
    public String toString() {
        String s = this.pageSizeUnits+" : "+memoryOrSwapFile;
        return s;
    }
}

