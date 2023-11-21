package systems.arthais.image.manager.api.exceptions;

public class ImageDensityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageDensityException(String message) {
		super(message);
	}

	public ImageDensityException() {
		super("A densidade da imagem não está dentro dos limites permitidos.");
	}

}
