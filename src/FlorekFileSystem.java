/******************************************************************/
/*                     FLOREK FILE SYSTEM v1.0                    */
/*                     Author: Łukasz Florczak                    */
/*                  Last update: 28.11.2015 10:37                 */
/******************************************************************/
package florekfilesystem;
import java.util.Scanner;
/******************************************************************/
public class FlorekFileSystem {   
    public static void main(String[] args) { 
       Scanner Command = new Scanner(System.in);
       String Decision;
       Disk SysDisk = new Disk("Dysk Systemowy", "Florek File System", 1024, 32); 
       
       while(true) {
           System.out.print("Komenda: ");
           Decision = Command.nextLine(); 
           if(Decision.equals("ext"))
               break;
           SysDisk.Disk_Command(Decision);
       }       
    }  
}
/******************************************************************/
