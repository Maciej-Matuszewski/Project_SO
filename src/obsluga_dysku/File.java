/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Ĺ�ukasz Florczak                    */
/*                    Created: 02.11.2015 09:54                   */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;
import java.util.ArrayList;
import java.util.List;

import Interpreter.Output;
import obsluga_dysku.FlorekFileSystem;
/******************************************************************/
public class File {
    String F_Name;      // nazwa pliku
    int F_iNode_Id;     // identyfikator i-wÄ™zĹ‚a
    /**************************************************************/
        File(String F_Name, Disk SysDisk, char F_Type, String Content) {
        int hlp_Block = SysDisk.D_GetFreeBlock();
        int hlp_iNode = SysDisk.D_GetFreeiNode();
         
        if(F_Name.length() > 8) {
        	F_Name = F_Name.substring(0, 7);
        }
        if(F_Type != 'C') {
            File hlp_File = SysDisk.D_FindFile(F_Name); //obsĹ‚uga nadpisania pliku
            if(hlp_File != null) {
                FlorekFileSystem.F_Delete(F_Name);
            }
        }
            if(hlp_Block != -1 && hlp_iNode != -1) {   
                this.F_Name = F_Name;
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
                        SysDisk.D_iNode[hlp_iNode].DirBlock[0].B_Bytes[i] = 1; // blokuje caĹ‚y blok
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
                Output.write("Nie udalo sie stworzyc plikow, brak wolnego miejsca na dysku!");
            }        
    }
    /**************************************************************/
    Block F_CheckBlock(Disk SysDisk) {
        Block hlp_Block;
        for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
            if(SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i] == null) {
                if(SysDisk.D_GetFreeBlock() != -1) {
                    SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i] = SysDisk.D_Block[SysDisk.D_GetFreeBlock()]; // wpis nowego bloku do i-wÄ™zĹ‚a
                    SysDisk.D_iNode[this.F_iNode_Id].iNode_AddBlockId(SysDisk.D_Block[SysDisk.D_GetFreeBlock()].B_Id);
                    hlp_Block = SysDisk.D_Block[SysDisk.D_GetFreeBlock()];
                    SysDisk.D_BitVector_Block[SysDisk.D_GetFreeBlock()] = 1;
                    return hlp_Block;
                }
                else {
                    return null;
                }
            }
            else if(!SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i].B_BlockIsFull()) /* jeĹĽeli nie w peĹ‚ni zapeĹ‚niony*/ {
                return SysDisk.D_iNode[this.F_iNode_Id].DirBlock[i];
            }
        }
        for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
            if(SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i] == null) {
                if(SysDisk.D_GetFreeBlock() != -1) {
                    SysDisk.D_iNode[this.F_iNode_Id].iNode_AddBlockId(SysDisk.D_Block[SysDisk.D_GetFreeBlock()].B_Id);
                    SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i] = SysDisk.D_Block[SysDisk.D_GetFreeBlock()]; // wpis nowego bloku do i-wÄ™zĹ‚a
                    hlp_Block = SysDisk.D_Block[SysDisk.D_GetFreeBlock()];
                    SysDisk.D_BitVector_Block[SysDisk.D_GetFreeBlock()] = 1;
                    return hlp_Block;
                }
                else {
                    return null;
                }
            }
            else if(!SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i].B_BlockIsFull()) /* jeĹĽeli nie w peĹ‚ni zapeĹ‚niony*/ {
                return SysDisk.D_iNode[this.F_iNode_Id].InDirBlock[0][i];
            }
        }
        return null; // w przypadku braku wolnych blokĂłw
    }
    /**************************************************************/
}
/******************************************************************/
