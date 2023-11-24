package systems.arthais.image.manager.api.models;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {

	private InputStream imageStream;
	private String contentType;

}
