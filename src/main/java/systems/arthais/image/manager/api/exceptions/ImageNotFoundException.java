package systems.arthais.image.manager.api.exceptions;

public class ImageNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageNotFoundException(String message) {
		super(message);
	}

	public ImageNotFoundException() {
		super("The image was not found.");
	}
}
