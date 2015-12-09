/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Ĺ�ukasz Florczak                    */
/*                  Last update: 07.12.2015 10:08                 */
/******************************************************************/
package obsluga_dysku;
import pamiec_wirtualna.MemoryManagement;
import zarzadzanie_procesami.Management;
import zarzadzanie_procesami.Proces;

import java.util.Scanner;

import obsluga_procesora.Scheduler;
import Interpreter.Interpreter;
import Interpreter.Output;
/******************************************************************/
public class FlorekFileSystem {   
    /****************************************************************/
    public static Disk SysDisk = new Disk("Dysk Systemowy", "Florek File System", 1024, 32); 
    /****************************************************************/
    public static char[] F_Read(String F_Name, int From, int To) {
        File hlp_File = SysDisk.D_FindFile(F_Name);
        char[] hlp;
        String h_String;
                
        if(hlp_File != null) {
            //char[] Content = new char[SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size];
            String Content = "";
            int k = 0;
            if(SysDisk.D_BitVector_iNode[hlp_File.F_iNode_Id] == 1) {
                for(int i = 0; i < SysDisk.D_MaxDirectBlock; i++) {
                    if(SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i] != null) {
                        for(int j = 0; j < SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i].B_PointerToFreeByte; j++) {
                            Content += SysDisk.D_iNode[hlp_File.F_iNode_Id].DirBlock[i].B_Content[j];
                            k++;
                        }
                    }
                }
                for(int i = 0; i < SysDisk.D_MaxInDirectBlock; i++) {
                    if(SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i] != null) {
                        for(int j = 0; j < SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i].B_PointerToFreeByte; j++) {
                            Content += SysDisk.D_iNode[hlp_File.F_iNode_Id].InDirBlock[0][i].B_Content[j];
                            k++;
                        }
                    }
                }
            }
            else {
                Output.write("Plik nie moze zostac odczytany, gdyz nie istnieje!");
                return null;
            }
            if(From < 0 || From > SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size || From == -1) {
                From = 0;
            }
            if(To < 0 || To > SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size || To == -1) {
                To = SysDisk.D_iNode[hlp_File.F_iNode_Id].F_Size;
            }
            h_String = Content.substring(From, To);
            hlp = h_String.toCharArray();
            return hlp;
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
                    SysDisk.D_BitVector_iNode[hlp_File.F_iNode_Id] = 0; // zwalnianie i-węzła        
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
                    SysDisk.D_BitVector_iNode[hlp_File.F_iNode_Id] = 0; // zwalnianie i-węzła   
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
    public static boolean StringIsNumber(String hlp) {
        for(int i = 0; i < hlp.length(); i++) {
            if(hlp.charAt(i) != '0' && hlp.charAt(i) != '1' && hlp.charAt(i) != '2' && hlp.charAt(i) != '3' && hlp.charAt(i) != '4' 
                    && hlp.charAt(i) != '5' && hlp.charAt(i) != '6' && hlp.charAt(i) != '7' && hlp.charAt(i) != '8' && hlp.charAt(i) != '9') {
                return false;
            }
        }
        return true;
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
                    hlp_File = SysDisk.D_FindFile(Com[1].substring(1,9));  
                    if(hlp_char == '/') {
                        if(hlp_File != null) {
                        	Decision = Output.loadCMD("Plik o podanej nazwie istnieje, czy chcesz go nadpisac(stracisz wszystkie informacje w nim zawarte)? T/N");
                            //Decision = YesNo.nextLine();
                            if(Decision.equals("T")) {
                                F_Delete(hlp_File.F_Name);
                                Create_File(Com[1].substring(1), "");
                                if(Com.length > 2) {
                                    for(int i = 2; i < Com.length; i++) {
                                        F_Write(Com[1].substring(1), Com[i] + " ");
                                    }
                                }
                                Output.write("Plik zostal pomyslnie utworzony!");
                            }
                            else {
                                Output.write("Plik nie zostal utworzony!");
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
                                Output.write("Plik zostal pomyslnie utworzony!");
                        }
                    }
                    else {
                        Output.write("Nie rozpoznano sciezki! Pamiętaj o '/'!");
                    }      
                }
                else {
                        Output.write("Nie rozpoznano komendy!");
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
                SysDisk.D_ShowBlockContent();
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
                    int hlp;
                    int hlp2;
                    char hlp_char = Com[1].charAt(0);
                    if(hlp_char == '/') {
                        if(Com.length == 3) {
                            if(StringIsNumber(Com[2])) {
                                hlp = Integer.parseInt(Com[2]);
                            }
                            else {
                                hlp = 0;
                            }
                            SysDisk.D_OpenFile(Com[1].substring(1), hlp, -1);
                        }
                        else if(Com.length == 4) {
                            if(StringIsNumber(Com[2])) {
                                hlp = Integer.parseInt(Com[2]);
                            }
                            else {
                                hlp = 0;
                            }
                            if(StringIsNumber(Com[3])) {
                                hlp2 = Integer.parseInt(Com[3]);   
                            }
                            else {
                                hlp2 = -1;
                            }
                            SysDisk.D_OpenFile(Com[1].substring(1), hlp, hlp2);
                        }
                        else {
                            SysDisk.D_OpenFile(Com[1].substring(1), 0, -1);
                        }
                    }
                    else {
                        Output.write("Nie rozpoznano scieżki! Pamietaj o '/'!");
                    }
                }
                else {
                    Output.write("Nie rozpoznano komendy!");
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
                                    Output.write("Nie udalo sie zapisac zawartosci, brak wolnych blokow lub plik osiągnal maksymalny rozmiar!");
                                    break;
                                }        
                            }   
                        }
                        else {
                            Output.write("Nie mozna edytowac pliku! Plik nie istnieje!");
                        }
                    }
                    else {
                        Output.write("Nie rozpoznano scieżki! Pamietaj o '/'!");
                    }
                }
                else {
                    Output.write("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("scb")) {
                SysDisk.D_ShowBlockContent();
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
                        	Decision = Output.loadCMD("Czy na pewno chcesz usunac ten plik? T/N");
                            //Decision = YesNo.nextLine();
                            if(Decision.equals("T")) {
                                F_Delete(hlp_File.F_Name);
                                Output.write("Plik zostal usuniety!");
                            }
                            else {
                                Output.write("Anulowano usuniecie pliku!");
                            }
                        } 
                        else {
                            Output.write("Plik o podanej nazwie nie istnieje!");
                        }
                    }
                    else {
                        Output.write("Nie rozpoznano sciezki! Pamietaj o '/'!");
                    }
                }
                else {
                    Output.write("Nie rozpoznano komendy!");
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
                        Output.write("Nie rozpoznano sciezki! Pamietaj o '/'!");
                    }                
                }
                else {
                    Output.write("Nie rozpoznano komendy!");
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("bcp")) {
                SysDisk.BackupProgramFiles();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(Com[0].equals("i_test")) {
				Interpreter.test();
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////
			else if(Com[0].equals("")) {
			//pusty ENTER - kontynuacja wykonywania procesu
			}
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("run")) {//NIEPRZETESTOWANE JESZCZE
                String filename = Com[1];
                if(F_Read(filename, -1, -1) != null){
	                Proces tenproces = Management.fork();
	                Scheduler.add_to_ready(tenproces);
	                Management.exec(filename,tenproces.PID);
                }
                else
                	Output.write("Podany plik nie istnieje.");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if (Com[0].equals("ds")){
                MemoryManagement.displayStatus();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if (Com[0].equals("da")){
                int pid = Integer.parseInt(Com[1]);
                MemoryManagement.displayAddressSpace(pid);
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("rmem")){
                int va =Integer.parseInt(Com[1]);
                int size = Integer.parseInt(Com[2]);
                int pid = Integer.parseInt(Com[3]);
                Output.write(String.valueOf(MemoryManagement.readMemory(va,size,pid)));
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("wmem")){
                int va =Integer.parseInt(Com[1]);
                char[] text = Com[2].toCharArray();
                int pid = Integer.parseInt(Com[3]);
                MemoryManagement.displayStatus();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("shr")){
            	Scheduler.show_ready_list();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("shw")){
            	Scheduler.show_wait_list();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else if(Com[0].equals("shutdown")){
            	Interpreter.shutdown = true;
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            else {
                Output.write("Nie rozpoznano komendy!");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    /**************************************************************/  
}
/******************************************************************/
