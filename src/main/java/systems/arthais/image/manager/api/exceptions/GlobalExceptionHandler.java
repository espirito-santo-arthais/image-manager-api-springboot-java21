package systems.arthais.image.manager.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsupportedImageFormatException.class)
    public ResponseEntity<Object> handleUnsupportedImageFormatException(UnsupportedImageFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ImageSizeException.class)
    public ResponseEntity<Object> handleImageSizeException(ImageSizeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ImageHeightException.class)
    public ResponseEntity<Object> handleImageHeightException(ImageHeightException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ImageWidthException.class)
    public ResponseEntity<Object> handleImageWidthException(ImageWidthException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ImageDensityException.class)
    public ResponseEntity<Object> handleImageDensityException(ImageDensityException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<Object> handleImageNotFoundException(ImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<Object> handleInternalServerError(InternalServerError ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
