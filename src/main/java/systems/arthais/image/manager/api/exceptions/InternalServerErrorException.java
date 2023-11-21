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
		super("Não foi possível realizar a operação.");
	}

	public InternalServerErrorException(Throwable cause) {
		super("Não foi possível realizar a operação.", cause);
	}

}
