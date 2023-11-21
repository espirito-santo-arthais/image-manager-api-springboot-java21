package systems.arthais.image.manager.api.exceptions;

public class UnsupportedImageFormatException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedImageFormatException(String message) {
		super(message);
	}

	public UnsupportedImageFormatException() {
		super("O formato de arquivo da imagem não é suportado.");
	}
}
