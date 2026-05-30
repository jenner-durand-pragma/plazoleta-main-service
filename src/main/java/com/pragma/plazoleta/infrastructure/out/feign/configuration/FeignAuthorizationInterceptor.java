package com.pragma.plazoleta.infrastructure.out.feign.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthorizationInterceptor implements RequestInterceptor {

    private static final String AUTH_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {

            var incomingRequest = ((ServletRequestAttributes) attributes).getRequest();
            var authHeader = incomingRequest.getHeader(AUTH_HEADER);

            if (authHeader != null && !authHeader.isBlank()) {
                template.header(AUTH_HEADER, authHeader);
            }
        }
    }
}
