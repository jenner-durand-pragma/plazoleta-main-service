package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.domain.spi.IUserValidationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IUserFeignMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserValidationAdapter implements IUserValidationPort {

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    @Override
    public UserInformation getUserById(Long id) {
        try {
            var dto = userFeignClient.getUserById(id);

            return userFeignMapper.toUserInformation(dto);
        } catch (FeignException.NotFound e) {

            return null;
        }
    }
}
