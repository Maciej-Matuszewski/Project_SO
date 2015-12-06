/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                  Last update: 28.11.2015 10:37                 */
/******************************************************************/
package florekfilesystem;
import java.util.Scanner;
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
    int D_GetFreeBlock() {
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
            System.out.print("\t\t" + this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_Name + "\t\t" +
                               + this.D_iNode[hlp].F_Size + "B" + "\t\t" + this.D_iNode[this.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.get(i).F_iNode_Id + "\t");
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
            char[] hlp_Content = hlp_File.F_Read(this);
            System.out.println(hlp_Content);
        }
        else {
            System.out.println("Plik o podanej ścieżce nie istnieje!");
        }
    }
    /**************************************************************/
    int[] M_GetFileBlock(String F_Name) {
        int j = 0;
        File hlp_File = this.D_FindFile(F_Name);
        if(hlp_File != null) {
            iNode hlp_iNode = this.D_iNode[hlp_File.F_iNode_Id];
            for(int i = 0; i < hlp_iNode.DirBlock.length; i++) {
                if(hlp_iNode.DirBlock[i] != null) {
                    hlp_iNode.FileBlockIds[j] = hlp_iNode.DirBlock[i].B_Id;
                    j++;
                }
                else {
                    hlp_iNode.FileBlockIds[j] = -1;
                }
            }
            for(int i = 0; i < hlp_iNode.InDirBlock[0].length; i++) {
                if(hlp_iNode.InDirBlock[0][i] != null) {
                    hlp_iNode.FileBlockIds[j] = hlp_iNode.InDirBlock[0][i].B_Id;
                    j++;
                }
                else {
                    hlp_iNode.FileBlockIds[j] = -1;
                }
            }
            return hlp_iNode.FileBlockIds;
        }
        return null;
    }
    /**************************************************************/
    void D_Info() {
        System.out.println("Nazwa dysku: \t" + this.D_Name);
        System.out.println("System plików: \t" + this.D_NameFileSystem);
        System.out.println("Wielkość bloku: " + this.D_BlockSize);
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
    void Disk_Command(String Command) {
        Scanner YesNo = new Scanner(System.in);
        String Decision;
        String Com[] = null;
            Com = Command.split(" ");
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(Com[0].equals("crt")) {
                File hlp_File;
                if(Com.length > 1) {
                    hlp_File = this.D_FindFile(Com[1]);  
                    if(hlp_File != null) {
                        System.out.println("Plik o podanej nazwie istnieje, czy chcesz go nadpisać(stracisz wszystkie informacje w nim zawarte)? T/N");
                        Decision = YesNo.nextLine();
                        if(Decision.equals("T")) {
                            hlp_File.F_Delete(this);
                            hlp_File = new File(Com[1], this, 'F', "");
                            if(Com.length > 2) {
                                for(int i = 2; i < Com.length; i++) {
                                    hlp_File.F_Write(this, Com[i]);
                                }
                            }
                            System.out.println("Plik został pomyślnie utworzony!");
                        }
                        else {
                            System.out.println("Plik nie został utworzony!");
                        }
                    }
                    else {
                        hlp_File = new File(Com[1], this, 'F', "");
                        if(Com.length > 2) {
                            for(int i = 2; i < Com.length; i++) {
                                hlp_File.F_Write(this, Com[i]);
                            }
                        }
                        hlp_File = this.D_FindFile(Com[1]);
                        if(hlp_File != null)
                            System.out.println("Plik został pomyślnie utworzony!");
                    }
                }
            }    
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("sba")) {
                this.D_ShowBlockBytes();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("ls")) {
                this.D_ShowCatalog();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("sbc")) {
                if(Com.length > 1) {
                    int BlockNumber = Integer.parseInt(Com[1]);
                    if((BlockNumber > this.D_BlockValue - 1) || (BlockNumber < 0)) {
                        System.out.println("Blok o podanym indeksie nie istnieje!");
                    }
                    else if(BlockNumber == 0) {
                        System.out.println("Blok zarezerwowany dla katalogu głównego, nie można wyświetlić zawartości!");
                    }
                    else if(BlockNumber > 0 && BlockNumber < this.D_BlockValue) {
                        this.D_Block[BlockNumber].B_ShowBlockBytes();
                    }   
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("siv")) {
                this.D_ShowiNodesBitVector();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("sbv")) {
                this.D_ShowBlocksBitVector();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("opn")) {
                if(Com.length > 1) {
                    char hlp_char = Com[1].charAt(0);
                    if(hlp_char == '/') {
                        this.D_OpenFile(Com[1].substring(1));
                    }
                    else {
                        System.out.println("Nie rozpoznano ścieżki! Pamiętaj o '/'!");
                    }
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("vi")) {
                if(Com.length > 2) {
                    File hlp_File = this.D_FindFile(Com[1]);
                    if(hlp_File != null) {
                        for(int i = 2; i < Com.length; i++) {
                            if(this.D_iNode[hlp_File.F_iNode_Id].F_Size  < this.D_MaxFileSize) {
                                hlp_File.F_Write(this, Com[i]);
                                hlp_File.F_Write(this, " ");
                            }
                            else {
                                System.out.println("Nie udało się zapisać zawartości, brak wolnych bloków lub plik osiągnął maksymalny rozmiar!");
                                break;
                            }        
                        }   
                    }
                    else {
                        System.out.println("Nie można edytować pliku! Plik nie istnieje!");
                    }    
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("scb")) {
                if(Com.length > 1) {
                    int BlockNumber = Integer.parseInt(Com[1]);
                    if((BlockNumber > this.D_BlockValue - 1) || (BlockNumber < 0)) {
                        System.out.println("Blok o podanym indeksie nie istnieje!");
                    }
                    else if(BlockNumber == 0) {
                        System.out.println("Blok zarezerwowany dla katalogu głównego, nie można wyświetlić zawartości!");
                    }
                    else if(BlockNumber > 0 && BlockNumber < this.D_BlockValue) {
                        if(Com.length == 3) {
                            this.D_Block[BlockNumber].B_ShowBlockContent(Integer.parseInt(Com[2]), -1);
                        }
                        else if(Com.length == 4) {
                            this.D_Block[BlockNumber].B_ShowBlockContent(Integer.parseInt(Com[2]), Integer.parseInt(Com[3]));
                        }
                        else {
                            this.D_Block[BlockNumber].B_ShowBlockContent(-1, -1);
                        }
                    }   
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("inf")) {
                this.D_Info();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("rm")) {
                if(Com.length > 1) {
                    File hlp_File = this.D_FindFile(Com[1]);
                    if(hlp_File != null) {
                        System.out.println("Czy na pewno chcesz usunąć ten plik? T/N");
                        Decision = YesNo.nextLine();
                        if(Decision.equals("T")) {
                            hlp_File.F_Delete(this);
                            System.out.println("Plik został usunięty!");
                        }
                        else {
                            System.out.println("Anulowano usunięcie pliku!");
                        }
                    } 
                    else {
                        System.out.println("Plik o podanej nazwie nie istnieje!");
                    }
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("mv")) {
                if(Com.length > 2) {
                    this.D_ChangeNameFile(Com[1], Com[2]);
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else {
                System.out.println("Nie rozpoznano komendy!");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    /**************************************************************/
}
/******************************************************************/
