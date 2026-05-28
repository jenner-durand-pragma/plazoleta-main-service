package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.UserInformation;

public interface IUserValidationPort {

    UserInformation getUserById(Long id);

}
