package ZarzadzanieProcesami;

public class Management 
{	
	static void fork()
	{
		Proces proces = new Proces();
		System.out.println("Powsta�: " + proces.nazwa);
		
	}
	
	static void fork(Proces proces)
	{
		Proces proces2 = new Proces(proces);
		System.out.println("Powsta�: " + proces.nazwa);
		
	}
	
	static void kill()
	{
		System.out.println("Tu b�dzie metoda kill");
	}
	static void exit()
	{
		System.out.println("Tu b�dzie metoda exit");
	}
	
	static void exec()
	{
		System.out.println("Tu b�dzie metoda exec");
	}
	
	static void ProcessPrint()
	{
		System.out.println("Tu b�dzie metoda ProcessPrint");
	}
	
}
