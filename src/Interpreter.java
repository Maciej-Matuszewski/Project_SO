

public class Interpreter {
	private int RA = 63;
	private int RB;
	private int PC;
	private boolean CF;
	private int  CPU;
	
	
	
	Interpreter(Scheduler sch){
		int przelicz = 0;
		String cmd = "mv RB,50";
		String arg1;
		String arg2;
		while(true){
			if(sch.change_context())
			{
				RA = sch.pr_rdy.pRA;
				RB = sch.pr_rdy.pRB;
				PC = sch.pr_rdy.pPC;
				CF = sch.pr_rdy.pCF;
			}
			while(CPU < 3){
				//pobranie kolejnego rozkazu
				arg1 = cmd.substring(3, 5);
				arg2 = cmd.substring(6, 8);
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
					cmd = j0(arg1);
					break;
				case "j1":
					cmd = j1(arg1);
					break;
				case "fk":
					break;
				case "ex":
					break;
				case "et":
					break;
				case "fr":
					break;
				case "fw":
					break;
				}
				CPU++; przelicz++;
				System.out.println("RA = " + RA + ", RB = " + RB + ", CF =" + CF + ", CPU = " + CPU);
			}
			sch.pr_rdy.pRA = RA;
			sch.pr_rdy.pRB = RB;
			sch.pr_rdy.pPC = PC;
			sch.pr_rdy.pCF = CF;
			sch.pr_rdy.cpu +=CPU;
			//zwrot kontekstu
			CPU = 0;
		}
	}

	void mi(String arg1, String arg2){
		if (arg1 == "RA"){
			RA += Integer.parseInt(arg2, 16);
		}
		else if(arg2 == "RB"){
			RB += Integer.parseInt(arg1, 16);
		}
		else{
			//zapis do pamieci operacyjnej
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}

	void mv(String arg1, String arg2){
			switch(arg1){
			case "RA":
				if(arg2.equals("RB"))
					RA = RB;
				else
					RA = Integer.parseInt(arg2, 16);
				break;
			case "RB":
				if(arg2.equals("RA"))
					RB = RA;
				else
					RB = Integer.parseInt(arg2, 16);
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
			break;
		case "RB":
			if(arg2.equals("RA"))
				RB += RA;
			else
				RB += Integer.parseInt(arg2, 16);
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
			break;
		case "RB":
			if(arg2.equals("RA"))
				RB -= RA;
			else
				RB -= Integer.parseInt(arg2, 16);
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
			break;
		case "RB":
			if(arg2.equals("RA"))
				RB *= RA;
			else
				RB *= Integer.parseInt(arg2, 16);
			break;
		}
		PC += 8; //zwiekszenie licznika rozkazow
	}
	
	String j0(String arg1){
		if(CF == false);
		//wczytanie rozkazu z pod adresu arg1
		PC += 5; //zwiekszenie licznika rozkazow
		return "rozkaz";
	}
	
	String j1(String arg1){
		if(CF == true);
		//wczytanie rozkazu z pod adresu arg1
		PC += 5; //zwiekszenie licznika rozkazow
		return "rozkaz";
	}
	
	void et(){
		//zakonczenie procesu
		PC += 2; //zwiekszenie licznika rozkazow
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scheduler sch = new Scheduler();
		Interpreter a = new Interpreter(sch);
	}

}