package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.spi.IUserValidationPort;
import com.pragma.plazoleta.infrastructure.out.feign.adapter.UserValidationAdapter;
import com.pragma.plazoleta.infrastructure.out.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IUserFeignMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    @Bean
    public IUserValidationPort userValidationPort() {
        return new UserValidationAdapter(
                userFeignClient,
                userFeignMapper
        );
    }
}
