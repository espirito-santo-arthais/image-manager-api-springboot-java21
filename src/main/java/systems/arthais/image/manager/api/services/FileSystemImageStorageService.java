package systems.arthais.image.manager.api.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import systems.arthais.image.manager.api.exceptions.ImageNotFoundException;
import systems.arthais.image.manager.api.exceptions.InternalServerErrorException;
import systems.arthais.image.manager.api.exceptions.UnsupportedImageFormatException;
import systems.arthais.image.manager.api.models.ImageData;

@Service
@Slf4j
public class FileSystemImageStorageService implements ImageStorageService {

    @Override
    public void uploadImage(MultipartFile imageFile, String extension, String basePath, UUID id) {
        try {
            byte[] bytes = imageFile.getBytes();
            Path path = Paths.get(basePath + id.toString() + extension);
            Files.write(path, bytes);
            log.info("Image successfully saved. UUID: {}", id);
        } catch (Exception ex) {
            log.error("Error saving image: {}", ex.getMessage());
            throw new InternalServerErrorException("Unable to save the image.", ex);
        }
    }

    @Override
    public void updateImage(MultipartFile imageFile, List<String> allowedExtensions, String basePath, UUID id) {
        Path path = searchImagePath(allowedExtensions, basePath, id);

        try {
            byte[] bytes = imageFile.getBytes();
            Files.write(path, bytes);
            log.info("Image successfully replaced.");
        } catch (Exception ex) {
            log.error("Error replacing image: {}", ex.getMessage());
            throw new InternalServerErrorException("Unable to update the image.", ex);
        }
    }

    @Override
    public void deleteImage(List<String> allowedExtensions, String basePath, UUID id) {
        Path path = searchImagePath(allowedExtensions, basePath, id);

        try {
            Files.delete(path);
            log.info("Image successfully deleted.");
        } catch (Exception ex) {
            log.error("Error deleting image: {}", ex.getMessage());
            throw new InternalServerErrorException("Unable to delete the image.", ex);
        }
    }

    @Override
    public ImageData getImage(List<String> allowedExtensions, String basePath, UUID id) {
        Path path = searchImagePath(allowedExtensions, basePath, id);

        try {
            String contentType = Files.probeContentType(path);
            InputStream imageStream = new FileInputStream(path.toFile());

            log.info("Image successfully retrieved.");
            return new ImageData(imageStream, contentType);
        } catch (UnsupportedImageFormatException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error retrieving image: {}", ex.getMessage());
            throw new InternalServerErrorException("Unable to retrieve the image.", ex);
        }
    }

    // Private methods

    private Path searchImagePath(List<String> allowedExtensions, String basePath, UUID id) {
        for (String ext : allowedExtensions) {
            Path path = Paths.get(basePath + id.toString() + ext);
            try {
                if (Files.exists(path)) {
                    return path;
                }
            } catch (Exception ex) {
                log.error("Error trying to find the image: {}", ex.getMessage());
                throw new InternalServerErrorException("Failed to attempt to find the image.", ex);
            }
        }
        ImageNotFoundException imageNotFoundException = new ImageNotFoundException(String.format("The image was not found. id = %s", id));
        log.warn(imageNotFoundException.getMessage());
        throw imageNotFoundException;
    }
}
