import java.util.ArrayList;
import java.util.List;


public class Scheduler {
		
	ArrayList<ArrayList<Proces>> qs = new ArrayList<ArrayList<Proces>>(8);
	ArrayList<Proces> wait_list = new ArrayList<Proces>();
	boolean[] whichqs = new boolean[8]; //ktore kolejki sa nie puste
	public int base = 8;
	Proces pr_rdy;
	
	Scheduler()
	{
		for(int i=0;i<7;i++)
		{
			qs.add(i,new ArrayList());
			whichqs[i] = false;
		}
	}
	
	public void add_to_ready(Proces pr)
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
			
		whichqs[pr.pri/4] = true;
	}
	
	public void add_to_wait(Proces pr)
	{
		int tmp = qs.get(pr.pri/4).indexOf(pr);
		wait_list.add(qs.get(pr.pri/4).get(tmp));
		qs.get(pr.pri/4).remove(tmp);
	}
	
	
	public boolean przelicz()  
	{
		Proces tmp_proces;
		ArrayList<ArrayList<Proces>> tmp_qs = new ArrayList<ArrayList<Proces>>(8);
		boolean tmp_whichqs[] = whichqs;
		System.out.println("Przeliczanie priorytetu");
		for(int i=0;i<7;i++)
		{
			if(whichqs[i])
			{
				for(int j=0;j<qs.get(i).size();j++)
				{
					qs.get(i).get(j).cpu /= 2;
					qs.get(i).get(j).uspri = base + qs.get(i).get(j).cpu/2 + qs.get(i).get(j).nice;
					qs.get(i).get(j).pri = qs.get(i).get(j).uspri;
				}
			}
		}
		tmp_qs = qs;
		for(int i=0;i<7;i++)
		{
			qs.get(i).clear();
			whichqs[i]=false;
		}
		for(int i=0;i<7;i++)
		{
			if(tmp_whichqs[i])
			{
				for(int j=0;j<tmp_qs.get(i).size();j++)
				{
					add_to_ready(tmp_qs.get(i).get(j));
				}
			}
		}
		int first_not_empty=8;
		for(int i=0;i<8;i++)
		{
			if(whichqs[i])
			{
				first_not_empty = i;
				break;
			}
		}
		if(first_not_empty != 8 && qs.get(first_not_empty).get(0).pri>pr_rdy.pri)
			return true;
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
			return false;		//nie udalo sie zmienic kontekstu / brak gotowych procesow
		
		pr_rdy = qs.get(first_not_empty).get(0);
		
		return true; // udalo sie zmienic kontekst
	}
}
