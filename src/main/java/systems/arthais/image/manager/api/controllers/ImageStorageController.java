package systems.arthais.image.manager.api.controllers;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import systems.arthais.image.manager.api.models.ImageData;
import systems.arthais.image.manager.api.services.ImageStorageOrquestratorService;

@RestController
@RequestMapping("/images")
@Validated
@Tag(name = "Image Management", description = "Operations pertaining to image management in the system.")
@ApiResponses(value = { 
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error") })
public class ImageStorageController {

	private final ImageStorageOrquestratorService imageStorageOrquestratorService;

	public ImageStorageController(ImageStorageOrquestratorService imageStorageOrquestratorService) {
		super();
		this.imageStorageOrquestratorService = imageStorageOrquestratorService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
			summary = "Upload image", 
			description = "This method upload a new image to system storage.",
			responses = { @ApiResponse(responseCode = "201", description = "Created") })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> uploadImage(
			@Parameter(
					description = "Image file to upload", 
					required = true)
	        @NotNull 
	        @RequestParam("image") MultipartFile imageFile) {
		UUID imageId = imageStorageOrquestratorService.uploadImage(imageFile);
		return ResponseEntity.ok(imageId.toString());
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
			summary = "Replace image", 
			description = "This method replace a image into system storage.", 
			responses = {
					@ApiResponse(responseCode = "200", description = "OK"),
					@ApiResponse(responseCode = "404", description = "Not Found") })
	public ResponseEntity<String> updateImage(
			@Parameter(
					description = "Image file' ID", 
					required = true)
			@PathVariable UUID id,
			@Parameter(
					description = "Image file that will to replace the current one", 
					required = true)
			@NotNull 
			@RequestParam("image") MultipartFile imageFile) {
		imageStorageOrquestratorService.updateImage(id, imageFile);
		return ResponseEntity.ok("Imagem atualizada com sucesso!");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete image", description = "This method delete a image from the system storage.", responses = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	public ResponseEntity<String> deleteImage(
			@Parameter(
					description = "Image file' ID", 
					required = true)
			@PathVariable UUID id) {
		imageStorageOrquestratorService.deleteImage(id);
		return ResponseEntity.ok("Imagem excluída com sucesso!");
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get image", description = "This method get a image from the system storage.", responses = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	public ResponseEntity<InputStreamResource> getImage(
			@Parameter(
					description = "Image file' ID", 
					required = true)
			@PathVariable UUID id) {
		ImageData imageData = imageStorageOrquestratorService.getImage(id);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(imageData.getContentType())).body(new InputStreamResource(imageData.getImageStream()));
	}

}
