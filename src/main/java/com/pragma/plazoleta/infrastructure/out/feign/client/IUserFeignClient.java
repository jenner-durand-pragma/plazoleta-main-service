package com.pragma.plazoleta.infrastructure.out.feign.client;

import com.pragma.plazoleta.domain.model.UserInformation;
import com.pragma.plazoleta.infrastructure.out.feign.dto.UserFeignResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "users-service", url = "${users-service.url}")
@RequestMapping("/api/v1/users")
public interface IUserFeignClient {

    @GetMapping("/{id}")
    UserFeignResponseDto getUserById(@PathVariable("id") Long id);

    @PostMapping("/employee")
    UserFeignResponseDto createEmployee(@RequestBody UserInformation userInformation);
}
