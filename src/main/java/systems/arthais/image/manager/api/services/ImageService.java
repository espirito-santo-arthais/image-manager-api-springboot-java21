package systems.arthais.image.manager.api.services;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import systems.arthais.image.manager.api.exceptions.ImageHeightException;
import systems.arthais.image.manager.api.exceptions.ImageNotFoundException;
import systems.arthais.image.manager.api.exceptions.ImageSizeException;
import systems.arthais.image.manager.api.exceptions.ImageWidthException;
import systems.arthais.image.manager.api.exceptions.InternalServerErrorException;
import systems.arthais.image.manager.api.exceptions.UnsupportedImageFormatException;
import systems.arthais.image.manager.api.models.ImageData;

@Service
@Slf4j
public class ImageService {

	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/svg+xml");
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".png", ".svg");
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
	private static final long ALLOWED_MIN_HEIGHT = 512;
	private static final long ALLOWED_MAX_HEIGHT = 4096;
	private static final long ALLOWED_MIN_WIDTH = 512;
	private static final long ALLOWED_MAX_WIDTH = 4096;
	private static final String BASE_PATH = "C:\\Workspaces\\Growth7\\images\\";

	public UUID uploadImage(MultipartFile imageFile) {
		log.info("Iniciando o salvamento da imagem...");

		validateImage(imageFile);

		UUID id = UUID.randomUUID();

		try {
			byte[] bytes = imageFile.getBytes();
			Path path = Paths.get(BASE_PATH + id.toString() + getExtension(imageFile.getContentType()));
			Files.write(path, bytes);
			log.info("Imagem salva com sucesso. UUID: {}", id);
		} catch (Exception ex) {
			log.error("Erro ao salvar a imagem: {}", ex.getMessage());
			throw new InternalServerErrorException("Não foi possível salvar a imagem.", ex);
		}

		return id;
	}

	public void updateImage(UUID id, MultipartFile imageFile) {
		log.info("Iniciando a substituição da imagem. UUID: {}", id);

		validateImage(imageFile);

		Path path = searchImagePath(id);

		try {
			byte[] bytes = imageFile.getBytes();
			Files.write(path, bytes);
			log.info("Imagem substituída com sucesso.");
		} catch (Exception ex) {
			log.error("Erro ao substituir a imagem: {}", ex.getMessage());
			throw new InternalServerErrorException("Não foi possível atualizar a imagem.", ex);
		}
	}

	public void deleteImage(UUID id) {
		log.info("Iniciando a exclusão da imagem. UUID: {}", id);

		Path path = searchImagePath(id);

		try {
			Files.delete(path);
			log.info("Imagem excluída com sucesso.");
		} catch (Exception ex) {
			log.error("Erro ao excluir a imagem: {}", ex.getMessage());
			throw new InternalServerErrorException("Não foi possível excluir a imagem.", ex);
		}
	}

	public ImageData getImage(UUID id) {
		log.info("Iniciando a obtenção da imagem. UUID: {}", id);

		Path path = searchImagePath(id);

		try {
			String contentType = Files.probeContentType(path);
			InputStream imageStream = new FileInputStream(path.toFile());

			log.info("Imagem obtida com sucesso.");
			return new ImageData(imageStream, contentType);
		} catch (UnsupportedImageFormatException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Erro ao obter a imagem: {}", ex.getMessage());
			throw new InternalServerErrorException("Não foi possível obter a imagem.", ex);
		}
	}

	// Métodos privados

	private String getExtension(String contentType) {
		switch (contentType) {
		case "image/jpeg":
			return ".jpg";
		case "image/png":
			return ".png";
		case "image/svg+xml":
			return ".svg";
		default:
			throw new UnsupportedImageFormatException();
		}
	}

	private void validateImage(MultipartFile imageFile) {
		log.info("Iniciando a validação da imagem: {}", imageFile.getOriginalFilename());
		if (!isContentTypeAllowed(imageFile)) {
			UnsupportedImageFormatException unsupportedImageFormatException = new UnsupportedImageFormatException(
					"O formato de arquivo da imagem não é suportado. Formatos válidos: " + ALLOWED_CONTENT_TYPES);
			log.warn(unsupportedImageFormatException.getMessage());
			throw unsupportedImageFormatException;
		}
		if (!isSizeValid(imageFile)) {
			ImageSizeException imageSizeException = new ImageSizeException(
					"A quantidade de bytes da imagem excedeu o limite permitido: " + MAX_FILE_SIZE + " bytes.");
			log.warn(imageSizeException.getMessage());
			throw imageSizeException;
		}

		try {
			BufferedImage image = ImageIO.read(imageFile.getInputStream());
			int height = image.getHeight();
			int width = image.getWidth();

			if (!isHeightValid(imageFile, height)) {
				ImageHeightException imageHeightException = new ImageHeightException(
						"A altura da imagem não está dentro dos limites permitidos. Limites permitidos: "
								+ ALLOWED_MIN_HEIGHT + " X " + ALLOWED_MAX_HEIGHT + " pixels.");
				log.warn(imageHeightException.getMessage());
				throw imageHeightException;
			}
			if (!isWidthValid(imageFile, width)) {
				ImageWidthException imageWidthException = new ImageWidthException(
						"A largura da imagem não está dentro dos limites permitidos. Limites permitidos: "
								+ ALLOWED_MIN_WIDTH + " X " + ALLOWED_MAX_WIDTH + " pixels.");
				log.warn(imageWidthException.getMessage());
				throw imageWidthException;
			}
			log.info("Imagem validada com sucesso.");
		} catch (ImageHeightException ex) {
			throw ex;
		} catch (ImageWidthException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Erro ao validar a imagem: {}", ex.getMessage());
			throw new InternalServerErrorException("Não foi possível validar a imagem.", ex);
		}
	}

	private boolean isContentTypeAllowed(MultipartFile file) {
		return ALLOWED_CONTENT_TYPES.contains(file.getContentType());
	}

	private boolean isSizeValid(MultipartFile file) {
		return file.getSize() <= MAX_FILE_SIZE;
	}

	private boolean isHeightValid(MultipartFile file, int height) {
		return height >= ALLOWED_MIN_HEIGHT && height <= ALLOWED_MAX_HEIGHT;
	}

	private boolean isWidthValid(MultipartFile file, int width) {
		return width >= ALLOWED_MIN_WIDTH && width <= ALLOWED_MAX_WIDTH;
	}

	private Path searchImagePath(UUID id) {
		for (String ext : ALLOWED_EXTENSIONS) {
			Path path = Paths.get(BASE_PATH + id.toString() + ext);
			if (Files.exists(path)) {
				return path;
			}
		}
		ImageNotFoundException imageNotFoundException = new ImageNotFoundException(
				"A imagem não foi encontrada. id = " + id);
		log.warn(imageNotFoundException.getMessage());
		throw imageNotFoundException;
	}
}
