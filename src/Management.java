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
		System.out.println("Powstal: " + proces.nazwa);
		procesList.add(proces);
	}
	
	public static void fork(Proces pro)
	{
		Proces proces = new Proces(pro);
		System.out.println("Powstal: " + proces.nazwa);
		procesList.add(proces);
		
	}
	
	static void kill(int pid)
	{
		FindProces(pid);
		procesList.remove(FindProces(pid));
	}
	
	static void exit()
	{
		for(int i=0; i<procesList.size(); i++)
		{
			if(procesList.get(i).stan == 4)
			{
				if(Is_parent(procesList.get(i)) == false)
				{
					kill(procesList.get(i).PID);
				}
				else
				{
					for(int j=0; j<procesList.size(); j++)
					{
						if(procesList.get(j).PPID == procesList.get(i).PID)
						{
							procesList.get(j).PPID = 69;
						}
					}
					kill(procesList.get(i).PID);
				}
			}
		}	
	}
	
	static void exec()
	{
		System.out.println("Tu bêdzie metoda exec");
	}
	
	static void ProcessPrint()
	{
		System.out.print("Name\t\t" + "PID\t" + "PPID\t" + "Priorytet\t" + "Stan");
		System.out.println("");
		for(int i=0; i<procesList.size(); i++)
		{
			System.out.print(procesList.get(i).nazwa +"\t"+ procesList.get(i).PID +"\t" + procesList.get(i).PPID + "\t" +  procesList.get(i).pri + "\t\t" + procesList.get(i).stan);
			System.out.println("");
		}
		
	}
	
	static int FindProces(int pid)
	{
		int id=0;
		for(int i=0; i<procesList.size(); i++)
		{
			if(procesList.get(i).PID == pid)
			{
				id = i;
			}
		}
		return id;
	}
	
	static boolean Is_parent(Proces proces)
	{
		boolean x = false;
		for(int i=0; i<procesList.size(); i++)
		{
			if(procesList.get(i).PPID == proces.PID)
			{
				 x = true;
			}
		}		
		return x;
	}
	
	
	
}