package systems.arthais.image.manager.api.controllers;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/images")
public class ImageController {
	
	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/svg+xml");
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
	
	// Injeção de dependências para o serviço de imagem (a ser implementado)
	// @Autowired
	// private ImageService imageService;
	
    private boolean isContentTypeAllowed(MultipartFile file) {
        return ALLOWED_CONTENT_TYPES.contains(file.getContentType());
    }

    private boolean isFileSizeValid(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(
    		@NotEmpty @RequestParam("image") MultipartFile imageFile) {
        if (!isContentTypeAllowed(imageFile)) {
            return ResponseEntity.badRequest().body("Formato de arquivo não suportado. Formatos válidos: " + ALLOWED_CONTENT_TYPES);
        }
        if (!isFileSizeValid(imageFile)) {
            return ResponseEntity.badRequest().body("Tamanho do arquivo excede o limite de " + (MAX_FILE_SIZE / 1024 / 1024) + "MB.");
        }

        UUID imageId = UUID.randomUUID(); // Gerar UUID único para a imagem
        // Lógica para processar o upload da imagem

        return ResponseEntity.ok(imageId.toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateImage(
    		@PathVariable UUID id, 
    		@NotEmpty @RequestParam("image") MultipartFile imageFile) {
    	
        if (!isContentTypeAllowed(imageFile)) {
            return ResponseEntity.badRequest().body("Formato de arquivo não suportado. Formatos válidos: " + ALLOWED_CONTENT_TYPES);
        }
        if (!isFileSizeValid(imageFile)) {
            return ResponseEntity.badRequest().body("Tamanho do arquivo excede o limite de " + (MAX_FILE_SIZE / 1024 / 1024) + "MB.");
        }

        // Lógica para atualizar a imagem com base no UUID

        return ResponseEntity.ok("Imagem atualizada com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(
    		@NotNull @PathVariable UUID id) {
         // Lógica para excluir a imagem com base no UUID

        return ResponseEntity.ok("Imagem excluída com sucesso!");
    }
    
	@GetMapping("/{id}")
	public ResponseEntity<InputStreamResource> getImage(
			@PathVariable UUID id) {
         
		// Lógica para recuperar a imagem com base no UUID
		InputStream imageStream = null; // Obter o InputStream da imagem

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG) // Ajustar o tipo de mídia conforme necessário
				.body(new InputStreamResource(imageStream));
	}

}
