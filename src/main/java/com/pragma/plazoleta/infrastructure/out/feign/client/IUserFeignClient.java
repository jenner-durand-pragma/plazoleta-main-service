package com.pragma.plazoleta.infrastructure.out.feign.client;

import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.infrastructure.out.feign.dto.UserFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users-service", url = "${users-service.url}")
public interface IUserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/users/employee")
    UserFeignResponseDto createEmployee(@RequestBody UserInformation userInformation);
}
