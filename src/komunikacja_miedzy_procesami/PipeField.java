package komunikacja_miedzy_procesami;

import Interpreter.Output;

public class PipeField {

	private Pipe pipe = null;
	
	public PipeField(Pipe pipe){
		this.pipe = pipe;
		Output.write("Utorzono uchwyt odczytu dla pipe pomiedzy procesem rodzica: "+pipe.parent.PID+" a procesem potomka: "+pipe.child.PID);
	}
	
	public String read() throws Exception{
		if(pipe.pipeListToChild.size()>0){
			String temp = pipe.pipeListToChild.get(0);
			Output.write("Proces potomka: "+pipe.child.PID+" odczytał dane od procesu rodzica: "+pipe.parent.PID+" o treści: \""+temp+"\"");
			pipe.pipeListToChild.remove(0);
			Output.write("Dane przeznaczone dla procesu potomka: "+pipe.child.PID+" o treści: \""+temp+"\" zostały usunięte z pipe");
			return temp;
		}
		throw new Exception("Kolejka jest pusta");
	}
	
	public int write(String data){
		pipe.pipeListToParrent.add(data);
		Output.write("Proces potomka: "+pipe.child.PID+" zapisał dane dla procesu rodzica: "+pipe.parent.PID+" o treści: \""+data+"\"");
		return 0;
	}
	
}
