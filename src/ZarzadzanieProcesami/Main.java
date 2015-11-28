package ZarzadzanieProcesami;

public class Main {

	public static void main(String[] args) 
	{
		Management man = new Management();
		
		man.fork();
		man.kill();
		man.exit();
		man.fork(man.procesList.get(0));
		System.out.println("Jego PID: " + man.procesList.get(1).PID);
		System.out.println("Jego PPID: " + man.procesList.get(1).PPID);
		System.out.println("Jego Priorytet: " + man.procesList.get(1).priorytet);
		
		System.out.println("");
		man.ProcessPrint();
	}
}