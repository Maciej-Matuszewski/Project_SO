package komunikacja_miedzy_procesami;

import java.util.ArrayList;

import ZarzadzanieProcesami.Proces;

public class Pipe {

	ArrayList<String> pipeListToParrent = new ArrayList<>();
	ArrayList<String> pipeListToChild = new ArrayList<>();
	private Proces parent;
	private Proces child;

	public Pipe(Proces parentProces, Proces childProces) throws Exception {
		if(parentProces.PID != childProces.PPID) throw new Exception("Te procesy nie sa w relacji RODZIC-POTOMEK");
		else{
			this.child = childProces;
			this.parent = parentProces;
			parentProces.pipes.add(this);
			childProces.childPipe = new PipeField(this);
		}
	}
	
	public void write(String data){
		this.pipeListToChild.add(data);
	}
	
	public String read() throws Exception{
		if(this.pipeListToParrent.size()>0){
			String temp = this.pipeListToParrent.get(0);
			this.pipeListToParrent.remove(0);
			return temp;
		}
		throw new Exception("Kolejka jest pusta");
	}
	
	public void close(){
		child.childPipe = null;
		for(int i = 0; i < parent.pipes.size();i++){
			if(parent.pipes.get(i).equals(this)){
				parent.pipes.remove(i);
				break;
			}
		}
	}
	
	public boolean connectedWithChild(int childPID){
		return this.child.PID == childPID ? true : false;
	}
	
		
}