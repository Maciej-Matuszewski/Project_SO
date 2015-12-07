/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package Obsluga_dysku;
/******************************************************************/
public class iNode {
    int F_Size;             // rozmiar pliku
    Disk SysDisk;           // dysk na którym i-węzeł się znajduje
    char[] F_Type;          // typ pliku C - catalog  F - file
    Block[] DirBlock;       // bloki bezpośrednie
    Block[][] InDirBlock;   // bloki jednopośrednie
    int[] FileBlockIds;     // tablica przechowująca identyfikatory bloków zajętych przez plik
    /**************************************************************/
    iNode(int i_MaxDirBlock, int i_MaxInDirBlock) {
        this.F_Size = 0;
        this.F_Type = new char[1];
        this.DirBlock = new Block[i_MaxDirBlock];
        this.InDirBlock = new Block[1][i_MaxInDirBlock];
        this.FileBlockIds = new int[i_MaxDirBlock + i_MaxInDirBlock];
        /**********************************************************/
        for(int i = 0; i < i_MaxDirBlock; i++) {
            this.DirBlock[i] = null;
        }
        /**********************************************************/
        for(int i = 0; i < i_MaxInDirBlock; i++) {
            this.InDirBlock[0][i] = null;
        }
        /**********************************************************/
    }
    /**************************************************************/
    void iNode_AddBlockId(int value) {
        int hlp_var = 0;
        for(int i = 0; i < this.FileBlockIds.length; i++) {
            if(this.FileBlockIds[i] == value) {
                hlp_var = 1;
            }
        }
        if(hlp_var == 0) {
            for(int i = 0; i < this.FileBlockIds.length; i++) {
                if(this.FileBlockIds[i] == -1) {
                    this.FileBlockIds[i] = value;
                    break;
                }
            }
        }
    }
    /**************************************************************/
    void iNode_Clean() {
        for(int i = 0; i < this.FileBlockIds.length; i++) {
            this.FileBlockIds[i] = -1;
        }
    }
    /**************************************************************/
}
/******************************************************************/
