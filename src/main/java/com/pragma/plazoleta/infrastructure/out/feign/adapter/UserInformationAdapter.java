package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.domain.exception.restaurantemployee.UserInformationConflictException;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IUserInformationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IUserFeignMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserInformationAdapter implements IUserInformationPort {

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    private final ObjectMapper objectMapper;

    @Override
    public UserInformation getUserById(Long id) {
        try {
            var dto = userFeignClient.getUserById(id);

            return userFeignMapper.toUserInformation(dto);
        } catch (FeignException.NotFound e) {

            return null;
        }
    }

    @Override
    public Long createEmployee(UserInformation employeeInformation) {
        return null;
    }
}
