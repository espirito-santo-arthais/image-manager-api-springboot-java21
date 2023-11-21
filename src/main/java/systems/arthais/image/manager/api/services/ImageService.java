package systems.arthais.image.manager.api.services;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import systems.arthais.image.manager.api.exceptions.ImageSizeException;
import systems.arthais.image.manager.api.exceptions.UnsupportedImageFormatException;
import systems.arthais.image.manager.api.models.ImageData;

@Service
public class ImageService {

	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/svg+xml");
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
	private static final long ALLOWED_MIN_HEIGHT = 800;
	private static final long ALLOWED_MAX_HEIGHT = 800;
	private static final long ALLOWED_MIN_WIDTH = 800;
	private static final long ALLOWED_MAX_WIDTH = 800;
	private static final long ALLOWED_MIN_DENSITY = 800;
	private static final long ALLOWED_MAX_DENSITY = 800;

	public UUID uploadImage(MultipartFile imageFile) {
		validateImage(imageFile);
		UUID imageId = UUID.randomUUID();
		// Lógica para salvar a imagem
		if (!isContentTypeAllowed(imageFile)) {
			return ResponseEntity.badRequest()
					.body("Formato de arquivo não suportado. Formatos válidos: " + ALLOWED_CONTENT_TYPES);
		}
		if (!isFileSizeValid(imageFile)) {
			return ResponseEntity.badRequest()
					.body("Tamanho do arquivo excede o limite de " + (MAX_FILE_SIZE / 1024 / 1024) + "MB.");
		}

		UUID imageId = UUID.randomUUID(); // Gerar UUID único para a imagem
		// Lógica para processar o upload da imagem
		return imageId;
	}

	public void updateImage(UUID id, MultipartFile imageFile) {
		validateImage(imageFile);
		// Lógica para atualizar a imagem

		// Lógica para atualizar a imagem com base no UUID
	}

	public void deleteImage(UUID id) {
		// Verificar se a imagem existe
		// Lógica para excluir a imagem
	}

	public ImageData getImage(UUID id) {
		// Verificar se a imagem existe
		// Lógica para recuperar a imagem
		// Lógica para recuperar a imagem com base no UUID
		String contentType = "";
		InputStream imageStream = null; // Obter o InputStream da imagem
		// Sua lógica para encontrar a imagem
		// Retorna um novo objeto ImageData com os dados da imagem e o tipo de conteúdo
		return new ImageData(imageStream, contentType); // Exemplo
	}

	// Métodos privados

	private void validateImage(MultipartFile imageFile) {
		if (!isContentTypeAllowed(imageFile)) {
			throw new UnsupportedImageFormatException(
					"O formato de arquivo da imagem não é suportado. Formatos válidos: " + ALLOWED_CONTENT_TYPES);
		}
		if (!isSizeValid(imageFile)) {
			throw new ImageSizeException(
					"A quantidade de bytes da imagem excedeu o limite permitido: " + MAX_FILE_SIZE + " bytes.");
		}
		if (!isHeightValid(imageFile)) {
			throw new ImageSizeException(
					"A altura da imagem não está dentro dos limites permitidos. Limites permitidos: " + ALLOWED_MIN_HEIGHT + " X " + ALLOWED_MAX_HEIGHT + "pixels.");
		}
		if (!isWidthValid(imageFile)) {
			throw new ImageSizeException(
					"A largura da imagem não está dentro dos limites permitidos. Limites permitidos: " + ALLOWED_MIN_WIDTH + " X " + ALLOWED_MAX_WIDTH + "pixels.");
		}
		if (!isDensityValid(imageFile)) {
			throw new ImageSizeException(
					"A densidade da imagem não está dentro dos limites permitidos. Limites permitidos: " + ALLOWED_MIN_DENSITY + " X " + ALLOWED_MAX_DENSITY + "pixels.");
		}
	}

	private boolean isContentTypeAllowed(MultipartFile file) {
		return ALLOWED_CONTENT_TYPES.contains(file.getContentType());
	}

	private boolean isSizeValid(MultipartFile file) {
		return file.getSize() <= MAX_FILE_SIZE;
	}

	private boolean isHeightValid(MultipartFile file) {
		return file.getSize() >= ALLOWED_MIN_HEIGHT && file.getSize() <= ALLOWED_MAX_HEIGHT;
	}

	private boolean isWidthValid(MultipartFile file) {
		return file.getSize() >= ALLOWED_MIN_WIDTH && file.getSize() <= ALLOWED_MAX_WIDTH;
	}

	private boolean isDensityValid(MultipartFile file) {
		return file.getSize() >= ALLOWED_MIN_DENSITY && file.getSize() <= ALLOWED_MAX_DENSITY;
	}

}
