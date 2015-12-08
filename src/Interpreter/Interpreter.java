package Interpreter;

import java.io.IOException;
import java.util.Scanner;



import obsluga_dysku.FlorekFileSystem;
import obsluga_procesora.Scheduler;
import pamiec_wirtualna.MemoryManagement;
import zarzadzanie_procesami.Management;
import zarzadzanie_procesami.Proces;

public class Interpreter{
	static int RA;
	static int RB;
	static int PC;
	static boolean ZF;
	static int  CPU;
	
	boolean exit = false;
	static boolean test = false;
	private int wynik;
	private int  przelicz;
	private String Decision;
	private String tmp;
	
	private static Scheduler scheduler;
	private static Management management;
	Proces tenproces;
	
	private String cmd = "ml RA,RB";
	private String arg1;
	private String arg2;
	
	Scanner input;
	
	//String program = "mv RA,05mi 50,RAmv BR,RAml BR,BA sb b,01j1 25et";

	public Interpreter() throws Exception{
		scheduler= new Scheduler();
		management = new Management();
		new MemoryManagement();
		
		input = new Scanner(System.in);
		start();
	}
	
	void start() throws Exception{
		test();
		while(true){
			exit = false;
			if(test || scheduler.change_context()){
					// pobranie kontekstu
				if(!test){
					RA = scheduler.pr_rdy.pRA;
					RB = scheduler.pr_rdy.pRB;
					PC = scheduler.pr_rdy.pPC;
					ZF = scheduler.pr_rdy.pZF;
				}
				while(!exit && CPU < 4 && (test || MemoryManagement.inMemory(PC, scheduler.pr_rdy.PID))){
					/*if(System.in.read() == 27)
						FlorekFileSystem.cmd();*/
					if(test){
						System.out.println("Podaj rozkaz: ");
						cmd = input.nextLine();
					}
					else{
					cmd = String.valueOf(MemoryManagement.readMemory(PC,8,scheduler.pr_rdy.PID)); //pobranie kolejnego rozkazu
					}
					if(cmd.length() >= 5)
						arg1 = cmd.substring(3, 5);
					if(cmd.length() >= 8)
						arg2 = cmd.substring(6, 8);
					if(cmd.length() < 2)  // brak rozkazu obsluga
						cmd = "  ";
					try{
					switch(cmd.substring(0, 2)){				//rozpoznawanie rozkazu i jego obsluga
					case "mi":				//zapisywanie/czytanie z pamieci operacyjnej arg1(rejestr lub adres) arg2(rejestr lub adres)
						mi(arg1,arg2);
						break;
					case "mv":				//przypisywanie wartosci do rejestru arg1(rejestr) arg2(wartosc lub rejestr)
						mv(arg1,arg2);
						break;
					case "ad":				//dodawanie
						ad(arg1,arg2);
						break;
					case "sb":				//odejmowanie
						sb(arg1,arg2);
						break;
					case "ml":				//mnozenie
						ml(arg1,arg2);
						break;
					case "j0":				//jump jezeli ZF == false arg1(adres)
						j0(arg1);
						break;
					case "j1":				//jump jezeli ZF == true arg1(adres)
						j1(arg1);
						break;
					case "fk":				//fork()
						tenproces = Management.fork(scheduler.pr_rdy);
						scheduler.add_to_ready(tenproces);
						Management.exec("Program1",tenproces.PID);
						//scheduler.add_to_ready(management.fork(scheduler.pr_rdy));
						break;
					case "ex":				//exec()
						
						break;
					case "et":				//zakonczenie wykonywania procesu
						if(test){
							test = false;
							System.out.println("Zakonczono testowanie.");
						}
						exit = true;
						et();
						break;
					case "wt":				//wait()
						wt();
						break;
					case "fm":				//tworzenie nowego pliku o nazwie PID porcesu
						fm();
						break;
					case "fr":				//odczyt z pliku arg1(rejestr)
						fr(arg1);
						break;
					case "fw":				//zapis do pliku arg1(rejestr
						fw(arg1);
						break;
					case "pr":				//pr rejestr,od PA(rodzic)/nr rury(potomek)
						pr(arg1, arg2);
						break;
					case "pw":				//pw PA(rodzic)/nr rury(potomek),rejestr
						pw(arg1, arg2);
						break;
					default:
						System.out.println(cmd + " - jest nierozpoznawalny");
						/*if(!test)
							management.exit();*/
						break;
					}
					}
					catch(NumberFormatException e){
						System.out.println("Nieznany format liczby");
						error_exit();
					}
					set_ZF();
					CPU++;
					przelicz++;
					
					if(przelicz == 12 && !test){
						przelicz = 0;
						System.out.println("Hurra1");
						if(scheduler.przelicz()){
							System.out.println("Hurra");
							break;
						}
					}
					aktualny_stan();
				}
				//zwrot kontekstu
				wywlaszczenie();
			}
		}
	}

	private void error_exit() {
		exit = true;
		if(!test)
			Management.exit(scheduler.pr_rdy.PID);
	}

	void mi(String arg1, String arg2){
		if (arg1.equals("RA")){
			//RA = wczytanie z pamieci operacyjnej

			RA = Integer.parseInt(String.valueOf(MemoryManagement.readMemory(Integer.parseInt(arg2,16),2,scheduler.pr_rdy.PID)),16);
			MemoryManagement.displayStatus();
		}
		else if(arg1.equals("RB")){
			//RB = wczytanie z pamieci operacyjnej
			RB = Integer.parseInt(String.valueOf(MemoryManagement.readMemory(Integer.parseInt(arg2,16),2,scheduler.pr_rdy.PID)),16);
			MemoryManagement.displayStatus();
		}
		else{
			//zapis do pamieci operacyjnej

			MemoryManagement.writeMemory(Integer.parseInt(arg1,16), arg2.toCharArray(), scheduler.pr_rdy.PID);
			MemoryManagement.displayStatus();
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}

	void mv(String arg1, String arg2){
			switch(arg1){
			case "RA":
				if(arg2.equals("RB"))
					wynik = RA = RB;
				else
					wynik = RA = Integer.parseInt(arg2, 16);
				break;
			case "RB":
				if(arg2.equals("RA"))
					wynik = RB = RA;
				else
					wynik = RB = Integer.parseInt(arg2, 16);
				break;
			default:
				System.out.println(cmd + " - jest nierozpoznawalny");
				error_exit();
				break;	
			}
		PC += 8; //zwiekszenie licznika rozkazow
	}
	
	void ad(String arg1, String arg2){
		switch(arg1){
		case "RA":
			if(arg2.equals("RB"))
				RA += RB;
			else
				RA += Integer.parseInt(arg2, 16);
			wynik = RA;
			break;
		case "RB":
			if(arg2.equals("RA"))
				RB += RA;
			else
				RB += Integer.parseInt(arg2, 16);
			wynik = RB;
			break;
		default:
			System.out.println(cmd + " - jest nierozpoznawalny");
			error_exit();
			break;
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}

	void sb(String arg1, String arg2){
		switch(arg1){
		case "RA":
			if(arg2.equals("RB"))
				RA -= RB;
			else
				RA -= Integer.parseInt(arg2, 16);
			wynik = RA;
			break;
		case "RB":
			if(arg2.equals("RA"))
				RB -= RA;
			else
				RB -= Integer.parseInt(arg2, 16);
			wynik = RB;
			break;
		default:
			System.out.println(cmd + " - jest nierozpoznawalny");
			error_exit();
			break;
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}

	void ml(String arg1, String arg2){
		switch(arg1){
		case "RA":
			if(arg2.equals("RB"))
				RA *= RB;
			else
				RA *= Integer.parseInt(arg2, 16);
			wynik = RA;
			break;
		case "RB":
			if(arg2.equals("RA"))
				RB *= RA;
			else
				RB *= Integer.parseInt(arg2, 16);
			wynik = RB;
			break;
		default:
			System.out.println(cmd + " - jest nierozpoznawalny");
			error_exit();
			break;
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}
	
	void j0(String arg1){
		if(ZF == false);
		PC = Integer.parseInt(arg1); 
	}
	
	void j1(String arg1){
		if(ZF == true);
		PC = Integer.parseInt(arg1); 
	}

	void pr(String arg1, String arg2) throws Exception{
		if(arg2.equals("PA")){
			switch(arg1){
				case "RA":
					RA = Integer.parseInt(scheduler.pr_rdy.childPipe.read(),16);
					break;
				case "RB":
					RB = Integer.parseInt(scheduler.pr_rdy.childPipe.read(),16);
					break;
				default:
					System.out.println(cmd + " - jest nierozpoznawalny");
					error_exit();
					break;
			}
		}
		else{
			switch(arg1){
			case "RA":
				RA = Integer.parseInt(scheduler.pr_rdy.pipes.get(Integer.parseInt(arg1,16)).read(),16);
				break;
			case "RB":
				RB = Integer.parseInt(scheduler.pr_rdy.pipes.get(Integer.parseInt(arg1,16)).read(),16);
				break;
			default:
				System.out.println(cmd + " - jest nierozpoznawalny");
				error_exit();
				break;
			}
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}
	
	void pw(String arg1, String arg2){
		if(arg1.equals("PA")){
			if(arg2.equals("RA"))
				scheduler.pr_rdy.childPipe.write(Integer.toString(RA));
			else if(arg2.equals("RB"))
				scheduler.pr_rdy.childPipe.write(Integer.toString(RB));
			else
				scheduler.pr_rdy.childPipe.write(arg2);
		}
		else{
			if(arg2.equals("RA"))
				scheduler.pr_rdy.pipes.get(Integer.parseInt(arg1)).write(Integer.toString(RA));
			else if(arg2.equals("RB"))
				scheduler.pr_rdy.pipes.get(Integer.parseInt(arg1)).write(Integer.toString(RB));
			else
				scheduler.pr_rdy.pipes.get(Integer.parseInt(arg1)).write(arg2);
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}
	
	void fm(){
		scheduler.pr_rdy.nazwa_pliku = String.valueOf("plik"+scheduler.pr_rdy.PID);
		FlorekFileSystem.Create_File(scheduler.pr_rdy.nazwa_pliku, "");
		PC += 2; //zwiekszenie licznika rozkazow
	}
	
	void fr(String arg1){
		try{
		String tmp = String.valueOf(FlorekFileSystem.F_Read(scheduler.pr_rdy.nazwa_pliku));
		System.out.println(tmp);
		switch(arg1){
		case "RA":
			RA = Integer.parseInt(tmp.substring(tmp.length()-2, tmp.length()),16);
			break;
		case "RB":
			RB =  Integer.parseInt(tmp.substring(tmp.length()-2, tmp.length()),16);
			break;
		default:
			System.out.println(cmd + " - jest nierozpoznawalny");
			error_exit();
			break;
		}
		}catch(Exception e){
			System.out.println("Plik nie istnieje");
			error_exit();
		}
		PC += 5; //zwiekszenie licznika rozkazow
	}
	
	void fw(String arg1){
		switch(arg1){
		case "RA":
			if(RA < 16)
				tmp = "0" + String.valueOf(Integer.toHexString(RA));
			else 
				tmp = String.valueOf(Integer.toHexString(RA));
			FlorekFileSystem.F_Write(scheduler.pr_rdy.nazwa_pliku, tmp);
			break;
		case "RB":
			if(RB < 16)
				tmp = "0" + String.valueOf(Integer.toHexString(RB));
			else 
				tmp = String.valueOf(Integer.toHexString(RB));
			FlorekFileSystem.F_Write(scheduler.pr_rdy.nazwa_pliku, tmp);
			break;
		default:
			System.out.println(cmd + " - jest nierozpoznawalny");
			error_exit();
			break;
		}		
		PC += 5; //zwiekszenie licznika rozkazow
	}
	
	void aktualny_stan() throws IOException{
		System.out.println("Stan Interpretera:");
		System.out.println("RA = " + RA + ", RB = " + RB + ", ZF = " + ZF + ", PC = " + PC + ", CPU = " + CPU);
		if(!test){
			Decision = " ";
			while(!Decision.equals("")){
				System.out.println("Podaj komende lub wcisnij ENTER aby kontynuowac");
				Decision = input.nextLine();
				FlorekFileSystem.Disk_Command(Decision);
			}
		}
	}
	
	void et()
	{
		scheduler.pr_rdy.stan = 4;
		if(Management.exit(scheduler.pr_rdy.PID))
			scheduler.wakeup(scheduler.pr_rdy.PPID);	
		scheduler.remove(scheduler.pr_rdy);
		PC += 2; //zwiekszenie licznika rozkazow
	}
	
	void wt()
	{
		int tmp = management.wait(scheduler.pr_rdy);
		if(tmp == 0)
		{
			scheduler.add_to_wait(scheduler.pr_rdy);
		}
		else if(tmp == -1)
		{
			//obsluga bledu metody wait
		}
		else if(tmp > 0)
		{
			//tmp posiada pid nieistniejacego juz potomka
		}
		PC += 2; //zwiekszenie licznika rozkazow
	}
	
	void set_ZF(){
		if(wynik == 0)
			ZF = true;
		else 
			ZF = false;
	}
	
	public static void test(){
		test = true;
		scheduler.add_to_ready_test(Management.fork(Management.procesList.get(0)));
		scheduler.change_context();
		RA = 0;
		RB = 0;
		PC = 0;
	}

	void wywlaszczenie(){
		if(!test){
			scheduler.pr_rdy.pRA = RA;
			scheduler.pr_rdy.pRB = RB;
			scheduler.pr_rdy.pPC = PC;
			scheduler.pr_rdy.pZF = ZF;
			scheduler.pr_rdy.cpu +=CPU;
		}
		CPU = 0;
	}

}