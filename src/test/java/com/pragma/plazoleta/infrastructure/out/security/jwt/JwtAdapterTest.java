package com.pragma.plazoleta.infrastructure.out.security.jwt;

import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.infrastructure.configuration.security.token.exception.InvalidTokenException;
import com.pragma.plazoleta.infrastructure.out.security.jwt.configuration.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAdapterTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtAdapter jwtAdapter;

    private UserInformation validUser;

    private static final String BASE64_SECRET = "bXktc3VwZXItc2VjcmV0LWtleS10aGF0LWlzLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=";

    @BeforeEach
    void setUp() {
        validUser = UserInformation.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();
    }

    @Test
    @DisplayName("Should successfully validate a token and return the payload")
    void shouldValidateTokenAndReturnPayload() {
        when(jwtProperties.getSecret()).thenReturn(BASE64_SECRET);
        when(jwtProperties.getExpirationMs()).thenReturn(1000L * 60L * 60L);

        var now = new Date();
        var expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        var decoded = Base64.getDecoder().decode(jwtProperties.getSecret());

        var signingKey = Keys.hmacShaKeyFor(decoded);

        var validToken = Jwts.builder()
                .setSubject(validUser.getEmail())
                .claim("userId", validUser.getId())
                .claim("role", validUser.getRoleName())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        var payload = jwtAdapter.validate(validToken);

        assertThat(payload).isNotNull();
        assertThat(payload.getEmail()).isEqualTo("jenner.durand@plazoleta.com");
        assertThat(payload.getUserId()).isEqualTo(10L);
        assertThat(payload.getRole()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when the token is malformed or invalid")
    void shouldThrowExceptionWhenTokenIsInvalid() {
        when(jwtProperties.getSecret()).thenReturn(BASE64_SECRET);

        var invalidToken = "invalid.jwt";

        assertThatThrownBy(() -> jwtAdapter.validate(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid or expired token");
    }
}
