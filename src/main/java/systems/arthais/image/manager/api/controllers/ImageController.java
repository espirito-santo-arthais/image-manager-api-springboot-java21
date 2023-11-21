package systems.arthais.image.manager.api.controllers;

import java.io.InputStream;
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

@RestController
@RequestMapping("/images")
public class ImageController {

	// Injeção de dependências para o serviço de imagem (a ser implementado)
	// @Autowired
	// private ImageService imageService;

	@PostMapping
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile imageFile) {
		UUID imageId = UUID.randomUUID(); // Gerar UUID único para a imagem
		// Lógica para processar o upload da imagem

		return ResponseEntity.ok(imageId.toString());
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateImage(@PathVariable UUID id, @RequestParam("image") MultipartFile imageFile) {
		// Lógica para atualizar a imagem com base no UUID

		return ResponseEntity.ok("Imagem atualizada com sucesso!");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteImage(@PathVariable UUID id) {
		// Lógica para excluir a imagem com base no UUID

		return ResponseEntity.ok("Imagem excluída com sucesso!");
	}

	@GetMapping("/{id}")
	public ResponseEntity<InputStreamResource> getImage(@PathVariable UUID id) {
		// Lógica para recuperar a imagem com base no UUID
		InputStream imageStream = null; // Obter o InputStream da imagem

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG) // Ajustar o tipo de mídia conforme necessário
				.body(new InputStreamResource(imageStream));
	}

}
