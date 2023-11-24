package systems.arthais.image.manager.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import systems.arthais.image.manager.api.models.ImageData;

public interface ImageStorageService {

	void uploadImage(MultipartFile imageFile, String extension, String basePath, UUID id);
	void updateImage(MultipartFile imageFile, List<String> allowedExtensions, String basePath, UUID id);
	void deleteImage(List<String> allowedExtensions, String basePath, UUID id);
	ImageData getImage(List<String> allowedExtensions, String basePath, UUID id);

}
