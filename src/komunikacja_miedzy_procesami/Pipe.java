package komunikacja_miedzy_procesami;

import java.util.ArrayList;

import Interpreter.Output;
import zarzadzanie_procesami.Proces;

public class Pipe {

	ArrayList<String> pipeListToParrent = new ArrayList<>();
	ArrayList<String> pipeListToChild = new ArrayList<>();
	protected Proces parent;
	protected Proces child;

	public Pipe(Proces parentProces, Proces childProces) throws Exception {
		if(parentProces.PID != childProces.PPID) throw new Exception("Te procesy nie sa w relacji RODZIC-POTOMEK");
		else{
			this.child = childProces;
			this.parent = parentProces;
			parentProces.pipes.add(this);
			childProces.childPipe = new PipeField(this);
			Output.write("Utorzono pipe pomiedzy procesem rodzica: "+parent.PID+" a procesem potomka: "+child.PID);
		}
	}
	
	public void write(String data){
		this.pipeListToChild.add(data);

		Output.write("Proces rodzica: "+parent.PID+" zapisał dane dla procesu potomka: "+child.PID+" o treści: \""+data+"\"");
	}
	
	public String read() throws Exception{
		if(this.pipeListToParrent.size()>0){
			String temp = this.pipeListToParrent.get(0);
			Output.write("Proces rodzica: "+parent.PID+" odczytał dane od procesu potomka: "+child.PID+" o treści: \""+temp+"\"");
			this.pipeListToParrent.remove(0);
			Output.write("Dane przeznaczone dla procesu rodzica: "+parent.PID+" o treści: \""+temp+"\" zostały usunięte z pipe");
			return temp;
		}
		throw new Exception("Kolejka jest pusta");
	}
	
	public void close(){
		child.childPipe = null;
		for(int i = 0; i < parent.pipes.size();i++){
			if(parent.pipes.get(i).equals(this)){
				parent.pipes.remove(i);
				Output.write("Zamknięto pipe pomiedzy procesem rodzica: "+parent.PID+" a procesem potomka: "+child.PID);
				break;
			}
		}
	}
	
	public boolean connectedWithChild(int childPID){
		return this.child.PID == childPID ? true : false;
	}
	
		
}