package systems.arthais.image.manager.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OpenApiConfig {

	@Value("${api.info.title}")
	public String title;

	@Value("${api.info.description}")
	public String description;

	@Value("${api.info.version}")
	public String version;

	@Value("${api.info.termsOfServiceUrl}")
	public String termsOfServiceUrl;

	@Value("${api.info.contact.name}")
	public String contactName;

	@Value("${api.info.contact.url}")
	public String contactUrl;

	@Value("${api.info.contact.email}")
	public String contactEmail;

	@Value("${api.info.license}")
	public String license;

	@Value("${api.info.licenseUrl}")
	public String licenseUrl;

	@Value("${api.auth.header.token.key}")
	private String apiAuthHeaderTokenKey;

    @Bean
    OpenAPI customOpenAPI() {
    	final String securitySchemeName = "apiKey";
    	
        log.info("Configuring OpenAPI with title: {}", title);

        OpenAPI openApi = new OpenAPI()
	            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
	            .components(new Components()
	                .addSecuritySchemes(
	                	securitySchemeName,
	                    new SecurityScheme()
	                        .name(securitySchemeName)
	                        .type(SecurityScheme.Type.APIKEY)
	                        .in(SecurityScheme.In.HEADER)
	                        .name(apiAuthHeaderTokenKey)))
	            .info(new Info()
				.title(title)
				.description(description)
				.version(version)
				.termsOfService(termsOfServiceUrl)
				.contact(new Contact()
						.name(contactName)
						.url(contactUrl)
						.email(contactEmail))
				.license(new License()
						.name(license)
						.url(licenseUrl))
				);
		
        log.info("OpenAPI configuration completed successfully.");

        return openApi;
	}
}