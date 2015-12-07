package zarzadzanie_procesami;

import java.awt.List;
import java.util.ArrayList;

public class Management 
{	
	public static ArrayList<Proces> procesList = new ArrayList<>();
	public static ArrayList<Proces> zombies_list = new ArrayList<Proces>();
	static Proces proces = new Proces();
	
	public Management()
	{
		procesList.add(proces);
	}
	
	public static void fork()
	{
		fork(proces);
	}
	
	public static Proces fork(Proces pro)
	{
		Proces proces = new Proces(pro);
		procesList.add(proces);
		return proces;
	}
	
	static void kill(int pid)
	{
		procesList.remove(FindProces(pid));
	}
	
	public int wait(Proces pr)
	{
		if(Is_parent(pr))
		{
			for(Proces temp: zombies_list)
			{
				if(temp.PPID == pr.PID)
				{
					int tmp_pid = temp.PID;
					zombies_list.remove(temp);
					kill(tmp_pid);
					System.out.println("Proces "+temp.PID+" zostal usuniety przez proces macierzysty "+pr.PID+".");
					return tmp_pid; // zwrot pid procesu zakonczonego
				}	
			}
			System.out.println("Proces "+pr.PID+" oczekuje na zakonczenie jednego z potomkow.");
			return 0; // doda ojca do listy wait w scheduler
		}
		else
		{
			System.out.println("Blad metody wait(). Proces "+pr.PID+" nie posiada potomkow!");
			return -1; // nie jest ojcem nie moze wykonac wait
		}
	}
	
	
	
	public static void exit_all()
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
							procesList.get(j).PPID = 1;
						}
					}
					kill(procesList.get(i).PID);
				}
			}
		}	
	}
	
	public static boolean exit(int pid)
	{
		int i = FindProces(pid);
		int id_parent = FindProces(procesList.get(i).PPID);
		if(procesList.get(i).stan == 4)
		{
			zombies_list.add(procesList.get(i));
			procesList.get(i).stan = 5;
			if(Is_parent(procesList.get(i)))
			{
				for(int j=0; j<procesList.size(); j++)
				{
					if(procesList.get(j).PPID == procesList.get(i).PID)
					{
						procesList.get(j).PPID = 1;
					}
				}
				//kill(procesList.get(i).PID);
			}
			if(procesList.get(id_parent).stan == 3)
			{
				return true;
			}
		}
		return false;
	}
	
	public static void exec()
	{
		System.out.println("Tu b�dzie metoda exec");
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
	
	static public Proces processLookup(int id){
		for(Proces temp : procesList){
			if(temp.PID == id){ //PID or PPID?
			return temp;
			}
		}
		return null;
	}
	
}
