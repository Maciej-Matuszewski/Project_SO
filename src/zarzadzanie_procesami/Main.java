package zarzadzanie_procesami;

public class Main {

	public static void main(String[] args) 
	{
		Management man = new Management();
		
		man.fork();
		man.exec();
		
		man.fork(man.procesList.get(0));
		
		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
		
		man.kill(2);
		
		man.ProcessPrint();
		System.out.println("");
		
		man.fork();
		man.fork(man.procesList.get(0));
		man.fork(man.procesList.get(1));
		
		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
				
		man.procesList.get(1).stan = 4;
		
		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
		
		man.exit();
		
		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
		
		man.fork(man.procesList.get(man.FindProces(4)));
		man.fork(man.procesList.get(man.FindProces(7)));
		man.fork(man.procesList.get(man.FindProces(8)));

		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
		
		man.procesList.get(man.FindProces(7)).stan = 4;
		man.exit();
		
		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
	}
}