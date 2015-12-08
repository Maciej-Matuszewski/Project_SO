/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;
import java.util.Scanner;
import obsluga_dysku.FlorekFileSystem;
/******************************************************************/
public class Disk {
    String D_Name;              // nazwa dysku
    String D_NameFileSystem;    // nazwa zamontowanego systemu plików
    int D_Space;                // rozmiar dysku
    int D_BusySpace;            // zajęte miejsce na dysku
    int D_BlockSize;            // rozmiar bloku
    int D_BlockValue;           // liczba bloków
    Block[] D_Block;            // tablica z blokami danych
    int[] D_BitVector_Block;    // wektor bitowy bloków
    iNode[] D_iNode;            // tablica z i-węzłami
    int[] D_BitVector_iNode;    // wektor bitowy i-węzłów
    int D_MaxDirectBlock;       // liczba bezpośrednich bloków
    int D_MaxInDirectBlock;     // liczba bloków w bloku pośrednm
    int D_MaxFileSize;          // maksymalny rozmiar pliku
    File D_Catalog;             // katalog główny systemu
    /**************************************************************/
    Disk(String D_Name, String D_NameFileSystem, int D_Space, int D_BlockSize) {
        this.D_Name                 =   D_Name;
        this.D_NameFileSystem       =   D_NameFileSystem;
        this.D_Space                =   D_Space;
        this.D_BusySpace            =   0;
        this.D_BlockSize            =   D_BlockSize;
        this.D_BlockValue           =   (this.D_Space / this.D_BlockSize);
        this.D_Block                =   new Block[this.D_BlockValue];
        this.D_BitVector_Block      =   new int[this.D_BlockValue];
        this.D_iNode                =   new iNode[this.D_BlockValue];
        this.D_BitVector_iNode      =   new int[this.D_BlockValue];    
        this.D_MaxDirectBlock       =   2;
        this.D_MaxInDirectBlock     =   4;
        this.D_MaxFileSize          =   ((this.D_MaxDirectBlock + this.D_MaxInDirectBlock) * this.D_BlockSize); 
        /**********************************************************/
        for(int i = 0; i < this.D_BlockValue; i++) {
            this.D_Block[i] = new Block(this.D_BlockSize, i);
            this.D_iNode[i] = new iNode(this.D_MaxDirectBlock, this.D_MaxInDirectBlock);
            this.D_iNode[i].iNode_Clean();
            this.D_BitVector_Block[i] = 0; // ustawienie bloków na wolne
            this.D_BitVector_iNode[i] = 0; // ustawienie i-węzłów na wolne
        }
        this.D_Catalog              =   new File("/", this, 'C', "");
        /**********************************************************/
    }
    /**************************************************************/
    void D_CleanCatalogEntry(String F_Name) {
        for(int i = 0; i < this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.size(); i++) {
            File hlp_File = this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i);
            if(F_Name == hlp_File.F_Name) {
                this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.remove(i);
            }
        }
    }
    /**************************************************************/
    public int D_GetFreeBlock() {
        for(int i = 0; i < this.D_BlockValue; i++) {
            if(this.D_BitVector_Block[i] == 0) {
                return i;
            }
        }
        return -1;
    }
    /**************************************************************/
    int D_GetFreeiNode() {
        for(int i = 0; i < this.D_BlockValue; i++) {
            if(this.D_BitVector_iNode[i] == 0) {
                return i;
            }
        }
        return -1;
    }
    /**************************************************************/
    void D_ShowBlockBytes() {
        for(int i = 1; i < this.D_BlockValue; i++) {
            System.out.print("Blok nr " + i + ": \t");
            this.D_Block[i].B_ShowBlockBytes();
        }
    }
    /**************************************************************/
    void D_ShowBlocksBitVector() {
        System.out.println("Wektor bitowy bloków:");
        for(int i = 0; i < this.D_BlockValue; i++) {
            System.out.print(this.D_BitVector_Block[i]);
        }
        System.out.println();
    }
    /**************************************************************/
    void D_ShowiNodesBitVector() {
        System.out.println("Wektor bitowy i-węzłów:");
        for(int i = 0; i < this.D_BlockValue; i++) {
            System.out.print(this.D_BitVector_iNode[i]);
        }
        System.out.println();
    }/**************************************************************/
    void D_ShowBlockContent() {
        for(int i = 1; i < this.D_BlockSize; i++) {
            System.out.print("Blok nr " + i + ": \t");
            this.D_Block[i].B_ShowBlockContent(-1, -1);
        }
    }
    /**************************************************************/
    void D_ShowCatalog() {
        System.out.println("Katalog\t\tPlik\t\tRozmiar\t\ti-Węzeł\tBloki");
        System.out.println("ROOT" + "\t\t\t\t\t\t" + this.D_Catalog.F_iNode_Id + "\t" + "0");
        for(int i = 0; i < this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.size(); i++) {
            int hlp = this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_iNode_Id;
            System.out.print("\t\t" + this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name + "\t");
            if(this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name.length() < 8) {
                System.out.print("\t");
            }
                               System.out.print(this.D_iNode[hlp].F_Size + "B" + "\t\t" + this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_iNode_Id + "\t");
            for(int j = 0; j < this.D_iNode[hlp].FileBlockIds.length; j++) {
                if(this.D_iNode[hlp].FileBlockIds[j] != -1) {
                    System.out.print(this.D_iNode[hlp].FileBlockIds[j]);
                    System.out.print(" ");
                }
            }
            System.out.println();
        }        
    }
    /**************************************************************/
    int D_GetValueOfFreeBlocks() {
        int hlp_FreeBlocks = 0;
        for(int i = 0; i < this.D_BlockValue; i++) {
            if(this.D_BitVector_Block[i] == 0) {
                hlp_FreeBlocks++;
            }
        }
        return hlp_FreeBlocks;
    }
    /**************************************************************/
    int D_GetValueOfFreeiNodes() {
        int hlp_FreeiNodes = 0;
        for(int i = 0; i < this.D_BlockValue; i++) {
            if(this.D_BitVector_iNode[i] == 0) {
                hlp_FreeiNodes++;
            }
        }
        return hlp_FreeiNodes;
    }
    /**************************************************************/
    File D_FindFile(String F_Name) {
        for(int i = 0; i < this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.size(); i++) {
            if(F_Name.equals(this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name)) {
                return this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i);
            }
        }
        return null;
    }
    /**************************************************************/
    void D_ChangeNameFile(String From, String To) {
        File hlp_File = this.D_FindFile(From);
        if(hlp_File != null) {
            hlp_File.F_Name = To;
            System.out.println("Nazwa pliku została pomyślnie zmieniona!");
        }
        else {
            System.out.println("Nie udało się zmienić nazwy pliku, plik nie istnieje!");
        }
    }
    /**************************************************************/
    void D_OpenFile(String F_Name) {
        File hlp_File = this.D_FindFile(F_Name);
        if(hlp_File != null) {
            char[] hlp_Content = FlorekFileSystem.F_Read(F_Name);
            System.out.println(hlp_Content);
        }
        else {
            System.out.println("Plik o podanej ścieżce nie istnieje!");
        }
    }
    /**************************************************************/
    void D_Info() {
        System.out.println("Nazwa dysku: \t" + this.D_Name);
        System.out.println("System plików: \t" + this.D_NameFileSystem);
        System.out.println("Rozmiar dysku: \t" + this.D_Space + "B");
        System.out.println("Rozmiar bloku: \t" + this.D_BlockSize + "B");
        System.out.println("Wolne bloki: \t" + this.D_GetValueOfFreeBlocks());
        System.out.println("Zajęte bloki: \t" + (this.D_BlockValue - this.D_GetValueOfFreeBlocks()));
        System.out.println("Wolne i-węzły:  " + this.D_GetValueOfFreeiNodes());
        System.out.println("Zajęte i-węzły: " + (this.D_BlockValue - this.D_GetValueOfFreeiNodes()));
        System.out.println("Wolne miejsce: \t" + (this.D_Space - this.D_BusySpace) + "B");
        System.out.println("Zajęte miejsce: " + this.D_BusySpace + "B");
    }
    /**************************************************************/
    boolean F_CheckFileNames(String F_Name) {
        for(int i = 0; i < this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.size(); i++) {
            if(this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name.equals(F_Name)) {
                return false;
            }
        }
        return true;
    }
    /**************************************************************/
  public  void BackupProgramFiles() {
        FlorekFileSystem.Create_File("Program1", "mv RA,01\nmv RB,05\nad RA,RB\nj1 00");
        FlorekFileSystem.Create_File("Program2", "mv RA,05\nmi 50,RA\nmv BR,RA\nml BR,BA \nsb b,01\nj1 25\net");
    }
    /**************************************************************/
}
/******************************************************************/
