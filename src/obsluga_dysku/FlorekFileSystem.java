/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Ĺ�ukasz Florczak                    */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;
import java.util.Scanner;

import Interpreter.Interpreter;
import obsluga_dysku.FlorekFileSystem;
import obsluga_procesora.Scheduler;
import pamiec_wirtualna.MemoryManagement;
import zarzadzanie_procesami.Management;
/******************************************************************/
public class FlorekFileSystem {   
    /****************************************************************/
    public static Disk SysDisk = new Disk("Dysk Systemowy", "Florek File System", 1024, 32); 
    /****************************************************************/
    public static char[] F_Read(String F_Name) {
        File hlp_File = SysDisk.D_FindFile(F_Name);
        if(hlp_File != null) {
            char[] Content = new char[SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size];
            int k = 0;
            if(SysDisk.D_BitVector_iNode[hlp_File.F_iNode_Id] == 1) {
                for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
                    if(SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i] != null) {
                        for(int j = 0; j < SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i].B_PointerToFreeByte; j++) {
                            Content[k] = SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i].B_Content[j];
                            k++;
                        }
                    }
                }
                for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
                    if(SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i] != null) {
                        for(int j = 0; j < SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i].B_PointerToFreeByte; j++) {
                            Content[k] = SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i].B_Content[j];
                            k++;
                        }
                    }
                }
            }
            else {
                System.out.println("Plik nie moĹĽe zostaÄ‡ odczytany, gdyĹĽ nie istnieje!");
                return null;
            }
            return Content;
        }
        return null;
    }
    /****************************************************************/
    public static void F_Write(String F_Name, String Content) {
        File hlp_File = SysDisk.D_FindFile(F_Name);
        if(hlp_File != null) {
            Block hlp_Block = hlp_File.F_CheckBlock(SysDisk);
            if(SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size < SysDisk.D_MaxFileSize && hlp_Block != null) {
                for(int i = 0; i < Content.length(); i++) {
                   if(hlp_Block.B_BlockIsFull()) {
                       hlp_Block = hlp_File.F_CheckBlock(SysDisk);
                   }
                   if(hlp_Block != null && SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size < SysDisk.D_MaxFileSize) {
                       hlp_Block.B_Bytes[hlp_Block.B_PointerToFreeByte] = 1;
                       hlp_Block.B_Content[hlp_Block.B_PointerToFreeByte] = Content.charAt(i);
                       SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size++;
                       SysDisk.D_BusySpace++;
                       hlp_Block.B_PointerToFreeByte++;
                   }
                   else {
                       break;
                   }
               }
            }  
        }
    }
    /**************************************************************/
    public static void F_Delete(String F_Name) {
        File hlp_File = SysDisk.D_FindFile(F_Name);
        if(hlp_File != null) {
            for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
                Block hlp_Block = SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i];
                if(hlp_Block != null) {
                    hlp_Block.B_PointerToFreeByte = 0;
                    hlp_Block.B_CleanBlock();
                    SysDisk.D_CleanCatalogEntry(hlp_File.F_Name);
                    SysDisk.D_BitVector_Block[SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i].B_Id] = 0; // zwalnianie bloku
                    SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i] = null;
                    SysDisk.D_BitVector_iNode[hlp_File.F_iNode_Id] = 0; // zwalnianie i-wÄ™zĹ‚a        
                }
            }
            for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
                Block hlp_Block = SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i];
                if(hlp_Block != null) {
                    hlp_Block.B_PointerToFreeByte = 0;
                    hlp_Block.B_CleanBlock();
                    SysDisk.D_CleanCatalogEntry(hlp_File.F_Name);
                    SysDisk.D_BitVector_Block[SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i].B_Id] = 0; // zwalnianie bloku
                    SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i] = null;
                    SysDisk.D_BitVector_iNode[hlp_File.F_iNode_Id] = 0; // zwalnianie i-wÄ™zĹ‚a   
                }
            }
            SysDisk.D_BusySpace -= SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size;
            SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size = 0;
            SysDisk.D_iNode[hlp_File.F_iNode_Id].iNode_Clean();
        }
    }
    /**************************************************************/
    public static void Create_File(String F_Name, String Content) {
        File hlp_File = new File(F_Name, SysDisk, 'F', Content);
    }
    /**************************************************************/
    public static void Disk_Command(String Command) {
        Scanner YesNo = new Scanner(System.in);
        String Decision;
        String Com[] = null;
            Com = Command.split(" ");
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(Com[0].equals("crt")) {
                File hlp_File;
                if(Com.length > 1) {
                    char hlp_char = Com[1].charAt(0);
                    hlp_File = SysDisk.D_FindFile(Com[1].substring(1));  
                    if(hlp_char == '/') {
                        if(hlp_File != null) {
                            System.out.println("Plik o podanej nazwie istnieje, czy chcesz go nadpisaÄ‡(stracisz wszystkie informacje w nim zawarte)? T/N");
                            Decision = YesNo.nextLine();
                            if(Decision.equals("T")) {
                                F_Delete(hlp_File.F_Name);
                                Create_File(Com[1].substring(1), "");
                                if(Com.length > 2) {
                                    for(int i = 2; i < Com.length; i++) {
                                        F_Write(Com[1].substring(1), Com[i] + " ");
                                    }
                                }
                                System.out.println("Plik zostaĹ‚ pomyĹ›lnie utworzony!");
                            }
                            else {
                                System.out.println("Plik nie zostaĹ‚ utworzony!");
                            }
                        }
                        else {
                            hlp_File = new File(Com[1].substring(1), SysDisk, 'F', "");
                            if(Com.length > 2) {
                                for(int i = 2; i < Com.length; i++) {
                                    F_Write(Com[1].substring(1), Com[i] + " ");
                                }
                            }
                            hlp_File = SysDisk.D_FindFile(Com[1].substring(1));
                            if(hlp_File != null)
                                System.out.println("Plik zostaĹ‚ pomyĹ›lnie utworzony!");
                        }
                    }
                    else {
                        System.out.println("Nie rozpoznano Ĺ›cieĹĽki! PamiÄ™taj o '/'!");
                    }      
                }
                else {
                        System.out.println("Nie rozpoznano komendy!");
                }
            }    
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("sba")) {
                SysDisk.D_ShowBlockBytes();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("ls")) {
                SysDisk.D_ShowCatalog();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("sbc")) {
                if(Com.length > 1) {
                    int BlockNumber = Integer.parseInt(Com[1]);
                    if((BlockNumber > SysDisk.D_BlockValue - 1) || (BlockNumber < 0)) {
                        System.out.println("Blok o podanym indeksie nie istnieje!");
                    }
                    else if(BlockNumber == 0) {
                        System.out.println("Blok zarezerwowany dla katalogu gĹ‚Ăłwnego, nie moĹĽna wyĹ›wietliÄ‡ zawartoĹ›ci!");
                    }
                    else if(BlockNumber > 0 && BlockNumber < SysDisk.D_BlockValue) {
                        SysDisk.D_Block[BlockNumber].B_ShowBlockBytes();
                    }   
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("siv")) {
                SysDisk.D_ShowiNodesBitVector();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("sbv")) {
                SysDisk.D_ShowBlocksBitVector();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("opn")) {
                if(Com.length > 1) {
                    char hlp_char = Com[1].charAt(0);
                    if(hlp_char == '/') {
                        SysDisk.D_OpenFile(Com[1].substring(1));
                    }
                    else {
                        System.out.println("Nie rozpoznano Ĺ›cieĹĽki! PamiÄ™taj o '/'!");
                    }
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("vi")) {
                if(Com.length > 2) {
                    char hlp_char = Com[1].charAt(0);
                    File hlp_File = SysDisk.D_FindFile(Com[1].substring(1));
                    if(hlp_char == '/') {
                        if(hlp_File != null) {
                            for(int i = 2; i < Com.length; i++) {
                                if(SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size  < SysDisk.D_MaxFileSize) {
                                    F_Write(Com[1].substring(1), Com[i] + " ");
                                }
                                else {
                                    System.out.println("Nie udaĹ‚o siÄ™ zapisaÄ‡ zawartoĹ›ci, brak wolnych blokĂłw lub plik osiÄ…gnÄ…Ĺ‚ maksymalny rozmiar!");
                                    break;
                                }        
                            }   
                        }
                        else {
                            System.out.println("Nie moĹĽna edytowaÄ‡ pliku! Plik nie istnieje!");
                        }
                    }
                    else {
                        System.out.println("Nie rozpoznano Ĺ›cieĹĽki! PamiÄ™taj o '/'!");
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
                    if((BlockNumber > SysDisk.D_BlockValue - 1) || (BlockNumber < 0)) {
                        System.out.println("Blok o podanym indeksie nie istnieje!");
                    }
                    else if(BlockNumber == 0) {
                        System.out.println("Blok zarezerwowany dla katalogu gĹ‚Ăłwnego, nie moĹĽna wyĹ›wietliÄ‡ zawartoĹ›ci!");
                    }
                    else if(BlockNumber > 0 && BlockNumber < SysDisk.D_BlockValue) {
                        if(Com.length == 3) {
                            SysDisk.D_Block[BlockNumber].B_ShowBlockContent(Integer.parseInt(Com[2]), -1);
                        }
                        else if(Com.length == 4) {
                            SysDisk.D_Block[BlockNumber].B_ShowBlockContent(Integer.parseInt(Com[2]), Integer.parseInt(Com[3]));
                        }
                        else {
                            SysDisk.D_Block[BlockNumber].B_ShowBlockContent(-1, -1);
                        }
                    }   
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("inf")) {
                SysDisk.D_Info();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("rm")) {
                if(Com.length > 1) {
                    char hlp_char = Com[1].charAt(0);
                    File hlp_File = SysDisk.D_FindFile(Com[1].substring(1));
                    if(hlp_char == '/') {
                        if(hlp_File != null) {
                            System.out.println("Czy na pewno chcesz usunÄ…Ä‡ ten plik? T/N");
                            Decision = YesNo.nextLine();
                            if(Decision.equals("T")) {
                                F_Delete(hlp_File.F_Name);
                                System.out.println("Plik zostaĹ‚ usuniÄ™ty!");
                            }
                            else {
                                System.out.println("Anulowano usuniÄ™cie pliku!");
                            }
                        } 
                        else {
                            System.out.println("Plik o podanej nazwie nie istnieje!");
                        }
                    }
                    else {
                        System.out.println("Nie rozpoznano Ĺ›cieĹĽki! PamiÄ™taj o '/'!");
                    }
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("mv")) {
                if(Com.length > 2) {
                    char hlp_char = Com[1].charAt(0);
                    if(hlp_char == '/') {
                        SysDisk.D_ChangeNameFile(Com[1].substring(1), Com[2]);
                    }
                    else {
                        System.out.println("Nie rozpoznano Ĺ›cieĹĽki! PamiÄ™taj o '/'!");
                    }                
                }
                else {
                    System.out.println("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("bcp")) {
                SysDisk.BackupProgramFiles();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("i_test")) {
                Interpreter.test();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(Com[0].equals(""));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else {
                System.out.println("Nie rozpoznano komendy!");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    /**************************************************************/
     public static void main(String[] args) { 
       Scanner Command = new Scanner(System.in);
       String Decision;
       
       Create_File("Program1", "mv RA,01\nmv RB,05\nad RA,RB\nj1 00");
       Create_File("Program2", "mv RA,05\nmi 50,RA\nmv BR,RA\nml BR,BA \nsb b,01\nj1 25\net");
       
       while(true) {
           System.out.print("Komenda: ");
           Decision = Command.nextLine(); 
           if(Decision.equals("ext"))
               break;
           Disk_Command(Decision);
       }       
    }  
}
/******************************************************************/
