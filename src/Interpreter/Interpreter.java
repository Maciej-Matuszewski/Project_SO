package Interpreter;

import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import obsluga_dysku.FlorekFileSystem;
import obsluga_procesora.Scheduler;
import pamiec_wirtualna.MemoryManagement;
import zarzadzanie_procesami.Management;
import komunikacja_miedzy_procesami.Pipe;

public class Interpreter extends FlorekFileSystem {
	static int RA;
	static int RB;
	static int PC;
	static boolean CF;
	static int  CPU;
	
	boolean test = false;
	private int wynik;
	private int  przelicz;
	
	private Pipe pipe;
	private Scheduler scheduler;
	private Management management;
	private MemoryManagement MemManagement;
	
	private String cmd = "ml RA,RB";
	private String arg1;
	private String arg2;
	
	Scanner input;
	
	//String program = "mv RA,05mi 50,RAmv BR,RAml BR,BA sb b,01j1 25et";

	public Interpreter() throws Exception{
		//pipe = new Pipe(null, null);
		scheduler= new Scheduler();
		management = new Management();
		MemManagement = new MemoryManagement();
		management.fork();
		management.fork();
		management.fork();
		scheduler.add_to_ready(management.procesList.get(0));
		scheduler.add_to_ready(management.procesList.get(1));
		scheduler.add_to_ready(management.procesList.get(2));
		
		input = new Scanner(System.in);
		start();
	}
	
	public void start() throws IOException{
		test();
		while(true){
			if(test || scheduler.change_context()){
					// pobranie kontekstu
				if(!test){
					RA = scheduler.pr_rdy.pRA;
					RB = scheduler.pr_rdy.pRB;
					PC = scheduler.pr_rdy.pPC;
					CF = scheduler.pr_rdy.pCF;
				}
				
				while(CPU < 4){
					if(test){
						System.out.println("Podaj rozkaz: ");
						cmd = input.nextLine();
					}
					else{
					cmd = String.valueOf(MemManagement.readMemory(PC,8,scheduler.pr_rdy.PID)); //pobranie kolejnego rozkazu
					}
					if(cmd.length() >= 5)
						arg1 = cmd.substring(3, 5);
					if(cmd.length() >= 8)
						arg2 = cmd.substring(6, 8);
					if(cmd.length() < 2)  // brak rozkazu obsluga
						cmd = "  ";
					try{
					switch(cmd.substring(0, 2)){				//rozpoznawanie rozkazu i jego obsluga
					case "mi":
						mi(arg1,arg2);
						break;
					case "mv":
						mv(arg1,arg2);
						break;
					case "ad":
						ad(arg1,arg2);
						break;
					case "sb":
						sb(arg1,arg2);
						break;
					case "ml":
						ml(arg1,arg2);
						break;
					case "j0":
						j0(arg1);
						break;
					case "j1":
						j1(arg1);
						break;
					case "fk":
						management.fork();
						break;
					case "ex":
						management.exec();
						break;
					case "et":
						if(test){
							test = false;
							System.out.println("Zakonczono testowanie.");
						}
						management.exit();
						break;
					case "fr":
						break;
					case "fw":
						break;
					case "pc":
						
						break;
					case "pr":
						break;
					case "pw":
						break;
					default:
						System.out.println(cmd + " - jest nierozpoznawalny");
						if(!test)
							management.exit();
						break;
					}
					}
					catch(NumberFormatException e){
						System.out.println("Nieznany format liczby");
						if(!test)
							management.exit();
					}
					set_CF();
					CPU++;
					przelicz++;
					
					if(przelicz == 12 && !test){
						przelicz = 0;
						if(scheduler.przelicz()){
							System.out.println("Hurra");
							break;
						}
					}
					
					aktualny_stan();
					
				}
				//zwrot kontekstu
				if(!test){
					scheduler.pr_rdy.pRA = RA;
					scheduler.pr_rdy.pRB = RB;
					scheduler.pr_rdy.pPC = PC;
					scheduler.pr_rdy.pCF = CF;
					scheduler.pr_rdy.cpu +=CPU;
				}
				CPU = 0;
			}
		}
	}

	void mi(String arg1, String arg2){
		if (arg1 == "RA"){
			//RA = wczytanie z pamieci operacyjnej
			RA = Integer.parseInt(String.valueOf(MemManagement.readMemory(Integer.parseInt(arg2,16),8,scheduler.pr_rdy.PID)),16);
		}
		else if(arg2 == "RB"){
			//RB = wczytanie z pamieci operacyjnej
			RB = Integer.parseInt(String.valueOf(MemManagement.readMemory(Integer.parseInt(arg2,16),8,scheduler.pr_rdy.PID)),16);
		}
		else{
			//zapis do pamieci operacyjnej
			MemManagement.writeMemory(Integer.parseInt(arg1,16), arg2.toCharArray(), scheduler.pr_rdy.PID);
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
				if(!test)
					management.exit();
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
			if(!test)
				management.exit();
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
			if(!test)
				management.exit();
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
			if(!test)
				management.exit();
			break;
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}
	
	void j0(String arg1){
		if(CF == false);
		PC = Integer.parseInt(arg1); 
	}
	
	void j1(String arg1){
		if(CF == true);
		PC = Integer.parseInt(arg1); 
	}
	
	void aktualny_stan() throws IOException{
		System.out.println("Stan Interpretera:");
		System.out.println("RA = " + RA + ", RB = " + RB + ", CF = " + CF + ", CPU = " + CPU);
		if(!test){
			System.out.println("ENTER aby kontynuowac");
			if(System.in.read() == 13);
		}
	}
	
	void set_CF(){
		if(wynik == 0)
			CF = true;
		else 
			CF = false;
	}
	
	public void test(){
		test = true;
		management.fork();
		RA = 0;
		RB = 0;
		PC = 0;
	}
}