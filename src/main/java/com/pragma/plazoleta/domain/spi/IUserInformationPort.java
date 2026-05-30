package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.UserInformation;

public interface IUserInformationPort {

    UserInformation getUserById(Long id);
    Long createEmployee(UserInformation employeeInformation);

}
