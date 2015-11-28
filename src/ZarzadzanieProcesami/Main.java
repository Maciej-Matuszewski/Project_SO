package ZarzadzanieProcesami;

public class Main {

	public static void main(String[] args) 
	{
		Management man = new Management();
		
		man.fork();
		man.exit();
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
		
		System.out.println(man.Is_parent(man.procesList.get(0)));
		System.out.println(man.Is_parent(man.procesList.get(3)));
		
		man.procesList.get(0).stan = 4;
		
		man.exit();
		
		System.out.println("");
		man.ProcessPrint();
		System.out.println("");
	}
}