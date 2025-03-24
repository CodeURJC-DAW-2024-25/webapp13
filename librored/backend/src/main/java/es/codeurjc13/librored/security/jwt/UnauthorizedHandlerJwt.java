package es.codeurjc13.librored.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnauthorizedHandlerJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(UnauthorizedHandlerJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        logger.info("Unauthorized error: {}", ex.getMessage());

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "message: %s, path: %s".formatted(ex.getMessage(), request.getServletPath()));
    }
}
