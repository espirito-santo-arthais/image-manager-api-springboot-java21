package systems.arthais.image.manager.api.services;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${image.allowed-content-types}")
	private List<String> allowedContentTypes;

	@Value("${image.allowed-extensions}")
	private List<String> allowedExtensions;

	@Value("${image.max-file-size}")
	private long maxFileSize;

	@Value("${image.min-height}")
	private long allowedMinHeight;

	@Value("${image.max-height}")
	private long allowedMaxHeight;

	@Value("${image.min-width}")
	private long allowedMinWidth;

	@Value("${image.max-width}")
	private long allowedMaxWidth;

	@Value("${image.base-path}")
	private String basePath;

	public UUID uploadImage(MultipartFile imageFile) {
		log.info("Iniciando o salvamento da imagem...");

		validateImage(imageFile);

		UUID id = UUID.randomUUID();

		try {
			byte[] bytes = imageFile.getBytes();
			Path path = Paths.get(basePath + id.toString() + getExtension(imageFile.getContentType()));
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
					String.format("O formato de arquivo da imagem não é suportado. Formatos válidos: %1s",
							allowedContentTypes));
			log.warn(unsupportedImageFormatException.getMessage());
			throw unsupportedImageFormatException;
		}
		if (!isSizeValid(imageFile)) {
			ImageSizeException imageSizeException = new ImageSizeException(String
					.format("A quantidade de bytes da imagem excedeu o limite permitido: %1s bytes.", maxFileSize));
			log.warn(imageSizeException.getMessage());
			throw imageSizeException;
		}

		try {
			BufferedImage image = ImageIO.read(imageFile.getInputStream());
			int height = image.getHeight();
			int width = image.getWidth();

			if (!isHeightValid(imageFile, height)) {
				ImageHeightException imageHeightException = new ImageHeightException(String.format(
						"A altura da imagem não está dentro dos limites permitidos. Limites permitidos: %1s até %2s pixels.",
						allowedMinHeight, allowedMaxHeight));
				log.warn(imageHeightException.getMessage());
				throw imageHeightException;
			}
			if (!isWidthValid(imageFile, width)) {
				ImageWidthException imageWidthException = new ImageWidthException(String.format(
						"A largura da imagem não está dentro dos limites permitidos. Limites permitidos: %1s até %2s pixels.",
						allowedMinWidth, allowedMaxWidth));
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
		return allowedContentTypes.contains(file.getContentType());
	}

	private boolean isSizeValid(MultipartFile file) {
		return file.getSize() <= maxFileSize;
	}

	private boolean isHeightValid(MultipartFile file, int height) {
		return height >= allowedMinHeight && height <= allowedMaxHeight;
	}

	private boolean isWidthValid(MultipartFile file, int width) {
		return width >= allowedMinWidth && width <= allowedMaxWidth;
	}

	private Path searchImagePath(UUID id) {
		for (String ext : allowedExtensions) {
			Path path = Paths.get(basePath + id.toString() + ext);
			if (Files.exists(path)) {
				return path;
			}
		}
		ImageNotFoundException imageNotFoundException = new ImageNotFoundException(
				String.format("A imagem não foi encontrada. id = %s", id));
		log.warn(imageNotFoundException.getMessage());
		throw imageNotFoundException;
	}
}
