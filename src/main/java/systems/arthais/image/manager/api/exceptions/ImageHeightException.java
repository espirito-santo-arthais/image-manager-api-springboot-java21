package systems.arthais.image.manager.api.exceptions;

public class ImageHeightException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageHeightException(String message) {
		super(message);
	}

	public ImageHeightException() {
		super("The image height is not within the allowed limits.");
	}
}
