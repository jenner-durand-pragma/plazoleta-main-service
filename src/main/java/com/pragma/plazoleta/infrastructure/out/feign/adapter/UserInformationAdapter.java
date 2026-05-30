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
        try {
            var response = userFeignClient.createEmployee(employeeInformation);

            return response.getId();
        } catch (FeignException ex) {
            if (ex.status() >= 400 && ex.status() < 500) {
                log.warn("users-service rejected employee creation (status {}): {}", ex.status(), ex.contentUTF8());

                throw new UserInformationConflictException(extractErrorMessage(ex.contentUTF8()));
            }

            log.error("users-service unavailable when creating employee (status {})", ex.status(), ex);

            throw ex;
        }
    }

    private String extractErrorMessage(String feignResponseBody) {
        var fallbackMessage = "Unknown error from users-service";

        if (feignResponseBody == null || feignResponseBody.isBlank()) {
            return fallbackMessage;
        }

        try {
            var root = objectMapper.readTree(feignResponseBody);

            var fieldErrors = root.path("fieldErrors");
            if (fieldErrors.isArray() && !fieldErrors.isEmpty()) {
                var firstError = fieldErrors.get(0);
                if (firstError.has("message")) {

                    return firstError.get("message").asText();
                }
            }

            if (root.has("message")) {
                return root.get("message").asText();
            }

        } catch (Exception e) {
            log.error("Failed to parse Feign error response body", e);
        }

        return fallbackMessage;
    }
}
