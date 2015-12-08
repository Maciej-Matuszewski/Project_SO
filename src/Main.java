import java.io.IOException;
import java.util.Scanner;

import obsluga_dysku.FlorekFileSystem;
import Interpreter.Interpreter;

public class Main {
	
	public static void main(String[] args) {
		FlorekFileSystem.SysDisk.BackupProgramFiles(); // inicjalizacja plików z programami ;)
		try{
			Interpreter interpreter = new Interpreter();
			//interpreter.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
