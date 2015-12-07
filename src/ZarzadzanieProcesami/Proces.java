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
	// STAN 1 - NOWY
	// STAN 2 - GOTOWY
	// STAN 3 - CZEKAJACY
	// STAN 4 - WYKONANY
	Random random = new Random();
	public ArrayList<Pipe> pipes = new ArrayList<>();
	public PipeField childPipe = null;
	
	Proces()
	{
		nr++;
		nazwa = "root(Init)";
		PID = nr;
		priorytet = 0;
		stan = 0;
		System.out.println("Powstal proces systemowy - root(Init)");
	}
	
	Proces(Proces proces)
	{
		nr++;
		nazwa = "proces " + nr;
		PID = nr;
		PPID = proces.PID;
		priorytet = random.nextInt(32);
		stan = random.nextInt(4) + 1;
		System.out.println("Powstal: " + this.nazwa + ", Od procesu: " + proces.nazwa);
		try {
			new Pipe(proces, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
