package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.infrastructure.out.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.UserFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IUserFeignMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationAdapterTest {

    @Mock
    private IUserFeignClient userFeignClient;

    @Mock
    private IUserFeignMapper userFeignMapper;

    @InjectMocks
    private UserInformationAdapter userInformationAdapter;

    @Test
    @DisplayName("Should return mapped UserInfo when users-service responds")
    void shouldReturnUserInfoWhenFound() {
        UserFeignResponseDto dto = UserFeignResponseDto.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();
        var expected = UserInformation.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();

        when(userFeignClient.getUserById(5L)).thenReturn(dto);
        when(userFeignMapper.toUserInformation(dto)).thenReturn(expected);

        var result = userInformationAdapter.getUserById(5L);

        assertThat(result).isNotNull();
        assertThat(result.getRoleName()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("Should return null when users-service returns 404")
    void shouldReturnNullWhenNotFound() {
        var request = Request.create(
                Request.HttpMethod.GET,
                "/api/v1/users/99",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var notFound = new FeignException.NotFound(
                "Not Found",
                request,
                null,
                Collections.emptyMap()
        );

        when(userFeignClient.getUserById(99L)).thenThrow(notFound);

        var result = userInformationAdapter.getUserById(99L);

        assertThat(result).isNull();
    }
}
