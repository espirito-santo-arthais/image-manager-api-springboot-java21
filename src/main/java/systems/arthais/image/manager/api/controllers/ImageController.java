package systems.arthais.image.manager.api.controllers;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import systems.arthais.image.manager.api.models.ImageData;
import systems.arthais.image.manager.api.services.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {

	private final ImageService imageService;

	public ImageController(ImageService imageService) {
		super();
		this.imageService = imageService;
	}

	@PostMapping
	public ResponseEntity<String> uploadImage(@NotEmpty @RequestParam("image") MultipartFile imageFile) {
		UUID imageId = imageService.uploadImage(imageFile);
		return ResponseEntity.ok(imageId.toString());
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateImage(@PathVariable UUID id,
			@NotEmpty @RequestParam("image") MultipartFile imageFile) {
		imageService.updateImage(id, imageFile);
		return ResponseEntity.ok("Imagem atualizada com sucesso!");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteImage(@PathVariable UUID id) {
		imageService.deleteImage(id);
		return ResponseEntity.ok("Imagem excluída com sucesso!");
	}

	@GetMapping("/{id}")
	public ResponseEntity<InputStreamResource> getImage(@PathVariable UUID id) {
		ImageData imageData = imageService.getImage(id);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(imageData.getContentType()))
				.body(new InputStreamResource(imageData.getImageStream()));
	}

}
