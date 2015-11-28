package ZarzadzanieProcesami;

public class Management 
{	
	static void fork()
	{
		Proces proces = new Proces();
		System.out.println("Powsta³: " + proces.nazwa);
		
	}
	
	static void fork(Proces proces)
	{
		Proces proces2 = new Proces(proces);
		System.out.println("Powsta³: " + proces.nazwa);
		
	}
	
	static void kill()
	{
		System.out.println("Tu bêdzie metoda kill");
	}
	static void exit()
	{
		System.out.println("Tu bêdzie metoda exit");
	}
	
	static void exec()
	{
		System.out.println("Tu bêdzie metoda exec");
	}
	
	static void ProcessPrint()
	{
		System.out.println("Tu bêdzie metoda ProcessPrint");
	}
	
}
