package systems.arthais.image.manager.api.exceptions;

public class ImageHeightException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageHeightException(String message) {
		super(message);
	}

	public ImageHeightException() {
		super("A altura da imagem não está dentro dos limites permitidos.");
	}

}
