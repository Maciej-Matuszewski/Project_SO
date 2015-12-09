/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;
import java.util.ArrayList;
import java.util.List;
/******************************************************************/
public class Block {
    int B_Id;
    int[] B_Bytes;
    char[] B_Content;
    int B_PointerToFreeByte;
    int B_Size;
    List<File>CatalogEntry; // dostępny tylko dla katalogu
    /**************************************************************/
    Block(int B_Size, int Id) {
        this.B_Id = Id;
        this.B_Size = B_Size;
        this.B_PointerToFreeByte = 0;
        this.B_Bytes = new int[B_Size];
        this.B_Content = new char[B_Size];
        /**********************************************************/
        for(int i = 0; i < this.B_Size; i++) {
            this.B_Bytes[i] = 0;
        }
    }
    /**************************************************************/
    void B_CleanBlock() {
        for(int i = 0; i < this.B_Size; i++) {
            this.B_Bytes[i] = 0;
        }
    }
    /**************************************************************/
    Boolean B_BlockIsFull() {
        for(int i = 0; i < this.B_Size; i++) {
            if(this.B_Bytes[i] == 0) {
                return false;
            }
        }
        return true;
    }
    /**************************************************************/
    void B_ShowBlockBytes() {
        for(int i = 0; i < this.B_Size; i++) {
            System.out.print(this.B_Bytes[i]);
        }
        System.out.println();
    }
    /**************************************************************/
    void B_ShowBlockContent() {
        for(int i = 0; i < this.B_Size; i++) {
            System.out.print(this.B_Content[i]);
        }
        System.out.println();
    }
    /**************************************************************/
}
/******************************************************************/
