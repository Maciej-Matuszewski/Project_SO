import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//JOptionPane.showMessageDialog(null, "Hello world\nPrzemo tu byl!");
		Proces p1 = new Proces();
		Proces p2 = new Proces();
		Proces p3 = new Proces();
		p1.pri = 0;
		p2.pri = 3;
		p3.pri = 2;
		Proces p4 = new Proces();
		p4.pri = 2;
		
		Scheduler sch = new Scheduler();
		sch.add_to_ready(p1);
		for(int i = 0; i<sch.qs.get(0).size();i++)
			System.out.println(sch.qs.get(0).get(i).pri+" "+sch.qs.get(0).get(i).PID);
		
		System.out.println();
		
		sch.add_to_ready(p2);
		for(int i = 0; i<sch.qs.get(0).size();i++)
			System.out.println(sch.qs.get(0).get(i).pri+" "+sch.qs.get(0).get(i).PID);
		
		System.out.println();

		
		sch.add_to_ready(p3);
		for(int i = 0; i<sch.qs.get(0).size();i++)
			System.out.println(sch.qs.get(0).get(i).pri+" "+sch.qs.get(0).get(i).PID);
		
		System.out.println();

		
		sch.add_to_ready(p4);
		for(int i = 0; i<sch.qs.get(0).size();i++)
			System.out.println(sch.qs.get(0).get(i).pri+" "+sch.qs.get(0).get(i).PID);
		
		sch.przelicz();

		

	}

}
