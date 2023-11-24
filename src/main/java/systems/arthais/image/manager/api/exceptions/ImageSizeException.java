package systems.arthais.image.manager.api.exceptions;

public class ImageSizeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageSizeException(String message) {
		super(message);
	}

	public ImageSizeException() {
		super("The image size exceeds the allowed limit.");
	}
}
