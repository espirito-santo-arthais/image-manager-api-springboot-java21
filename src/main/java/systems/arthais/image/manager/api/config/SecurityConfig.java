package systems.arthais.image.manager.api.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Value("${api.auth.header.name}")
	private String apiAuthHeaderName;

	private final RequestHeaderAuthenticationProvider requestHeaderAuthenticationProvider;

	public SecurityConfig(RequestHeaderAuthenticationProvider requestHeaderAuthenticationProvider) {
		this.requestHeaderAuthenticationProvider = requestHeaderAuthenticationProvider;
	}

	@Bean
	RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
		RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
		filter.setPrincipalRequestHeader(apiAuthHeaderName);
		filter.setExceptionIfHeaderMissing(false);
		filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/images/**"));
		filter.setAuthenticationManager(authenticationManager());

		return filter;
	}

	@Bean
	protected AuthenticationManager authenticationManager() {
		return new ProviderManager(Collections.singletonList(requestHeaderAuthenticationProvider));
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterAfter(requestHeaderAuthenticationFilter(), HeaderWriterFilter.class)
			.authorizeHttpRequests((authorize) -> {
				authorize.requestMatchers("/swagger-ui/**").permitAll();
				authorize.requestMatchers("/swagger-resources/**").permitAll();
				authorize.requestMatchers("/swagger-ui.html").permitAll();
				authorize.requestMatchers("/webjars/**").permitAll();
				authorize.requestMatchers("/v3/api-docs/**").permitAll();
				authorize.requestMatchers("/actuator/health").permitAll();
				authorize.requestMatchers("/actuator/info").permitAll();
				authorize.requestMatchers("/images/**").authenticated();
			});

		return http.build();
	}
}
