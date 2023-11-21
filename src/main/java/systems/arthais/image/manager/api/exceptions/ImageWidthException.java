package systems.arthais.image.manager.api.exceptions;

public class ImageWidthException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageWidthException(String message) {
		super(message);
	}

	public ImageWidthException() {
		super("A largura da imagem não está dentro dos limites permitidos.");
	}

}
