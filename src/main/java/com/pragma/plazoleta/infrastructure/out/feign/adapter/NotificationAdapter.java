package com.pragma.plazoleta.infrastructure.out.feign.adapter;

import com.pragma.plazoleta.domain.exception.order.OrderCannotBeCancelledException;
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

    @Override
    public void notifyOrderReady(Order order, String clientPhone) {
        try {
            var message = String.format(
                    MESSAGE_ORDER_READY_TEMPLATE,
                    order.getId(),
                    order.getSecurityPin()
            );

            var sendSmsRequest = SendSmsRequestDto.builder()
                    .message(message)
                    .to(clientPhone)
                    .build();

            notificationFeignClient.sendSms(sendSmsRequest);
        } catch (FeignException ex) {
            if (ex.status() >= 400 && ex.status() < 500) {
                log.warn("messaging-service rejected the SMS (status {}): {}", ex.status(), ex.contentUTF8());
            }

            log.error("messaging-service unavailable when sending SMS for order {} (status {})", order.getId(), ex.status(), ex);

            throw ex;
        }
    }

    @Override
    public void notifyOrderCannotCancelled(Order order, String clientPhone) {
        return;
    }
}
