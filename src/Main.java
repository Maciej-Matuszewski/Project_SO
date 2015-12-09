
import obsluga_dysku.FlorekFileSystem;
import Interpreter.Interpreter;
import Interpreter.Output;

public class Main {
	
	public static Output output = new Output();
	
	public static void main(String[] args) {
		Output.main(args);
		FlorekFileSystem.SysDisk.BackupProgramFiles(); // inicjalizacja plików z programami ;)
		try{
			new Interpreter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
