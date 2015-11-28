package komunikacja_miedzy_procesami;

import java.util.ArrayList;

public class Pipe {
	
	private ArrayList<String> pipeList;
	private int childPID; 

	public Pipe() {
		pipeList = new ArrayList<>();
	}
	
	public static int write(Pipe p, String data){
		
		p.pipeList.add(data);
		return 0;
	}
	
	public static String read(Pipe p){
		if(p.pipeList.size()>0){
			String temp = p.pipeList.get(0);
			p.pipeList.remove(0);
			return temp;
		}
		return null;
	}	
}