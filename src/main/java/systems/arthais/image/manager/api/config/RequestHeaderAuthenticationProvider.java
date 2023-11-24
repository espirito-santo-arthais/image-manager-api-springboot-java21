package systems.arthais.image.manager.api.config;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RequestHeaderAuthenticationProvider implements AuthenticationProvider {

    @Value("${api.auth.header.token.value}")
    private String apiAuthHeaderTokenValue;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String requestApiAuthSecret = String.valueOf(authentication.getPrincipal());

        log.debug("Authenticating request with token: {}", requestApiAuthSecret);

        if (StringUtils.isBlank(requestApiAuthSecret)) {
            log.warn("Request header token is blank.");
            throw new BadCredentialsException("Bad request header credentials - token is blank.");
        }

        if (!requestApiAuthSecret.equals(apiAuthHeaderTokenValue)) {
            log.warn("Invalid request header token received.");
            throw new BadCredentialsException("Bad request header credentials - token mismatch.");
        }

        log.info("Request authenticated successfully with token: {}", requestApiAuthSecret);

        return new PreAuthenticatedAuthenticationToken(authentication.getPrincipal(), null, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
