/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                    Created: 02.11.2015 09:54                   */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;
import java.util.ArrayList;
import java.util.List;
import obsluga_dysku.FlorekFileSystem;
/******************************************************************/
public class File {
    String F_Name;      // nazwa pliku
    int F_iNode_Id;     // identyfikator i-węzła
    /**************************************************************/
    File(String F_Name, Disk SysDisk, char F_Type, String Content) {
        int hlp_Block = SysDisk.D_GetFreeBlock();
        int hlp_iNode = SysDisk.D_GetFreeiNode();
         
        if(F_Type != 'C') {
            File hlp_File = SysDisk.D_FindFile(F_Name); //obsługa nadpisania pliku
            if(hlp_File != null) {
                FlorekFileSystem.F_Delete(F_Name);
            }
        }
            if(hlp_Block != -1 && hlp_iNode != -1) {
                if(F_Name.length() > 8) {
                    this.F_Name = F_Name.substring(0, 7);
                }
                else {
                    this.F_Name = F_Name;
                }
                this.F_iNode_Id = hlp_iNode;
                SysDisk.D_iNode[hlp_iNode].F_Type[0] = F_Type;
                SysDisk.D_iNode[hlp_iNode].DirBlock[0] = SysDisk.D_Block[hlp_Block];
                SysDisk.D_iNode[hlp_iNode].iNode_AddBlockId(hlp_Block);
                SysDisk.D_BitVector_Block[hlp_Block] = 1;
                SysDisk.D_BitVector_iNode[hlp_iNode] = 1;
                /**********************************************************/
                if(SysDisk.D_iNode[hlp_iNode].F_Type[0] == 'C') {
                    SysDisk.D_Block[hlp_Block].CatalogEntry = new ArrayList<File>();
                    /**********************************************************/
                    for(int i = 0; i < SysDisk.D_BlockSize; i++) {
                        SysDisk.D_iNode[hlp_iNode].DirBlock[0].B_Bytes[i] = 1; // blokuje cały blok
                        SysDisk.D_BusySpace++;
                    }
                    /******************************************************/
                }
                else {
                    SysDisk.D_iNode[SysDisk.D_Catalog.F_iNode_Id].DirBlock[0].CatalogEntry.add(this); // wpis katalogowy
                }
                /**********************************************************/
                 if(Content.length() > 0) {
                    FlorekFileSystem.F_Write(this.F_Name, Content);
                }
                /**********************************************************/
            }
            else {
                System.out.println("Nie udało się stworzyć plików, brak wolnego miejsca na dysku!");
            }        
    }
    /**************************************************************/
    Block F_CheckBlock(Disk SysDisk) {
        Block hlp_Block;
        for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
            if(SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i] == null) {
                if(SysDisk.D_GetFreeBlock() != -1) {
                    SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i] = SysDisk.D_Block[SysDisk.D_GetFreeBlock()]; // wpis nowego bloku do i-węzła
                    SysDisk.D_iNode[this.F_iNode_Id].iNode_AddBlockId(SysDisk.D_Block[SysDisk.D_GetFreeBlock()].B_Id);
                    hlp_Block = SysDisk.D_Block[SysDisk.D_GetFreeBlock()];
                    SysDisk.D_BitVector_Block[SysDisk.D_GetFreeBlock()] = 1;
                    return hlp_Block;
                }
                else {
                    return null;
                }
            }
            else if(!SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i].B_BlockIsFull()) /* jeżeli nie w pełni zapełniony*/ {
                return SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i];
            }
        }
        for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
            if(SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i] == null) {
                if(SysDisk.D_GetFreeBlock() != -1) {
                    SysDisk.D_iNode[this.F_iNode_Id].iNode_AddBlockId(SysDisk.D_Block[SysDisk.D_GetFreeBlock()].B_Id);
                    SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i] = SysDisk.D_Block[SysDisk.D_GetFreeBlock()]; // wpis nowego bloku do i-węzła
                    hlp_Block = SysDisk.D_Block[SysDisk.D_GetFreeBlock()];
                    SysDisk.D_BitVector_Block[SysDisk.D_GetFreeBlock()] = 1;
                    return hlp_Block;
                }
                else {
                    return null;
                }
            }
            else if(!SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i].B_BlockIsFull()) /* jeżeli nie w pełni zapełniony*/ {
                return SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i];
            }
        }
        return null; // w przypadku braku wolnych bloków
    }
    /**************************************************************/
   /* void F_Write(String Content) {
        Block hlp_Block = this.F_CheckBlock(SysDisk);
        if(SysDisk.D_iNode[this.F_iNode_Id].F_Size < SysDisk.D_MaxFileSize && hlp_Block != null) {
            for(int i = 0; i < Content.length(); i++) {
               if(hlp_Block.B_BlockIsFull()) {
                   hlp_Block = this.F_CheckBlock(SysDisk);
               }
               if(hlp_Block != null && SysDisk.D_iNode[this.F_iNode_Id].F_Size < SysDisk.D_MaxFileSize) {
                   hlp_Block.B_Bytes[hlp_Block.B_PointerToFreeByte] = 1;
                   hlp_Block.B_Content[hlp_Block.B_PointerToFreeByte] = Content.charAt(i);
                   SysDisk.D_iNode[this.F_iNode_Id].F_Size++;
                   SysDisk.D_BusySpace++;
                   hlp_Block.B_PointerToFreeByte++;
               }
               else {
                   System.out.println("Nie udało się zapisać zawartości, brak wolnych bloków lub plik osiągnął maksymalny rozmiar!");
                   break;
               }
           }
        }         
    }*/
    /**************************************************************/
    /*char[] F_Read(Disk SysDisk) {
        char[] Content = new char[SysDisk.D_iNode[this.F_iNode_Id].F_Size];
        int k = 0;
        if(SysDisk.D_BitVector_iNode[this.F_iNode_Id] == 1) {
            for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
                if(SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i] != null) {
                    for(int j = 0; j < SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i].B_PointerToFreeByte; j++) {
                        Content[k] = SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i].B_Content[j];
                        k++;
                    }
                }
            }
            for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
                if(SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i] != null) {
                    for(int j = 0; j < SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i].B_PointerToFreeByte; j++) {
                        Content[k] = SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i].B_Content[j];
                        k++;
                    }
                }
            }
        }
        else {
            System.out.println("Plik nie może zostać odczytany, gdyż nie istnieje!");
            return null;
        }
        return Content;
    }*/
    /**************************************************************/
    /*void F_Delete(Disk SysDisk) {
        if(SysDisk.D_iNode[this.F_iNode_Id].F_isOpen == false) {
            for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
                Block hlp_Block = SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i];
                if(hlp_Block != null) {
                    hlp_Block.B_PointerToFreeByte = 0;
                    hlp_Block.B_CleanBlock();
                    SysDisk.D_CleanCatalogEntry(this.F_Name);
                    SysDisk.D_BitVector_Block[SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i].B_Id] = 0; // zwalnianie bloku
                    SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i] = null;
                    SysDisk.D_BitVector_iNode[this.F_iNode_Id] = 0; // zwalnianie i-węzła        
                }
            }
            for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
                Block hlp_Block = SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i];
                if(hlp_Block != null) {
                    hlp_Block.B_PointerToFreeByte = 0;
                    hlp_Block.B_CleanBlock();
                    SysDisk.D_CleanCatalogEntry(this.F_Name);
                    SysDisk.D_BitVector_Block[SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i].B_Id] = 0; // zwalnianie bloku
                    SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i] = null;
                    SysDisk.D_BitVector_iNode[this.F_iNode_Id] = 0; // zwalnianie i-węzła   
                }
            }
            SysDisk.D_BusySpace -= SysDisk.D_iNode[this.F_iNode_Id].F_Size;
            SysDisk.D_iNode[this.F_iNode_Id].F_Size = 0;
            SysDisk.D_iNode[this.F_iNode_Id].iNode_Clean();
        }
        else {
            System.out.println("Pliku nie można usunąć, gdyż jest on używany przez inny proces!");
        }
    }*/
    /**************************************************************/
}
/******************************************************************/
