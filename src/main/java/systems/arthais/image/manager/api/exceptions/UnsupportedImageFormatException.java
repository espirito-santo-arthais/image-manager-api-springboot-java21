package systems.arthais.image.manager.api.exceptions;

public class UnsupportedImageFormatException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedImageFormatException(String message) {
		super(message);
	}

	public UnsupportedImageFormatException() {
		super("The image file format is not supported.");
	}
}
