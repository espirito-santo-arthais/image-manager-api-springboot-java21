package systems.arthais.image.manager.api.exceptions;

public class ImageSizeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageSizeException(String message) {
		super(message);
	}

	public ImageSizeException() {
		super("A quantidade de bytes da imagem não está dentro dos limites permitidos.");
	}

}
