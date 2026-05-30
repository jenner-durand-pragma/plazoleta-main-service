package com.pragma.plazoleta.infrastructure.configuration.security.token;

import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;

public interface ITokenValidationPort {

    AuthenticatedUser validate(String token);

}
