package com.pragma.plazoleta.infrastructure.out.security.jwt;

import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import com.pragma.plazoleta.infrastructure.configuration.security.token.exception.InvalidTokenException;
import com.pragma.plazoleta.infrastructure.out.security.jwt.configuration.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Base64;

@RequiredArgsConstructor
public class JwtAdapter implements ITokenValidationPort {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;

    @Override
    public AuthenticatedUser validate(String token) {
        try {
            var claims = parseClaims(token);
            return new AuthenticatedUser(
                    claims.get(CLAIM_USER_ID, Long.class),
                    claims.getSubject(),
                    claims.get(CLAIM_ROLE, String.class)
            );
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid or expired token", ex);
        }
    }

    private SecretKey signingKey() {
        var decoded = Base64.getDecoder().decode(jwtProperties.getSecret());

        return Keys.hmacShaKeyFor(decoded);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
