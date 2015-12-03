import java.util.ArrayList;
import java.util.List;


	public class Scheduler {
		
	ArrayList<ArrayList<Proces>> qs = new ArrayList<ArrayList<Proces>>(8);
	boolean[] whichqs = new boolean[8]; //ktore kolejki sa nie puste
	public int base = 8;
	
	Scheduler()
	{
		
		for(int i=0;i<7;i++)
			whichqs[i] = false;
	}
	
	public void add_to_ready(Proces pr)
	{
		qs.add(0,new ArrayList<Proces>());
		qs.get(pr.pri/4).add(pr);	
		whichqs[pr.pri/4] = true;


}
	
	
	public boolean przelicz()  
	{
		Proces tmp;
		System.out.println("Przeliczanie priorytetu");
		for(int i=0;i<7;i++)
		{
			if(whichqs[i])
			{
				for(int j=0;j<qs.get(i).size();j++)
				{
					qs.get(i).get(j).cpu /= 2;
					qs.get(i).get(j).uspri = base + qs.get(i).get(j).cpu/2 + qs.get(i).get(j).nice;
					
				}
						//qs[i];
			}
		}
		
		
		return true;
	}
}
