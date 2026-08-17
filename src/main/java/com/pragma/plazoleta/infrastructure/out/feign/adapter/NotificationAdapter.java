package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.spi.INotificationPort;
import com.pragma.plazoleta.infrastructure.out.feign.client.INotificationFeignClient;
import com.pragma.plazoleta.infrastructure.out.feign.dto.SendSmsRequestDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotificationAdapter implements INotificationPort {

    private final INotificationFeignClient notificationFeignClient;

    private static final String MESSAGE_ORDER_READY_TEMPLATE = "Your order #%d is ready. PIN: %s";
    private static final String MESSAGE_ORDER_CANNOT_BE_CANCELLED
            = "Sorry, your order is already in preparation and cannot be cancelled.";

    @Override
    public void notifyOrderReady(Order order, String clientPhone) {
        var message = String.format(
                MESSAGE_ORDER_READY_TEMPLATE,
                order.getId(),
                order.getSecurityPin()
        );

        sendSmsNotification(message, clientPhone);
    }

    @Override
    public void notifyOrderCannotCancelled(Order order, String clientPhone) {
        sendSmsNotification(MESSAGE_ORDER_CANNOT_BE_CANCELLED, clientPhone);
    }

    private void sendSmsNotification(String message, String phone) {
        try {
            var sendSmsRequest = SendSmsRequestDto.builder()
                    .message(message)
                    .to(phone)
                    .build();

            notificationFeignClient.sendSms(sendSmsRequest);
        } catch (FeignException ex) {
            if (ex.status() >= 400 && ex.status() < 500) {
                log.warn("messaging-service rejected the SMS (status {}): {}", ex.status(), ex.contentUTF8());
            }

            log.error(
                    "messaging-service unavailable when sending SMS. Phone: {} Message: {} (status {})",
                    phone,
                    message,
                    ex.status(),
                    ex
            );

            throw ex;
        }
    }
}
