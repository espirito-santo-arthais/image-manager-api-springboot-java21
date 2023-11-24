package systems.arthais.image.manager.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import systems.arthais.image.manager.api.models.ImageData;

@Service
@Slf4j
public class OracleCloudInfrastructureObjectStorageImageStorageService implements ImageStorageService {

	@Override
	public void uploadImage(MultipartFile imageFile, String extension, String basePath, UUID id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateImage(MultipartFile imageFile, List<String> allowedExtensions, String basePath, UUID id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteImage(List<String> allowedExtensions, String basePath, UUID id) {
		// TODO Auto-generated method stub
	}

	@Override
	public ImageData getImage(List<String> allowedExtensions, String basePath, UUID id) {
		// TODO Auto-generated method stub
		return null;
	}
}
