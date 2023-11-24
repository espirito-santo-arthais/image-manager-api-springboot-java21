package systems.arthais.image.manager.api.services;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import lombok.extern.slf4j.Slf4j;
import systems.arthais.image.manager.api.exceptions.ImageNotFoundException;
import systems.arthais.image.manager.api.exceptions.InternalServerErrorException;
import systems.arthais.image.manager.api.models.ImageData;

@Service
@Slf4j
public class AwsS3ImageStorageService implements ImageStorageService {

	private final AmazonS3 amazonS3;

	public AwsS3ImageStorageService(AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
	}
	
	@Override
	public void uploadImage(MultipartFile imageFile, String extension, String basePath, UUID id) {
		String fileName = id.toString() + extension;

		try {
			ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(imageFile.getContentType());
            metadata.setContentLength(imageFile.getSize());
            PutObjectRequest putObjectRequest = new PutObjectRequest(basePath, fileName, imageFile.getInputStream(), metadata);
            
            amazonS3.putObject(putObjectRequest);
            log.info("Image successfully saved to AWS S3. UUID: {}", id);
		} catch (Exception ex) {
			log.error("Error saving image to AWS S3: {}", ex.getMessage());
			throw new InternalServerErrorException("Unable to save the image on AWS S3.", ex);
		}
	}

	@Override
	public void updateImage(MultipartFile imageFile, List<String> allowedExtensions, String basePath, UUID id) {
		String fileName = searchFileName(amazonS3, allowedExtensions, basePath, id);

		try {
			ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(imageFile.getContentType());
            metadata.setContentLength(imageFile.getSize());
            PutObjectRequest putObjectRequest = new PutObjectRequest(basePath, fileName, imageFile.getInputStream(), metadata);
            
            amazonS3.putObject(putObjectRequest);
            log.info("Image successfully replaced on AWS S3. UUID: {}", id);
		} catch (Exception ex) {
			log.error("Error replacing image on AWS S3: {}", ex.getMessage());
			throw new InternalServerErrorException("Unable to update the image on AWS S3.", ex);
		}
	}

	@Override
	public void deleteImage(List<String> allowedExtensions, String basePath, UUID id) {
		String fileName = searchFileName(amazonS3, allowedExtensions, basePath, id);

		try {
			amazonS3.deleteObject(basePath, fileName);
			log.info("Image successfully deleted from AWS S3.");
		} catch (Exception ex) {
			log.error("Error deleting image from AWS S3: {}", ex.getMessage());
			throw new InternalServerErrorException("Unable to delete the image from AWS S3.", ex);
		}
	}

	@Override
	public ImageData getImage(List<String> allowedExtensions, String basePath, UUID id) {
		String fileName = searchFileName(amazonS3, allowedExtensions, basePath, id);

		try {
			S3Object s3Object = amazonS3.getObject(new GetObjectRequest(basePath, fileName));
            InputStream inputStream = s3Object.getObjectContent();
            String contentType = s3Object.getObjectMetadata().getContentType();
            
            log.info("Image successfully retrieved from AWS S3.");
			return new ImageData(inputStream, contentType);
		} catch (Exception ex) {
			log.error("Error retrieving image from AWS S3: {}", ex.getMessage());
			throw new InternalServerErrorException("Unable to retrieve the image from AWS S3.", ex);
		}
	}

	// Métodos privados

	private String searchFileName(AmazonS3 amazonS3, List<String> allowedExtensions, String bucketName, UUID id) {
		for (String ext : allowedExtensions) {
			String objectKey = id.toString() + ext;
			String logMessage = "Error trying to find the image on AWS S3: {}";
			String exceptionMessage = "Failed to attempt to find the image on AWS S3.";
			try {
				ObjectMetadata objectMetadata = amazonS3.getObjectMetadata(bucketName, objectKey);
				if (objectMetadata.getContentLength() > 0) {
		            return objectKey;
				}
	        } catch (AmazonS3Exception ex) {
	            if (!ex.getErrorCode().contains("404")) {
					log.error(logMessage, ex.getMessage());
					throw new InternalServerErrorException(exceptionMessage, ex);
	            }
	        } catch (Exception ex) {
				log.error(logMessage, ex.getMessage());
				throw new InternalServerErrorException(exceptionMessage, ex);
	        }
		}
		ImageNotFoundException imageNotFoundException = new ImageNotFoundException(String.format("The image was not found on AWS S3. id = %s", id));
		log.warn(imageNotFoundException.getMessage());
		throw imageNotFoundException;
	}
}
