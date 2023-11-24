package systems.arthais.image.manager.api.config;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import systems.arthais.image.manager.api.services.AwsS3ImageStorageService;
import systems.arthais.image.manager.api.services.AzureBlobStorageImageStorageService;
import systems.arthais.image.manager.api.services.FileSystemImageStorageService;
import systems.arthais.image.manager.api.services.GoogleCloudStorageImageStorageService;
import systems.arthais.image.manager.api.services.IbmCloudObjectStorageImageStorageService;
import systems.arthais.image.manager.api.services.ImageStorageService;
import systems.arthais.image.manager.api.services.OciObjectStorageImageStorageService;

@Configuration
@Slf4j
public class ImageStorageServiceConfig {

	@Bean
	Map<String, ImageStorageService> imageStorageServiceMap(
			AwsS3ImageStorageService awsS3ImageStorageService,
			AzureBlobStorageImageStorageService azureBlobStorageImageStorageService,
			FileSystemImageStorageService fileSystemImageStorageService,
			GoogleCloudStorageImageStorageService googleCloudStorageImageStorageService,
			IbmCloudObjectStorageImageStorageService ibmCloudObjectStorageImageStorageService,
			OciObjectStorageImageStorageService ociObjectStorageImageStorageService) {
		log.info("Initializing Map of image storage services...");

		Map<String, ImageStorageService> services = new ConcurrentHashMap<>();
		services.put("AWS_S3", awsS3ImageStorageService);
		services.put("AZURE_BLOB_STORAGE", azureBlobStorageImageStorageService);
		services.put("FILE_SYSTEM", fileSystemImageStorageService);
		services.put("GOOGLE_CLOUD_STORAGE", googleCloudStorageImageStorageService);
		services.put("IBM_CLOUD_OBJECT_STORAGE", ibmCloudObjectStorageImageStorageService);
		services.put("OCI_OBJECT_STORAGE", ociObjectStorageImageStorageService);

		log.debug("Storage services added to Map: {}", Arrays.toString(services.keySet().toArray()));

		log.info("Successfully initialized image storage services Map.");
		log.debug("Final Map of storage services: {}", services);

		return services;
	}
}