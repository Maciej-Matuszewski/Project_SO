package zarzadzanie_procesami;

import java.util.Random;

import Interpreter.Output;

import java.util.ArrayList;

import komunikacja_miedzy_procesami.Pipe;
import komunikacja_miedzy_procesami.PipeField;
import pamiec_wirtualna.*;

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
	// STAN 5 - ZOMBIE
	Random random = new Random();
	public PageTable ptable = new PageTable();
	public ArrayList<Pipe> pipes = new ArrayList<>();
	public PipeField childPipe = null;
	

	public int pri; //P
	public int uspri; //P
	public int nice; //P parametr nadawany przez uzytkownika
	public int cpu; //P wykorzystanie procesora
	public int pRA;  //P
	public int pRB;  //P
	public int pPC;  //P
	public boolean pZF;  //P
	public String nazwa_pliku;
	public String codeFile;
	
	Proces()
	{
		nr++;
		nazwa = "root(Init)";
		PID = nr;
		pri = 0; //P
		uspri = 0; //P
		stan = 0;
		Output.write("Powstal proces systemowy - root(Init)");
	}
	
	Proces(Proces proces)
	{
		nr++;
		nazwa = "proces " + nr;
		PID = nr;
		PPID = proces.PID;
		uspri = 8+nice; //P
		pri = uspri; //P
		stan = 1;
		pRA = proces.pRA;
		pRB = proces.pRB;
		pPC = proces.pPC;
		pZF = proces.pZF;
		
		Output.write("Powstal: " + this.nazwa + ", Od procesu: " + proces.nazwa);
		nazwa_pliku = proces.nazwa_pliku;
		try {
			new Pipe(proces, this);
		} catch (Exception e) {
			Output.write("Error: " + e.getMessage());
		}
	}
	
}
