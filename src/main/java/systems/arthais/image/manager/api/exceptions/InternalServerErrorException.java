package systems.arthais.image.manager.api.exceptions;

public class InternalServerErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String message) {
		super(message);
	}

	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InternalServerErrorException() {
	    super("The operation could not be performed.");
	}

	public InternalServerErrorException(Throwable cause) {
	    super("The operation could not be performed.", cause);
	}
}
