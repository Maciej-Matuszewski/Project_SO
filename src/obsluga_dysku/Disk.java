/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;

import Interpreter.Output;
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
            Output.writeInLine("Blok nr " + i + ": \t");
            this.D_Block[i].B_ShowBlockBytes();
        }
    }
    /**************************************************************/
    void D_ShowBlocksBitVector() {
        Output.write("Wektor bitowy blokow:");
        for(int i = 0; i < this.D_BlockValue; i++) {
            Output.writeInLine(this.D_BitVector_Block[i]+"");
        }
        Output.write("");
    }
    /**************************************************************/
    void D_ShowiNodesBitVector() {
        Output.write("Wektor bitowy i-wezlow:");
        for(int i = 0; i < this.D_BlockValue; i++) {
            Output.writeInLine(this.D_BitVector_iNode[i]+"");
        }
        Output.write("");
    }/**************************************************************/
    void D_ShowBlockContent() {
        for(int i = 1; i < this.D_BlockSize; i++) {
            Output.writeInLine("Blok nr " + i + ": \t");
            this.D_Block[i].B_ShowBlockContent();
        }
    }
    /**************************************************************/
    void D_ShowCatalog() {
        Output.write("Katalog\t\tPlik\tRozmiar\ti-Wezel\tBloki");
        Output.write("ROOT" + "\t\t\t\t" + this.D_Catalog.F_iNode_Id + "\t" + "0");
        for(int i = 0; i < this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.size(); i++) {
            int hlp = this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_iNode_Id;
            Output.writeInLine("\t\t" + this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name + "\t");
           /* if(this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name.length() < 8) {
                Output.writeInLine("\t");
            }*/
                               Output.writeInLine(this.D_iNode[hlp].F_Size + "B" + "\t" + this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_iNode_Id + "\t");
            for(int j = 0; j < this.D_iNode[hlp].FileBlockIds.length; j++) {
                if(this.D_iNode[hlp].FileBlockIds[j] != -1) {
                    Output.writeInLine(this.D_iNode[hlp].FileBlockIds[j]+"");
                    Output.writeInLine(" ");
                }
            }
            Output.write("");
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
        File hlp_File2 = this.D_FindFile(To);
        String Decision;
        if(hlp_File != null) {
        	if(hlp_File2 == null) {
        		hlp_File.F_Name = To;	
        	}
        	else {
                Decision = Decision = Output.loadCMD("Plik o podanej nazwie istnieje, czy chcesz go nadpisac(stracisz wszystkie informacje w nim zawarte)? T/N");
                if(Decision.equals("T")) {
                    FlorekFileSystem.F_Delete(To);
                    hlp_File.F_Name = To;   
                }  
            Output.write("Nazwa pliku zostala pomyslnie zmieniona!");
        	}
        }
        else {
            Output.write("Nie udalo sie zmienic nazwy pliku, plik nie istnieje!");
        }
    }
    /**************************************************************/
    void D_OpenFile(String F_Name, int From, int To) {
        File hlp_File = this.D_FindFile(F_Name);
        String hlp = "";
        if(hlp_File != null) {
            char[] hlp_Content = FlorekFileSystem.F_Read(F_Name, From, To);
            for(int i = 0; i < hlp_Content.length; i++) {
                hlp += hlp_Content[i]; 
            } 
            Output.write(hlp);
        }
        else {
        	Output.write("Plik o podanej scieżce nie istnieje!");
        }
    }
    /**************************************************************/
    void D_Info() {
    	Output.write("");
        Output.write("Nazwa dysku:\t\t" + this.D_Name);
        Output.write("System plikow:\t" + this.D_NameFileSystem);
        Output.write("Rozmiar dysku:\t" + this.D_Space + "B");
        Output.write("Rozmiar bloku:\t" + this.D_BlockSize + "B");
        Output.write("Wolne bloki:\t\t" + this.D_GetValueOfFreeBlocks());
        Output.write("Zajete bloki:\t\t" + (this.D_BlockValue - this.D_GetValueOfFreeBlocks()));
        Output.write("Wolne i-wezly:\t" + this.D_GetValueOfFreeiNodes());
        Output.write("Zajete i-wezly:\t" + (this.D_BlockValue - this.D_GetValueOfFreeiNodes()));
        Output.write("Wolne miejsce:\t" + (this.D_Space - this.D_BusySpace) + "B");
        Output.write("Zajete miejsce:\t" + this.D_BusySpace + "B");
        Output.write("");
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
    public void BackupProgramFiles() {
        FlorekFileSystem.Create_File("Program1", "mv RA,01" 
                        + "mv RB,05" 
                        + "ad RA,RB" 
                        + "j0 00");
        FlorekFileSystem.Create_File("Program2", "mv RA,05"
        		+ "mv RB,03"
        		+ "mi A0,RA"
        		+ "mi AA,RB"
        		+ "mi RB,A0"
        		+ "ml RA,RB"
        		+ "mi RB,AA"
        		+ "sb RB,01"
        		+ "j0 18"
        		+ "et");
        FlorekFileSystem.Create_File("Program3", "mv RA,05"
        		+ "fk"
        		+ "ex"
        		+ "pw 00,RA"
        		+ "wt"
        		+ "pr RB,00"
        		+ "fm"
        		+ "fw RB"
        		+ "et");
        FlorekFileSystem.Create_File("Program4", "pr RA,PA"
        		+ "ad RA,A0"
        		+ "pw PA,RA"
        		+ "et");
    }
    /**************************************************************/
}
/******************************************************************/
