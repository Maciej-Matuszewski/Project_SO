package komunikacja_miedzy_procesami;

public class PipeField {

	private Pipe pipe = null;
	
	public PipeField(Pipe pipe){
		this.pipe = pipe;
	}
	
	public String read() throws Exception{
		if(pipe.pipeListToChild.size()>0){
			String temp = pipe.pipeListToChild.get(0);
			pipe.pipeListToChild.remove(0);
			return temp;
		}
		throw new Exception("Kolejka jest pusta");
	}
	
	public int write(String data){
		pipe.pipeListToParrent.add(data);
		return 0;
	}
	
}
