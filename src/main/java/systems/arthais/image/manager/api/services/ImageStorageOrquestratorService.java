package systems.arthais.image.manager.api.services;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import systems.arthais.image.manager.api.exceptions.ImageHeightException;
import systems.arthais.image.manager.api.exceptions.ImageSizeException;
import systems.arthais.image.manager.api.exceptions.ImageWidthException;
import systems.arthais.image.manager.api.exceptions.InternalServerErrorException;
import systems.arthais.image.manager.api.exceptions.UnsupportedImageFormatException;
import systems.arthais.image.manager.api.models.ImageData;

@Service
@Slf4j
@DependsOn("imageStorageServiceMap")
public class ImageStorageOrquestratorService {
    
    @Value("${file.storage-platform}")
    private String storagePlatform;

    @Value("${file.storage-path}")
    private String storagePath;

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

    private final Map<String, ImageStorageService> imageStorageServices;

    public ImageStorageOrquestratorService(@Qualifier("imageStorageServiceMap") Map<String, ImageStorageService> imageStorageServices) {
        this.imageStorageServices = imageStorageServices;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Keys of the map after injection: {}", Arrays.toString(imageStorageServices.keySet().toArray()));
    }

    public UUID uploadImage(MultipartFile imageFile) {
        log.info("Starting the image upload process...");

        validateImage(imageFile);

        UUID id = UUID.randomUUID();
        String extension = getExtension(imageFile.getContentType());
        if (extension == null) {
            Exception ex = new Exception(String.format("The value of 'extension' is 'null' for 'imageFile.getContentType()' equal to '%1s'.", imageFile.getContentType()));
            log.error("Error saving image: {}", ex.getMessage());
            throw new InternalServerErrorException("Failed to save the image.", ex);
        }

        ImageStorageService imageStorageService = getImageStorageService(storagePlatform);
        imageStorageService.uploadImage(imageFile, extension, storagePath, id);

        return id;
    }

    public void updateImage(UUID id, MultipartFile imageFile) {
        log.info("Starting the image replacement process. UUID: {}", id);

        validateImage(imageFile);

        ImageStorageService imageStorageService = getImageStorageService(storagePlatform);
        imageStorageService.updateImage(imageFile, allowedExtensions, storagePath, id);
    }

    public void deleteImage(UUID id) {
        log.info("Starting the image deletion process. UUID: {}", id);

        ImageStorageService imageStorageService = getImageStorageService(storagePlatform);
        imageStorageService.deleteImage(allowedExtensions, storagePath, id);
    }

    public ImageData getImage(UUID id) {
        log.info("Starting the process of obtaining the image. UUID: {}", id);

        ImageStorageService imageStorageService = getImageStorageService(storagePlatform);
        return imageStorageService.getImage(allowedExtensions, storagePath, id);
    }

    private String getExtension(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/svg+xml":
                return ".svg";
            default:
                throw new UnsupportedImageFormatException("File format not supported.");
        }
    }

    private void validateImage(MultipartFile imageFile) {
        log.info("Starting image validation: {}", imageFile.getOriginalFilename());
        if (!isContentTypeAllowed(imageFile)) {
            throw new UnsupportedImageFormatException("Image file format not supported. Supported formats: " + allowedContentTypes);
        }
        if (!isSizeValid(imageFile)) {
            throw new ImageSizeException("Image file size exceeds the permitted limit: " + maxFileSize + " bytes.");
        }

        try {
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            int height = image.getHeight();
            int width = image.getWidth();

            if (!isHeightValid(imageFile, height)) {
                throw new ImageHeightException("Image height is not within the permitted limits.");
            }
            if (!isWidthValid(imageFile, width)) {
                throw new ImageWidthException("Image width is not within the permitted limits.");
            }
            log.info("Image successfully validated.");
        } catch (Exception ex) {
            log.error("Error validating image: {}", ex.getMessage());
            throw new InternalServerErrorException("Unable to validate the image.", ex);
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

    private ImageStorageService getImageStorageService(String storagePlatform) {
        log.debug("Determining the storage platform for: {}", storagePlatform);
        ImageStorageService imageStorageService = imageStorageServices.get(storagePlatform);
        if (imageStorageService == null) {
            Exception ex = new Exception(String.format("The value of 'imageStorageService' is 'null' for 'storagePlatform' equal to '%1s'.", storagePlatform));
            log.error("Error while determining the storage platform: {}", ex.getMessage());
            throw new InternalServerErrorException("Unable to determine the storage platform. Valid platforms: " + Arrays.toString(imageStorageServices.keySet().toArray()), ex);
        }

        return imageStorageService;
    }
}
