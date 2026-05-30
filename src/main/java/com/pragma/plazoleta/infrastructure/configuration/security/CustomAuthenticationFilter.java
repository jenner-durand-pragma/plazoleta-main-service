package com.pragma.plazoleta.infrastructure.configuration.security;

import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import com.pragma.plazoleta.infrastructure.configuration.security.token.exception.InvalidTokenException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    private final ITokenValidationPort tokenValidationPort;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        var token = extractToken(request);
        if (token != null) {
            try {
                var payload = tokenValidationPort.validate(token);
                var authentication = buildAuthentication(payload, request);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (InvalidTokenException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        var header = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {

            return header.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(
            AuthenticatedUser principal,
            HttpServletRequest request
    ) {
        var authority = new SimpleGrantedAuthority(ROLE_PREFIX + principal.getRole());

        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(authority)
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }
}
