package obsluga_procesora;
import java.util.ArrayList;

import zarzadzanie_procesami.*;


public class Scheduler {
		
	static ArrayList<ArrayList<Proces>> qs = new ArrayList<ArrayList<Proces>>(8);
	ArrayList<Proces> wait_list = new ArrayList<Proces>();
	static boolean[] whichqs = new boolean[8]; //ktore kolejki sa nie puste
	public int base = 8;
	public Proces pr_rdy;
	
	public Scheduler()
	{
		for(int i=0;i<7;i++)
		{
			qs.add(i,new ArrayList<Proces>());
			whichqs[i] = false;
		}
	}
	
	public static void add_to_ready(Proces pr)
	{
		add_to_ready_list(pr);
		System.out.println("Dodano proces PID: "+pr.PID+" do kolejki procesow gotowych.");
	}
	
	public static void add_to_ready_list(Proces pr)
	{
		if(qs.get(pr.pri/4).size()>0)
		{
			boolean add=false;
			for(int i=0; i<qs.get(pr.pri/4).size();i++)
			{
				if(pr.pri<qs.get(pr.pri/4).get(i).pri)
				{
					qs.get(pr.pri/4).add(i,pr);
					add = true;
					break;
				}
			}
			if(add == false)
				qs.get(pr.pri/4).add(pr);
		}
		else
		{
			qs.get(pr.pri/4).add(pr);
		}
		pr.stan = 2;
			
		whichqs[pr.pri/4] = true;
		
	}
	
	
	public void add_to_ready_test(Proces pr)
	{
		pr.pri = 0;
		pr.uspri = 0;
		qs.get(0).add(0, pr);
		pr.stan = 2;
			
		whichqs[0] = true;
		System.out.println("Dodano proces PID: "+pr.PID+" do kolejki procesow gotowych na pozycje pierwsza.");
	}
	
	public void remove(Proces pr)
	{
		qs.get(pr.pri/4).remove(pr);
		if(qs.get(pr.pri/4).size() == 0)
			whichqs[pr.pri/4]= false;
	}
	
	public void add_to_wait(Proces pr)
	{
		int tmp = qs.get(pr.pri/4).indexOf(pr);
		wait_list.add(qs.get(pr.pri/4).get(tmp));
		qs.get(pr.pri/4).remove(tmp);
		pr.stan=3;
	}
	
	public void wakeup(int PID)
	{
		for(Proces temp : wait_list){
			if(temp.PID == PID){
				System.out.println("Proces PID: "+temp.PID+" zostal obudzony.");
				add_to_ready(temp);
				wait_list.remove(temp);	
				break;
			}
			}
	}
	
	
	public boolean przelicz()  
	{
		ArrayList<ArrayList<Proces>> tmp_qs = new ArrayList<ArrayList<Proces>>(8);
		System.out.println("Przeliczanie priorytetu");
		for(int i=0;i<7;i++)
		{
			if(whichqs[i])
			{
				for(int j=0;j<qs.get(i).size();j++)
				{
					qs.get(i).get(j).cpu = qs.get(i).get(j).cpu / 2;
					qs.get(i).get(j).uspri = base + qs.get(i).get(j).cpu/2 + qs.get(i).get(j).nice;
					qs.get(i).get(j).pri = qs.get(i).get(j).uspri;
				}
			}
		}
		for(int i=0;i<8;i++){
			tmp_qs.add(i, new ArrayList<Proces>());
			if(whichqs[i] == true)
			for(int j=0;j<qs.get(i).size();j++)
				tmp_qs.get(i).add(qs.get(i).get(j));
			
		}


		for(int i=0;i<7;i++)
		{
			qs.get(i).clear();
		}

		for(int i=0;i<7;i++)
		{
			
			if(whichqs[i] == true)
			{
				for(int j=0;j<tmp_qs.get(i).size();j++)
				{
					add_to_ready_list(tmp_qs.get(i).get(j));
				}
			}
		}
		
		for(int i=0;i<7;i++)
		{
			if(qs.get(i).size()>0)
				whichqs[i] = true;
			else
				whichqs[i] = false;
		}
		
		int first_not_empty=8;
		for(int i=0;i<8;i++)
		{
			if(whichqs[i] == true)
			{
				first_not_empty = i;
				break;
			}
		}
		if(first_not_empty != 8 && qs.get(first_not_empty).get(0).pri<pr_rdy.pri)
		{
			System.out.println("Znaleziono proces o wyzszym priorytecie. Wykonywanie wywlaszczenia.");
			return true;
		}
		else
			return false;
	}
	
	public boolean change_context()
	{
		System.out.println("Zmiana kontekstu");
		int first_not_empty=8;
		for(int i=0;i<8;i++)
		{
			if(whichqs[i] == true)
			{
				first_not_empty = i;
				break;
			}
		}
		if(first_not_empty==8)
		{
			System.out.println("Brak gotowych procesow");
			return false;	
		}//nie udalo sie zmienic kontekstu / brak gotowych procesow
		if(pr_rdy==qs.get(first_not_empty).get(0))
		{
			qs.get(first_not_empty).remove(0);
			add_to_ready_list(pr_rdy);
			pr_rdy = qs.get(first_not_empty).get(0);
		}
		else
			pr_rdy = qs.get(first_not_empty).get(0);
		System.out.println("Kontekst zmieniono.\nAktualny proces: "+pr_rdy.PID);
		
		return true; // udalo sie zmienic kontekst
	}
}
