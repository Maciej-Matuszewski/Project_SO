import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//JOptionPane.showMessageDialog(null, "Hello world\nPrzemo tu byl!");
		Proces abc = new Proces();
		Scheduler sch = new Scheduler();
		abc.nice = 20;
		System.out.println(abc.nice);
		sch.add_to_ready(abc);
		sch.qs.get(0).get(0).nice = 100;
		System.out.println(abc.nice);
		sch.przelicz();
		System.out.println(abc.nice);
		

	}

}
