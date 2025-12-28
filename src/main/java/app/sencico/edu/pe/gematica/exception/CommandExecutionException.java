package app.sencico.edu.pe.gematica.exception;

public class CommandExecutionException extends RuntimeException{
	private static final long serialVersionUID = 3693487245964497682L;
	
	public CommandExecutionException(String message) {
        super(message);
    }
    
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
	

}
