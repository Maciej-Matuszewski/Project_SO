package ZarzadzanieProcesami;

import java.awt.List;
import java.util.ArrayList;

public class Management 
{	
	public static ArrayList<Proces> procesList;
	
	Management()
	{
		procesList = new ArrayList<>();
	}
	
	static void fork()
	{
		Proces proces = new Proces();
		System.out.println("Powsta³: " + proces.nazwa);
		procesList.add(proces);
	}
	
	public static void fork(Proces pro)
	{
		Proces proces = new Proces(pro);
		System.out.println("Powsta³: " + proces.nazwa);
		procesList.add(proces);
		
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
		System.out.print("Name\t\t" + "PID\t" + "PPID\t" + "Priorytet\t");
		System.out.println("");
		for(int i=0; i<procesList.size(); i++)
		{
			System.out.print(procesList.get(i).nazwa +"\t"+ procesList.get(i).PID +"\t" + procesList.get(i).PPID + "\t" +  procesList.get(i).priorytet);
			System.out.println("");
		}
		
	}
	
}
