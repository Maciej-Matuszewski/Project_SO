import java.io.IOException;
import java.util.Scanner;

import obsluga_dysku.FlorekFileSystem;
import Interpreter.Interpreter;
import Interpreter.Output;

public class Main {
	
	public static Output output = new Output();
	
	public static void main(String[] args) {
		output.main(args);
		FlorekFileSystem.SysDisk.BackupProgramFiles(); // inicjalizacja plików z programami ;)
		try{
			output.write("Welcome");
			Interpreter interpreter = new Interpreter();
			//interpreter.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
