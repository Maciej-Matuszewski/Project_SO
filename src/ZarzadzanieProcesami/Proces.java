package ZarzadzanieProcesami;

public class Proces {

	public static int nr = 0;
	String nazwa;
	int ID;
	int IDR;
	
	

	
	
	Proces()
	{
		nr++;
		nazwa = "proces " + nr;
		ID = nr;
	}
	
	Proces(Proces proces)
	{
		nr++;
		nazwa = "proces potomny od: " + proces.nazwa;
		ID = nr;
		IDR = proces.ID;
	}
	
}
