package komunikacja_miedzy_procesami;

public class PipeField {

	private Pipe pipe = null;
	
	public PipeField(Pipe pipe){
		this.pipe = pipe;
		System.out.println("Utorzono uchwyt odczytu dla pipe pomiedzy procesem rodzica: "+pipe.parent.PID+" a procesem potomka: "+pipe.child.PID);
	}
	
	public String read() throws Exception{
		if(pipe.pipeListToChild.size()>0){
			String temp = pipe.pipeListToChild.get(0);
			System.out.println("Proces potomka: "+pipe.child.PID+" odczytał dane od procesu rodzica: "+pipe.parent.PID+" o treści: \""+temp+"\"");
			pipe.pipeListToChild.remove(0);
			System.out.println("Dane przeznaczone dla procesu potomka: "+pipe.child.PID+" o treści: \""+temp+"\" zostały usunięte z pipe");
			return temp;
		}
		throw new Exception("Kolejka jest pusta");
	}
	
	public int write(String data){
		pipe.pipeListToParrent.add(data);
		System.out.println("Proces potomka: "+pipe.child.PID+" zapisał dane dla procesu rodzica: "+pipe.parent.PID+" o treści: \""+data+"\"");
		return 0;
	}
	
}
