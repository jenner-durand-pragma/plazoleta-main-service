package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.infrastructure.out.feign.client.INotificationFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.SendSmsRequestDto;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationAdapterTest {

    @Mock
    private INotificationFeignClient notificationFeignClient;

    @InjectMocks
    private NotificationAdapter notificationAdapter;

    @Test
    @DisplayName("Should format message correctly and send SMS when notification-service responds successfully")
    void shouldFormatMessageAndSendSmsSuccessfully() {
        var order = Order.builder()
                .id(42L)
                .securityPin("123456")
                .build();
        var customerPhone = "+5198576854";

        notificationAdapter.notifyOrderReady(order, customerPhone);

        var captor = ArgumentCaptor.forClass(SendSmsRequestDto.class);
        verify(notificationFeignClient).sendSms(captor.capture());

        var capturedRequest = captor.getValue();
        assertThat(capturedRequest.getTo()).isEqualTo("+5198576854");
        assertThat(capturedRequest.getMessage()).isEqualTo("Your order #42 is ready. PIN: 123456");
    }

    @Test
    @DisplayName("Should rethrow FeignException when notification-service returns 400 Bad Request")
    void shouldRethrowFeignExceptionOnBadRequest() {
        var order = Order.builder().id(42L).securityPin("123456").build();
        var customerPhone = "+5198576854";
        var request = Request.create(
                Request.HttpMethod.POST,
                "/api/v1/notifications/sms",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var badRequestException = getBadRequest(request);

        doThrow(badRequestException)
                .when(notificationFeignClient)
                .sendSms(any(SendSmsRequestDto.class));

        assertThatThrownBy(() -> notificationAdapter.notifyOrderReady(order, customerPhone))
                .isInstanceOf(FeignException.BadRequest.class);
    }

    @Test
    @DisplayName("Should rethrow FeignException when notification-service returns 500 Internal Server Error")
    void shouldRethrowFeignExceptionOnInternalServerError() {
        var order = Order.builder().id(42L).securityPin("123456").build();
        var customerPhone = "+5198576854";
        var request = Request.create(
                Request.HttpMethod.POST,
                "/api/v1/notifications/sms",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );
        var serverErrorException = getInternalServerError(request);

        doThrow(serverErrorException)
                .when(notificationFeignClient).sendSms(any(SendSmsRequestDto.class));

        assertThatThrownBy(() -> notificationAdapter.notifyOrderReady(order, customerPhone))
                .isInstanceOf(FeignException.InternalServerError.class);
    }

    private FeignException.@NotNull BadRequest getBadRequest(Request request) {
        var jsonBody = "{\n" +
                "    \"message\": \"Invalid phone format\"\n" +
                "}";

        return new FeignException.BadRequest(
                "Bad Request",
                request,
                jsonBody.getBytes(StandardCharsets.UTF_8),
                Collections.emptyMap()
        );
    }

    private FeignException.@NotNull InternalServerError getInternalServerError(Request request) {
        return new FeignException.InternalServerError(
                "Internal Server Error",
                request,
                null,
                Collections.emptyMap()
        );
    }
}
