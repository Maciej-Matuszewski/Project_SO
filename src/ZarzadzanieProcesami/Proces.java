package ZarzadzanieProcesami;

import java.util.Random;
import java.util.ArrayList;

import komunikacja_miedzy_procesami.Pipe;
import komunikacja_miedzy_procesami.PipeField;

public class Proces {

	public static int nr = 0;
	String nazwa;
	public int PID;
	public int PPID;
	public int priorytet;
	public int stan;
	Random random = new Random();
	public ArrayList<Pipe> pipes = new ArrayList<>();
	public PipeField childPipe = null;
	
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
		try {
			new Pipe(proces, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
