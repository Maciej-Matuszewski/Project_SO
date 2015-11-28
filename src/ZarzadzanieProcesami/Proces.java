package ZarzadzanieProcesami;

import java.util.Random;

public class Proces {

	public static int nr = 0;
	String nazwa;
	public int PID;
	public int PPID;
	public int priorytet;
	public int stan;
	Random random = new Random();
	
	
	Proces()
	{
		nr++;
		nazwa = "proces " + nr;
		PID = nr;
		priorytet = random.nextInt(39);
		stan = 1;
	}
	
	Proces(Proces proces)
	{
		nr++;
		nazwa = "proces " + nr;
		PID = nr;
		PPID = proces.PID;
		priorytet = random.nextInt(39);
		stan = 1;
	}
	
}
