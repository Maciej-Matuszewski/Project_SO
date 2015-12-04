import java.util.Random;

public class Proces {

	public static int nr = 0;
	String nazwa;
	public int PID;
	public int PPID;
	public int pri; //P
	public int uspri; //P
	public int nice; //P parametr nadawany przez uzytkownika
	public int cpu; //P wykorzystanie procesora
	public int pRA = 63;  //P
	public int pRB;  //P
	public int pPC;  //P
	public boolean pCF;  //P
	public int stan;
	Random random = new Random();
	
	
	Proces()
	{
		nr++;
		nazwa = "proces " + nr;
		PID = nr;
		pri = 0; //P
		uspri = 0; //P
		stan = 1;
		
		
	}
	
	Proces(Proces proces)
	{
		nr++;
		nazwa = "proces " + nr;
		PID = nr;
		PPID = proces.PID;
		pri = proces.pri; //P
		uspri = proces.uspri; //P
		stan = 1;
	}
	
}