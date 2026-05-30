package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.domain.exception.restaurantemployee.UserInformationConflictException;
import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.infrastructure.out.feign.client.IUserFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.UserFeignResponseDto;
import com.pragma.plazoleta.infrastructure.out.feign.mapper.IUserFeignMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationAdapterTest {

    @Mock
    private IUserFeignClient userFeignClient;

    @Mock
    private IUserFeignMapper userFeignMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UserInformationAdapter userInformationAdapter;

    private FeignException.@NotNull BadRequest getBadRequest(Request request) {
        var jsonBody = "{\n" +
                "    \"message\": \"Validation failed\",\n" +
                "    \"fieldErrors\": [\n" +
                "        { \"field\": \"documentNumber\", \"message\": \"Document number must be numeric only\" }\n" +
                "    ]\n" +
                "}";

        return new FeignException.BadRequest(
                "Bad Request",
                request,
                jsonBody.getBytes(StandardCharsets.UTF_8),
                Collections.emptyMap()
        );
    }

    @Test
    @DisplayName("Should return mapped UserInfo when users-service responds")
    void shouldReturnUserInfoWhenFound() {
        var dto = UserFeignResponseDto.builder()
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

    @Test
    @DisplayName("Should return created employee ID successfully")
    void shouldCreateEmployeeSuccessfully() {
        var employee = UserInformation.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .build();
        var responseDto = UserFeignResponseDto.builder()
                .id(5L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();

        when(userFeignClient.createEmployee(employee)).thenReturn(responseDto);

        var result = userInformationAdapter.createEmployee(employee);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should extract message from fieldErrors when 400 Bad Request occurs")
    void shouldExtractFieldErrorOnBadRequest() {
        var employee = UserInformation.builder().build();
        var createEmployeeRequest = Request.create(
                Request.HttpMethod.POST,
                "/api/v1/users/employee",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var badRequestException = getBadRequest(createEmployeeRequest);

        when(userFeignClient.createEmployee(employee)).thenThrow(badRequestException);

        assertThatThrownBy(() -> userInformationAdapter.createEmployee(employee))
                .isInstanceOf(UserInformationConflictException.class)
                .hasMessage("Document number must be numeric only");
    }
}
