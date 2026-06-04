package com.pragma.plazoleta.infrastructure.out.feign.client;

import com.pragma.plazoleta.infrastructure.out.feign.dto.SendSmsRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "messaging-service", url = "${messaging-service.url}")
@RequestMapping("/api/v1/notifications")
public interface INotificationFeignClient {

    @PostMapping("/sms")
    void sendSms(@RequestBody SendSmsRequestDto request);

}
