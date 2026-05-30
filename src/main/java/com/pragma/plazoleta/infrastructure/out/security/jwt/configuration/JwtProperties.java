package com.pragma.plazoleta.infrastructure.out.security.jwt.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String secret;
    private Long expirationMs;

}
